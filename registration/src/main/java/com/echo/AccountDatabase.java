package com.echo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class AccountDatabase {
    Main main;
    Map<Integer, Account> accountDB = new HashMap<>();
    ResourceLoader loader = new ResourceLoader();
    String csvFileName;    
    String csvContent;
    

    // AccountDatabase Constructor
    public AccountDatabase(Main mainIN, String csvFileName) throws IOException {
        this(mainIN, csvFileName, false);
    }
    // AccountDatabase Constructor, with settable test variable
    public AccountDatabase(Main mainIN, String csvFileName, Boolean test) throws IOException {    
        main = mainIN;             
        if (csvFileName == null) {
            throw new IllegalArgumentException("csv string cannot be null");
        }
        this.csvFileName = csvFileName;
        System.out.println(csvFileName);
        try {
            this.accountDB = loadAccounts(csvFileName, test);
        } catch (IOException e) {
            throw new IOException("Error loading accounts from file: " + e.getMessage());
            
        }
    }

    Account getAccount(Integer userID) {
        return accountDB.get(userID);
    }

    StudentAccount createAccount(Session session, String loginName, String password) throws AccessViolationException, ExpiredSessionException, DuplicateRecordException {
        return createAccount(session, loginName, password, null);
    }
    // Create a new account - Requires Role.ADMIN
    StudentAccount createAccount(Session session, String loginName,
    String password, StudentData studentData) throws AccessViolationException, ExpiredSessionException, DuplicateRecordException {
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
        if (studentData == null) {
            studentData = studentDataInput();
        }
        StudentAccount student = new StudentAccount((String) studentData.getDob(), 
                                                    (Gender) studentData.getGender(),
                                                    (String) studentData.getPhoneNumber(), 
                                                    (String) studentData.getAcademicHistory(), 
                                                    account.userID);
        account.setStudentAccount(student);
        accountDB.put(userID, account);
        session.setHasModified();
        return student;
    }

    // input student data
    StudentData studentDataInput() {
        return studentDataInput(main.scanner);
    }
    StudentData studentDataInput(Scanner scanner) {
        System.out.println("Enter Date of Birth (MM/DD/YYYY): ");
        Boolean correct = false;
        String dob = "";
        while (correct == false) {
            dob = main.getInput(scanner);
            System.out.println(dob);
            correct = dob.matches("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/[0-9]{4}$");
            if (correct == false) {
                System.out.println("Invalid date format. Please enter a valid date in MM/DD/YYYY format.");
                System.exit(1);
            }
        }
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
        correct = false;
        String phoneNumber = "";
        while (correct == false) {
            System.out.println("Enter phone number: ");
            phoneNumber = main.getInput(scanner);
            correct = phoneNumber.matches("^\\d{3}-\\d{3}-\\d{4}$");
            if (correct == false) {
                System.out.println("Invalid phone number format. Please enter a valid phone number in XXX-XXX-XXXX format.");
            }
        }
        StudentData studentData = new StudentData(dob, gender, "", phoneNumber);
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
        if (session == null) {
            throw new AccessViolationException("Session is null");
        }
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
        Integer size = this.accountDB.size();
        Boolean found = false;
        for (Account account : this.accountDB.values()) {
            size --;
            if (account.loginName.equals(loginName)) {
                found = true;
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
        if (found == false) {
            throw new AccessViolationException("Account not found");
        }
        
    }
 
    // check if the password meets the requirements
    boolean passwordCheck(Session session, String password) throws IOException {
        boolean test = password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$");
        if (test == true) {
            if (!password.matches(session.getAccount().password)) {
            return true;
            } else {
                throw new IOException("Password cannot be the same as the current password.");
            }
        }
        throw new IOException("Password does not meet the requirements.");
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
        Boolean test = false;
        while (!test) {
            try {
                passwordCheckInstruction();
                System.out.println("Enter a new password: ");
                password = main.getInput();
                test = passwordCheck(session, password);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        if (test) {
            session.getAccount().password = password;
            session.setHasModified();
            session.logout(true);
            return true;
        } else {
            return false;
        }
    }

    // check if you can change the login name
    static boolean changeLoginName(Session session, String newLoginName) throws AccessViolationException, ExpiredSessionException, IOException {
        try {
            if (session.getAccount().studentAccount == null && !(session.getAccount().role == Role.STUDENT)) {
                StudentAccount studentAccount = new StudentAccount(true);
                studentAccount.validateSessionForChange(session, true);
            } else {
                throw new AccessViolationException("Cannot change login name as a student.");
            }
        }  catch (AccessViolationException e) {
            throw new AccessViolationException("Access violation: " + e.getMessage());
        } catch (ExpiredSessionException e) {
            throw new ExpiredSessionException("Session expired: " + e.getMessage());
        }
        Map<Integer, Account> accountDB = session.getSessionManager().getAccountDatabase().getAccountDB();
        // test if login name is taken
        for (Account account : accountDB.values()) {
            if (account.loginName.equals(newLoginName)) {
                throw new IOException("Login name already exists: " + newLoginName);
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
            csvContent = loader.getResourceAsString(csvFileName);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            csvContent = null;
        }
        String[] lines;
        try {
            lines = csvContent.split(System.lineSeparator());
        } catch (NullPointerException e) {
            throw new IOException("Error loading accounts: " + e.getMessage());
        }
        ArrayList<Account> brokenAccounts = new ArrayList<>();

        for (String line : lines) {
            String[] fields = line.split(",");
            if (fields.length >= 5) {
                Account account;
                try {
                    account = new Account(Integer.parseInt(fields[0]), fields[1], fields[2], Role.valueOf(fields[3]), AccountStatus.valueOf(fields[4]));
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    throw new IOException(e.getMessage());
                }
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
                            input = "n";
                        } else {
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
            } else {
                throw new IOException("Invalid account data format. Please ensure the file is in the correct format.");
            }
        }
        
        if (brokenAccounts.size() > 0) {
            System.out.println("Let's fill in the missing student data.");
            accountDB = missingStudentAccount(brokenAccounts, accountDB);
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
            StudentData studentData = studentDataInput();
            StudentAccount student = new StudentAccount((String) studentData.getDob(), 
                                                        (Gender) studentData.getGender(),
                                                        (String) studentData.getPhoneNumber(), 
                                                        (String) studentData.getAcademicHistory(), 
                                                        account.userID);
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
            csvContent = loader.getResourceAsString(csvFileName);
            StringBuilder csvBuilder = new StringBuilder();
            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter(stringWriter);
            for (Account account : accountDB.values()) {
                try { 
                    StudentAccount studentAccount = account.getStudentAccount(session);
                    writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        account.userID, account.loginName, account.password, account.role, account.status, studentAccount.getDob(session), 
                        studentAccount.getGender(session), studentAccount.getAcademicHistory(session), studentAccount.getPhoneNumber(session)));
                } catch (NotStudentException e) { 
                    // Not a student account
                    writer.write(String.format("%d,%s,%s,%s,%s\n",
                        account.userID, account.loginName, account.password, account.role, account.status));
                } catch (NullPointerException e) {
                    // Broken student account, probably needs studentAccount
                    System.err.println("Error saving student account: " + e.getMessage() + "\n" + 
                                    "Offending Account: " + account.userID + " : " + account.loginName);
                    writer.write(String.format("%d,%s,%s,%s,%s\n",
                        account.userID, account.loginName, account.password, account.role, account.status));
                }
            }
            writer.close();
            String csvContent = csvBuilder.toString();
            FileWriter fw = new FileWriter(csvFileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(csvContent);
            bw.close(); 
            session.setHasModified(false); 
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            csvContent = null;
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

class StudentData {
    Gender gender;
    String dob;
    String phoneNumber;
    String academicHistory;

    public StudentData(String dob, Gender gender, String academicHistory, String phoneNumber) {
        this.dob = dob;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.academicHistory = academicHistory;
    }

    public Gender getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAcademicHistory() {
        return academicHistory;
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