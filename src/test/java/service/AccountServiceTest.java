package service;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import domain.Account;

@TestMethodOrder(OrderAnnotation.class)
public class AccountServiceTest {
  
  private AccountService ACCOUNT_SERVICE;
  private final static String TEST_NAME = "Test";
  private final static long TEST_BALANCE = 100L;
  
  @BeforeEach
  public void init() {
    ACCOUNT_SERVICE = AccountService.getInstance();
  }
  
  @AfterEach
  public void cleanUp() {
    ACCOUNT_SERVICE.clearAccounts();
  }

  @Test
  @Order(1)
  public void testGetAccount() {
    String name = "Bob";
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(name.equals(account.getName()));
  }
  
  @Test
  @Order(2)
  public void testGetAccountEmpty() {
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(TEST_NAME);
    assertTrue(optAccount.isEmpty());
  }
  
  @Test
  @Order(3)
  public void testAddAccount() {
    ACCOUNT_SERVICE.addAccount(TEST_NAME, TEST_BALANCE);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(TEST_NAME);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(TEST_NAME.equals(account.getName()));
    assertTrue(TEST_BALANCE == account.getBalance());
  }
  
  @Test
  @Order(4)
  public void testAddAccountWithNoName() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.addAccount(null, TEST_BALANCE));
  }
  
  @Test
  @Order(5)
  public void testAddAccountWithEmptyName() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.addAccount("", TEST_BALANCE));
  }
  
  @Test
  @Order(6)
  public void testLoginUserWithNoName() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.loginUser(null));
  }
  
  @Test
  @Order(7)
  public void testLoginUserWithEmptyName() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.loginUser(""));
  }
  
  @Test
  @Order(8)
  public void testLoginUserWithNewName() {
    String name = "Test2";
    ACCOUNT_SERVICE.loginUser(name);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0L == account.getBalance());
  }
  
  @Test
  @Order(9)
  public void testTopupBalance() {
    ACCOUNT_SERVICE.addAccount(TEST_NAME, TEST_BALANCE);
    ACCOUNT_SERVICE.loginUser(TEST_NAME);
    ACCOUNT_SERVICE.topupBalance(TEST_BALANCE);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(TEST_NAME);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(TEST_NAME.equals(account.getName()));
    assertTrue((TEST_BALANCE+TEST_BALANCE) == account.getBalance());
  }
  
  @Test
  @Order(10)
  public void testTopupBalanceWithIllegalCredit() {
    ACCOUNT_SERVICE.addAccount(TEST_NAME, TEST_BALANCE);
    String payeeName = "Test3";
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(TEST_NAME);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    account.addOwesTo(payeeName, TEST_BALANCE);
    ACCOUNT_SERVICE.loginUser(TEST_NAME);
    Assertions.assertThrows(IllegalStateException.class, () -> ACCOUNT_SERVICE.topupBalance(TEST_BALANCE));
  }
  
  @Test
  @Order(11)
  public void testTopupBalanceLessCredit() {
    String name = "Bob";
    String payeeName = "Alice";
    long credit = 50L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccount.get();
    account.addOwesTo(payeeName, credit);
    accountPayee.addOwesFrom(name, credit); 
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.topupBalance(TEST_BALANCE);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(credit == account.getBalance());
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(credit == account.getBalance());
  }
  
  @Test
  @Order(12)
  public void testTopupBalanceEqualCredit() {
    String name = "Bob";
    String payeeName = "Alice";
    long credit = 100L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccount.get();
    account.addOwesTo(payeeName, credit);
    accountPayee.addOwesFrom(name, credit); 
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.topupBalance(TEST_BALANCE);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0L == account.getBalance());
    assertTrue(!account.doesOwesTo(payeeName));
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(credit == account.getBalance());
    assertTrue(!account.doesOwesFrom(name));
  }
  
  @Test
  @Order(13)
  public void testTopupBalanceMoreCredit() {
    String name = "Bob";
    String payeeName = "Alice";
    long credit = 200L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccountPayee.get();
    account.addOwesTo(payeeName, credit);
    accountPayee.addOwesFrom(name, credit); 
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.topupBalance(TEST_BALANCE);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0L == account.getBalance());
    assertTrue(account.doesOwesTo(payeeName));
    assertTrue(account.getOwesTo().get(payeeName) == TEST_BALANCE);
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(TEST_BALANCE == account.getBalance());
    assertTrue(account.doesOwesFrom(name));
    assertTrue(account.getOwesFrom().get(name) == TEST_BALANCE);
  }
  
  @Test
  @Order(14)
  public void testTransferAmountNoName() {
    ACCOUNT_SERVICE.addAccount(TEST_NAME, TEST_BALANCE);
    ACCOUNT_SERVICE.loginUser(TEST_NAME);
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.transferAmount(null, TEST_BALANCE));
  }
  
  @Test
  @Order(15)
  public void testTransferAmountEmptyName() {
    ACCOUNT_SERVICE.addAccount(TEST_NAME, TEST_BALANCE);
    ACCOUNT_SERVICE.loginUser(TEST_NAME);
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.transferAmount("", TEST_BALANCE));
  }
  
  @Test
  @Order(16)
  public void testTransferAmountIncorrectAmount() {
    String name = "Bob";
    ACCOUNT_SERVICE.addAccount(TEST_NAME, TEST_BALANCE);
    ACCOUNT_SERVICE.addAccount(name, TEST_BALANCE);
    ACCOUNT_SERVICE.loginUser(TEST_NAME);
    Assertions.assertThrows(IllegalArgumentException.class, () -> ACCOUNT_SERVICE.transferAmount(name, -TEST_BALANCE));
  }
  
  @Test
  @Order(17)
  public void testTransferAmountNoOwesLessAmount() {
    String name = "Bob";
    String payeeName = "Alice";
    long transfer = 50L;
    ACCOUNT_SERVICE.addAccount(name, TEST_BALANCE);
    ACCOUNT_SERVICE.addAccount(payeeName, TEST_BALANCE); 
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, transfer);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(transfer == account.getBalance());
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue((transfer + TEST_BALANCE) == account.getBalance());
  }
  
  @Test
  @Order(18)
  public void testTransferAmountNoOwesEqualAmount() {
    String name = "Bob";
    String payeeName = "Alice";
    ACCOUNT_SERVICE.addAccount(name, TEST_BALANCE);
    ACCOUNT_SERVICE.addAccount(payeeName, TEST_BALANCE); 
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, TEST_BALANCE);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0L == account.getBalance());
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue((TEST_BALANCE + TEST_BALANCE) == account.getBalance());
  }
  
  @Test
  @Order(19)
  public void testTransferAmountNoOwesMoreAmount() {
    String name = "Bob";
    String payeeName = "Alice";
    long transfer = 200L;
    ACCOUNT_SERVICE.addAccount(name, TEST_BALANCE);
    ACCOUNT_SERVICE.addAccount(payeeName, TEST_BALANCE); 
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, transfer);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0L == account.getBalance());
    assertTrue(account.doesOwesTo(payeeName));
    assertTrue(account.getOwesTo().get(payeeName) == TEST_BALANCE);
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue((TEST_BALANCE + TEST_BALANCE) == account.getBalance());
    assertTrue(account.doesOwesFrom(name));
    assertTrue(account.getOwesFrom().get(name) == TEST_BALANCE);
  }
  
  @Test
  @Order(20)
  public void testTransferAmountWithPayeeOwesLess() {
    String name = "Bob";
    String payeeName = "Alice";
    long transfer = 50L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccountPayee.get();
    account.addOwesFrom(payeeName, (transfer/2));
    accountPayee.addOwesTo(name, (transfer/2));
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, transfer);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(account.doesOwesTo(payeeName));
    assertTrue(account.getOwesTo().get(payeeName) == (transfer/2));
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(account.doesOwesFrom(name));
    assertTrue(account.getOwesFrom().get(name) == (transfer/2));
  }
  
  @Test
  @Order(21)
  public void testTransferAmountWithPayeeOwes() {
    String name = "Bob";
    String payeeName = "Alice";
    long transfer = 50L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccountPayee.get();
    account.addOwesFrom(payeeName, transfer);
    accountPayee.addOwesTo(name, transfer);
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, transfer);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(!account.doesOwesFrom(payeeName));
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(!account.doesOwesTo(name));
  }
  
  @Test
  @Order(22)
  public void testTransferAmountWithPayeeOwesMore() {
    String name = "Bob";
    String payeeName = "Alice";
    long transfer = 50L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccountPayee.get();
    account.addOwesFrom(payeeName, (2 * transfer));
    accountPayee.addOwesTo(name, (2 * transfer));
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, transfer);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(account.doesOwesFrom(payeeName));
    assertTrue(account.getOwesFrom().get(payeeName) == transfer);
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(account.doesOwesTo(name));
    assertTrue(account.getOwesTo().get(name) == transfer);
  }
  
  @Test
  @Order(23)
  public void testTransferAmountWithPayerOwesToPayee() {
    String name = "Bob";
    String payeeName = "Alice";
    long transfer = 50L;
    ACCOUNT_SERVICE.addAccount(name, 0);
    ACCOUNT_SERVICE.addAccount(payeeName, 0);
    Optional<Account> optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    Account account = optAccount.get();
    Optional<Account> optAccountPayee = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccountPayee.isPresent());
    Account accountPayee = optAccountPayee.get();
    account.addOwesTo(payeeName, transfer);
    accountPayee.addOwesFrom(name, transfer);
    ACCOUNT_SERVICE.loginUser(name);
    ACCOUNT_SERVICE.transferAmount(payeeName, transfer);
    optAccount = ACCOUNT_SERVICE.getAccount(name);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(name.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(account.doesOwesTo(payeeName));
    assertTrue(account.getOwesTo().get(payeeName) == (2 * transfer));
    optAccount = ACCOUNT_SERVICE.getAccount(payeeName);
    assertTrue(optAccount.isPresent());
    account = optAccount.get();
    assertTrue(payeeName.equals(account.getName()));
    assertTrue(0 == account.getBalance());
    assertTrue(account.doesOwesFrom(name));
    assertTrue(account.getOwesFrom().get(name) == (2 * transfer));
  }
}