package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.AccountService;
import service.LoggingService;
import service.PendingTransactionService;
import service.TransactionService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ATMTest {
    @Mock
    private TransactionService transactionService;
    @Mock
    private PendingTransactionService pendingTransactionService;
    @Mock
    private LoggingService loggingService;
    @Mock
    private AccountService accountService;

    private ATM atm;


    @BeforeEach
    void setUp() {
        atm = new ATM(accountService, loggingService, pendingTransactionService,transactionService);
    }

    @Test
    void shouldLoginUser() {

        String name = "Alice";
        String[] command = {"login", name};

        when(accountService.createAccount(name)).thenReturn(new Account(name, BigDecimal.ZERO));

        atm.loginUser(command);

        verify(pendingTransactionService).printCreditAndDebtMessages(name);
    }

    @Test
    void shouldLogoutWithLoggedInUser() {
        String name = "Alice";
        when(accountService.createAccount(name)).thenReturn(new Account(name, BigDecimal.ZERO));
        atm.loginUser(new String[]{"login", name});
        atm.logout();
    }

    @Test
    void shouldDepositValidAmount() {
        String name = "Alice";
        String[] depositCommand = {"deposit", "100"};
        when(accountService.createAccount(name)).thenReturn(new Account(name, BigDecimal.ZERO));

        atm.loginUser(new String[]{"login", name});
        atm.deposit(depositCommand);

        verify(transactionService).deposit(name, new BigDecimal("100"));
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        String[] depositCommand = {"deposit", "-50"};

        String user = "Alice";
        when(accountService.createAccount(user)).thenReturn(new Account(user, BigDecimal.ZERO));

        atm.loginUser(new String[]{"login", user});

        atm.deposit(depositCommand);

        verify(transactionService, never()).deposit(eq(user), any(BigDecimal.class));
    }

    @Test
    void shouldTransferValidAmount() {
        String user = "Alice";
        when(accountService.createAccount(user)).thenReturn(new Account(user, BigDecimal.ZERO));
        atm.loginUser(new String[]{"login", user});
        String beneficiary = "Bob";
        String amount = "50";
        String[] transferCommand = {"transfer", beneficiary, amount};

        atm.transfer(transferCommand);

        TransferRequest transferRequest = new TransferRequest(user, beneficiary, new BigDecimal(amount));
        verify(transactionService).transfer(transferRequest);
    }

    @Test
    void shouldTransferInvalidAmount() {
        String user = "Alice";
        when(accountService.createAccount(user)).thenReturn(new Account(user, BigDecimal.ZERO));

        atm.loginUser(new String[]{"login", "Alice"});
        String[] transferCommand = {"transfer", "Bob", "-50"};

        atm.transfer(transferCommand);

        verify(transactionService, never()).transfer(any(TransferRequest.class));


    }
}