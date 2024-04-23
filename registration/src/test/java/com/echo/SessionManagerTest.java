package com.echo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

class SessionManagerTest {

    private AccountDatabase accountDatabase;
    private SessionManager sessionManager;
    private Account testAccount;
    private Session testSession;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        
        String csvFile = getClass().getClassLoader().getResource("MOCK_DATA.csv").getFile(); 
        if (csvFile == null) {
            throw new FileNotFoundException("MOCK_DATA.csv not found in test resources");
        }
        try {
            accountDatabase = new AccountDatabase(csvFile);
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        } 
        sessionManager = new SessionManager(accountDatabase);
        testAccount = new Account();
        testAccount.userID = 1;
        testAccount.loginName = "testuser";
        testAccount.password = "password";
        testAccount.role = Role.STUDENT;
        testAccount.status = "active";

       
        accountDatabase.accounts.put(testAccount.userID, testAccount);


        testSession = new Session("sessionId", Role.STUDENT, testAccount, sessionManager, 1000); 
    }

    @Test
    void testSessionConstructor() {
        assertNotNull(testSession, "Session should be created successfully");
        assertEquals("active", testSession.getAccount().getStatus(), "Session account should be active");
        assertTrue(testSession.isActive(), "Session should be active upon creation");
    }

    @Test
    void testAutoLogout() throws InterruptedException {
        testSession = new Session("sessionId", Role.STUDENT, testAccount, sessionManager, 1000); 
        Thread.sleep(1500); 
        assertFalse(testSession.isActive(), "Session should be inactive after expiration time");
    }

    @Test
    void testLogoutMethod() {
        testSession.logout(false);
        assertFalse(testSession.isActive(), "Session should be set to inactive after logout");
        assertNull(sessionManager.getActiveSessions().get(testSession.getSessionId()), "Session should be removed from SessionManager");
    }

    @Test
    void testLoginMethodWithCorrectCredentials() throws SessionManager.InvalidCredentialsException {
        Session session = sessionManager.login("testuser", "password", Role.STUDENT);
        assertNotNull(session, "Session should be returned for correct credentials");
        assertNotNull(sessionManager.getActiveSessions().get(session.getSessionId()), "SessionManager should have the session listed");
    }

    @Test
    void testLoginMethodWithIncorrectCredentials() {
        assertThrows(SessionManager.InvalidCredentialsException.class, () -> {
            sessionManager.login("wronguser", "wrongpassword", Role.STUDENT);
        }, "InvalidCredentialsException should be thrown for wrong credentials");
    }

    @Test
    void testLoginMethodWithBlockedAccount() {
        testAccount.setStatus("blocked"); 
        assertThrows(SessionManager.InvalidCredentialsException.class, () -> {
            sessionManager.login("testuser", "password", Role.STUDENT);
        }, "InvalidCredentialsException should be thrown for a blocked account");
    }

    @Test
void testSessionListManagement() throws SessionManager.InvalidCredentialsException {
   
    for (Session session : sessionManager.getActiveSessions().values()) {
        session.logout(false);
    }


    Session session1 = sessionManager.login("testuser", "password", Role.STUDENT);
    Session session2 = sessionManager.login("testuser", "password", Role.STUDENT);

   
    assertEquals(2, sessionManager.getActiveSessions().size(), "SessionManager should have two active sessions");
    session1.logout(false);
    assertEquals(1, sessionManager.getActiveSessions().size(), "SessionManager should have one active session after the first logout");
    session2.logout(false);
    assertTrue(sessionManager.getActiveSessions().isEmpty(), "SessionManager should have no active sessions after both logouts");
}


    @AfterEach
    void tearDown() {
    }
}
