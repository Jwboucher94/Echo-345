package com.echo;

import java.io.IOException;

class ManualTest {
    static Session session;
    static AccountDatabase accountDatabase;
    static SessionManager sessionManager;
    
    static void main(String[] args) {
        // TODO:
        try {
            System.err.println("Starting Manual Test");
            init();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + ". Quitting.");
            System.exit(1);
        }
        // TODO: Create a test account for the test

        // TODO: Establish the initial session for the test
        
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
}
