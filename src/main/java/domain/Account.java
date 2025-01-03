package domain;

import java.math.BigDecimal;


public class Account {
    private final String name;
    private BigDecimal balance;

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void reduceBalance(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public boolean hasSufficientBalance(BigDecimal requiredAmount) {
        return this.balance.compareTo(requiredAmount) >= 0;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
