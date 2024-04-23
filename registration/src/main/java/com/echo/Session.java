package com.echo;
import java.util.Timer;
import java.util.TimerTask;

   
public class Session {
    private String sessionId;
    private Role role;
    private Account account;
    private SessionManager sessionManager;
    private Timer logoutTimer;
    private long expirationTime;
    private boolean isActive;

    public Session(String sessionId, Role role, Account account, SessionManager sessionManager, long expirationDuration) {
        this.sessionId = sessionId;
        this.role = role;
        this.account = account;
        this.sessionManager = sessionManager;
        this.expirationTime = System.currentTimeMillis() + expirationDuration;
        this.isActive = true;

        this.logoutTimer = new Timer(true);
        logoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isActive) {
                    logout(false);
                }
            }
        }, expirationDuration);
    }

    public Role getUserRole() {
        return this.account.role; // Replace with the actual method call to get the role as a String
    }

    public String getSessionOwner() {
        return this.account.loginName; // Use the loginName field from Account as the session owner's name
    }

    public static boolean validateSession(Session session) {
        return session.isActive() && System.currentTimeMillis() < session.getExpirationTime();
    }

    public void logout(Boolean modified) {
        if (isActive) {
            isActive = false;
            sessionManager.removeSession(this.sessionId);
            logoutTimer.cancel();
        }
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public Role getRole() {
        return role;
    }

    public Account getAccount() {
        return account;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public Timer getLogoutTimer() {
        return logoutTimer;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setter methods (if needed)
    // It might not be appropriate to set some of these fields after initialization
    public void setActive(boolean active) {
        isActive = active;
    }
    




}
