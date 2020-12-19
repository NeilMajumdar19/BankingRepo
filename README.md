# BankingRepo

Schema Definition
P1.Customer (ID, Name, Gender, Age, Pin)
P1.Account (Number, ID, Balance, Type, Status)

ID:integer(system generate starting from 100)- 
Name: varchar(15)
Gender: char(must be M or F)
Age: integer(>= 0)
Pin: integer(>= 0) 
Number: integer(system generate starting from 1000) 
Balance: integer(>= 0) 
Type: char(C for Checking, S for Saving) 
Status: char(A for Active, I for Inactive)

Java methods implemented
public static void newCustomer(String name, String gender, String age, String pin) 
-Creates a new customer in P1.Customer

public static void openAccount(String id, String type, String amount) 
-Creates a new account for a customer in P1.Account

public static void closeAccount(String accNum) 
-Sets account to inactive, sets balance to 0
-does not delete account from P1.Account

public static void deposit(String accNum, String amount) 
-adds balance to account

public static void withdraw(String accNum, String amount) 
-removes balance from account

public static void transfer(String srcAccNum, String destAccNum, String amount) 
-transfer amount from one account to another

public static void accountSummary(String cusID) 
-displays accounts for customer

public static void reportA() 
-displays total balance for each customer

public static void reportB(String min, String max)
-displays average balance for customers between min and max age
