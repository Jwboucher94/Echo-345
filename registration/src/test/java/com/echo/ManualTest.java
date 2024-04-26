package com.echo;

import java.io.IOException;

class ManualTest {
    static Session session;
    static AccountDatabase accountDatabase;
    static SessionManager sessionManager;
    static Boolean quit = false;
    
    static void main(String[] args) {
        try {
            System.err.println("Starting Manual Test");
            init();
            while (!quit) {
                System.out.println("Welcome. Please choose a test category to run:");
                for (TestList test : TestList.values()) {
                    System.out.println(test.ordinal() + ": " + test.name());
                }
                System.out.println(TestList.values().length + ": Quit");
                Integer choice = Integer.parseInt(System.console().readLine());
                if (choice == TestList.values().length) {
                    quit = true;
                } else if (choice < 0 || choice >= TestList.values().length) {
                    System.err.println("Invalid choice. Please try again.");
                } 
                Boolean testResult = testPicker(choice);
                if (!testResult) {
                    System.out.println("Test failed. Send q to quit or any other key to continue.");
                    String input = System.console().readLine();
                    if (input.toLowerCase().startsWith("q")) {
                        quit = true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + ". Quitting.");
            System.exit(1);
        } finally {
            cleanup();
            System.exit(0);
        }       
    }

    static Boolean testPicker(Integer choice) {
        Boolean result = false;
        Integer testChoice = null;
        while (!quit && (testChoice == null)) {
            switch (TestList.values()[choice]) {
                case Accounts:
                    System.out.println("Choose an Account function to test:");
                    for (AccountTestList test : AccountTestList.values()) {
                        System.out.println(test.ordinal() + ": " + test.name());
                    }
                    testChoice = Integer.parseInt(System.console().readLine());
                    
                    result = testAccounts(testChoice);
                    testChoice = null;
                    break;
                case Sessions:
                    result = testSessions(testChoice);
                    testChoice = null;
                    break;
                case Menus:
                    result = testMenus(testChoice);
                    testChoice = null;
                    break;
                case Roles:
                    result = testRoles(testChoice);
                    testChoice = null;
                    break;
                case Exit:
                    System.exit(0);
            }
        }
        return result;
    }

    static Boolean testAccounts(Integer testChoice) {
        Boolean result = false;
        // TODO:
        return result;
    }

    static Boolean testSessions(Integer testChoice) {
        Boolean result = false;
        // TODO:
        return result;
    }

    static Boolean testMenus(Integer testChoice) {
        Boolean result = false;
        // TODO:
        return result;
    }

    static Boolean testRoles(Integer testChoice) {
        Boolean result = false;
        // TODO:
        return result;
    }

    // TODO: Cleanup the test
    static void cleanup() {
        System.err.println("Cleaning up");
        session = null;
        accountDatabase = null;
        sessionManager = null;
    }

    static void init() throws IOException {
        AccountDatabase accountDatabase = new AccountDatabase("test_only_data.csv");
        SessionManager sessionManager = new SessionManager(accountDatabase);
        Account testAccount = new Account(1, "goodadmin", "test", Role.ADMIN, AccountStatus.ACTIVE);
        session = new Session("15", Role.ADMIN, testAccount, sessionManager, SessionManager.timeInteger);
    }

    enum TestList {
        Accounts,
        Sessions,
        Menus,
        Roles,
        Exit
    }
    
    enum AccountTestList {
        CreateStudent,
        Update,
        Delete,
        Exit
    }
}
