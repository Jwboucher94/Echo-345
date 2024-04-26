package com.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class AccountDatabaseTest {

    private AccountDatabase accountDB;
    Session session;
    
    @BeforeEach 
    public void setup() throws IOException {
        ResourceLoader loader = new ResourceLoader();
        String csvFileName = "test_only_data.csv";
        String csvFile;
        try {
            csvFile = loader.getResourcePath(csvFileName);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            csvFile = null;
        }
        try {
            csvFile = loader.getResourcePath(csvFileName);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            csvFile = null;
        }
        accountDB = new AccountDatabase(csvFile);
        accountDB.loadAccounts(csvFile, true);
        /* for (Account account : accountDB.accounts.values()) {
            System.out.println(account);
        } */
        SessionManager sessionManager = new SessionManager(accountDB);
        try {
            session = sessionManager.login("admin", "password", Role.ADMIN); // Use valid credentials
        } catch (InvalidCredentialsException e) {
            fail("Unexpected InvalidCredentialsException");
            System.err.println();
        }
    }

    @Test
    public void testCheckCredentials_ValidCredentials() {
        //System.out.println("\nRunning test: testCheck - Valid");
        assertTrue(accountDB.checkCredentials(359413893, "lbullimore6", "cV1{8NjIwh", Role.STUDENT));
    }

    
    @Test
    public void testCheckCredentials_InvalidCredentials() {
        System.out.println("\nRunning test: testCheck - Invalid");
        assertFalse(accountDB.checkCredentials(123456789, "invalidUser", "badPassword", Role.STUDENT));
    }

    
    @Test
    public void testCheckCredentials_BlockedAccount() {
        System.out.println("\nRunning test: testCheck - Blocked");
        assertFalse(accountDB.checkCredentials(596501201, "ebaff9", "iC7ja@~m>T*T", Role.STUDENT));
    }


    @Test
    public void testBlockAccount() throws IOException, AccessViolationException, ExpiredSessionException {
        System.out.println("\nRunning test: testBlockAccount()");
        Integer userIdToblock = 552281454; 
        try {
            // System.out.println("Blocking account");
            accountDB.block(session, userIdToblock);
            // System.out.println("Reloading accounts");
            accountDB.reloadAccounts(); 
            assertFalse(accountDB.checkCredentials(userIdToblock, "acicutto1", "kR4E9rcFM", Role.STUDENT)); 
        } catch(AccountNotFoundException e) {
            fail("Unexpected AccountNotFoundException"); 
        } 
    }

    @Test
    public void testUnBlockAccount() {
        System.out.println("\nRunning test: testUnBlockAccount()");
        Integer userIdToUnblock = 424564740; 
        try {
            accountDB.unblock(session, userIdToUnblock);
            assertTrue(accountDB.checkCredentials(424564740, "cpickaver4", "uL6r7=f)8+p1Ng|s", Role.STUDENT));
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
            StudentAccount studentAccount = accountDB.getStudentAccount(session, 711205600);
            assertEquals("1/10/1002", studentAccount.getDob(session));
        } catch(AccessViolationException e) {
            fail("Unexpected AccessViolationException"); 
        } catch(ExpiredSessionException e) {
            fail("Unexpected ExpiredSessionException"); 
        }
    }
}
