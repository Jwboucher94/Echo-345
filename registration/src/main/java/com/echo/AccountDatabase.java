package com.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
            accounts = loadAccounts(csvFile, false);
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    // Create a new account - Requires Role.ADMIN
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
        account.status = AccountStatus.ACTIVE;
        ArrayList<Object> studentData = studentDataInput();
        StudentAccount student = new StudentAccount((String) studentData.get(0), 
                                                    (Gender) studentData.get(1),
                                                    (String) studentData.get(2), 
                                                    (String) studentData.get(3), 
                                                    account.userID            );
        account.setStudentAccount(student);
        accounts.put(userID, account);
        session.setHasModified();
        return student;
    }

    // input student data
    ArrayList<Object> studentDataInput() {
        ArrayList<Object> studentData = new ArrayList<>();
        System.out.println("Enter Date of Birth (MM/DD/YYYY): ");
        String dob = Main.getInput();
        System.out.println("Choose a gender: 1. Male, 2. Female, 3. Other");
        Integer genderChoice = Main.getMenuInput(3);
        Gender gender;
        switch (genderChoice) {
            case 1:
                gender = Gender.MALE;
                break;
            case 2:
                gender = Gender.FEMALE;
                break;
            case 3:
                gender = Gender.OTHER;
                break;
            default:
                gender = Gender.OTHER;
        }
        String academicHistory = "";
        System.out.println("Enter phone number: ");
        String phoneNumber = Main.getInput();
        studentData.add(dob);
        studentData.add(gender);
        studentData.add(academicHistory);
        studentData.add(phoneNumber);
        return studentData;
    }

    public void setPhoneNumber(Session session) {
        // set phone number
        String phoneNumber;
        boolean test = false;
        while (!test) {
            System.out.println("Enter new phone number:");
            phoneNumber = Main.getInput();
            test = phoneNumber.matches("^(1-)?\\d{3}-\\d{3}-\\d{4}$");
            if (test) {
                try {
                    session.getAccount().getStudentAccount(session).setPhoneNumber(session, phoneNumber);
                }  catch (AccessViolationException e) {
                    System.err.println("Access violation: " + e.getMessage());
                } catch (ExpiredSessionException e) {
                    System.err.println("Session expired: " + e.getMessage());
                } catch (NotStudentException e) {
                    System.err.println("Account is not a student account: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid phone number format. Please enter a valid phone number.");
            }
        }
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
            account.status.equals(AccountStatus.BLOCKED)) { 
            return false;
        }
        return true; 
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
                        System.err.println("Account is not a student account. You should not get this.");
                    } catch (NullPointerException e) {
                        System.err.println("This is a student account, but it is missing student data. Please have an admin update this account.");
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
        while (!passwordCheck(password)) {
            passwordCheckInstruction();
            System.out.println("Enter a new password: ");
            password = Main.getInput();
        }
        if (passwordCheck(password)) {
            session.getAccount().password = password;
            session.setHasModified();
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

    // Block the account using userID
    public void block(Session session, Integer userID) throws AccountNotFoundException, AccessViolationException, ExpiredSessionException {    
        if (!accounts.containsKey(userID)) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        Account account = accounts.get(userID);
        account.status = AccountStatus.BLOCKED;
        saveAccounts(session); 
    }
    // Unblock the account using userID
    public void unblock(Session session, Integer userID) throws AccountNotFoundException, AccessViolationException, ExpiredSessionException {  
        Account account = accounts.get(userID);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        account.status = AccountStatus.ACTIVE;
        saveAccounts(session); 
    }

    // Load the accounts from a file and return a map of accounts
    public Map<Integer, Account> loadAccounts(String csvFile, Boolean test) throws IOException { 
        csvFile = getClass().getClassLoader().getResource("MOCK_DATA.csv").getFile();
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
                        account.status = AccountStatus.valueOf(fields[4]);
                        // If the account is a student account, create a student account object for them
                        if (account.role == Role.STUDENT) {
                            try {
                                StudentAccount studentAccount = new StudentAccount(fields[5], Gender.valueOf(fields[6]), fields[7], fields[8], account.userID);
                                account.setStudentAccount(studentAccount);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.err.println("Error loading student account: " + e.getMessage() + "\n" + 
                                                   "Is it a student account?\n" + 
                                                   "Offending Account: " + account.userID + " : " + account.loginName);
                                System.out.println("Would you like to create a student account for this user? (Y/N)");
                                String input;
                                if (test == true) {
                                    // System.out.println("Test mode: Y");
                                    input = "Y";
                                } else {
                                    // System.out.println("Test mode: N");
                                    input = Main.getInput();
                                }
                                if (input.toLowerCase().equals("y")) {
                                    ArrayList<Object> studentData = studentDataInput();
                                    StudentAccount student = new StudentAccount((String) studentData.get(0), 
                                                                                (Gender) studentData.get(1),
                                                                                (String) studentData.get(2), 
                                                                                (String) studentData.get(3), 
                                                                                account.userID            );
                                    account.setStudentAccount(student);
                                } 
                            }
                        } 
                        if (!accounts.containsKey(account.userID)) {
                            accounts.put(account.userID, account);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            System.exit(1);
        } 
        return accounts;
    }

    
    // Save the accounts to a file
    public void saveAccounts(Session session) throws AccessViolationException, ExpiredSessionException {    
        ResourceLoader loader = new ResourceLoader();
        String filePath;
        try {
            filePath = loader.getResourcePath("MOCK_DATA.csv");
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            filePath = null;
        }
        try (FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw)) {
                for (Account account : accounts.values()) {
                    String line;
                    try { 
                        StudentAccount studentAccount = account.getStudentAccount(session);
                        line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            account.userID, account.loginName, account.password, account.role, account.status, studentAccount.getDob(session), 
                            studentAccount.getGender(session), studentAccount.getAcademicHistory(session), studentAccount.getPhoneNumber(session));
                    } catch (NotStudentException e) { 
                        // Not a student account
                        line = String.format("%d,%s,%s,%s,%s\n",
                            account.userID, account.loginName, account.password, account.role, account.status);
                    } catch (NullPointerException e) {
                        // Broken student account, probably needs studentAccount
                        System.err.println("Error saving student account: " + e.getMessage() + "\n" + 
                                           "Offending Account: " + account.userID + " : " + account.loginName);
                        line = String.format("%d,%s,%s,%s,%s\n",
                            account.userID, account.loginName, account.password, account.role, account.status);
                    }
                    bw.write(line);
                }
            session.setHasModified(false);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        } 
    }

    // Returns an account using userID
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
        accounts = loadAccounts(csvFile, false); 
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
    AccountStatus status; 
    private StudentAccount studentAccount; // Student account object


    public void setStudentAccount(StudentAccount studentAccount) {
        if (role == Role.STUDENT) { 
            this.studentAccount = studentAccount;
        }
    }
    
    public StudentAccount getStudentAccount(Session session) throws NotStudentException {
        if (role == Role.STUDENT) { 
            try {
                this.studentAccount.getUserID(session);
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

    public AccountStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}

// Roles for the account
enum Role {  
    STUDENT,
    ADMIN,
    ADVISOR,
}

enum Gender {
    MALE,
    FEMALE,
    OTHER;
}

enum AccountStatus {
    ACTIVE,
    BLOCKED;
}