package com.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AccountDatabase {
    Map<Integer, Account> accounts = new HashMap<>();                 


    // Constructor
    public AccountDatabase(String csvFile) throws IOException {                 
        if (csvFile == null) {
            throw new IllegalArgumentException("csv string cannot be null");
        }
        try {
            accounts = loadAccounts(csvFile);
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    StudentAccount createAccount(Session session, String loginName,
    String password) throws AccessViolationException, ExpiredSessionException, DuplicateRecordException {
        if (session == null) {
            throw new AccessViolationException("Session is null");
        }
        if (session.isActive() == false) {
            throw new ExpiredSessionException("Session is expired");
        }
        if (session.getRole() != Role.ADMIN) {
            throw new AccessViolationException("Access violation: Only admin can create accounts");
        }
        Integer userID = accounts.size() + 1;
        Account account = new Account();
        account.userID = userID;
        // check if the login name already exists
        for (Account existingAccount : accounts.values()) {
            if (existingAccount.loginName.equals(loginName)) {
                throw new DuplicateRecordException("Login name already exists: " + loginName);
            }
        }
        account.loginName = loginName;
        account.password = password;
        account.role = Role.STUDENT;
        account.status = "active";
        accounts.put(userID, account);
        saveAccounts(session);
        return new StudentAccount("01/01/2000", Gender.MALE, "History", "1234567890", loginName);
    }

    StudentAccount getStudentAccount(Session session, Integer userID) throws AccessViolationException, ExpiredSessionException, NotStudentException {
        if (session == null) {
            throw new AccessViolationException("Session is null");
        }
        if (session.isActive() == false) {
            throw new ExpiredSessionException("Session is expired");
        }
        if (session.getRole() != Role.ADMIN) {
            throw new AccessViolationException("Access violation: Only admin can get student accounts");
        }
        Account account = accounts.get(userID);
        if (account == null) {
            return null;
        }
        if (account.role != Role.STUDENT) {
            throw new NotStudentException("Account is not a student account");
        }
        return account.getStudentAccount(session);
    }

    // takes userID, loginName, password, and role as arguments and checks if the credentials are valid
    public boolean checkCredentials(Integer userID, String loginName, 
                                    String password, Role role) {        
        Account account = accounts.get(userID);
        if (account == null || !account.loginName.equals(loginName) || 
            !account.password.equals(password) || !account.role.equals(role) || 
            account.status.equals("blocked")) { 
            return false;
        }
        return true; 
    }
    // Block the account using userID
    public void block(Session session, Integer userID) throws AccountNotFoundException, AccessViolationException, ExpiredSessionException {    
        if (!accounts.containsKey(userID)) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        Account account = accounts.get(userID);
        account.status = "blocked";
        saveAccounts(session); 
    }
    
    void viewAccount(Session session) throws AccessViolationException, ExpiredSessionException {
        if (session == null) {
            throw new AccessViolationException("Session is null");
        }
        if (session.isActive() == false) {
            throw new ExpiredSessionException("Session is expired");
        }
        String loginName = session.getSessionOwner();
        for (Account account : accounts.values()) {
            if (account.loginName.equals(loginName)) {
                System.out.println        ("         Current Users Data");
                System.out.println        ("------------------------------------");
                System.out.println        ("      Account ID: " + account.userID);
                System.out.println        ("      Login Name: " + account.loginName);
                System.out.println        ("            Role: " + account.role);
                System.out.println        ("          Status: " + account.status);
                if (account.role == Role.STUDENT) {
                    try {
                        StudentAccount studentAccount = account.getStudentAccount(session);
                        System.out.println("   Date of Birth: " + studentAccount.getDob(session));
                        System.out.println("          Gender: " + studentAccount.getGender(session));
                        System.out.println("Academic History: " + studentAccount.getAcademicHistory(session));
                        System.out.println("    Phone Number: " + studentAccount.getPhoneNumber(session));
                    } catch (NotStudentException e) {
                        System.out.println("Account is not a student account. You should not get this.");
                    }
                }
                System.out.println        ("------------------------------------");
            }
        }
        
    }
    // need a test for this
    boolean passwordCheck(String password) {
        boolean test = password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$");
        return test;
    }

    // need a test for this
    void passwordCheckInstruction() {
        System.out.println("Password must contain, at minimum:\n"+
                           "8 Total characters\n"+
                           "1 uppercase letter\n"+
                           "1 lowercase letter\n"+
                           "1 number\n"+
                           "1 special character.");
    }

    // need a test for this
    boolean changePassword(Session session, String password) {
        if (passwordCheck(password)) {
            session.logout(true);
            return true;
        } else {
            return false;
        }
    }

    boolean changeLoginName(Session session, String loginName) {
        Session.validateSession(session);
        return true;
    }

    // Unblock the account using userID
    public void unblock(Session session, Integer userID) throws AccountNotFoundException, AccessViolationException, ExpiredSessionException {  
        Account account = accounts.get(userID);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        account.status = "active";
        saveAccounts(session); 
    }
    // Load the accounts from a file and return a map of accounts
    public Map<Integer, Account> loadAccounts(String csvFile) throws IOException { 
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {  
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 5) {
                    Account account = new Account();
                    account.userID = Integer.parseInt(fields[0]);
                    account.loginName = fields[1];
                    account.password = fields[2];
                    account.role = Role.valueOf(fields[3]);
                    account.status = fields[4];
                    // If the account is a student account, create a student account object
                    if (account.role == Role.STUDENT) {
                        StudentAccount studentAccount = new StudentAccount(fields[5], Gender.valueOf(fields[6]), fields[7], fields[8], fields[1]);
                        account.setStudentAccount(studentAccount);
                    }
                    if (!accounts.containsKey(account.userID)) {
                        accounts.put(account.userID, account);
                    }
                } 
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        } 
        return accounts;
    }

    // Get the absolute path of the file
    private String getResourcePath(String filename) {          
        URL url = getClass().getClassLoader().getResource(filename);
        if (url == null) {
            throw new RuntimeException("Resource not found: " + filename);
        }
        return new File(url.getFile()).getAbsolutePath();
    }
    // Save the accounts to a file
    public void saveAccounts(Session session) throws AccessViolationException, ExpiredSessionException {    
        String filePath = getResourcePath("MOCK_DATA.csv");
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw)) {
                for (Account account : accounts.values()) {
                    String line;
                    try { 
                        StudentAccount studentAccount = account.getStudentAccount(session);
                        line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            account.userID, account.loginName, account.password, account.role, account.status, studentAccount.getDob(session), studentAccount.getGender(session), studentAccount.getAcademicHistory(session), studentAccount.getPhoneNumber(session));
                    } catch (NotStudentException e) {
                        // Not a student account
                        line = String.format("%d,%s,%s,%s,%s\n",
                            account.userID, account.loginName, account.password, account.role, account.status);
                    }
                    bw.write(line);
                }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        } 
    }
    // Get an account using userID
    public Account getsAccount(Integer userID) {
        Account account = accounts.get(userID);
        return account;
    }
    // Reload the accounts from the file
    public void reloadAccounts() throws IOException {  
        String csvFile = getClass().getClassLoader().getResource("MOCK_DATA.csv").getFile(); 
        if (csvFile == null) {
            throw new FileNotFoundException("MOCK_DATA.csv not found in test resources");
        }
        accounts = loadAccounts(csvFile); 
    }


    
    public class DuplicateRecordException extends Exception {
        public DuplicateRecordException(String message) {
            super(message);
        }
    }
    
}

// Account class
class Account {    
    Integer userID;
    String loginName;
    String password;
    Role role;
    String status; 
    private StudentAccount studentAccount; // Student account object

    public void setStudentAccount(StudentAccount studentAccount) {
        if (role == Role.STUDENT) { 
            this.studentAccount = studentAccount;
        }
    }
    
    public StudentAccount getStudentAccount(Session session) throws NotStudentException {
        if (role == Role.STUDENT) { 
            try {
                this.studentAccount.getLoginName(session);
            } catch (ExpiredSessionException e) {
                System.err.println("Session expired");
            } catch (AccessViolationException e) {
                System.err.println("Access violation");
            }
            return this.studentAccount;
        } else {
            throw new NotStudentException("Account is not a student account");
        }
    }

    public Integer getUserID() {
        return userID;
    }
    public Role getRole() {
        return role;
    }
    public String getLoginName() {
        return this.loginName;
    }

    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

// Roles for the account
enum Role {  
    STUDENT,
    ADMIN,
    ADVISOR,
    FACULTY;
}

enum Gender {
    MALE,
    FEMALE,
    OTHER;
}