package service;

import domain.PendingTransaction;
import domain.TransferRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PendingTransactionService {

    private final LoggingService loggingService;

    public PendingTransactionService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    private final List<PendingTransaction> pendingTransactions = new ArrayList<>();

    public void add(PendingTransaction pendingTransaction) {

        this.pendingTransactions.add(pendingTransaction);
    }

    public void createPendingTransaction(String sender, String beneficiary, BigDecimal amountOwedToBeneficiary) {
        Optional<PendingTransaction> optionalPendingTransaction = findBySenderAndBeneficiary(sender, beneficiary);
        optionalPendingTransaction.ifPresentOrElse(pendingTransaction -> pendingTransaction.addAmount(amountOwedToBeneficiary),
                () -> {
                    PendingTransaction pendingTransaction = new PendingTransaction(sender, beneficiary, amountOwedToBeneficiary);
                    this.pendingTransactions.add(pendingTransaction);
                });
    }

    public List<PendingTransaction> findBySender(String sender) {
        return pendingTransactions.stream()
                .filter(pendingTransaction -> pendingTransaction.getSender().equals(sender))
                .toList();
    }

    public Optional<PendingTransaction> findBySenderAndBeneficiary(String sender, String beneficiary) {
        return pendingTransactions.stream()
                .filter(pendingTransaction -> pendingTransaction.getSender().equals(sender))
                .filter(pendingTransaction -> pendingTransaction.getBeneficiary().equals(beneficiary))
                .findFirst();
    }


    public void removePendingTransaction(PendingTransaction pendingTransaction) {
        pendingTransactions.remove(pendingTransaction);
    }

    public void printDebtMessage(String user) {
        this.pendingTransactions.stream()
                .filter(pendingTransaction -> user.equals(pendingTransaction.getSender()))
                .forEach(loggingService::logDebtMessage);

    }

    public void printCreditMessage(String user) {
        this.pendingTransactions.stream()
                .filter(pendingTransaction -> user.equals(pendingTransaction.getBeneficiary()))
                .forEach(loggingService::logCreditMessage);
    }

    public BigDecimal settlePendingTransaction(TransferRequest transferRequest) {

        Optional<PendingTransaction> optionalPendingTransaction = this.findBySenderAndBeneficiary(transferRequest.beneficiary(), transferRequest.sender());

        return optionalPendingTransaction.map(pendingTransaction -> {
            BigDecimal requiredAmount = pendingTransaction.getAmount();

            if (requiredAmount.compareTo(transferRequest.amount()) <= 0) {
                this.removePendingTransaction(pendingTransaction);
                return transferRequest.amount().subtract(requiredAmount);
            }

            pendingTransaction.reduceAmount(transferRequest.amount());
            return transferRequest.amount().subtract(requiredAmount);
        }).orElse(transferRequest.amount());
    }

    public void printCreditAndDebtMessages(String user) {
        printCreditMessage(user);
        printDebtMessage(user);
    }
}
