package com.echo;
import java.util.Timer;
import java.util.TimerTask;

   
public class Session {
    private String sessionId; // Added to uniquely identify the session
    private Role role; 
    private Account account;
    private SessionManager sessionManager;
    private Timer logoutTimer;
    private long expirationTime; 
    private boolean isActive; // Added to track if session is active

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
                    logout();
                }
            }
        }, expirationDuration);
    }

    public void logout() {
        if (isActive) {
            isActive = false;
            sessionManager.removeSession(this.sessionId); // Pass the sessionId string here
            logoutTimer.cancel(); // Stop the logout timer.
        }
    }
    




class StudentAccount {
    // Attributes will be added in the future
    public StudentAccount() {
        // Nothing to initialize for now
    }
}


}
