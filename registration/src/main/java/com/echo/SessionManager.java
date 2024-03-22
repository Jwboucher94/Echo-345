package com.echo;
import com.echo.Session;
import com.echo.SessionManager.InvalidCredentialsException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Map<String, Session> activeSessions = new HashMap<>();
    private AccountDatabase accountDatabase;

    public SessionManager(AccountDatabase accountDatabase) {
        this.accountDatabase = accountDatabase;
    }

    public Session login(String loginName, String password, Role role) throws InvalidCredentialsException {
        // Iterate over all accounts to find one with matching credentials
        for (Account account : accountDatabase.accounts.values()) {
            if (account.loginName.equals(loginName) && account.password.equals(password)) {
                // Check if the role and account status are valid
                if (account.role == role && !"blocked".equals(account.status)) {
                    // Credentials are valid and account is active, create a new Session
                    String sessionId = generateSessionId();
                    Session newSession = new Session(sessionId, role, account, this, 30 * 60 * 1000); // 30 minutes
                    activeSessions.put(sessionId, newSession);
                    return newSession;
                } else if ("blocked".equals(account.status)) {
                    throw new InvalidCredentialsException("Account is blocked.");
                }
            }
        }
        throw new InvalidCredentialsException("Invalid login credentials.");
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    private String generateSessionId() {
        // Implement session ID generation logic
        return ""; // Placeholder
    }

    // Exception class for invalid credentials
    public static class InvalidCredentialsException extends Exception {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    // ... Other session management functionality ...
}
