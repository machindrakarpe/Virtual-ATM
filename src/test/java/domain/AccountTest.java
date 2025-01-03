package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("TestUser", BigDecimal.valueOf(1000));
    }

    @Test
    void shouldReduceBalance() {
        account.reduceBalance(BigDecimal.valueOf(200));
        assertEquals(BigDecimal.valueOf(800), account.getBalance(), "Balance should be reduced by the specified amount.");
    }

    @Test
    void shouldAddBalance() {
        account.addBalance(BigDecimal.valueOf(500));
        assertEquals(BigDecimal.valueOf(1500), account.getBalance(), "Balance should increase by the added amount.");
    }
}