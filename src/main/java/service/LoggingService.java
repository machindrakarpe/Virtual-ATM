package service;


import domain.PendingTransaction;

import java.math.BigDecimal;

public class LoggingService {

    public void log(String message) {
        System.out.println(message);
    }

    public void greetUser(String name) {
        System.out.println("Hello, " + name + "!");
    }

    public void logTransferMessage(BigDecimal transferredAmount, String beneficiary) {
        System.out.printf("Transferred $%s to %s%n", transferredAmount, beneficiary);
    }

    public void printBalance(BigDecimal balance) {
        System.out.printf("Your balance is $%s%n", balance);
    }

    public void logCreditMessage(PendingTransaction pendingTransaction) {
        System.out.printf("Owed $%s from %s%n", pendingTransaction.getAmount(), pendingTransaction.getSender());
    }

    public void logDebtMessage(PendingTransaction pendingTransaction) {
        System.out.printf("Owed $%s to %s%n", pendingTransaction.getAmount(), pendingTransaction.getBeneficiary());
    }
}
