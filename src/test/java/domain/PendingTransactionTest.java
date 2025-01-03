package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PendingTransactionTest {

    private PendingTransaction pendingTransaction;

    @BeforeEach
    void setUp() {
        pendingTransaction = new PendingTransaction("Alice", "Bob",  BigDecimal.valueOf(1000));
    }

    @Test
    void shouldReduceBalance() {
        pendingTransaction.reduceAmount(BigDecimal.valueOf(200));
        assertEquals(BigDecimal.valueOf(800), pendingTransaction.getAmount(), "Balance should be reduced by the specified amount.");
    }

    @Test
    void shouldAddBalance() {
        pendingTransaction.addAmount(BigDecimal.valueOf(500));
        assertEquals(BigDecimal.valueOf(1500), pendingTransaction.getAmount(), "Balance should increase by the added amount.");
    }
}