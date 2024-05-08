# echo-345
SUFFOLK U - 24SP - CMPSC - F345 - A - SOFTWARE ENGINEERING - ECHO

Last info update: 20 Jan 2024

## Group Members
- Boucher, Jack
- Sabrina Quadir
- Lucas Kay

## Getting started

To run: 
java -jar registration.jar

Test data is provided for the system. The intended final system would contain a different database system. However, for now it is a plaintext CSV file. passwords are also currently plaintext, which would need to be modified, of course. A few menus aren't implemented yet, as well as many features. However, the provided features are sufficient for the assignments given. This is, by no means, a final product. Additionally, the test data provided is not purposefully valid for any real world use, for obvious reasons. 

Please be advised that, as of now, the Account page is the only functional testing page in system. 

### How to build

In the event the prepackaged jar is unable to run, you will need to install maven and package it. follow the below steps. 

mvn is required to build. 
apt install maven

in registration/ run mvn package.
then, run java -jar registration/registration-1.0-jar-with-dependencies.jar

### Accounts
Currently, there is a working admin account in MOCK_DATA.csv, the following are useable accounts to access the system. In order to access testing, you will need to login to the admin menu.

Administrator account:
username: admin
password: password

Student Account
username: gstudent
password: test

Advisor account:
username: advisor
password: test



## Event Log
20 JAN - JB -   INITIAL COMMIT\
22 JAN - SQ -   INITIAL COMMIT\
30 Jan - LK -   INITIAL COMMIT\
30 Jan - JB -   UPDATED REPO TITLE\
30 Jan - JB -   Posted updated RSD plan and moscow plan doc\
20 Feb - JB -   Posted Use Case diagram\
20 Feb - LK -   Posted UML Diagram\
21 Feb - JB -   Posted Horizontal plan\
18 Mar - JB -   Created maven java project "registration".\
--- Added JUnit.\
--- Added mock accountDatabase csv as 'MOCK_DATA.csv' inside test\/resources. There is a second, larger file with more data points.\
--- created AccountDatabase, AccountNotFoundException, Role, AccountDatabaseTest java files.\
--- Main.java created with maven setup, includes a simple hello world, for now.