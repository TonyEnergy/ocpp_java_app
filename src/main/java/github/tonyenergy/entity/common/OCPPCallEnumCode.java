package github.tonyenergy.entity.common;

import github.tonyenergy.entity.req.ResetReq;


/**
 * OCPP Call Enum Code
 *
 * @author Tony
 * @date 2025/5/12
 */
public enum OCPPCallEnumCode {
    /**
     * OCPP Call command, from Open Charge Point Protocol 1.6
     */

    Reset {
        @Override
        public String handle(String messageId, ResetTypeEnumCode resetTypeEnumCode) {
            return new ResetReq(resetTypeEnumCode.name()).getRequest(messageId);
        }
    };

    public abstract String handle(String messageIdm, ResetTypeEnumCode resetTypeEnumFCode);
}
