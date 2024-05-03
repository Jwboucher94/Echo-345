package com.echo;

public class AdminMenu {
    public static boolean displayAdminMenu(Main main, AccountDatabase accountDB, Session session, Boolean logout) throws AccessViolationException, ExpiredSessionException {
        Integer input;
        while (!logout) {
            main.clearScreen("Admin Menu");
            System.out.println("1. Create Account");
            System.out.println("2. Manage Student Account Access");
            System.out.println("3. View My Account");
            System.out.println("4. Run testing");
            System.out.println("5. Save and Logout");
            System.out.println("6. Exit without saving");
            input = main.getMenuInput(6);
            switch (input) { // Main Admin Menu Switch
                case 1: //  Create Student
                    main.clearScreen("Create New Student Account");
                    System.out.println("Enter login name:");
                    String loginName = main.getInput();
                    System.out.println("Enter password:");
                    String password = main.getInput();
                    try {
                        StudentAccount studentAccount = accountDB.createAccount(session, loginName, password);
                        System.out.println("Account created successfully. User ID: " + studentAccount.getUserID(session));
                        session.setHasModified();
                        loginName = null;
                    } catch (AccessViolationException e) {
                        System.err.println("Access Violation: " + e.getMessage());
                        return true;
                    } catch (ExpiredSessionException e) {
                        System.err.println("Session Expired: " + e.getMessage());
                        return true;
                    } catch (DuplicateRecordException e) {
                        System.err.println("Duplicate Record: " + e.getMessage());
                        return true;
                    }
                    break;
                case 2: // Manage Students
                    main.clearScreen("Student Account Management");
                    System.out.println("Enter the Login Name of the student account you want to manage:");
                    String studentloginName = main.getInput();
                    try {
                        displayStudentMGMTMenu(main, accountDB, session, studentloginName);
                        break;
                    } catch (AccountNotFoundException e) {
                        System.err.println("Account not found: " + e.getMessage());
                        System.out.println("Try again? (y/n)");
                        String choice = main.getInput();
                        if (!choice.startsWith("y")) {
                            break;
                        }
                    }
                case 3: // View My Account
                    try {
                        Main.clearScreen();
                        accountDB.viewAccount(session);
                    } catch (AccessViolationException e) {
                        System.err.println("Access violation: " + e.getMessage());
                        return true;
                    } catch (ExpiredSessionException e) {
                        System.err.println("Session expired: " + e.getMessage());
                        return true;
                    }
                    break;
                case 4:
                    System.out.println("Running tests...");
                    ManualTest.manualTest();
                    System.out.println("Press Enter to continue");
                    main.getInput();
                    break;
                case 5: //  Return to Main
                    System.out.println("Logging out...");
                    logout = true;
                    break;
                case 6: // Exit W/o Saving
                    System.out.println("Exiting without saving...");
                    System.exit(0);
            }
        }
        return logout;
    }

    public static Boolean displayStudentMGMTMenu(Main main, AccountDatabase accountDB, Session session, String studentLoginName) throws AccessViolationException, ExpiredSessionException, AccountNotFoundException {
        Boolean thisStudent = true;
        // Find the Account
        for (Account account : accountDB.accountDB.values()) {
            while (account.getLoginName().equals(studentLoginName) && account.role == Role.STUDENT && thisStudent) {
                main.clearScreen("Student Account Management");
                System.out.println("Chosen student: " + studentLoginName);
                System.out.println("1. View Account");
                System.out.println("2. Enable Account");
                System.out.println("3. Disable Account");
                System.out.println("4. Back");
                System.out.println("5. Exit without saving");
                Integer input = main.getMenuInput(5);
                switch (input) { // Main Student Management Switch
                    case 1: //    View Account
                        Main.clearScreen();
                        accountDB.viewAccount(session, studentLoginName);
                        System.out.println("Press enter to continue...");
                        main.getInput();
                        break;
                    case 2: //  Enable Account
                        System.err.println("Are you sure you want to enable this account? (y/n)");
                        if (main.getInput().startsWith("y")) {
                            accountDB.unblock(session, account.userID);
                            System.out.println("Account enabled.");
                            session.setHasModified();
                            System.out.println("Press enter to continue...");
                            main.getInput();
                            break;
                        }
                        System.out.println("Account not enabled.");
                        break;
                    case 3: // Disable Account
                        System.err.println("Are you sure you want to disable this account? (y/n)");
                        if (main.getInput().startsWith("y")) {
                            accountDB.block(session, account.userID);
                            System.out.println("Account disabled.");
                            session.setHasModified();
                            System.out.println("Press enter to continue...");
                            main.getInput();
                            break;
                        }
                        System.out.println("Account not disabled.");
                        break;
                    case 4: // Return to Admin
                        thisStudent = false;
                        return false;
                    case 5: // Exit W/o Saving
                        System.out.println("Exiting without saving...");
                        System.exit(0);
                }    
            }
        }
        return false;
    }
}
