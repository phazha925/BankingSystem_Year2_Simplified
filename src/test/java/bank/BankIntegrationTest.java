package bank;

import bank.controller.AppContext;
import bank.controller.DemoData;
import bank.dao.Database;
import bank.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests: controller -> model -> DAO -> H2 database.
 * Each test gets its own empty in-memory H2 database, so tests never affect each other
 * and never touch your real data/bankdb file.
 */
class BankIntegrationTest {
    private AppContext ctx;

    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        db.init();
        ctx = new AppContext(db);
        DemoData.ensure(ctx);            // 3 users + 10 sample customers
    }

    private void assertMessage(String expected, Executable action) {
        BankException e = assertThrows(BankException.class, action);
        assertEquals(expected, e.getMessage());
    }

    private int newCustomer() {
        return ctx.customers.register("Test", "Customer", "Gaborone", "70000000");
    }

    // ---------------------------------------------------------------- UC-01 Log In
    @Test void loginSucceeds_andRoleIsSet() {
        User u = ctx.login.login("teller1", "teller123");
        assertEquals(Role.TELLER, u.getRole());
        assertEquals("teller1", ctx.session.getUser().getUsername());
    }

    @Test void threeWrongPasswordsLockTheAccount_andManagerCanUnlock() {
        assertMessage(Messages.INVALID_LOGIN, () -> ctx.login.login("teller1", "wrong"));
        assertMessage(Messages.INVALID_LOGIN, () -> ctx.login.login("teller1", "wrong"));
        assertMessage(Messages.ACCOUNT_LOCKED, () -> ctx.login.login("teller1", "wrong"));
        assertMessage(Messages.ACCOUNT_LOCKED, () -> ctx.login.login("teller1", "teller123"));   // even the right password
        ctx.login.login("manager1", "manager123");
        ctx.users.unlock("teller1");
        assertEquals(Role.TELLER, ctx.login.login("teller1", "teller123").getRole());
    }

    @Test void unknownUsernameGivesTheSameMessageAsWrongPassword() {
        assertMessage(Messages.INVALID_LOGIN, () -> ctx.login.login("nobody", "x"));
    }

    @Test void emptyFieldsAreRejected() {
        assertMessage(Messages.FIELDS_REQUIRED, () -> ctx.login.login("", ""));
    }

    // ---------------------------------------------------------------- sample data
    @Test void tenSampleCustomersAreStored() throws Exception {
        assertEquals(10, ctx.customerDAO.count());
        assertTrue(ctx.accountDAO.count() >= 10);
    }

    // ---------------------------------------------------------------- UC-02 Open Account
    @Test void accountOpeningRulesAreEnforced() {
        ctx.login.login("teller1", "teller123");
        int id = newCustomer();
        assertMessage(Messages.MIN_INVESTMENT, () -> ctx.accounts.openAccount(id, "INVESTMENT", 499.99, "Main", null, null));
        assertMessage(Messages.EMPLOYER_REQUIRED, () -> ctx.accounts.openAccount(id, "CHEQUE", 0, "Main", "", ""));
        assertMessage(Messages.CUSTOMER_NOT_FOUND, () -> ctx.accounts.openAccount(9999, "SAVINGS", 0, "Main", null, null));
        assertTrue(ctx.accounts.openAccount(id, "INVESTMENT", 500, "Main", null, null).startsWith("INV"));
    }

    @Test void oneCustomerCanHoldAllThreeAccountTypes() {
        ctx.login.login("teller1", "teller123");
        int id = newCustomer();
        ctx.accounts.openAccount(id, "SAVINGS", 100, "Main", null, null);
        ctx.accounts.openAccount(id, "INVESTMENT", 500, "Main", null, null);
        ctx.accounts.openAccount(id, "CHEQUE", 0, "Main", "BPC", "Plot 1");
        assertEquals(3, ctx.accounts.accountsOfCustomer(id).size());
    }

    // ---------------------------------------------------------------- UC-03 Deposit
    @Test void depositUpdatesBalanceAndHistory() {
        ctx.login.login("teller1", "teller123");
        String acc = ctx.accounts.openAccount(newCustomer(), "SAVINGS", 1000, "Main", null, null);
        ctx.accounts.deposit(acc, 250.50);
        assertEquals(1250.50, ctx.accounts.getAccount(acc).getBalance(), 0.001);
        assertEquals(2, ctx.accounts.history(acc).size());          // opening deposit + this deposit
    }

    @Test void depositRulesAreEnforced() {
        ctx.login.login("teller1", "teller123");
        String acc = ctx.accounts.openAccount(newCustomer(), "SAVINGS", 100, "Main", null, null);
        assertMessage(Messages.AMOUNT_NOT_POSITIVE, () -> ctx.accounts.deposit(acc, 0));
        assertMessage(Messages.ACCOUNT_NOT_FOUND, () -> ctx.accounts.deposit("SAV99999999", 10));
    }

    // ---------------------------------------------------------------- UC-04 Withdraw
    @Test void savingsCannotWithdraw_investmentCan_overdrawIsRejected() {
        ctx.login.login("teller1", "teller123");
        int id = newCustomer();
        String sav = ctx.accounts.openAccount(id, "SAVINGS", 1000, "Main", null, null);
        String inv = ctx.accounts.openAccount(id, "INVESTMENT", 1000, "Main", null, null);
        assertMessage(Messages.SAVINGS_NO_WITHDRAWAL, () -> ctx.accounts.withdraw(sav, 10));
        ctx.accounts.withdraw(inv, 400);
        assertEquals(600.00, ctx.accounts.getAccount(inv).getBalance(), 0.001);
        assertMessage(Messages.INSUFFICIENT_FUNDS, () -> ctx.accounts.withdraw(inv, 5000));
        assertEquals(600.00, ctx.accounts.getAccount(inv).getBalance(), 0.001);      // unchanged
    }

    // ---------------------------------------------------------------- UC-05 Interest
    @Test void interestIsPaidOncePerMonthToTheRightAccounts() {
        ctx.login.login("manager1", "manager123");
        int id = newCustomer();
        String sav = ctx.accounts.openAccount(id, "SAVINGS", 2000, "Main", null, null);
        String inv = ctx.accounts.openAccount(id, "INVESTMENT", 2000, "Main", null, null);
        String chq = ctx.accounts.openAccount(id, "CHEQUE", 2000, "Main", "BPC", "Plot 1");

        ctx.accounts.runMonthEndInterest();
        assertEquals(2001.00, ctx.accounts.getAccount(sav).getBalance(), 0.001);    // 0.05% of 2000 = 1.00
        assertEquals(2100.00, ctx.accounts.getAccount(inv).getBalance(), 0.001);    // 5% of 2000 = 100.00
        assertEquals(2000.00, ctx.accounts.getAccount(chq).getBalance(), 0.001);    // cheque earns nothing

        String second = ctx.accounts.runMonthEndInterest();                          // nobody is paid twice
        assertTrue(second.contains("Paid: 0"), second);
        assertEquals(2100.00, ctx.accounts.getAccount(inv).getBalance(), 0.001);
    }

    @Test void onlyAManagerMayRunInterest() {
        ctx.login.login("teller1", "teller123");
        assertMessage(Messages.NOT_AUTHORISED, () -> ctx.accounts.runMonthEndInterest());
    }

    // ---------------------------------------------------------------- roles
    @Test void aCustomerCanOnlyUseTheirOwnAccounts() {
        ctx.login.login("cust1", "cust123");                                         // owner of SAV00000001
        assertDoesNotThrow(() -> ctx.accounts.deposit("SAV00000001", 50));
        assertMessage(Messages.NOT_AUTHORISED, () -> ctx.accounts.deposit("INV00000003", 50));   // someone else's
        assertMessage(Messages.NOT_AUTHORISED, () -> ctx.customers.list());
    }
}
