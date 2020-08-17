package banking;

import java.sql.*;
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
    public static boolean CheckLuhnAlgorithm(String ccNumber) {
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
    private static Connection connectToDatabase(String url) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Exception while connecting to database");
            System.out.println(e.getMessage());
        }
        return connection;
    }
    public static void createNewDatabase(String url) {
        try (Connection connection = connectToDatabase(url)) {
            if (connection != null) {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void createNewTableInDatabase(String url) {

        // SQl statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "     id INTEGER PRIMARY KEY,\n"
                + "     number TEXT,\n"
                + "     pin TEXT,\n"
                + "     balance INTEGER DEFAULT 0"
                + ");";

        try (Connection connection = connectToDatabase(url)){
            Statement statement = connection.createStatement();
            // create a new table
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insertDataToDatabase(String data, String url) {
        String sql;
        if (data.length() > 4) {
            sql = "INSERT INTO card(number) VALUES(?)";
        } else {
            sql = "INSERT INTO card(pin) VALUES(?)";
        }
        try (Connection connection = connectToDatabase(url);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, data);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void selectDataFromDatabase(String data, String url) {
        String sql = "";
        String temp = "";
        if (data.length() == 16) {
            sql = "SELECT number FROM card WHERE number=?";
            temp = "number";
        } else if (data.length() == 4) {
            sql = "SELECT pin FROM card WHERE pin=?";
            temp = "pin";
        } else if (data.equals("Balance")) {
            sql = "SELECT balance FROM card";
            temp = "balance";
        }
        try (Connection connection = connectToDatabase(url);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (data.equals("Balance")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println(resultSet.getInt(temp));
            } else {
                preparedStatement.setString(1, data);
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println(resultSet.getString(temp));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static boolean valueExistsOrNotInDatabase(String data, String url) {
        String sql;
        if (data.length() == 4) {
            sql = "SELECT EXISTS(SELECT pin FROM card WHERE pin = data)";
        } else {
            sql = "SELECT EXISTS(SELECT number FROM card WHERE number = data)";
        }
        try (Connection connection = connectToDatabase(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public static void dropTableInDatabase(String url) {
        String sql = "DROP TABLE IF EXISTS card";
        try (Connection connection = connectToDatabase(url);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean tr = true;
        boolean ft = true;
        String url = "jdbc:sqlite:" + args[1];

        dropTableInDatabase(url);
        createNewDatabase(url);
        createNewTableInDatabase(url);
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
                            insertDataToDatabase("400000" + tempForCard, url);
                            selectDataFromDatabase("400000" + tempForCard, url);
                            break;
                        }
                    }

                    System.out.println("Your card PIN:");
                    StringBuilder tempForPin = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        tempForPin.append(random.nextInt(10));
                    }
                    insertDataToDatabase(String.valueOf(tempForPin), url);
                    selectDataFromDatabase(String.valueOf(tempForPin), url);
                    System.out.println();
                    break;
                case 2:
                    Scanner scanner1 = new Scanner(System.in);
                    System.out.println("Enter your card number:");
                    String card = scanner1.nextLine();
                    System.out.println("Enter your PIN:");
                    String pin = scanner1.nextLine();
                    if (!(valueExistsOrNotInDatabase(card, url)) || !(valueExistsOrNotInDatabase(pin, url))) {
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
                                    System.out.print("Balance: ");
                                    selectDataFromDatabase("Balance", url);
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
