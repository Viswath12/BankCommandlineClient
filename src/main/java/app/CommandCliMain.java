package app;

import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enums.CommandAction;
import service.AccountService;

/**
 * @author viswa
 * 
 * Main class of this application
 */
public class CommandCliMain {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandCliMain.class);
  
  private static final AccountService ACCOUNT_SERVICE = AccountService.getInstance();
  
  /**
   * main method that reads input from CommandLine.
   * User can type in "exit" in commandLine to quit this application  
   * @param args
   */
  public static void main(String[] args) {
    LOGGER.info("===>Welcome to Retail Bank<===");
    LOGGER.info("Login to do Banking.");
    try(Scanner in = new Scanner(System.in)) {
      String input = "";
      while (!CommandAction.EXIT.getCommand().equals(input)) {
        input = in.nextLine();
        if (StringUtils.isBlank(input)) {
          LOGGER.error("Input command is null/empty");
          continue;
        }
        String[] commands = input.trim().split("\\s+");
        if (commands == null) {
          LOGGER.error("Commands not entered.");
        }
        CommandAction action = CommandAction.fromCommand(commands[0]);
        if (action == null) {
          LOGGER.error("Not a correct command=[{}]. Please enter again.", commands[0]);
        } else {
          doAction(action, commands);
        }
      }
    }
    LOGGER.info("Exiting, Thanks for using the application.");
  }
  
  /**
   * perform the correcponding action based on the first word in the command
   * @param action
   * @param commands
   */
  private static void doAction(CommandAction action, String[] commands) {
    LOGGER.info("Commands received. Processing.");
    try {
      switch (action)
      {
        case LOGIN:
          login(commands);
          break;
        case TOPUP:
          topup(commands);
          break;
        case PAY:
          pay(commands);
          break;
        default:
          LOGGER.info("Not a valid command");
          break;
      }
    } catch (Exception e) {
      LOGGER.error("There is some exception in processing request. Please try again.");
      LOGGER.error("Exception=[{}].", e.getMessage());
    }
  }
  
  /**
   * performs the login action
   * @param commands
   */
  private static void login(String[] commands) {
    LOGGER.info("Logging In.");
    if (commands.length < 2) {
      LOGGER.error("Not enough commands to execute an action.");
      return;
    }
    String name = commands[1];
    ACCOUNT_SERVICE.loginUser(name);
  }
  
  /**
   * performs the topup action
   * @param commands
   */
  private static void topup(String[] commands) {
    LOGGER.info("Topping up.");
    if (commands.length < 2) {
      LOGGER.error("Not enough commands to execute an action.");
      return;
    }
    String value = commands[1];
    long amount = 0;
    try {
      amount = Long.parseLong(value);
    } catch (NumberFormatException e) {
      LOGGER.error("Input is not a valid Number=[{}].", value);
      LOGGER.error("Exception=[{}].", e.getMessage());
      return;
    }
    ACCOUNT_SERVICE.topupBalance(amount);
    LOGGER.info("Topup action completed.");
  }
  
  /**
   * performs the payment/transfer action
   * @param commands
   */
  private static void pay(String[] commands) {
    LOGGER.info("Paying.");
    if (commands.length < 3) {
      LOGGER.error("Not enough commands to execute an action.");
      return;
    }
    String name = commands[1];
    String value = commands[2];
    long amount = 0;
    try {
      amount = Long.parseLong(value);
    } catch (NumberFormatException e) {
      LOGGER.error("Input is not a valid Number=[{}].", value);
      LOGGER.error("Exception=[{}].", e.getMessage());
      return;
    }
    ACCOUNT_SERVICE.transferAmount(name, amount);
    LOGGER.info("Payment action completed.");
  }
}