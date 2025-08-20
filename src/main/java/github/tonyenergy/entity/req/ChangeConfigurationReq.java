package github.tonyenergy.entity.req;

import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.common.OCPPActionEnumCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Change configuration request
 *
 * @author Tony
 * @date 2025/5/18
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeConfigurationReq {

    private String key;

    private String value;

    public Object[] getRequest(String uniqueId) {
        return new Object[]{
                MessageTypeEnumCode.CALL.getMessageTypeNumber(),
                uniqueId,
                OCPPActionEnumCode.ChangeConfiguration.name(),
                this
        };
    }

}
