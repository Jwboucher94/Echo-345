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
        Session session = null;
        Integer counter;
        Boolean quit = false;
        while (!quit) {
            clearScreen();
            System.out.println(" Welcome to the Registration System!");
            System.out.println("------------------------------------");
            System.out.println("Please select a role:");
            counter = 1;
            for (Role role : Role.values()){
                System.out.println(counter + ". Login - " + role);
                counter++;
            }
            System.out.println(counter++ +". Exit");
            Integer input = getMenuInput(counter - 1);
            if (input == counter - 1) {
                System.out.println("Exiting...");
                System.exit(0);
            }
            Role role;
            try {
                role = Role.values()[input - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Invalid input. Please enter a number.");
                displayLoginMenu(accountDB);
                return;
            }
            while (session == null) {
                try {
                    session = getSession(sessionManager, role);
                } catch (RuntimeException e) {
                    displayLoginMenu(accountDB);
                    return;
                }
            }
            clearScreen(("Login Successful. Welcome, " + session.getAccount().getLoginName()));
            System.out.println();
            Role loginRole = session.getRole();
            Boolean logout = false;
            try {
                while (session.validateSession() && !logout){
                    if (loginRole == Role.ADMIN) {
                        logout = AdminMenu.displayAdminMenu(accountDB, session, logout);
                    } else if (loginRole == Role.STUDENT) {
                        logout = StudentMenu.displayStudentMenu(accountDB, session, logout);
                    } else if (loginRole == Role.ADVISOR) {
                        logout = AdvisorMenu.displayAdvisorMenu(accountDB, session, logout);
                    } else {
                        System.err.println("Invalid role: " + loginRole);
                    }
                }
            } catch (AccessViolationException | ExpiredSessionException e) {
                System.err.println("Session expired: " + e.getMessage());
                logout = false;
                System.err.println("Session expired. Logging out...");
            }
            session.logout(session.getHasModified());
            session = null;
            logout = false;
            System.out.println("Press Enter to continue");
        }
    }

    static Session getSession (SessionManager sessionManager, Role role) {
        clearScreen();
        System.out.println("You have selected " + role + " role.");
        System.out.println("Please enter your username:");
        String username = getInput();
        System.out.println("Please enter your password:");
        String password = getInput(); 
        try {
            Session session = sessionManager.login(username, password, role); // Use valid credentials
            return session;
        } catch (InvalidCredentialsException e) {
            System.err.println("Invalid credentials: " + e.getMessage());
            System.out.println("Press Enter to try again or type 'q' to return to main menu");
            String input = getInput();
            if (input.toLowerCase().startsWith("q")) {
                throw new RuntimeException("Returning to main menu");
            }
            return null;
        }
    }

    static AccountDatabase loadDatabase() throws FileNotFoundException, IOException {
        String csvFile = "MOCK_DATA.csv"; // "MOCK_DATA.csv" is the file name
        try {
            AccountDatabase accountDB = new AccountDatabase(csvFile);
            return accountDB;
        } catch (IOException e) {
            throw new IOException("Error loading account database: " + e.getMessage());
        }
    }

    public static void clearScreen(String title) {
        System.out.println("\n".repeat(50));
        int titleLength = title.length();
        int dashLength = 0;
        if (titleLength < 36) {
            dashLength = 36 - titleLength;
        }
        System.out.println(" ".repeat(dashLength / 2) + title + " ".repeat(dashLength / 2));
        System.out.println("------------------------------------");
    }
    public static void clearScreen() {
        System.out.println("\n".repeat(50));
    }

    public static String getInput() {
        System.out.print("> ");
        return scanner.nextLine();
    }

    static Integer getMenuInput(Integer validOptions) {
        Integer input = 0;
        while (input == 0) {
            try {
                System.out.print("> ");
                char firstint = scanner.next().charAt(0);
                scanner.nextLine();
                input = Character.getNumericValue(firstint);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
            }
            if (input == 0 || input > validOptions) {
                System.out.println("Invalid input. Please enter a number.");
                input = 0;
            }
        }
        return input;
    }


}