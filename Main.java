package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void printAction() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }
    public static void printActionAfterLog() {
        System.out.println("1. Balance\n" +
                "2. Log out\n" +
                "0. Exit");
    }
    public static boolean CheckLuhnAlgorithm(String ccNumber)
    {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
    public static void main(String[] args) {
        List<String> cardNumberS = new ArrayList<>();
        List<String> cardPinS = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean tr = true;
        boolean ft = true;
        while (tr) {
            printAction();
            int action = scanner.nextInt();
            System.out.println();
            switch (action) {
                case 0:
                    System.out.println("Bye!");
                    tr = false;
                    break;
                case 1:
                    System.out.println("Your card has been created");

                    System.out.println("Your card number:");
                    while (true) {
                        StringBuilder tempForCard = new StringBuilder();
                        for (int i = 0; i < 10; i++) {
                            tempForCard.append(random.nextInt(10));
                        }
                        if (CheckLuhnAlgorithm("400000" + tempForCard)) {
                            cardNumberS.add("400000" + tempForCard);
                            System.out.println(cardNumberS.get(cardNumberS.size() - 1));
                            break;
                        }
                    }

                    System.out.println("Your card PIN:");
                    StringBuilder tempForPin = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        tempForPin.append(random.nextInt(10));
                    }
                    cardPinS.add("" + tempForPin);
                    System.out.println(cardPinS.get(cardPinS.size() - 1));
                    System.out.println();
                    break;
                case 2:
                    Scanner scanner1 = new Scanner(System.in);
                    System.out.println("Enter your card number:");
                    String card = scanner1.nextLine();
                    System.out.println("Enter your PIN:");
                    String pin = scanner1.nextLine();
                    if (!(cardNumberS.contains(card)) || !(cardPinS.contains(pin))) {
                        System.out.println("Wrong card number or PIN!");
                        System.out.println();
                    } else {
                        System.out.println("You have successfully logged in!");
                        System.out.println();
                        while (ft) {
                            printActionAfterLog();
                            int actionLog = scanner.nextInt();
                            System.out.println();
                            switch (actionLog) {
                                case 0:
                                    ft = false;
                                    tr = false;
                                    break;
                                case 1:
                                    System.out.println("Balance: 0");
                                    System.out.println();
                                    break;
                                case 2:
                                    System.out.println("You have successfully logged out!");
                                    ft = false;
                                    System.out.println();
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }
}
