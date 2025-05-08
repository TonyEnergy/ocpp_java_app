package github.tonyenergy.entity.conf;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.MessageTypeEnumCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * BootNotificationConf, this object as payload, will transfer to Json automatically, then response charger
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootNotificationConf {
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC") // OCPP Using ISO8601 Format
    private Date timestamp;

    private int interval;

    public String getResponse(String uniqueId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object[] ocppResponse = new Object[]{
                    MessageTypeEnumCode.CALL_RESULT.getMessageTypeNumber(),
                    uniqueId,
                    this
            };
            return mapper.writeValueAsString(ocppResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
