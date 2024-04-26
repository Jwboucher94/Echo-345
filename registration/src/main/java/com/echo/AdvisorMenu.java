package com.echo;

import java.util.Map;

public class AdvisorMenu {

    public static Boolean displayAdvisorMenu(Main main, AccountDatabase accountDB, Session session, Boolean logout) {
        main.clearScreen("Welcome to the Advisor Menu");
        System.out.println("1. View all students");
        System.out.println("2. View all courses");
        System.out.println("3. View all courses for a student");
        System.out.println("4. Logout");
        System.out.println("5. Exit");
        Integer input = main.getMenuInput(4);

        switch (input) {
            case 1:
                viewAllStudents(accountDB.accountDB);
                break;
            case 2:
                viewAllCourses(accountDB.accountDB);
                break;
            case 3:
                viewAllCoursesForStudent(accountDB.accountDB);
                break;
            case 4:
                logout = true;
                break;
            case 5:
                System.exit(0);
                break;
            default:
                break;
        }
    return logout;
    }

    // TODO: Implement viewAllStudents for the Advisor Menu
    private static void viewAllStudents(Map<Integer, Account> accountDB) {
        System.err.println("Unimplemented method 'viewAllStudents'");
    }

    // TODO: Implement viewAllCourses for the Advisor Menu
    private static void viewAllCourses(Map<Integer, Account> accountDB) {
        System.err.println("Unimplemented method 'viewAllCourses'");
    }

    // TODO: Implement viewAllCoursesForStudent for the Advisor Menu
    private static void viewAllCoursesForStudent(Map<Integer, Account> accountDB) {
        // TODO Auto-generated method stub
        System.err.println("Unimplemented method 'viewAllCoursesForStudent'");
    }



}
