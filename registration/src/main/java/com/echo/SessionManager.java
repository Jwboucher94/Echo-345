package com.echo;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private Map<String, Session> activeSessions = new HashMap<>();
    private AccountDatabase accountDatabase;
    public static Integer timeInteger = 30 * 60 * 1000; // to set account expiration time to 30 minutes

    public SessionManager(AccountDatabase accountDatabase) {
        this.accountDatabase = accountDatabase;
    }

    public Session login(String loginName, String password, Role role) throws InvalidCredentialsException {
        return login(loginName, password, role, timeInteger);
    }


    public Session login(String loginName, String password, Role role, Integer SetTimeInteger) throws InvalidCredentialsException {

        for (Account account : accountDatabase.accountDB.values()) {
            
            if (account.loginName.equals(loginName) && account.password.equals(password)) {
            
                if (account.role == role && !AccountStatus.BLOCKED.equals(account.status)) {
                    String sessionId = generateSessionId();
                    Session newSession = new Session(sessionId, role, account, this, SetTimeInteger); // 30 minutes
                    activeSessions.put(sessionId, newSession);
                    return newSession;
                } else if (AccountStatus.BLOCKED.equals(account.status)) {
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
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        return uuidString;
    }

    public Map<String, Session> getActiveSessions() {
        return new HashMap<>(activeSessions); 
    }

    public AccountDatabase getAccountDatabase() {
        return accountDatabase;
    }

    public void saveAccount(Session session) throws AccessViolationException, ExpiredSessionException {
        accountDatabase.saveAccounts(session);
    }


   
}
