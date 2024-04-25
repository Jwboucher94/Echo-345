package com.echo;

public class StudentMenu {

    public static Boolean displayStudentMenu(Session session, Boolean logout) {
        System.out.println("Student Menu");
        System.out.println("1. View My Account");
        System.out.println("2. Update My Account");
        System.out.println("3. Save and Logout");
        System.out.println("4. Exit without saving");

        Integer input = Main.getMenuInput(4);
        switch (input) {
            case 1:
                try {
                    Main.clearScreen();
                    session.getSessionManager().getAccountDatabase().viewAccount(session);
                } catch (AccessViolationException e) {
                    System.err.println("Access violation: " + e.getMessage());
                } catch (ExpiredSessionException e) {
                    System.err.println("Session expired: " + e.getMessage());
                }
                break;
            case 2:
                Boolean success = updatePrompt(session);
                if (success) {
                    System.out.println("Account updated successfully.");
                    session.setHasModified();
                }
                break;
            case 3:
                System.out.println("Logging out...");
                return true;
            case 4:
                System.out.println("Exiting without saving...");
                System.exit(0);
                break;
        }
        return logout;
    }

    private static Boolean updatePrompt(Session session) {
        AccountDatabase accountDB = session.getSessionManager().getAccountDatabase();
        StudentAccount account = (StudentAccount) session.studentAccount;
        Main.clearScreen("Update My Account");
        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Gender");
        System.out.println("2. Password");
        System.out.println("3. Phone Number");

        Integer input = Main.getMenuInput(3);
        switch (input) {
            case 1:
                // choosing to edit the student's Gender
                Main.clearScreen("Chose your preferred gender:\n");
                Integer counter = 1;
                for (Gender gender : Gender.values()) {
                    System.out.println(counter + ". " + gender);
                    counter++;
                }
                input = Main.getMenuInput(counter - 1);
                Gender gender = null;
                while (gender == null) {
                    try {
                        gender = Gender.values()[input - 1];
                        try {
                            account.setGender(session, gender);
                        } catch (ExpiredSessionException | AccessViolationException e) {
                            System.out.println("Failed due to Session issue: " + e.getMessage());
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Invalid input. Please enter a number.");
                    }
                }                
                session.setHasModified();
                return true;
            case 2:
                Main.clearScreen("Enter new password:");
                String password = Main.getInput();
                accountDB.changePassword(session, password);
                return true;
            case 3:
                accountDB.setPhoneNumber(session);
                session.setHasModified();
                return true;
        }
        return false;
    }

}