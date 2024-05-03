package com.echo;

import java.io.IOException;

public class ManualTest {
    static Main mainObject = new Main();
    static Session session;
    static AccountDatabase accountDB;
    static SessionManager sessionManager;
    static Boolean success = false;
    
    static void manualTest() {
        try {
            System.err.println("Starting Manual Test");
            init();
            while (!success) {
                System.out.println("Please choose a test category to run:");
                int length = TestList.values().length;
                for (TestList test : TestList.values()) {
                    System.out.println((test.ordinal() + 1) + ": " + test.name());
                }
                Integer choice = Integer.parseInt(System.console().readLine());
                if (choice == length) {
                    System.exit(0);
                } else if (choice-1 == 0 || choice <= length) {
                    Integer testResult = testPicker(choice-1);
                    if (testResult == -1) {
                        System.out.println("Test failed. Send q to quit or any other key to continue.");
                    }
                    else if (testResult == 1) {
                        System.out.println("Test passed. Send q to quit or any other key to continue.");
                    }
                    else {
                        System.out.println("Send q to quit or any other key to continue.");
                    }
                    String input = System.console().readLine();
                    if (input.toLowerCase().startsWith("q")) {
                        System.exit(0);
                    }
                    System.err.println("Invalid choice. Please try again.");
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

    static Integer testPicker(Integer choice) {
        Boolean result = null;
        Integer testChoice = null;
        while (testChoice == null) {
            switch (TestList.values()[choice]) {
                case Accounts:
                    System.out.println("Choose an Account function to test:");
                    for (AccountTestList test : AccountTestList.values()) {
                        System.out.println((test.ordinal()+1) + ": " + test.name());
                    }
                    testChoice = (Integer.parseInt(System.console().readLine())-1);
                    result = testAccounts(testChoice);
                    testChoice = 1;
                    break;
                case Sessions:
                    result = testSessions(testChoice);
                    testChoice = 1;
                    break;
                case Menus:
                    result = testMenus(testChoice);
                    testChoice = 1;
                    break;
                case Roles:
                    result = testRoles(testChoice);
                    testChoice = 1;
                    break;
                case Back:
                    return -1;
                case Exit:
                    System.exit(0);
                default:
                    System.err.println("Invalid choice. Please try again.");
                    break;
            }
        }
        if (result == true) {
            return 1;
        }
        else if (result == false) {
            return -1;
        }
        return 0;
    }

    static Boolean testAccounts(Integer testchoice) {
        Boolean result = false;
        switch (AccountTestList.values()[testchoice]) {
            case CreateStudent:
                try {
                    StudentAccount studentAccount = accountDB.createAccount(session, "tester", "tester");
                    System.out.println("Account created successfully. User ID: " + studentAccount.getUserID(session));
                    session.setHasModified();

                    result = true;
                } catch (AccessViolationException e) {
                    System.err.println("Access Violation: " + e.getMessage());
                    return false;
                } catch (ExpiredSessionException e) {
                    System.err.println("Session Expired: " + e.getMessage());
                    return false;
                } catch (DuplicateRecordException e) {
                    System.err.println("Duplicate Record: " + e.getMessage());
                    return false;
                }
                break;
            case Update:
                // TODO
                break;
            case Delete:
                break;
            case Exit:
                break;
        }
        
        // TODO:
        return result;
    }

    static Boolean testSessions(Integer testChoice) {
        Boolean result = true;
        // TODO:
        System.out.println("test sessions");
        return result;
    }

    static Boolean testMenus(Integer testChoice) {
        Boolean result = false;
        // TODO:
        System.out.println("test menus");
        return result;
    }

    static Boolean testRoles(Integer testChoice) {
        Boolean result = false;
        System.out.println("test roles");
        // TODO:
        return result;
    }

    // TODO: Cleanup the test
    static void cleanup() {
        System.err.println("Cleaning up");
        session = null;
        accountDB = null;
        sessionManager = null;
    }

    static void init() throws IOException {
        AccountDatabase accountDatabase = new AccountDatabase(mainObject, "test_only_data.csv");
        SessionManager sessionManager = new SessionManager(accountDatabase);
        Account testAccount = new Account(1, "goodadmin", "test", Role.ADMIN, AccountStatus.ACTIVE);
        session = new Session("15", Role.ADMIN, testAccount, sessionManager, SessionManager.timeInteger);
    }

    enum TestList {
        Accounts,
        Sessions,
        Menus,
        Roles,
        Back,
        Exit
    }
    
    enum AccountTestList {
        CreateStudent,
        Update,
        Delete,
        Exit
    }
}
