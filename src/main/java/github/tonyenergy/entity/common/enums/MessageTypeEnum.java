package github.tonyenergy.entity.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Message Type Enum Code
 *
 * @Author: Tony
 * @Date: 2025/5/8
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum implements BaseEnum<Integer, String> {
    /**
     * Message type, from Open Charge Point Protocol JSON 1.6, OCPP-J 1.6 Specification 4.1.3 The message type
     */
    CALL(2, "Client-to-Server"),
    CALL_RESULT(3, "Server-to-Client"),
    CALL_ERROR(4, "Server-to-Client"),
    ;

    private final Integer code;
    private final String desc;

    public static MessageTypeEnum fromNumber(int number) {
        for (MessageTypeEnum type : values()) {
            if (type.getCode() == number) {
                return type;
            }
        }
        return null;
    }
}
