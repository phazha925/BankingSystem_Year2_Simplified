package bank.controller;

import bank.model.Role;
import bank.model.User;
import java.sql.SQLException;

public class DemoData {
    public static void ensure(AppContext ctx) throws SQLException {
        if (ctx.userDAO.count() == 0) {
            ctx.userDAO.create(new User("manager1", "manager123", Role.MANAGER, null));
            ctx.userDAO.create(new User("teller1", "teller123", Role.TELLER, null));
        }

        if (ctx.customerDAO.count() == 0) {
            loadSampleCustomers(ctx);
        }
    }

    private static void loadSampleCustomers(AppContext ctx) throws SQLException {
        User manager = ctx.userDAO.findByUsername("manager1");
        ctx.session.login(manager);

        try {
            String[][] people = {
                    {"Kabo", "Molefe", "Plot 12, Gaborone", "71234501"},
                    {"Naledi", "Sebego", "Block 6, Francistown", "71234502"},
                    {"Thabo", "Kgosi", "Phakalane, Gaborone", "71234503"},
                    {"Lorato", "Dube", "Maun", "71234504"},
                    {"Tumelo", "Phiri", "Palapye", "71234505"},
                    {"Boitumelo", "Seretse", "Mogoditshane", "71234506"},
                    {"Onkabetse", "Mmusi", "Kasane", "71234507"},
                    {"Neo", "Tau", "Lobatse", "71234508"},
                    {"Masego", "Ndlovu", "Serowe", "71234509"},
                    {"Kagiso", "Banda", "Gaborone West", "71234510"}
            };

            for (int i = 0; i < people.length; i++) {
                int id = ctx.customers.register(people[i][0], people[i][1], people[i][2], people[i][3]);

                if (i % 3 == 0) {
                    ctx.accounts.openAccount(id, "SAVINGS", 1000 + i * 100, "Main Branch", null, null);
                } else if (i % 3 == 1) {
                    ctx.accounts.openAccount(id, "INVESTMENT", 2000 + i * 100, "Main Branch", null, null);
                } else {
                    ctx.accounts.openAccount(id, "CHEQUE", 3000 + i * 100, "Main Branch",
                            "Botswana Power Corporation", "Plot 1, Gaborone");
                }

                if (i % 4 == 0) {
                    ctx.accounts.openAccount(id, "SAVINGS", 500, "Main Branch", null, null);
                }

                if (i == 0) {
                    ctx.userDAO.create(new User("cust1", "cust123", Role.CUSTOMER, id));
                }
            }
        } finally {
            ctx.session.logout();
        }
    }
}
