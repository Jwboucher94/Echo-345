package com.echo;

import com.echo.AccountDatabase.DuplicateRecordException;

public class AdminMenu {
    public static boolean displayAdminMenu(Session session, Boolean logout) {
        System.out.println("Admin Menu");
        System.out.println("1. Create Account");
        System.out.println("2. Delete Account");
        System.out.println("3. View My Account");
        System.out.println("4. Save and Logout");
        System.out.println("5. Exit without saving");

        Integer input = Main.getMenuInput(5);
        switch (input) {
            case 1:
                System.out.println("Create New Student Account");
                System.out.println("Enter login name:");
                String loginName = Main.getInput();
                System.out.println("Enter password:");
                String password = Main.getInput();
                try {
                    StudentAccount studentAccount = session.getSessionManager().getAccountDatabase().createAccount(session, loginName, password);
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
                System.out.println("Delete Account");
                break;
            case 3:
                try {
                    Main.clearScreen();
                    session.getSessionManager().getAccountDatabase().viewAccount(session);
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
