package github.tonyenergy.entity.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OCPP action
 *
 * @author Tony
 * @date 2025/5/12
 */
@Getter
@AllArgsConstructor
public enum OCPPActionEnum implements BaseEnum<String, String> {
    /**
     * OCPP action, from Open Charge Point Protocol 1.6
     */

    Reset("Reset", "Reset"),
    GetConfiguration("GetConfiguration", "GetConfiguration"),
    ChangeConfiguration("ChangeConfiguration", "ChangeConfiguration"),
    BootNotification("BootNotification", "BootNotification"),
    StatusNotification("StatusNotification", "StatusNotification"),
    Heartbeat("Heartbeat", "Heartbeat");

    private final String code;
    private final String desc;

    public static OCPPActionEnum from(String name) {
        try {
            return OCPPActionEnum.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
