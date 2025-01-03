package domain;

import exception.InvalidAmountException;
import service.AccountService;
import service.LoggingService;
import service.PendingTransactionService;
import service.TransactionService;

import java.math.BigDecimal;

import static domain.Constant.*;

public class ATM {

    private String user;
    private AccountService accountService = new AccountService();
    private LoggingService loggingService = new LoggingService();
    private PendingTransactionService pendingTransactionService = new PendingTransactionService(loggingService);
    private TransactionService transactionService = new TransactionService(accountService, pendingTransactionService, loggingService);

    public ATM(AccountService accountService, LoggingService loggingService, PendingTransactionService pendingTransactionService, TransactionService transactionService) {
        this.accountService = accountService;
        this.loggingService = loggingService;
        this.pendingTransactionService = pendingTransactionService;
        this.transactionService = transactionService;
    }

    public ATM() {
    }

    public void loginUser(String[] command) {
        if (command.length < 2) {
            loggingService.log(PLEASE_PROVIDE_YOUR_USERNAME_LOGIN_USERNAME);
            return;
        }

        if(user != null){
            loggingService.log(user + ALREADY_LOGGED_IN);
            return;
        }

        user = command[1];

        Account account = accountService.createAccount(user);
        loggingService.greetUser(account.getName());
        loggingService.printBalance(account.getBalance());
        pendingTransactionService.printCreditAndDebtMessages(user);
    }

    public void transfer(String[] parts) {
        if (parts.length < 3) {
            loggingService.log(INVALID_TRANSFER_COMMAND);
            return;
        }

        String beneficiary = parts[1];
        String amountString = parts[2];

        BigDecimal amountToBeTransferred = convertToBigDecimal(amountString);

        if (amountToBeTransferred.compareTo(BigDecimal.ZERO) <= 0) {
            loggingService.log(TRANSFERS_WITH_NEGATIVE_OR_ZERO_AMOUNT);
            return;
        }

        TransferRequest transferRequest = new TransferRequest(this.user, beneficiary, amountToBeTransferred);
        transactionService.transfer(transferRequest);
    }

    private BigDecimal convertToBigDecimal(String amountString) {
        try {
            return new BigDecimal(amountString);
        } catch (Exception e) {
            throw new InvalidAmountException(INVALID_AMOUNT);
        }
    }

    public void logout() {
        if (user != null) {
            loggingService.log("Goodbye, " + user);
            user = null;
            return;
        }
        loggingService.log(NO_USER_IS_LOGGED_IN);
    }
    
    public void deposit(String[] parts) {
        if (user == null) {
            loggingService.log(YOU_NEED_LOGIN_FIRST);
            return;
        }

        if (parts.length < 2) {
            loggingService.log(INVALID_DEPOSIT_COMMAND);
            return;
        }

        BigDecimal depositAmount = convertToBigDecimal(parts[1]);
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            loggingService.log(ERROR_DEPOSIT_NEGATIVE_OR_ZERO_AMOUNT);
            return;
        }

        BigDecimal updatedBalance = transactionService.deposit(user, depositAmount);
        
        loggingService.printBalance(updatedBalance);
        pendingTransactionService.printCreditAndDebtMessages(user);
    }

    public void withdraw(String[] parts) {
        if (user == null) {
            loggingService.log(YOU_NEED_LOGIN_FIRST);
            return;
        }

        if (parts.length < 2) {
            loggingService.log(INVALID_DEPOSIT_COMMAND);
            return;
        }

        BigDecimal withdrawAmount = convertToBigDecimal(parts[1]);
        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            loggingService.log(ERROR_WITHDRAW_NEGATIVE_OR_ZERO_AMOUNT);
            return;
        }

        BigDecimal remainingBalance = transactionService.withdraw(user, withdrawAmount);

        loggingService.printBalance(remainingBalance);
        pendingTransactionService.printCreditAndDebtMessages(user);
    }

}
