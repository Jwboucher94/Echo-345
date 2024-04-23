package com.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;


public class AccountDataTest {
    private AccountDatabase accountDB;
    private Session session;

    @BeforeEach 
    public void setup() throws IOException {
        String csvFile = getClass().getClassLoader().getResource("MOCK_DATA.csv").getFile(); 
        System.out.println("CSV File: " + csvFile);
        if (csvFile == null) {
            throw new FileNotFoundException("MOCK_DATA.csv not found in test resources");
        }
        accountDB = new AccountDatabase(csvFile);
        accountDB.loadAccounts(csvFile);
        System.out.println("Accounts loaded");
        SessionManager sessionManager = new SessionManager(accountDB);
        try {
            session = sessionManager.login("admin", "password", Role.ADMIN); // Use valid credentials
        } catch (SessionManager.InvalidCredentialsException e) {
            fail("Unexpected InvalidCredentialsException");
        }
        System.out.println("Session created");
        System.out.println("Session Valid? " + Session.validateSession(session));
    }

    @Test
    public void testLoadData() {
        Account account = accountDB.getsAccount(552281454);
        if (account == null) {
            throw new AssertionFailedError("Account not found\n");
        }
        System.out.println(account);
        System.out.println(account.getRole());
        assertTrue(account.getRole() == Role.STUDENT);
        System.out.println(account.getLoginName());
        assertTrue(account.getLoginName().equals("acicutto1"));
        System.out.println(account.getStatus());
        assertTrue(account.getStatus().equals("active"));
        System.out.println(account.getUserID());
        assertTrue(account.getUserID() == 552281454);
        try {
            // This still needs work
            StudentAccount studentaccount = account.getStudentAccount(session);
            System.out.println(studentaccount.getDob(session));
            assertTrue(studentaccount.getDob(session).equals("1/10/1002"));
            System.out.println(studentaccount.getGender(session));
            assertTrue(studentaccount.getGender(session) == Gender.MALE);
            System.out.println(studentaccount.getAcademicHistory(session));
            assertTrue(studentaccount.getAcademicHistory(session).equals("Graduated 2019"));
            System.out.println(studentaccount.getPhoneNumber(session));
            assertTrue(studentaccount.getPhoneNumber(session).equals("617-111-2039"));
        } catch (NotStudentException e) {
            System.out.println("Not a student account");
        } catch (ExpiredSessionException e) {
            System.out.println("Session expired");
        } catch (AccessViolationException e) {
            System.out.println("Access violation :(");
        }

        assertNotNull(account);
    }

    @Test
    public void testViewAccount() {
        System.out.println("\ntestViewAccount");
        try {
            accountDB.viewAccount(session);
        } catch (AccessViolationException | ExpiredSessionException e) {
            fail("Unexpected exception: "+ e.getMessage());
        }
    }
}
