package com.echo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; // For newer JUnit
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.echo.AccountDatabase;
import com.echo.AccountNotFoundException;
import com.echo.Role;

@SuppressWarnings("unused")
public class AccountDatabaseTest {

    private AccountDatabase accountDB;

    @BeforeEach // JUnit 5 version of @Before
    public void setup() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("MOCK_DATA.csv");
        if (inputStream == null) {
            throw new FileNotFoundException("MOCK_DATA.csv not found in test resources");
        }
        accountDB = new AccountDatabase(inputStream);
    }
  // Test of valid credentials
    @Test
    public void testCheckCredentials_ValidCredentials() {
        assertTrue(accountDB.checkCredentials(359413893, "lbullimore6", "cV1{8NjIwh", Role.STUDENT));
    }

    // Test of invalid credentials
    @Test
    public void testCheckCredentials_InvalidCredentials() {
        assertFalse(accountDB.checkCredentials(123456789, "invalidUser", "badPassword", Role.STUDENT));
    }

    // Test of blocked account
    @Test
    public void testCheckCredentials_BlockedAccount() {
        assertFalse(accountDB.checkCredentials(596501201, "ebaff9", "iC7ja@~m>T*T", Role.STUDENT));
    }


    @Test
    public void testBlockAccount() throws IOException {
        int userIdToblock = 552281454; 
        try {
            accountDB.block(userIdToblock);
            accountDB.reloadAccounts(); 
            assertFalse(accountDB.checkCredentials(userIdToblock, "acicutto1", "kR4E9rcFM", Role.STUDENT)); 
        } catch(AccountNotFoundException e) {
            fail("Unexpected AccountNotFoundException"); 
        } 
    }

    @Test
    public void testUnBlockAccount() {
        int userIdToUnblock = 424564740; 
        try {
            accountDB.unblock(userIdToUnblock);
            assertTrue(accountDB.checkCredentials(424564740, "cpickaver4", "uL6r7=f)8+p1Ng|s", Role.STUDENT));
        } catch(AccountNotFoundException e) {
            fail("Unexpected AccountNotFoundException"); 
        } 
    }

}
