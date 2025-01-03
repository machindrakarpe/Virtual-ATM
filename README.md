# Virtual ATM

## Description:

This project is a Java-based application that simulates ATM transactions like deposits, withdrawals, transfers, etc. It uses Gradle for build management.

## Requirements
Before you can build and run the application, ensure you have the following installed:


- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Gradle](https://gradle.org/install/)
- A terminal or command-line interface (CLI)

## Note: ```User  names are case sensitive```


Steps to run application 

- Go to project directory base 
- run ```chmod +x start.sh```
- run ```./start.sh```

### Sample example

```
login Alice
Hello, Alice!
Your balance is $0

deposit 100
Your balance is $100

logout
Goodbye, Alice

login Bob
Hello, Bob!
Your balance is $0

deposit 80
Your balance is $80

transfer Alice 50
Transferred $50 to Alice
Your balance is $30

transfer Alice 100
Transferred $30 to Alice
Your balance is $0
Owed $70 to Alice

deposit 30
Transferred $30 to Alice
Your balance is $0
Owed $40 to Alice

logout
Goodbye, Bob

login Alice
Hello, Alice!
Your balance is $210
Owed $40 from Bob

transfer Bob 30
Your balance is $210
Owed $10 from Bob

logout
Goodbye, Alice

login Bob
Hello, Bob!
Your balance is $0
Owed $10 to Alice

deposit 100
Transferred $10 to Alice
Your balance is $90

logout
Goodbye, Bob


```
