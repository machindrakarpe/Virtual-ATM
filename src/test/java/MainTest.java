import domain.ATM;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    public void shouldTestAtm(){

        ATM atm = new ATM();

        Main.processCommand("login Alice", atm);
        Main.processCommand("deposit 100", atm);
        Main.processCommand("logout", atm);
        Main.processCommand("login Bob", atm);
        Main.processCommand("deposit 80", atm);
        Main.processCommand("transfer Alice 50", atm);
        Main.processCommand("transfer Alice 100", atm);
        Main.processCommand("deposit 30", atm);
        Main.processCommand("logout", atm);
        Main.processCommand("login Alice", atm);
        Main.processCommand("transfer Bob 30", atm);
        Main.processCommand("logout", atm);
        Main.processCommand("login Bob", atm);
        Main.processCommand("deposit 100", atm);
        Main.processCommand("logout", atm);
    }
}