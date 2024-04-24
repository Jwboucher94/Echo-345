package com.echo;
import java.io.Serializable;

class AccessViolationException extends Exception {
    public AccessViolationException(String message) {
        super(message);
    }
}

class ExpiredSessionException extends Exception {
    public ExpiredSessionException(String message) {
        super(message);
    }
}

class AcademicHistory implements Serializable {
    private String history;

    public AcademicHistory(String history) {
        this.history = history;
    }

    public String serialize() {
        return this.history;
    }

    public static AcademicHistory deserialize(String historyStr) {
        return new AcademicHistory(historyStr);
    }
}

public class StudentAccount { 
    // The constructor is used to create a new StudentAccount
    // object with specified details like date of birth, gender, academic history, phone number, and login name.
    private String dob; //store the date of birth of student
    private Gender gender; //gender from Gender enum
    private AcademicHistory academicHistory; //encapsulates the student's academic records
    private String phoneNumber; //stores the phone number of the student
    private Integer userID; //Stores the login name of the student's account

    public StudentAccount(String dob, Gender gender, String academicHistory, String phoneNumber, Integer userID) {
        this.dob = dob;
        this.gender = gender;
        this.academicHistory = AcademicHistory.deserialize(academicHistory);
        this.phoneNumber = phoneNumber;
        this.userID = userID;
    }

    public String getDob(Session session) throws ExpiredSessionException, AccessViolationException {
        validateSession(session);
        return dob;
       
    }

    public Gender getGender(Session session) throws ExpiredSessionException, AccessViolationException {
        validateSession(session);
        return gender;
      
    }

    public String getAcademicHistory(Session session) throws ExpiredSessionException, AccessViolationException {
        validateSession(session);
        return academicHistory.serialize();
    }

    public String getPhoneNumber(Session session) throws ExpiredSessionException, AccessViolationException {
        validateSession(session);
        return phoneNumber;
    }

    public Integer getUserID(Session session) throws ExpiredSessionException, AccessViolationException {
        validateSession(session);
        return userID;
    }


    public void setDob(Session session, String dob) throws ExpiredSessionException, AccessViolationException {
        validateSessionForChange(session, true);
        this.dob = dob;
    }

    public void setGender(Session session, Gender gender) throws ExpiredSessionException, AccessViolationException {
        validateSessionForChange(session, true);
        this.gender = gender;
    }

    public void setAcademicHistory(Session session, String history) throws ExpiredSessionException, AccessViolationException {
        validateSessionForChange(session, session.getUserRole().equals(Role.ADMIN));
        this.academicHistory = AcademicHistory.deserialize(history);
    }

    public void setPhoneNumber(Session session, String phoneNumber) throws ExpiredSessionException, AccessViolationException {
        validateSessionForChange(session, true);
        this.phoneNumber = phoneNumber;
    }

  
    public void setUserID(Session session, Integer userID) throws AccessViolationException {
        throw new AccessViolationException("Cannot change user ID.");
    }


    private void validateSession(Session session) throws ExpiredSessionException, AccessViolationException {
        if (!session.isActive()) {
            throw new ExpiredSessionException("Session is expired or inactive.");
        }
        Integer sessionuserID;
        try {
            sessionuserID = session.getAccount().getUserID();
        } catch (NullPointerException e) {
            throw new NullPointerException("No userID");
        }
        if (!sessionuserID.equals(this.userID) && 
            !session.getUserRole().equals(Role.ADMIN)) {
            throw new AccessViolationException("Access denied: You are not Admin!");
        }
    }

    private void validateSessionForChange(Session session, boolean isChangeAllowed) throws ExpiredSessionException, AccessViolationException {
        validateSession(session); 
        if (!isChangeAllowed) {
            throw new AccessViolationException("You are not authorized to modify this field.");
        }
    }
}
