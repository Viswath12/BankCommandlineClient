package service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Account;
import util.TransactionUtil;

/**
 * @author viswa
 * 
 * Service class to to operations on account objects.
 * this is a Singleton class so only one instance available per instance of application
 * stores account information in the private instance field
 * stores current user information in the private instance field
 * 
 */
public final class AccountService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
  
  /*
   * instance field to store account informations by user name to account data map
   */
  private final Map<String, Account> userAccounts;
  /*
   * instance field to store current user information
   */
  private Account currentUser;
  
  /**
   * private constructor for this class
   * calls private method getInitialAccounts to setup few intial account infos
   */
  private AccountService() {
    userAccounts = getInitialAccounts();
  }
  
  /**
   * @author viswa
   *
   * inner static class to get the singleton instance of the enclosing class
   * access modifier is private to restrict the other clients using this class
   */
  private static class AccountServiceSingletonHelper {
    /*
     * private static field to initialize the AccountService instance
     */
    private static final AccountService SERVICE_INSTANCE = new AccountService();
  }
  
  /**
   * method to get singleton instance of this class
   * @return the singleton instance of this class
   */
  public static AccountService getInstance() {
    return AccountServiceSingletonHelper.SERVICE_INSTANCE;
  }
  
  /**
   * method used to do initial setup for accounts
   * @return map couple of initial userAccounts, with name as key and account as value 
   */
  private Map<String, Account> getInitialAccounts() {
    Map<String, Account> userAccounts = new HashMap<String, Account>();
    userAccounts.put("Alice", new Account("Alice").setBalance(0L));
    userAccounts.put("Bob", new Account("Bob").setBalance(0L));
    return userAccounts;
  }
  
  /**
   * method to clear existing account information and loggedIn user
   */
  public void clearAccounts() {
    this.userAccounts.clear();
    this.currentUser = null;
  }
  
  /**
   * get user account by user name
   * @param name
   * @return An Optional enclosing Account object for the matching user name,
   * returns empty Optional if no match found
   */
  public Optional<Account> getAccount(String name) {
    LOGGER.info("Getting User Account by Name=[{}].", name);
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input name is null/empty");
    }
    Account account = userAccounts.get(name);
    LOGGER.info("Got User Account=[{}] by Name=[{}].", account, name);
    return Optional.ofNullable(account);
  }
  
  /**
   * adds account created based on given name and balance values to the UserAccounts
   * @throws IllegalArgumentException if input name is null or empty 
   * @param name
   * @param balance
   */
  public void addAccount(String name, long balance) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input name is null/empty");
    }
    LOGGER.info("Creating User Account with Name=[{}] Balance=[{}].", name, balance);
    Account account = new Account(name).setBalance(balance);
    this.userAccounts.put(name, account);
    LOGGER.info("Successfully created User Account with Name=[{}] Balance=[{}].", name, balance);
  }
  
  /**
   * method to log in the user based on given name
   * if account not already exists for the name, then creates new account with default balance value 
   * @throws IllegalArgumentException if input name is null or empty
   * @param name
   */
  public void loginUser(String name) {
    LOGGER.info("Trying to login user with name=[{}].", name);
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input name is null/empty");
    }
    Optional<Account> loginUser = this.getAccount(name);
    if (loginUser.isEmpty()) {
      LOGGER.warn("Creating user since Account not exists for the user. name=[{}].", name);
      this.addAccount(name, 0);
      currentUser = this.getAccount(name).get();
    } else {
      LOGGER.info("Account already exists for the user. name=[{}].", name);
      currentUser = loginUser.get();
    }
    LOGGER.info("Hello, [{}].", currentUser.getName());
    printCurrentUserBalance();
    printCurrentUserOwesTo();
    printCurrentUserOwesFrom();
  }
  
  /**
   * method to top up balance for the current user
   * the user should be already loggedIn for successful completion
   * @throws IllegalArgumentException if top up amount is negative value
   * this method also do credit/debit adjustments after top up 
   * @param amount
   */
  public void topupBalance(long amount) {
    LOGGER.info("Topping up balance for the current user. Amount=[{}].", amount);
    if (!isLoggedIn()) {
      LOGGER.error("User not logged in. Please login first");
      return;
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Amount cannot be less than 0.");
    }
    long prevBalance = currentUser.getBalance();
    currentUser.setBalance(amount + prevBalance);
    checkCurrentUserOwesToAndPay();
    printCurrentUserBalance();
    printCurrentUserOwesTo();
    printCurrentUserOwesFrom();
  }
  
  /**
   * method to transfer amount based on the given name
   * the user should be already loggedIn for successful completion
   * the name also should have corresponding account information
   * @throws IllegalArgumentException if input name is null or empty
   * @throws IllegalArgumentException if transfer amount is negative value
   * this method also takes into account of credit/debit between current and target accounts
   * @param name 
   * @param amount
   */
  public void transferAmount(String name, long amount) {
    LOGGER.info("Transferring amount=[{}] to the user=[{}].", amount, name);
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input name is null/empty");
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Paying amount cannot be less than 0.");
    }
    if (!isLoggedIn()) {
      LOGGER.error("User not logged in. Please login first");
      return;
    }
    Optional<Account> payee = this.getAccount(name);
    if (payee.isEmpty()) {
      LOGGER.error("Not a valid payee.");
      return;
    }
    TransactionUtil.transferAmount(currentUser, payee.get(), amount);
    LOGGER.info("Transferred amount=[{}] to the user=[{}].", amount, name);
    printCurrentUserBalance();
    printCurrentUserOwesTo();
    printCurrentUserOwesFrom();
  }
  
  /**
   * method to check the credit/debit details of current user account and do adjustments based on balance value
   */
  private void checkCurrentUserOwesToAndPay() {
    long balance = this.currentUser.getBalance();
    if (balance <= 0 || this.currentUser.getOwesTo() == null || this.currentUser.getOwesTo().isEmpty()) {
      return;
    }
    Iterator<Map.Entry<String, Long>> iterator = this.currentUser.getOwesTo().entrySet().iterator();
    while(balance > 0 && iterator.hasNext()) {
      Map.Entry<String, Long> entry = iterator.next();
      Optional<Account> payee = this.getAccount(entry.getKey());
      if (payee.isEmpty()) {
        throw new IllegalStateException("Payee Accout not exists.");
      }
      balance = TransactionUtil.adjustDebitsAndCredits(currentUser, payee.get(), balance, iterator);
      this.currentUser.setBalance(balance);
    }
  }
  
  /**
   * method to check if user is logged in
   * @return true if already loggedIn else false
   */
  private boolean isLoggedIn() {
    boolean isLoggedIn = false;
    if (currentUser != null) {
      isLoggedIn = true;
    }
    return isLoggedIn;
  }
  
  /**
   * method to print the current user balance
   * the user should be already loggedIn for successful completion
   */
  private void printCurrentUserBalance() {
    if (!isLoggedIn()) {
      LOGGER.error("User not logged in. Please login first");
      return;
    }
    LOGGER.info("Your balance is [{}].", currentUser.getBalance());
  }
  
  /**
   * method to print the current user credits
   * the user should be already loggedIn for successful completion
   * prints nothing if not credit info
   */
  private void printCurrentUserOwesTo() {
    if (!isLoggedIn()) {
      LOGGER.error("User not logged in. Please login first");
      return;
    }
    Map<String, Long> currentUserOwesTo = currentUser.getOwesTo();
    if (currentUserOwesTo == null || currentUserOwesTo.isEmpty()) {
      return;
    }
    currentUserOwesTo.forEach( (key, value) -> LOGGER.info("Owing [{}] to [{}].", value, key));
  }
  
  /**
   * method to print the current user debits
   * the user should be already loggedIn for successful completion
   * prints nothing if not debit info
   */
  private void printCurrentUserOwesFrom() {
    if (!isLoggedIn()) {
      LOGGER.error("User not logged in. Please login first");
      return;
    }
    Map<String, Long> currentUserOwesFrom = currentUser.getOwesFrom();
    if (currentUserOwesFrom == null || currentUserOwesFrom.isEmpty()) {
      return;
    }
    currentUserOwesFrom.forEach( (key, value) -> LOGGER.info("Owing [{}] from [{}].", value, key));
  }
}