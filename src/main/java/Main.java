
import domain.ATM;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("welcome to ATM!");
        startProcession();
    }

    private static void startProcession() {
        Scanner scanner = new Scanner(System.in);

        ATM atm = new ATM();

        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine().trim();
            try {
                processCommand(command, atm);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
    }

    public static void processCommand(String command, ATM atm) {
        System.out.println(command); //Uncomment this if you are running MainTest class
        String[] parts = command.split("\\s+");
        String action = parts[0].toLowerCase();

        switch (action) {
            case "login":
                atm.loginUser(parts);
                break;
            case "deposit":
                atm.deposit(parts);
                break;
            case "withdraw":
                atm.withdraw(parts);
                break;
            case "logout":
                atm.logout();
                break;
            case "transfer":
                atm.transfer(parts);
                break;
            default:
                System.out.println("Unknown command");
        }
        System.out.println();
    }
}
