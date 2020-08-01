package util;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Account;

/**
 * @author viswa
 *
 *  utility class to handle Transaction between accounts
 */
public final class TransactionUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionUtil.class);
  
  /**
   * method to transfer amount between payer and payee accounts
   * @throws IllegalArgumentException if payer or payee is null
   * @throws IllegalArgumentException if transfer amount is negative value
   * 
   * Transfer amount after checking the credits and debits between the accounts.
   * Uses Account objects OwesTo and OwesFrom attributes to check and do credit and debit transfers
   * 
   * @param payer
   * @param payee
   * @param amount
   */
  public static void transferAmount(Account payer, Account payee, long amount) {
  if (payer == null || payee == null) {
    throw new IllegalArgumentException("Payer or Payee not available in the Transaction.");
  }
  if (amount < 0) {
    throw new IllegalArgumentException("Payment Amount cannot be less than 0.");
  }
  String payerName = payer.getName();
  String payeeName = payee.getName();
  LOGGER.info("Transferring amount=[{}] to payee=[{}] from payer=[{}].", amount, payeeName, payerName);
  long payerBalance = payer.getBalance();
  long payeeBalance = payee.getBalance();
  LOGGER.info("Adjusting Debits/Credits between payee=[{}] and payer=[{}].", payeeName, payerName);
  if (payee.doesOwesTo(payerName)) {
    LOGGER.info("Payee=[{}] owes to Payer=[{}]. Recalculating debits and credits.", payeeName, payerName);
    long payeeAlreadyOwes = payee.getOwesTo().get(payerName);
    if (payeeAlreadyOwes <= amount) {
      LOGGER.info("Payer=[{}] pays Payee=[{}] from credits.", payerName, payeeName);
      payee.getOwesTo().remove(payerName);
      payer.getOwesFrom().remove(payeeName);
    } else {
      LOGGER.info("Adjusting Payee=[{}] and Payer=[{}] credits.", payeeName, payerName);
      payee.addOwesTo(payerName, -amount);
      payer.addOwesFrom(payeeName, -amount);
    }
    amount = amount - payeeAlreadyOwes;
  }
  if( amount <= 0) {
    LOGGER.info("Transaction processed between payee=[{}] and payer=[{}].", payerName, payeeName);
    return;
  }
  if (amount <= payerBalance) {
    LOGGER.info("Having sufficient amount to pay the user=[{}].", payeeName);
    payerBalance -= amount;
    payeeBalance += amount;
  } else {
    long deficit = amount - payerBalance;
    payeeBalance += payerBalance;
    payerBalance = 0;
    payer.addOwesTo(payeeName, deficit);
    payee.addOwesFrom(payerName, deficit);
  }
  payer.setBalance(payerBalance);
  payee.setBalance(payeeBalance);
  LOGGER.info("Transaction Completed.");
  }
  
  /**
   * method to adjust credit and debit values between payer and payee accounts
   * @throws IllegalArgumentException if payer or payee is null
   * @throws IllegalArgumentException if transfer amount is negative value
   * 
   * Uses Account object's OwesTo and OwesFrom attributes to check and do credit and debit adjustments
   * 
   * @param payer
   * @param payee
   * @param amount
   * @param iterator
   * @return adjusted amount value
   */
  public static long adjustDebitsAndCredits(Account payer, Account payee, long amount, Iterator<Entry<String, Long>> iterator) {
    if (payer == null || payee == null) {
      throw new IllegalArgumentException("Payer or Payee not available in the Transaction.");
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Payment Amount cannot be less than 0.");
    }
    String payerName = payer.getName();
    String payeeName = payee.getName();
    LOGGER.info("Adjusting Debits/Credits between payee=[{}] and payer=[{}].", payeeName, payerName);
    long payerAlreadyOwes = payer.getOwesTo().get(payeeName);
    if (payerAlreadyOwes <= amount) {
      LOGGER.info("Payer=[{}] pays Payee=[{}] from credits.", payerName, payeeName);
      amount = amount - payerAlreadyOwes;
      iterator.remove();
      payee.getOwesFrom().remove(payerName);
      payee.setBalance(payee.getBalance() + payerAlreadyOwes);
    } else {
      LOGGER.info("Adjusting Payee=[{}] and Payer=[{}] credits.", payeeName, payerName);
      payer.addOwesTo(payeeName, -amount);
      payee.addOwesFrom(payerName, -amount);
      payee.setBalance(payee.getBalance() + amount);
      amount = amount - payerAlreadyOwes;
    }
    return Math.max(amount, 0);
  }
}