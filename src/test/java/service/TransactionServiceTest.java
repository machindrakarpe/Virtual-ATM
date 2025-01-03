package service;

import domain.Account;
import domain.PendingTransaction;
import domain.TransferRequest;
import exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private PendingTransactionService pendingTransactionService;
    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldDepositUpdatesBalance() {

        String user = "Alice";
        BigDecimal depositAmount = new BigDecimal("100");
        Account account = new Account(user, new BigDecimal("50"));

        when(accountService.getAccount(user)).thenReturn(Optional.of(account));

        transactionService.deposit(user, depositAmount);

        assertEquals(new BigDecimal("150"), account.getBalance());
    }

    @Test
    void shouldDepositThrowsExceptionForNonExistentAccount() {

        String user = "Bob";
        BigDecimal depositAmount = new BigDecimal("100");

        when(accountService.getAccount(user)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.deposit(user, depositAmount));
        assertEquals("Account does not exist for user Bob", exception.getMessage());
    }

    @Test
    void shouldTransferFullAmount() {

        String sender = "Alice";
        String beneficiary = "Bob";
        BigDecimal transferAmount = new BigDecimal("50");
        Account senderAccount = new Account(sender, new BigDecimal("100"));
        Account beneficiaryAccount = new Account(beneficiary, new BigDecimal("20"));

        when(accountService.getAccount(sender)).thenReturn(Optional.of(senderAccount));
        when(accountService.getAccount(beneficiary)).thenReturn(Optional.of(beneficiaryAccount));
        when(pendingTransactionService.settlePendingTransaction(any())).thenReturn(new BigDecimal("50"));

        TransferRequest transferRequest = new TransferRequest(sender, beneficiary, transferAmount);

        transactionService.transfer(transferRequest);

        assertEquals(new BigDecimal("50"), senderAccount.getBalance());
        assertEquals(new BigDecimal("70"), beneficiaryAccount.getBalance());
        verify(loggingService).logTransferMessage(transferAmount, beneficiary);
    }

    @Test
    void shouldTransferPartialAmountWithPendingTransaction() {

        String sender = "Alice";
        String beneficiary = "Bob";
        BigDecimal transferAmount = new BigDecimal("50");
        Account senderAccount = new Account(sender, new BigDecimal("30"));
        Account beneficiaryAccount = new Account(beneficiary, new BigDecimal("20"));

        when(accountService.getAccount(sender)).thenReturn(Optional.of(senderAccount));
        when(accountService.getAccount(beneficiary)).thenReturn(Optional.of(beneficiaryAccount));
        when(pendingTransactionService.settlePendingTransaction(any())).thenReturn(new BigDecimal("50"));

        TransferRequest transferRequest = new TransferRequest(sender, beneficiary, transferAmount);

        transactionService.transfer(transferRequest);

        assertEquals(BigDecimal.ZERO, senderAccount.getBalance());
        assertEquals(new BigDecimal("50"), beneficiaryAccount.getBalance());

        verify(pendingTransactionService).createPendingTransaction(sender, beneficiary, new BigDecimal("20"));
    }

    @Test
    void shouldProcessPendingTransactions() {

        String sender = "Alice";
        Account senderAccount = new Account(sender, new BigDecimal("50"));
        PendingTransaction pendingTransaction = new PendingTransaction(sender, "Bob", new BigDecimal("30"));
        Account beneficiaryAccount = new Account("Bob", new BigDecimal("20"));

        when(pendingTransactionService.findBySender(sender)).thenReturn(List.of(pendingTransaction));
        when(accountService.getAccount("Bob")).thenReturn(Optional.of(beneficiaryAccount));

        transactionService.processPendingTransactions(senderAccount);

        assertEquals(new BigDecimal("20"), senderAccount.getBalance());
        assertEquals(new BigDecimal("50"), beneficiaryAccount.getBalance());
        verify(loggingService).logTransferMessage(new BigDecimal("30"), "Bob");
        verify(pendingTransactionService).removePendingTransaction(pendingTransaction);
    }

    @Test
    void shouldProcessPendingTransactionsWhenRequiredAmountIsMoreThanAvailableInSenderAccount() {
        String sender = "Alice";
        Account senderAccount = new Account(sender, new BigDecimal("20"));
        PendingTransaction pendingTransaction = new PendingTransaction(sender, "Bob", new BigDecimal("30"));
        Account beneficiaryAccount = new Account("Bob", new BigDecimal("20"));

        when(pendingTransactionService.findBySender(sender)).thenReturn(List.of(pendingTransaction));
        when(accountService.getAccount("Bob")).thenReturn(Optional.of(beneficiaryAccount));

        transactionService.processPendingTransactions(senderAccount);

        assertEquals(BigDecimal.ZERO, senderAccount.getBalance());
        assertEquals(new BigDecimal("40"), beneficiaryAccount.getBalance());
        verify(loggingService).logTransferMessage(new BigDecimal("20"), "Bob");
        assertEquals(new BigDecimal("10"), pendingTransaction.getAmount());
    }

    @Test
    void shouldWithdrawSuccessfulWhenAccountHasSufficientBalance() {
        String user = "Alice";
        BigDecimal withdrawAmount = BigDecimal.valueOf(100);
        Account account = new Account(user, new BigDecimal("900"));

        when(accountService.getAccount(user)).thenReturn(Optional.of(account));

        BigDecimal remainingBalance = transactionService.withdraw(user, withdrawAmount);

        assertEquals(BigDecimal.valueOf(800), remainingBalance, "Balance should be updated after successful withdrawal.");
        verify(loggingService, never()).log(anyString());
    }

    @Test
    void shouldNotWithdrawWhenInsufficientBalance() {
        String user = "Alice";
        BigDecimal withdrawAmount = BigDecimal.valueOf(110);
        Account account = new Account(user, BigDecimal.valueOf(100));

        when(accountService.getAccount(user)).thenReturn(Optional.of(account));

        BigDecimal remainingBalance = transactionService.withdraw(user, withdrawAmount);

        assertEquals(BigDecimal.valueOf(100), remainingBalance, "Balance should remain unchanged when funds are insufficient.");

        verify(loggingService).log("Insufficient balance");
    }

    @Test
    void shouldNotWithdrawWhenAccountNotFound() {
        String user = "NonExistentUser";
        BigDecimal withdrawAmount = BigDecimal.valueOf(100);

        when(accountService.getAccount(user)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
                () -> transactionService.withdraw(user, withdrawAmount));

        assertEquals("Account does not exist for user " + user, exception.getMessage(),
                "Exception message should indicate the account was not found.");
    }

}
