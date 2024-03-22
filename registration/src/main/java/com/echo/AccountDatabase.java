package com.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AccountDatabase {
    Map<Integer, Account> accounts = new HashMap<>();                   // Initialize to empty map


    public AccountDatabase(InputStream inputStream) {                   // Constructor
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }
        try {
            accounts = loadAccounts(inputStream); // Pass the inputStream directly
        } catch (IOException e) {
            // ... handle exception (consider rethrowing or logging)
        }
    }

    public boolean checkCredentials(int userID, String loginName, 
                                    String password, Role role) {       // Method to check credentials
        Account account = accounts.get(userID);
        if (account == null || !account.loginName.equals(loginName) || 
            !account.password.equals(password) || !account.role.equals(role) || 
            account.status.equals("blocked")) { 
            return false;
        }
        return true; 
    }

    public void block(int userID) throws AccountNotFoundException {     // Method to block account
        Account account = accounts.get(userID);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        account.status = "blocked";
        saveAccounts(); // Update the CSV
    }
    
    public void unblock(int userID) throws AccountNotFoundException {   // Method to unblock account
        Account account = accounts.get(userID);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for ID: " + userID);
        }
        account.status = "active";
        saveAccounts(); // Update the CSV
    }

    public Map<Integer, Account> loadAccounts(InputStream inputStream) throws IOException { // Method to load accounts
        accounts.clear();

    
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {  // Use the provided inputStream
    
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    Account account = new Account();
                    account.userID = Integer.parseInt(fields[0]);
                    account.loginName = fields[1];
                    account.password = fields[2];
                    account.role = Role.valueOf(fields[3]);
                    account.status = fields[4];
                    accounts.put(account.userID, account);
                } 
            }
        } catch (IOException e) {   // Catch IOException                              
            System.err.println("Error loading accounts: " + e.getMessage());
        } 
        return accounts;
    }
    private String getResourcePath(String filename) {   // Method to get the absolute path         
        URL url = getClass().getClassLoader().getResource(filename);
        if (url == null) {
            throw new RuntimeException("Resource not found: " + filename);
        }
        return new File(url.getFile()).getAbsolutePath();
    }
    
    
    public void saveAccounts() {    // Method to save accounts
        String filePath = getResourcePath("MOCK_DATA.csv");     // Get the absolute path
        System.out.println("Absolute Path: " + new File(filePath).getAbsolutePath());
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw)) {
                for (Account account : accounts.values()) {
                    String line = String.format("%d,%s,%s,%s,%s\n",
                            account.userID, account.loginName, account.password, account.role, account.status);
                    bw.write(line);
                }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        } 
    }

    public void reloadAccounts() throws IOException {   // Method to reload accounts
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("MOCK_DATA.csv"); 
        if (inputStream == null) {
            throw new FileNotFoundException("MOCK_DATA.csv not found in test resources");
        }
        accounts = loadAccounts(inputStream); 
    }
    
    
}

class Account {    // Account class
    int userID;
    String loginName;
    String password;
    Role role;
    String status; 
}

enum Role {  // public Role enum
    STUDENT,
    ADMIN,
    ADVISOR;
}