package service;

import domain.Account;
import domain.PendingTransaction;
import domain.TransferRequest;
import exception.AccountNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public class TransactionService {

    private final AccountService accountService;
    private final PendingTransactionService pendingTransactionService;
    private final LoggingService loggingService;

    public TransactionService(AccountService accountService, PendingTransactionService transactionService, LoggingService loggingService) {
        this.accountService = accountService;
        this.pendingTransactionService = transactionService;
        this.loggingService = loggingService;
    }

    public BigDecimal deposit(String user, BigDecimal depositAmount) {
        return accountService.getAccount(user).map(account -> {
            account.addBalance(depositAmount);
            processPendingTransactions(account);
            return account.getBalance();

        }).orElseThrow(() -> new AccountNotFoundException("Account does not exist for user " + user));
    }

    public BigDecimal withdraw(String user, BigDecimal withdrawAmount) {
        return accountService.getAccount(user).map(account -> {
            if (account.hasSufficientBalance(withdrawAmount)) {
               account.reduceBalance(withdrawAmount);
               return account.getBalance();
            }
            loggingService.log("Insufficient balance");
            return account.getBalance();

        }).orElseThrow(() -> new AccountNotFoundException("Account does not exist for user " + user));
    }

    public synchronized void transfer(final TransferRequest transferRequest) {
        Account senderAccount = accountService.getAccount(transferRequest.sender()).orElseThrow(() -> new RuntimeException("Account does not exist for user " + transferRequest.beneficiary()));
        Account beneficiaryAccount = accountService.getAccount(transferRequest.beneficiary()).orElseThrow(() -> new RuntimeException("Account does not exist for user " + transferRequest.beneficiary()));

        BigDecimal remainingAmount = pendingTransactionService.settlePendingTransaction(transferRequest);
        if(remainingAmount.compareTo(BigDecimal.ZERO) <= 0){
            loggingService.printBalance(senderAccount.getBalance());
            pendingTransactionService.printCreditAndDebtMessages(transferRequest.sender());
            return;
        }

        TransferRequest newTransferRequest = new TransferRequest(transferRequest.sender(), transferRequest.beneficiary(), remainingAmount);
        processTransfer(newTransferRequest, senderAccount, beneficiaryAccount);
        loggingService.printBalance(senderAccount.getBalance());
        pendingTransactionService.printCreditAndDebtMessages(transferRequest.sender());
    }

    private void processTransfer(TransferRequest transferRequest, Account senderAccount, Account beneficiaryAccount) {
        if (senderAccount.hasSufficientBalance(transferRequest.amount())) {
            senderAccount.reduceBalance(transferRequest.amount());
            loggingService.logTransferMessage(transferRequest.amount(), transferRequest.beneficiary());
            beneficiaryAccount.addBalance(transferRequest.amount());
        } else {
            partialTransfer(transferRequest, senderAccount, beneficiaryAccount);
        }
    }

    private void partialTransfer(TransferRequest transferRequest, Account senderAccount, Account beneficiaryAccount) {

        BigDecimal transferredAmount = senderAccount.getBalance();
        BigDecimal amountOwedToBeneficiary = transferRequest.amount().subtract(transferredAmount);
        senderAccount.setBalance(BigDecimal.ZERO);
        loggingService.logTransferMessage(transferredAmount, transferRequest.beneficiary());
        beneficiaryAccount.addBalance(transferredAmount);

        pendingTransactionService.createPendingTransaction(senderAccount.getName(), beneficiaryAccount.getName(), amountOwedToBeneficiary);
    }

    public void processPendingTransactions(Account senderAccount) {
        if (senderAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            List<PendingTransaction> pendingTransactions = pendingTransactionService.findBySender(senderAccount.getName());
            for (PendingTransaction pendingTransaction : pendingTransactions) {
                if (senderAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                Account beneficiaryAccount = accountService.getAccount(pendingTransaction.getBeneficiary())
                        .orElseThrow(() -> new AccountNotFoundException("Account does not exist for user " + pendingTransaction.getBeneficiary()));

                BigDecimal requiredAmount = pendingTransaction.getAmount();
                BigDecimal transferredAmount;
                BigDecimal availableBalance = senderAccount.getBalance();
                if (requiredAmount.compareTo(availableBalance) <= 0) {
                    transferredAmount = requiredAmount;
                    senderAccount.setBalance(availableBalance.subtract(requiredAmount));
                    beneficiaryAccount.addBalance(requiredAmount);
                    pendingTransactionService.removePendingTransaction(pendingTransaction);
                } else {
                    transferredAmount = senderAccount.getBalance();
                    pendingTransaction.reduceAmount(senderAccount.getBalance());
                    beneficiaryAccount.addBalance(senderAccount.getBalance());
                    senderAccount.setBalance(BigDecimal.ZERO);
                }

                loggingService.logTransferMessage(transferredAmount, pendingTransaction.getBeneficiary());
            }
        }
    }
}
