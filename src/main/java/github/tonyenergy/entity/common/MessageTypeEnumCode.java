package github.tonyenergy.entity.common;

/**
 * Message Type Enum Code
 *
 * @Author: Tony
 * @Date: 2025/5/8
 */
public enum MessageTypeEnumCode {
    /**
     * Message type, from Open Charge Point Protocol JSON 1.6, OCPP-J 1.6 Specification 4.1.3 The message type
     */
    CALL(2, "Client-to-Server"),
    CALL_RESULT(3, "Server-to-Client"),
    CALL_ERROR(4, "Server-to-Client");

    private final int messageTypeNumber;
    private final String direction;

    MessageTypeEnumCode(int messageTypeNumber, String direction) {
        this.messageTypeNumber = messageTypeNumber;
        this.direction = direction;
    }

    public int getMessageTypeNumber() {
        return messageTypeNumber;
    }

    public String getDirection() {
        return direction;
    }

    public static MessageTypeEnumCode fromNumber(int number) {
        for (MessageTypeEnumCode type : values()) {
            if (type.getMessageTypeNumber() == number) {
                return type;
            }
        }
        return null;
    }
}
