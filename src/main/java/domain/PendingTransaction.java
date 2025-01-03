package domain;

import java.math.BigDecimal;
import java.util.Objects;

public class PendingTransaction {
    private String sender;
    private final String beneficiary;
    private BigDecimal amount;

    public PendingTransaction(String sender, String beneficiary, BigDecimal amount) {
        this.sender = sender;
        this.beneficiary = beneficiary;
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void addAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void reduceAmount(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingTransaction that)) return false;
        return Objects.equals(sender, that.sender) && Objects.equals(beneficiary, that.beneficiary) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, beneficiary, amount);
    }

    @Override
    public String toString() {
        return "PendingTransaction{" +
                "sender='" + sender + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                ", amount=" + amount +
                '}';
    }
}