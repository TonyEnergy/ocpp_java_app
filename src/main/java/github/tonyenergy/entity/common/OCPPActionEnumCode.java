package github.tonyenergy.entity.common;


/**
 * OCPP action
 *
 * @author Tony
 * @date 2025/5/12
 */
public enum OCPPActionEnumCode {
    /**
     * OCPP action, from Open Charge Point Protocol 1.6
     */

    Reset,
    GetConfiguration,
    ChangeConfiguration,
    BootNotification,
    StatusNotification,
    Heartbeat;

    public static OCPPActionEnumCode from(String name) {
        try {
            return OCPPActionEnumCode.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
