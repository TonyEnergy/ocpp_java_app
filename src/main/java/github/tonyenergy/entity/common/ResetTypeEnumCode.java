package github.tonyenergy.entity.common;

import github.tonyenergy.entity.req.ResetReq;


/**
 * @author Tony
 * @date 2025/5/11
 */
public enum ResetTypeEnumCode {
    /**
     * Reset Type, from Open Charge Point Protocol 1.6
     */
    Hard {
        @Override
        public String handle(String messageId) {
            return new ResetReq("Hard").getRequest(messageId);
        }
    },
    Soft {
        @Override
        public String handle(String messageId) {
            return new ResetReq("Soft").getRequest(messageId);
        }
    };


    public abstract String handle(String messageId);

    public static ResetTypeEnumCode from(String key) {
        try {
            return ResetTypeEnumCode.valueOf(key);
        } catch (Exception e) {
            return null;
        }
    }
}
