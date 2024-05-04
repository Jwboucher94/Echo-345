package com.echo;

import java.io.IOException;

public class ManualTest {
    static Main mainObject = new Main();
    static Session testSession;
    static AccountDatabase accountTestDB;
    static Account testAccount;
    static SessionManager sessionTestManager;
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
            // Tests on Student creation
                try {
                    accountTestDB = new AccountDatabase(mainObject, "test_only_data.csv", true);
                } catch (IOException e) {
                    System.err.println("Error recreating AccountDatabase. " + e.getMessage());
                    return false;
                }
                StudentData studentData = new StudentData("01/02/1001", Gender.MALE, "", "123-123-1123");
                try {
                    StudentAccount studentAccount = accountTestDB.createAccount(testSession, "tester", "tester", studentData);
                    System.out.println("Account created successfully. User ID: " + studentAccount.getUserID(testSession));
                    testSession.setHasModified();
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
                    accountTestDB.createAccount(testSession, "tester", "tester", studentData);
                    System.err.println("Error: Duplicate Record created." + "\n");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException | DuplicateRecordException e) {
                    // Expected
                }
                try {
                    // Non-admin creation test
                    Account account = new Account(1, "tester", "tester", Role.STUDENT, AccountStatus.ACTIVE);
                    Session badsession = new Session("id", Role.STUDENT, account, sessionTestManager, 99999);
                    accountTestDB.createAccount(badsession, "shouldnt", "work", studentData);
                    System.err.println("Error: Account created without admin permissions.");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException | DuplicateRecordException e) {
                    // Expected
                }
                System.out.println();
                return true;

            case AccountDatabaseConstructor:
            // Tests on the Account Database Constructor
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

            case ViewAccountTest:
            // Tests on the viewAccount method
                try {
                    System.out.println("\nTesting current test session's account name:");
                    accountTestDB.viewAccount(testSession);
                    System.out.println("Testing valid secondary account view");
                    accountTestDB.viewAccount(testSession, "goodstudent");
                } catch (AccessViolationException | ExpiredSessionException e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                    return false;
                }
                try {
                    System.out.println("Testing invalid account name");
                    String testAccountName = "testers";
                    accountTestDB.viewAccount(testSession, testAccountName);
                    System.err.println("Error: Exception was not caught.\n");
                    return false;
                } catch (AccessViolationException | ExpiredSessionException e) {
                    // Expected
                }
                try {
                    // test student account with missing data
                    System.out.println("Testing student account with missing data");
                    accountTestDB.viewAccount(testSession, "brokenstudent");
                } catch (AccessViolationException | ExpiredSessionException e) {
                    // Expected
                }
                try {
                    System.out.println("Testing nonexistent login name");
                    accountTestDB.viewAccount(testSession, null);
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

            case passwordCheck:
            // Tests on the passwordCheck method
                System.out.println("Checking if the Instructions print without issue.\n-----");
                accountTestDB.passwordCheckInstruction();
                Account passwordChecktestAccount = new Account(123, "goodadmin", "!m123Password", Role.ADMIN, AccountStatus.ACTIVE);
                Session passwordCheckTestSession = new Session("213", Role.ADMIN, passwordChecktestAccount, sessionTestManager, SessionManager.timeInteger);
                try {
                    System.out.println("-----\nTesting invalid password");
                    accountTestDB.passwordCheck(passwordCheckTestSession, "badpassword");
                    System.err.println("Error: Exception was not caught.\n");
                    return false;
                } catch (IOException e) {
                    // Expected
                }
                try {
                    System.out.println("Testing valid, but original, password");
                    accountTestDB.passwordCheck(passwordCheckTestSession, "!m123Password");
                    System.err.println("Error: Exception was not caught.\n");
                } catch (IOException e) {
                    // Expected
                }
                try {
                    System.out.println("Testing valid new password");
                    accountTestDB.passwordCheck(passwordCheckTestSession, "1234!123Pass");
                } catch (IOException e) {
                    return false;
                }
                System.out.println();
                return true;
            // NOTE: Do we need more cases for Account testing?
            
            case Exit:
                break;
        }
        return result;
    }
    // TODO: Following test menus are TBD.
    static Boolean testSessions(Integer testChoice) {
        Boolean result = true;
        // TODO: Sessions testing
        System.out.println("test sessions not implemented");
        return result;
    }

    static Boolean testMenus(Integer testChoice) {
        Boolean result = false;
        // TODO: Menu testing
        System.out.println("test menus not implemented");
        return result;
    }

    static Boolean testRoles(Integer testChoice) {
        Boolean result = false;
        System.out.println("test roles not implemented");
        // TODO: Role tests
        return result;
    }

    // Cleanup the test
    // NOTE: do we need this? probably not.
    static void cleanup() {
        System.err.println("Cleaning up");
        testSession = null;
        accountTestDB = null;
        sessionTestManager = null;
    }

    static void init() throws IOException {
        System.out.println("Errors are expected! Please ignore them.");
        accountTestDB = new AccountDatabase(mainObject, "test_only_data.csv", true);
        sessionTestManager = new SessionManager(accountTestDB);
        testAccount = new Account(1, "goodadmin", "test", Role.ADMIN, AccountStatus.ACTIVE);
        testSession = new Session("15", Role.ADMIN, testAccount, sessionTestManager, SessionManager.timeInteger);
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
        ViewAccountTest,
        passwordCheck,
        Exit
    }
}
