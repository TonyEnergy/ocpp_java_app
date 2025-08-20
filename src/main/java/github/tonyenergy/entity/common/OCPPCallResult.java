package github.tonyenergy.entity.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class OCPPCallResult {
    int messageType;
    String messageId;
    Object payload;

    public String getCallResultJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object[] payloadJson = new Object[]{
                    messageType,
                    messageId,
                    payload
            };
            return mapper.writeValueAsString(payloadJson);
        } catch (JsonProcessingException e) {
            log.error("❌ Get call result json failed!");
            return "{}";
        }
    }
}
