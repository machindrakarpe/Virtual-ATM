package service;

import domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void shouldGetAccountWhenAccountExists() {
        String accountName = "Alice";
        accountService.createAccount(accountName);

        Optional<Account> result = accountService.getAccount(accountName);

        assertTrue(result.isPresent(), "Account should exist.");
        assertEquals(accountName, result.get().getName(), "Account name should match.");
    }

    @Test
    void shouldGetEmptyOptionalWhenAccountDoesNotExist() {
        String accountName = "Bob";

        Optional<Account> result = accountService.getAccount(accountName);

        assertTrue(result.isEmpty(), "Account should not exist.");
    }

    @Test
    void shouldCreateAccountWhenAccountDoesNotExist() {
        String accountName = "Charlie";

        Account account = accountService.createAccount(accountName);

        assertNotNull(account, "Account should be created.");
        assertEquals(accountName, account.getName(), "Account name should match.");
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Account balance should be initialized to 0.");
    }

    @Test
    void shouldGetExistingAccountWhenTryingToCreateAccountWhenAccountAlreadyExists() {

        String accountName = "David";
        accountService.createAccount(accountName);

        Account account = accountService.createAccount(accountName);

        assertNotNull(account, "Account should exist.");
        assertEquals(accountName, account.getName(), "Account name should match.");
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Existing account balance should remain 0.");
    }
}