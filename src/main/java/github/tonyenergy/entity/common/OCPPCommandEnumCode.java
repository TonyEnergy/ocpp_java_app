package github.tonyenergy.entity.common;

/**
 * OCPP Command Enum Code
 *
 * @author Tony
 * @date 2025/5/8
 */
public enum OCPPCommandEnumCode {
    /**
     * OCPPCommand, from Open Charge Point Protocol 1.6
     */
    BootNotification(10001, "BootNotification"),
    StatusNotification(10002, "StatusNotification"),
    Heartbeat(10003, "Heartbeat");

    private final int commandNumber;
    private final String commandInfo;

    OCPPCommandEnumCode(int commandNumber, String commandInfo) {
        this.commandNumber = commandNumber;
        this.commandInfo = commandInfo;
    }

    public int getCommandNumber() {
        return commandNumber;
    }

    public String getCommandInfo() {
        return commandInfo;
    }
}
