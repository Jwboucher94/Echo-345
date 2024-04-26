package com.echo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.Scanner;

class SessionManagerTest {
    private AccountDatabase accountDatabase;
    private Account testAccount;
    private Session testSession;
    private SessionManager sessionManager;

    @BeforeEach 
    public void setup() throws IOException {
        Scanner mockScanner = Mockito.mock(Scanner.class);
        Main mainObject = new Main(mockScanner);
        try {
            accountDatabase = new AccountDatabase(mainObject,"test_only_data.csv");
        } catch (IOException e) {
            System.err.println("Error reading file");
        }
        /* for (Account account : accountDB.accounts.values()) {
            System.out.println(account);
        } */
        testAccount = new Account(1, "testuser", "password", Role.STUDENT, AccountStatus.ACTIVE);
        this.sessionManager = new SessionManager(accountDatabase);
        accountDatabase.accountDB.put(99, testAccount);
        testSession = new Session("sessionId", Role.STUDENT, testAccount, sessionManager, 1000);
        testAccount = testSession.getAccount();
    }
    @Test
    void testSessionConstructor() {
        assertNotNull(testSession, "Session should be created successfully");
        assertEquals(AccountStatus.ACTIVE, testSession.getAccount().getStatus(), "Session account should be active");
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
    void testLoginMethodWithCorrectCredentials() throws InvalidCredentialsException {
        Session session = sessionManager.login("testuser", "password", Role.STUDENT);
        assertNotNull(session, "Session should be returned for correct credentials");
        assertNotNull(sessionManager.getActiveSessions().get(session.getSessionId()), "SessionManager should have the session listed");
    }

    @Test
    void testLoginMethodWithIncorrectCredentials() {
        assertThrows(InvalidCredentialsException.class, () -> {
            sessionManager.login("wronguser", "wrongpassword", Role.STUDENT);
        }, "InvalidCredentialsException should be thrown for wrong credentials");
    }

    @Test
    void testLoginMethodWithBlockedAccount() {
        testAccount.setStatus(AccountStatus.BLOCKED); 
        assertThrows(InvalidCredentialsException.class, () -> {
            sessionManager.login("testuser", "password", Role.STUDENT);
        }, "InvalidCredentialsException should be thrown for a blocked account");
    }

    @Test
void testSessionListManagement() throws InvalidCredentialsException {
   
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
