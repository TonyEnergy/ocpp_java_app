package github.tonyenergy.entity.req;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.common.OCPPCallEnumCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * ResetReq
 *
 * @author Tony
 * @date 2025/5/11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetReq {

    private String type;

    public String getRequest(String uniqueId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object[] ocppRequest = new Object[]{
                    MessageTypeEnumCode.CALL.getMessageTypeNumber(),
                    uniqueId,
                    OCPPCallEnumCode.Reset.name(),
                    this
            };
            return mapper.writeValueAsString(ocppRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
