package enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author viswa
 * 
 * enum constants for the predefined user actions
 */
public enum CommandAction {
  
  LOGIN("login"),
  TOPUP("topup"),
  PAY("pay"),
  EXIT("exit");
  
  private String command;
  
  private CommandAction(String command) {
    this.command = command;
  }
  
  public String getCommand() {
    return command;
  }
  
  /**
   * get commandAction from command value
   * @throws IllegalArgumentException if input command is null/empty
   * @param command
   * @return commandAction, if no matching found for input command then null
   */
  public static CommandAction fromCommand(String command) {
    if(StringUtils.isBlank(command)) {
      throw new IllegalArgumentException("Input command is null/empty");
    }
    CommandAction commandAction = null;
    for(CommandAction action: CommandAction.values()) {
      if(command.equals(action.getCommand())) {
        commandAction = action;
        break;
      }
    }
    return commandAction;
  }
}