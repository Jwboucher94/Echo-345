package com.echo;

public class AdminMenu {
    public static boolean displayAdminMenu(AccountDatabase accountDB, Session session, Boolean logout) {
        System.out.println("Admin Menu");
        System.out.println("1. Create Account");
        System.out.println("2. Manage Student Account Access");
        System.out.println("3. View My Account");
        System.out.println("4. Save and Logout");
        System.out.println("5. Exit without saving");
        String loginName;
        Integer input = Main.getMenuInput(5);
        switch (input) {
            case 1:
                Main.clearScreen("Create New Student Account");
                System.out.println("Enter login name:");
                loginName = Main.getInput();
                System.out.println("Enter password:");
                String password = Main.getInput();
                try {
                    StudentAccount studentAccount = accountDB.createAccount(session, loginName, password);
                    System.out.println("Account created successfully. User ID: " + studentAccount.getUserID(session));
                    session.setHasModified();
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
            case 2:
                Main.clearScreen("Student Account Management");
                System.out.println("Enter the Login Name of the student account you want to manage:");
                loginName = Main.getInput();
                while (!AccountDatabase.changeLoginName(session, loginName)) {
                    loginName = Main.getInput();
                }
                break;
            case 3:
                try {
                    Main.clearScreen();
                    accountDB.viewAccount(session);
                } catch (AccessViolationException e) {
                    System.err.println("Access violation: " + e.getMessage());
                } catch (ExpiredSessionException e) {
                    System.err.println("Session expired: " + e.getMessage());
                }
                break;
            case 4:
                System.out.println("Logging out...");
                return true;
            case 5:
                System.out.println("Exiting without saving...");
                System.exit(0);
                break;
        }
        return logout;
    }
}
