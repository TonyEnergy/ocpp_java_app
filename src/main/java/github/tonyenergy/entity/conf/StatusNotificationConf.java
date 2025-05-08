package github.tonyenergy.entity.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.MessageTypeEnumCode;

import java.util.Collections;

/**
 * StatusNotificationConf, this object as payload, will transfer to Json automatically, then response charger, always be Collections.emptyMap()
 *
 * @Author: Tony
 * @Date: 2025/5/8
 */
public class StatusNotificationConf {
    public String getResponse(String uniqueId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object[] ocppResponse = new Object[]{
                    MessageTypeEnumCode.CALL_RESULT.getMessageTypeNumber(),
                    uniqueId,
                    Collections.emptyMap()
            };
            return mapper.writeValueAsString(ocppResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
