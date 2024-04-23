package com.echo;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private Map<String, Session> activeSessions = new HashMap<>();
    private AccountDatabase accountDatabase;


    public SessionManager(AccountDatabase accountDatabase) {
        this.accountDatabase = accountDatabase;
    }

    public Session login(String loginName, String password, Role role) throws InvalidCredentialsException {

        for (Account account : accountDatabase.accounts.values()) {
            
            if (account.loginName.equals(loginName) && account.password.equals(password)) {
            
                if (account.role == role && !"blocked".equals(account.status)) {
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
        return UUID.randomUUID().toString();
    }

    
    public static class InvalidCredentialsException extends Exception {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public Map<String, Session> getActiveSessions() {
        return new HashMap<>(activeSessions); 
    }

    public AccountDatabase getAccountDatabase() {
        return accountDatabase;
    }


   
}
