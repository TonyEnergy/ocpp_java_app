package github.tonyenergy.entity.req;

import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.common.OCPPActionEnumCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GetConfigurationReq
 *
 * @author Tony
 * @date 2025/5/11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetConfigurationReq {

    private String[] keys;

    public Object[] getRequest(String uniqueId) {
        return new Object[]{
                MessageTypeEnumCode.CALL.getMessageTypeNumber(),
                uniqueId,
                OCPPActionEnumCode.GetConfiguration.name(),
                this
        };
    }
}
