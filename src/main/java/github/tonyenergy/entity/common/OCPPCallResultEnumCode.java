package github.tonyenergy.entity.common;

import github.tonyenergy.entity.conf.BootNotificationConf;
import github.tonyenergy.entity.conf.HeartbeatConf;
import github.tonyenergy.entity.conf.StatusNotificationConf;

import java.util.Date;

/**
 * OCPP Call Result Enum Code
 *
 * @author Tony
 * @date 2025/5/8
 */
public enum OCPPCallResultEnumCode {
    /**
     * OCPPCommand, from Open Charge Point Protocol 1.6
     */
    BootNotification {
        @Override
        public String handle(String messageId) {
            return new BootNotificationConf("Accepted", new Date(), 3600).getResponse(messageId);
        }
    },
    StatusNotification {
        @Override
        public String handle(String messageId) {
            return new StatusNotificationConf().getResponse(messageId);
        }
    },
    Heartbeat {
        @Override
        public String handle(String messageId) {
            return new HeartbeatConf(new Date()).getResponse(messageId);
        }
    };

    public abstract String handle(String messageId);


    public static OCPPCallResultEnumCode from(String name) {
        try {
            return OCPPCallResultEnumCode.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
