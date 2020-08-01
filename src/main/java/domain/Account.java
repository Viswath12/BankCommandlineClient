package domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author viswa
 * 
 * doamin class for Account information
 */
public class Account {
  
  private String name;
  private long balance;
  private Map<String, Long> owesTo;
  private Map<String, Long> owesFrom;
  
  /**
   * constructor for Account. Sets name from input param
   * @throws IllegalArgumentException if input command is null/empty
   * @param name
   */
  public Account(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Account.Name cannot be null/empty");
    }
    this.name = name;
    this.owesTo = new HashMap<>();
    this.owesFrom = new HashMap<>();
  }
  
  public String getName() {
    return name;
  }
  public Account setName(String name) {
    this.name = name;
    return this;
  }
  
  public long getBalance() {
    return balance;
  }
  public Account setBalance(long balance) {
    this.balance = balance;
    return this;
  }
  
  public Map<String, Long> getOwesTo() {
    return owesTo;
  }
  public Account setOwesTo(Map<String, Long> owes) {
    this.owesTo = owes;
    return this;
  }
  
  /**
   * adds given name and amount value to the OwesTo map of this account
   * @param name
   * @param amount
   * @return account object
   */
  public Account addOwesTo(String name, Long amount) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input Name cannot be null/empty");
    }
    if (amount == null) {
      throw new IllegalArgumentException("Owning amount cannot be null.");
    }
    if (this.getOwesTo().containsKey(name)) {
      Long currentOwes = this.getOwesTo().get(name);
      this.getOwesTo().put(name, (currentOwes + amount));
    } else {
      this.getOwesTo().put(name, amount);
    }
    return this;
  }
  
  /**
   * checks if given name is present in the OwesTo map
   * @param name
   * @return true if present else false
   */
  public boolean doesOwesTo(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input Name cannot be null/empty");
    }
    return this.getOwesTo().containsKey(name);
  }
  
  public Map<String, Long> getOwesFrom() {
    return owesFrom;
  }

  public Account setOwesFrom(Map<String, Long> owesFrom) {
    this.owesFrom = owesFrom;
    return this;
  }
  
  /**
   * adds given name and amount value to the OwesFrom map of this account
   * @param name
   * @param amount
   * @return account object
   */
  public Account addOwesFrom(String name, Long amount) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Name cannot be null/empty");
    }
    if (amount == null) {
      throw new IllegalArgumentException("Owning amount cannot be null.");
    }
    if (this.getOwesFrom().containsKey(name)) {
      Long currentOwes = this.getOwesFrom().get(name);
      this.getOwesFrom().put(name, (currentOwes + amount));
    } else {
      this.getOwesFrom().put(name, amount);
    }
    return this;
  }
  
  /**
   * checks if given name is present in the OwesFrom map
   * @param name
   * @return true if present else false
   */
  public boolean doesOwesFrom(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Input Name cannot be null/empty");
    }
    return this.getOwesFrom().containsKey(name);
  }

  /**
   * hashcode method uses only name attribute 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    return result;
  }

  /**
   * equal method compares only name attribute
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Account other = (Account) obj;
    if (getName() == null) {
      return false;
    } else if (!getName().equals(other.getName())) {
      return false;
    }
    return true;
  }

  /**
   * prints readable form of Account object, displays only name and balance
   */
  @Override
  public String toString() {
    return String.format("[Name=[%s] Balance=[%s]]", getName(), getBalance());
  }
}