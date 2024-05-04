package com.echo;

import java.io.IOException;

public class ManualTest {
    static Main mainObject = new Main();
    static Session session;
    static AccountDatabase accountTestDB;
    static Account testAccount;
    static SessionManager sessionManager;
    static Boolean success = false;
    
    static void manualTest() {
        try {
            System.err.println("Starting Manual Test");
            init();
            while (!success) {
                Main.clearScreen();
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
        Boolean result = true;
        switch (AccountTestList.values()[testchoice]) {
            case CreateStudent:
                try {
                    accountTestDB = new AccountDatabase(mainObject, "test_only_data.csv", true);
                } catch (IOException e) {
                    System.err.println("Error recreating AccountDatabase. " + e.getMessage());
                    return false;
                }
                StudentData studentData = new StudentData("01/02/1001", Gender.MALE, "", "123-123-1123");
                try {
                    StudentAccount studentAccount = accountTestDB.createAccount(session, "tester", "tester", studentData);
                    System.out.println("Account created successfully. User ID: " + studentAccount.getUserID(session));
                    session.setHasModified();
                } catch (AccessViolationException e) {
                    System.err.println("Access Violation: " + e.getMessage() + "\n");
                    return false;
                } catch (ExpiredSessionException e) {
                    System.err.println("Session Expired: " + e.getMessage() + "\n");
                    return false;
                } catch (DuplicateRecordException e) {
                    System.err.println("Duplicate Record: " + e.getMessage() + "\n");
                    return false;
                }
                try {
                    accountTestDB.createAccount(session, "tester", "tester", studentData);
                    System.err.println("Error: Duplicate Record created." + "\n");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException | DuplicateRecordException e) {
                    // Expected
                }
                System.out.println();
                return true;
            case AccountDatabaseConstructor:
                System.out.println("This will test the AccountDatabase constructor, as well as the loadAccount method.");
                try {
                    System.out.println("Testing working CSV file");
                    new AccountDatabase(mainObject, "test_only_data.csv", true);
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                    return false;
                }
                try {
                    System.out.println("Testing invalid CSV file");
                    new AccountDatabase(mainObject, "null/invalid CSV.csv");
                    System.err.println("Error: IOException was not caught.\n");
                    return false;
                } catch (IOException e) {
                    // Expected
                }
                try {
                    System.out.println("Testing broken template CSV file");
                    new AccountDatabase(mainObject, "test_broken_template.csv");
                    System.err.println("Error: IOException was not caught.\n");
                    return false;
                } catch (IOException e) {
                    // Expected
                }
                System.out.println();
                return true;
            case viewAccountTest:
                try {
                    System.out.println("\nTesting current test session's account name:");
                    accountTestDB.viewAccount(session);
                    System.out.println("Testing valid secondary account view");
                    accountTestDB.viewAccount(session, "goodstudent");
                } catch (AccessViolationException | ExpiredSessionException e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                    return false;
                }
                try {
                    System.out.println("Testing invalid account name");
                    String testAccountName = "testers";
                    accountTestDB.viewAccount(session, testAccountName);
                    System.err.println("Error: Exception was not caught.\n");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException e) {
                    // Expected
                }
                try {
                    // test student account with missing data
                    System.out.println("Testing student account with missing data");
                    accountTestDB.viewAccount(session, "brokenstudent");
                } catch (AccessViolationException | ExpiredSessionException e) {
                    // Expected
                }
                try {
                    System.out.println("Testing nonexistent login name");
                    accountTestDB.viewAccount(session, null);
                    // very unexpected to finish
                    System.out.println("loginName is null, but view passed?");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException e) {
                    // Expected
                }
                try {
                    System.out.println("Testing with nonexistent session");
                    accountTestDB.viewAccount(null);
                    // very unexpected to finish
                    System.out.println("Session is null, but test passed?");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException e) {
                    // Expected
                }
                System.out.println();
                return true;
            case Delete:
                break;
            case Exit:
                break;
            case Update:
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
        accountTestDB = null;
        sessionManager = null;
    }

    static void init() throws IOException {
        System.out.println("Errors are expected! Please ignore them.");
        accountTestDB = new AccountDatabase(mainObject, "test_only_data.csv", true);
        SessionManager sessionManager = new SessionManager(accountTestDB);
        testAccount = new Account(1, "goodadmin", "test", Role.ADMIN, AccountStatus.ACTIVE);
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
        AccountDatabaseConstructor,
        viewAccountTest,
        Update,
        Delete,
        Exit
    }
}
