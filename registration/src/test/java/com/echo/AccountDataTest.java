package com.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Scanner;

public class AccountDataTest {
    Scanner mockScanner = Mockito.mock(Scanner.class);
    Main mainObject = new Main(mockScanner);
    private Session session;
    AccountDatabase accountDB;
    @BeforeEach 
    public void setup() throws IOException {
        try {
            accountDB = new AccountDatabase(mainObject, "test_only_data.csv");
        } catch (IOException e) {
            System.err.println("Error reading file");
        }
        /* for (Account account : accountDB.accounts.values()) {
            System.out.println(account);
        } */
        SessionManager sessionManager = new SessionManager(accountDB);
        try {
            session = sessionManager.login("goodadmin", "test", Role.ADMIN); // Use valid credentials
        } catch (InvalidCredentialsException e) {
            fail("Unexpected InvalidCredentialsException");
            System.err.println();
        }
    }

    @Test
    public void testLoadData() {
        Account account = accountDB.getsAccount(1111);
        if (account == null) {
            throw new AssertionFailedError("Account 1111 not found\n");
        }
        assertTrue(account.getRole() == Role.STUDENT);
        assertTrue(account.getLoginName().equals("goodstudent"));
        assertTrue(account.getStatus().equals(AccountStatus.ACTIVE));
        assertTrue(account.getUserID() == 1111);
        try {
            // This still needs work
            StudentAccount studentaccount = account.getStudentAccount(session);
            // System.out.println(studentaccount.getDob(session));
            assertTrue(studentaccount.getDob(session).equals("1/11/11111"));
            // System.out.println(studentaccount.getGender(session));
            assertTrue(studentaccount.getGender(session) == Gender.FEMALE);
            // System.out.println(studentaccount.getAcademicHistory(session));
            assertTrue(studentaccount.getAcademicHistory(session).equals("Graduated 2019"));
            // System.out.println(studentaccount.getPhoneNumber(session));
            assertTrue(studentaccount.getPhoneNumber(session).equals("617-111-1111"));
        } catch (NotStudentException e) {
            System.err.println("Not a student account");
        } catch (ExpiredSessionException e) {
            System.err.println("Session expired");
        } catch (AccessViolationException e) {
            System.err.println("Access violation :(");
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

    @Test
    public void testDataInput() {
        Scanner mockScanner = Mockito.mock(Scanner.class);
        Mockito.when(mockScanner.nextLine())
               .thenReturn("02/03/1234");
        Mockito.when(mockScanner.next())
               .thenReturn("1");
        Mockito.when(mockScanner.nextLine())
               .thenReturn("617-222-2212");
        StudentData studentData = accountDB.studentDataInput(mockScanner);
        assertNotNull(studentData);
    }
}
