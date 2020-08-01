# BankCommandlineClient
A sample retail bank command line application to simulate interaction with a retail bank.

Assumptions and Restrictions

1. The application is command line application and accepts inputs from standard input only. No other form of input will be accepted (like REST call etc).
2. The application only supports the mentioned commands (with the exception of additional "exit" command). Adding support to new commands involves code change.
3. The application produces some additional outputs from the ones stated in question, also the output format might be differ slightly, but all the required output are displayed.

Since this is a basic command line application and supports only basic opration, the below are the some of the important restrictions.

1. This application is intended to run in single thread only and therefore thread safety is not considered. Any attempt to run this application in multithreaded environment will cause unpredictable results.
2. This application doesnot persist any data. All the data processing is done in application memory. Once the application is closed, all its data is lost and the applicaiton will go back to its initial state after restart.
3. Since it is processing data from application memory, this app doesnot intended for processing large amount of data and it may cause out of memory errors if tried to do so.


Application Components

Classes
1. Account - Domain class for the account information
2. AccountService - Singleton class to process login and other account actions
3. TransactionUtil - utility class to do transaction between accounts - Used in AccountService class
4. CommandlineClientMain - Main class which starts the application and also accepts input and process the input commands till appliciation exits
Enum
1. CommandAction - Enum constants for allowed actions

External dependencies used
1. Logback-Classis - for logging
2. Apache Commons-Lang3 - for utilities
3. Junit - for unit testing


