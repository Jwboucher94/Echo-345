package com.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;



public class AccountDatabase {
    Main main;
    Map<Integer, Account> accountDB = new HashMap<>();
    ResourceLoader loader = new ResourceLoader();
    String csvFileName;    
    String csvFilePath;
    

    // AccountDatabase Constructor
    public AccountDatabase(Main mainIN, String csvFileName) throws IOException {    
        main = mainIN;             
        if (csvFileName == null) {
            throw new IllegalArgumentException("csv string cannot be null");
        }
        this.csvFileName = csvFileName;
        this.csvFilePath = this.loader.getResourcePath(csvFileName);
        try {
            this.accountDB = loadAccounts(csvFileName, false);
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
        Integer userID = this.accountDB.size() + 1;
        Account account = new Account();
        account.userID = userID;
        // check if the login name already exists
        for (Account existingAccount : this.accountDB.values()) {
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
        accountDB.put(userID, account);
        session.setHasModified();
        return student;
    }

    // input student data
    ArrayList<Object> studentDataInput() {
        return studentDataInput(main.scanner);
    }
    ArrayList<Object> studentDataInput(Scanner scanner) {
        ArrayList<Object> studentData = new ArrayList<>();
        System.out.println("Enter Date of Birth (MM/DD/YYYY): ");
        String dob = main.getInput(scanner);
        System.out.println("Choose a gender: 1. Male, 2. Female, 3. Other");
        Integer genderChoice = main.getMenuInput(scanner, 3);
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
        String phoneNumber = main.getInput(scanner);
        studentData.add(dob);
        studentData.add(gender);
        studentData.add(academicHistory);
        studentData.add(phoneNumber);
        return studentData;
    }

    // set phone number
    public void setPhoneNumber(Session session) {
        // set phone number
        String phoneNumber;
        boolean test = false;
        while (!test) {
            main.clearScreen("Enter new phone number:");
            phoneNumber = main.getInput();
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

    // get student account using userID
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
        Account account = this.accountDB.get(userID);
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
        Account account = this.accountDB.get(userID);
        if (account == null || !account.loginName.equals(loginName) || 
            !account.password.equals(password) || !account.role.equals(role) || 
            account.status.equals(AccountStatus.BLOCKED)) { 
            return false;
        }
        return true; 
    }
    
    // view the current user's account

    void viewAccount(Session session) throws AccessViolationException, ExpiredSessionException {
        String loginName = session.getSessionOwner();
        viewAccount(session, loginName);
    }

    void viewAccount(Session session, String loginName) throws AccessViolationException, ExpiredSessionException {
        if (session == null) {
            throw new AccessViolationException("Session is null");
        }
        if (session.isActive() == false) {
            throw new ExpiredSessionException("Session is expired");
        }
        for (Account account : this.accountDB.values()) {
            if (account.loginName.equals(loginName)) {
                System.out.println        ("         "+ loginName+"'s Data");
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
 
    // check if the password meets the requirements
    boolean passwordCheck(Session session, String password) {
        boolean test = password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$");
        if (test == true) {
            if (!password.matches(session.getAccount().password)) {
            return true;
            } else {
                System.err.println("Password cannot be the same as the current password.");
            }
        }
        return false;
    }

    // display password check instructions
    void passwordCheckInstruction() {
        System.out.println("Password must contain, at minimum:\n"+
                           "8 Total characters\n"+
                           "1 uppercase letter\n"+
                           "1 lowercase letter\n"+
                           "1 number\n"+
                           "1 special character.");
    }

    // change the password
    boolean changePassword(Session session, String password) {
        while (!passwordCheck(session, password)) {
            passwordCheckInstruction();
            System.out.println("Enter a new password: ");
            password = main.getInput();
        }
        if (passwordCheck(session, password)) {
            session.getAccount().password = password;
            session.setHasModified();
            session.logout(true);
            return true;
        } else {
            return false;
        }
    }

    // check if you can change the login name
    static boolean changeLoginName(Session session, String loginName) {
        try {
            if (session.getAccount().studentAccount == null) {
                session.getAccount().studentAccount.validateSessionForChange(session, true);
            }
        }  catch (AccessViolationException e) {
            System.err.println("Access violation: " + e.getMessage());
        } catch (ExpiredSessionException e) {
            System.err.println("Session expired: " + e.getMessage());
        }
        Map<Integer, Account> accountDB = session.getSessionManager().getAccountDatabase().getAccountDB();
        // test if login name is taken
        for (Account account : accountDB.values()) {
            if (account.loginName.equals(loginName)) {
                System.err.println("Login name already exists: " + loginName);
                return false;
            }
        }
        return true;
    }

    // Block the account using userID
    public void block(Session session, Integer userID) throws AccountNotFoundException, AccessViolationException, ExpiredSessionException {    
        if (!accountDB.containsKey(userID)) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        Account account = accountDB.get(userID);
        account.status = AccountStatus.BLOCKED;
        saveAccounts(session); 
    }
 
    // Unblock the account using userID
    public void unblock(Session session, Integer userID) throws AccountNotFoundException, AccessViolationException, ExpiredSessionException {  
        Account account = accountDB.get(userID);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        account.status = AccountStatus.ACTIVE;
        saveAccounts(session); 
    }

    // Load the accounts from a file and return a map of accounts
    public Map<Integer, Account> loadAccounts(String csvFileName, Boolean test) throws IOException {
        try {
            csvFilePath = loader.getResourcePath(csvFileName);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            csvFilePath = null;
        }
        String line;
        ArrayList<Account> brokenAccounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {  
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 5) {
                    Account account = new Account(Integer.parseInt(fields[0]), fields[1], fields[2], Role.valueOf(fields[3]), AccountStatus.valueOf(fields[4]));
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
                                input = "n";
                            } else {
                                // System.out.println("Test mode: N");
                                input = main.getInput();
                            }

                            if (input.toLowerCase().equals("y")) {
                                brokenAccounts.add(account);
                            } else {
                                System.err.println("Account will be ignored, for now. Please correct the data in the file as soon as possible.");
                            }
                        }
                    } 
                    if (!accountDB.containsKey(account.userID)) {
                        accountDB.put(account.userID, account);
                    }
                }
            }
            br.close();
            if (brokenAccounts.size() > 0) {
                System.out.println("Let's fill in the missing student data.");
                accountDB = missingStudentAccount(brokenAccounts, accountDB);
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            System.exit(1);
        } 
        return accountDB;
    }

    // Fix any broken studentAccount data and return the updated accountDB
    Map<Integer, Account> missingStudentAccount (ArrayList<Account> brokenAccounts, Map<Integer, Account> accountDB) {

        // create a session to save the account database
        SessionManager sessionManager = new SessionManager(this);
        System.out.println("In order to modify, we need to login as an admin.\n"+
                           "Press enter to continue, or q to quit.");
        String input = main.getInput();
        if (input.toLowerCase().startsWith("q")) {
            System.exit(0);
        }
        Session session = null;
        while (session == null) {
            session = main.getSession(sessionManager, Role.ADMIN);
        }
        // Iterate for any broken student accounts     
        for (Account account : brokenAccounts) {
            ArrayList<Object> studentData = studentDataInput();
            StudentAccount student = new StudentAccount((String) studentData.get(0), 
                                                        (Gender) studentData.get(1),
                                                        (String) studentData.get(2), 
                                                        (String) studentData.get(3), 
                                                        account.userID            );
            // set student account and add the account back to the accountDB
            account.setStudentAccount(student);
            accountDB.put(account.userID, account);
        }
        session.logout(true);
        return accountDB;
    }

    // Get the account database
    public Map<Integer, Account> getAccountDB() {
        return accountDB;
    }
    
    // Save the accounts to a file
    public void saveAccounts(Session session) throws AccessViolationException, ExpiredSessionException {
        try {
            csvFilePath = loader.getResourcePath(csvFileName);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            csvFilePath = null;
        }
        try (FileWriter fw = new FileWriter(csvFilePath);
            BufferedWriter bw = new BufferedWriter(fw)) {
                for (Account account : accountDB.values()) {
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
        Account account = accountDB.get(userID);
        return account;
    }

    // Reload the accounts from the file
    public void reloadAccounts() throws IOException {  
        accountDB = loadAccounts(this.csvFileName, false); 
    }    
}

// Account class
class Account {    
    Integer userID;
    String loginName;
    String password;
    Role role;
    AccountStatus status; 
    StudentAccount studentAccount; // Student account object


    public Account() {
    }

    public Account(Integer userID, String loginName, String password, Role role, AccountStatus status) {
        this.userID = userID;
        this.loginName = loginName;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public void setStudentAccount(StudentAccount studentAccount) {
        if (role == Role.STUDENT) { 
            this.studentAccount = studentAccount;
        }
    }
    
    public StudentAccount getStudentAccount(Session session) throws NotStudentException {
        if (role == Role.STUDENT) { 
            session.validateSession();
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

// Gender roles
enum Gender {
    MALE,
    FEMALE,
    OTHER;
}

// Account status
enum AccountStatus {
    ACTIVE,
    BLOCKED;
}