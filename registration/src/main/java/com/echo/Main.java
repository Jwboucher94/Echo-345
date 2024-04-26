package com.echo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    Scanner scanner;

    public Main() {
        this.scanner = new Scanner(System.in);
    }
    public Main(Scanner scanner) {
        this.scanner = scanner;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Main mainObject = new Main(scanner);
        AccountDatabase accountDB;
        try {
            accountDB = mainObject.loadDatabase();
            mainObject.displayLoginMenu(accountDB);
        } catch (FileNotFoundException e) {
            System.err.println("Error loading database: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }

    void displayLoginMenu(AccountDatabase accountDB) {
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
            Integer input = this.getMenuInput(counter - 1);
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
                        logout = AdminMenu.displayAdminMenu(this, accountDB, session, logout);
                    } else if (loginRole == Role.STUDENT) {
                        logout = StudentMenu.displayStudentMenu(this, accountDB, session, logout);
                    } else if (loginRole == Role.ADVISOR) {
                        logout = AdvisorMenu.displayAdvisorMenu(this, accountDB, session, logout);
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

    Session getSession (SessionManager sessionManager, Role role) {
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

    AccountDatabase loadDatabase() throws FileNotFoundException, IOException {
        String csvFile = "MOCK_DATA.csv"; // "MOCK_DATA.csv" is the file name
        try {
            AccountDatabase accountDB = new AccountDatabase(this, csvFile);
            return accountDB;
        } catch (IOException e) {
            throw new IOException("Error loading account database: " + e.getMessage());
        }
    }

    public void clearScreen(String title) {
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

    public String getInput() {
        return getInput(this.scanner);
    }
    public String getInput(Scanner scanner) {
        System.out.print("> ");
        return scanner.nextLine();
    }

    Integer getMenuInput(Integer validOptions) {
        return getMenuInput(this.scanner, validOptions);
    }
    Integer getMenuInput(Scanner scanner, Integer validOptions) {
        Integer input = 0;
        while (input == 0) {
            try {
                System.out.print("> ");
                String firstint = scanner.next();
                scanner.nextLine();
                input = Integer.valueOf(firstint);
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