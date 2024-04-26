package com.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class AccountDatabaseTest {
    private Session session;
    AccountDatabase accountDB;
    @BeforeEach 
    public void setup() throws IOException {
        try {
            accountDB = new AccountDatabase("test_only_data.csv");
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
    public void testCheckCredentials_ValidCredentials() {
        //System.out.println("\nRunning test: testCheck - Valid");
        assertTrue(accountDB.checkCredentials(1111, "goodstudent", "test", Role.STUDENT));
    }

    
    @Test
    public void testCheckCredentials_InvalidCredentials() {
        System.out.println("\nRunning test: testCheck - Invalid");
        assertFalse(accountDB.checkCredentials(123456789, "invalidUser", "badPassword", Role.STUDENT));
    }

    
    @Test
    public void testCheckCredentials_BlockedAccount() {
        System.out.println("\nRunning test: testCheck - Blocked");
        assertFalse(accountDB.checkCredentials(1122, "badstudent", "test", Role.STUDENT));
    }


    @Test
    public void testBlockAccount() throws IOException, AccessViolationException, ExpiredSessionException {
        System.out.println("\nRunning test: testBlockAccount()");
        Integer userIdToblock = 2211; 
        try {
            // System.out.println("Blocking account");
            accountDB.block(session, userIdToblock);
            // System.out.println("Reloading accounts");
            accountDB.reloadAccounts(); 
            assertFalse(accountDB.checkCredentials(userIdToblock, "goodadvisor", "test", Role.ADVISOR)); 
        } catch(AccountNotFoundException e) {
            fail("Unexpected AccountNotFoundException"); 
        } 
    }

    @Test
    public void testUnBlockAccount() {
        System.out.println("\nRunning test: testUnBlockAccount()");
        Integer userIdToUnblock = 2222; 
        try {
            accountDB.unblock(session, userIdToUnblock);
            assertTrue(accountDB.checkCredentials(2222, "badadvisor", "test", Role.ADVISOR));
        } catch(AccountNotFoundException e) {
            fail("Account not found"); 
        } catch(AccessViolationException e) {
            fail("Invalid permissions"); 
        } catch(ExpiredSessionException e) {
            fail("Session has expired"); 
        }
    }

    @Test
    public void testStudentAccountDetails() throws AccessViolationException, ExpiredSessionException, NotStudentException {
        System.out.println("\nRunning test: testStudentAccountDetails()");
        try {
            StudentAccount studentAccount = accountDB.getStudentAccount(session, 1122);
            assertEquals("1/11/11122", studentAccount.getDob(session));
        } catch(AccessViolationException e) {
            fail("Unexpected AccessViolationException"); 
        } catch(ExpiredSessionException e) {
            fail("Unexpected ExpiredSessionException"); 
        }
    }
}
