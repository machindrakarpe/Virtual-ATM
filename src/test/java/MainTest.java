import domain.ATM;
import domain.Account;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    public static final BigDecimal INITIAL_ACCOUNT_BALANCE = BigDecimal.ZERO;
    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";

    @Test
    public void shouldTestAtm(){

        ATM atm = new ATM();

        Main.processCommand("login Alice", atm);

        Optional<Account> optionalAccount = atm.getAccountDetails(ALICE);

        assertTrue(optionalAccount.isPresent());
        Account aliceAccount = optionalAccount.get();

        assertEquals(INITIAL_ACCOUNT_BALANCE, aliceAccount.getBalance());

        Main.processCommand("deposit 100", atm);

        assertEquals(BigDecimal.valueOf(100), aliceAccount.getBalance());

        Main.processCommand("logout", atm);
        Main.processCommand("login Bob", atm);
        Optional<Account> optionalBobAccount = atm.getAccountDetails(BOB);

        assertTrue(optionalBobAccount.isPresent());
        Account bobAccount = optionalBobAccount.get();
        assertEquals(INITIAL_ACCOUNT_BALANCE, bobAccount.getBalance());

        Main.processCommand("deposit 80", atm);
        assertEquals(BigDecimal.valueOf(80), bobAccount.getBalance());

        Main.processCommand("transfer Alice 50", atm);

        assertEquals(BigDecimal.valueOf(30), bobAccount.getBalance());
        assertEquals(BigDecimal.valueOf(150), aliceAccount.getBalance());

        Main.processCommand("transfer Alice 100", atm); //At this point Bob has only $30 only that amount is transfer and Owed $70 to Alice.

        assertEquals(BigDecimal.valueOf(180), aliceAccount.getBalance());
        assertEquals(BigDecimal.ZERO, bobAccount.getBalance());

        Main.processCommand("deposit 30", atm); // As soon as Bob deposit $30 ATM transfers $30 to Alice, since Bob was owing $70 in total
                                                        // and now Bob is owed $40 to Alice

        assertEquals(BigDecimal.valueOf(210), aliceAccount.getBalance());
        assertEquals(BigDecimal.ZERO, bobAccount.getBalance()); //Since deposited money transferred to Alice

        Main.processCommand("logout", atm);
        Main.processCommand("login Alice", atm);
        Main.processCommand("transfer Bob 30", atm);

        assertEquals(BigDecimal.valueOf(210), aliceAccount.getBalance());
        assertEquals(BigDecimal.ZERO, bobAccount.getBalance()); //Since Bob was owing $40 Alice, this transfer has settled amount $30 and now Bob is owing only $10 to Alice


        Main.processCommand("logout", atm);
        Main.processCommand("login Bob", atm);
        Main.processCommand("deposit 100", atm); //As soon as Bob deposit $100 ATM transfer to $10 to Alice since Bob was owing $10 to Alice in total
                                                            // and now Bob is owed $40 to Alice

        assertEquals(BigDecimal.valueOf(220), aliceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(90), bobAccount.getBalance());

        Main.processCommand("logout", atm);
    }
}