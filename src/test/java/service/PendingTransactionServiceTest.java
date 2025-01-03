package service;

import domain.PendingTransaction;
import domain.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PendingTransactionServiceTest {

    @Mock
    LoggingService loggingService;

    @InjectMocks
    PendingTransactionService pendingTransactionService;

    @Test
    void shouldAddPendingTransaction() {
        LoggingService loggingService = mock(LoggingService.class);
        PendingTransactionService pendingTransactionService = new PendingTransactionService(loggingService);
        PendingTransaction transaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));

        pendingTransactionService.add(transaction);

        List<PendingTransaction> transactions = pendingTransactionService.findBySender("Alice");
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));
    }

    @Test
    void shouldCreatePendingTransaction() {

        String sender = "Alice";
        String beneficiary = "Bob";
        BigDecimal initialAmount = new BigDecimal("100");
        BigDecimal additionalAmount = new BigDecimal("50");

        pendingTransactionService.createPendingTransaction(sender, beneficiary, initialAmount);
        pendingTransactionService.createPendingTransaction(sender, beneficiary, additionalAmount);

        PendingTransaction transaction = pendingTransactionService.findBySenderAndBeneficiary(sender, beneficiary).get();

        assertEquals(sender, transaction.getSender());
        assertEquals(beneficiary, transaction.getBeneficiary());
        assertEquals(new BigDecimal("150"), transaction.getAmount());
    }

    @Test
    void shouldFindBySender() {
        pendingTransactionService.add(new PendingTransaction("Alice", "Bob", new BigDecimal("100")));
        pendingTransactionService.add(new PendingTransaction("Alice", "Charlie", new BigDecimal("50")));
        pendingTransactionService.add(new PendingTransaction("Bob", "Alice", new BigDecimal("20")));

        List<PendingTransaction> transactions = pendingTransactionService.findBySender("Alice");

        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().anyMatch(t -> t.getBeneficiary().equals("Bob")));
        assertTrue(transactions.stream().anyMatch(t -> t.getBeneficiary().equals("Charlie")));
    }

    @Test
    void shouldFindBySenderAndBeneficiary() {
        PendingTransaction transaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));
        pendingTransactionService.add(transaction);

        Optional<PendingTransaction> result = pendingTransactionService.findBySenderAndBeneficiary("Alice", "Bob");

        assertTrue(result.isPresent());
        assertEquals(transaction, result.get());
    }

    @Test
    void shouldRemovePendingTransaction() {
        PendingTransaction transaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));
        pendingTransactionService.add(transaction);

        pendingTransactionService.removePendingTransaction(transaction);

        List<PendingTransaction> transactions = pendingTransactionService.findBySender("Alice");
        assertTrue(transactions.isEmpty());
    }

    @Test
    void shouldPrintDebtMessage() {
        PendingTransaction transaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));
        pendingTransactionService.add(transaction);

        pendingTransactionService.printDebtMessage("Alice");

        verify(loggingService, times(1)).logDebtMessage(transaction);
    }

    @Test
    void shouldPrintCreditMessage() {
        PendingTransaction transaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));
        pendingTransactionService.add(transaction);

        pendingTransactionService.printCreditMessage("Bob");

        verify(loggingService, times(1)).logCreditMessage(transaction);
    }

    @Test
    void shouldSettlePendingTransaction() {
        PendingTransaction transaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));
        pendingTransactionService.add(transaction);

        TransferRequest request = new TransferRequest("Bob", "Alice", new BigDecimal("50"));

        BigDecimal remainingAmount = pendingTransactionService.settlePendingTransaction(request);

        assertEquals(new BigDecimal("-50"), remainingAmount);
        Optional<PendingTransaction> pendingTransaction = pendingTransactionService.findBySenderAndBeneficiary("Alice", "Bob");
        assertTrue(pendingTransaction.isPresent());
        assertEquals(new BigDecimal("50"), pendingTransaction.get().getAmount());
    }


    @Test
    void shouldPrintCreditAndDebtMessages() {
        PendingTransaction creditTransaction = new PendingTransaction("Alice", "Bob", new BigDecimal("100"));
        PendingTransaction debtTransaction = new PendingTransaction("Bob", "Alice", new BigDecimal("50"));
        pendingTransactionService.add(creditTransaction);
        pendingTransactionService.add(debtTransaction);

        pendingTransactionService.printCreditAndDebtMessages("Bob");

        verify(loggingService, times(1)).logCreditMessage(creditTransaction);
        verify(loggingService, times(1)).logDebtMessage(debtTransaction);
    }
}