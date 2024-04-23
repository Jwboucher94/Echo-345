package com.echo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    // this is not fully working yet. 
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        AccountDatabase accountDB;
        try {
            accountDB = loadDatabase();
            displayLoginMenu(accountDB);
        } catch (FileNotFoundException e) {
            System.err.println("Error loading database: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }

    static void displayLoginMenu(AccountDatabase accountDB) {
        SessionManager sessionManager = new SessionManager(accountDB);
        Session session;
        Integer counter = 1;
        for (Role role : Role.values()){
            System.out.println(counter + ". Login - " + role);
            counter++;
        }
        System.out.println(counter++ +". Exit");
        Integer input = getMenuInput(counter - 1);
        if (input == counter - 1) {
            System.out.println("Exiting...");
            return;
        }
        Role role = Role.values()[input - 1];
        clearScreen();
        System.out.println("You have selected " + role + " role.");
        System.out.println("Please enter your username:");
        System.out.print("> ");
        String username = scanner.nextLine();
        System.out.println("Please enter your password:");
        System.out.print("> ");
        String password = scanner.nextLine();
        System.out.println("User: " + username + " has logged in with role: " + role + " and password: " + password); // for testing - delete later
        try {
            session = sessionManager.login(username, password, role); // Use valid credentials
        } catch (SessionManager.InvalidCredentialsException e) {
            System.err.println("Unexpected InvalidCredentialsException");
        }
        
        System.out.println("Session created");
    }

    // need a test for this
    boolean passwordCheck(String password) {
        boolean test = password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$");
        return test;
    }

    // need a test for this
    void passwordCheckInstruction() {
        System.out.println("Password must contain, at minimum:\n"+
                           "8 Total characters\n"+
                           "1 uppercase letter\n"+
                           "1 lowercase letter\n"+
                           "1 number\n"+
                           "1 special character.");
    }

    // need a test for this
    boolean changePassword(Session session, String password) {
        if (passwordCheck(password)) {
            session.logout();
            return true;
        } else {
            return false;
        }
    }

    boolean changeLoginName(Session session, String loginName) {
        Session.validateSession(session);
        return true;
    }


    static AccountDatabase loadDatabase() throws FileNotFoundException, IOException {
        String csvFile = Thread.currentThread().getContextClassLoader().getResource("MOCK_DATA.csv").getFile(); 
        System.out.println("CSV File: " + csvFile);
        if (csvFile == null) {
            throw new FileNotFoundException("MOCK_DATA.csv not found in test resources");
        }
        try {
            AccountDatabase accountDB = new AccountDatabase(csvFile);
            return accountDB;
        } catch (IOException e) {
            throw new IOException("Error loading account database: " + e.getMessage());
        }
    }

    private static void clearScreen() {
        System.out.println("\n".repeat(50));
    }

    static Integer getMenuInput(Integer validOptions) {
        Integer input = 0;
        while (input == 0) {
            try {
                char firstint = scanner.next().charAt(0);
                scanner.nextLine();
                input = Character.getNumericValue(firstint);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
            }
            if (input == 0 || input > validOptions) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return input;
    }
}
