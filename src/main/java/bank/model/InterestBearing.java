package bank.model;

public interface InterestBearing {
    double calculateMonthlyInterest();
    Transaction payInterest();
}
