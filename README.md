# Banking System - Part B

This version keeps the main ideas from Part A and implements the banking system with straightforward Java.

## Main Java concepts used
- Classes and objects
- Constructors
- Encapsulation using private fields and getters
- Inheritance using `Account` and its three account types
- One abstract class
- Two simple interfaces
- Method overloading for deposits
- Method overriding for account types
- Basic polymorphism using `Account`, `Withdrawable` and `InterestBearing`
- ArrayList collections
- if/else statements and loops
- Basic exception handling
- JDBC for the H2 database
- JavaFX for the user interface

## Project structure

`bank.model` contains the banking classes.

`bank.dao` contains the SQL/database classes.

`bank.controller` connects the user interface to the model and database.

`bank.view` contains the JavaFX screens.

`data/bankdb.mv.db` is created automatically when the program first runs.

## Running

1. Open the project in IntelliJ IDEA.
2. Make sure Java 17 is selected.
3. Reload Maven.
4. Run `bank.Launcher`.
5. The first run creates the database tables and sample data.

Default logins:

- Manager: `manager1` / `manager123`
- Teller: `teller1` / `teller123`
- Customer: `cust1` / `cust123`
