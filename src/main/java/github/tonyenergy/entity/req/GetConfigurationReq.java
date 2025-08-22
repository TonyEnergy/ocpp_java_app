package github.tonyenergy.entity.req;

import github.tonyenergy.entity.common.enums.MessageTypeEnum;
import github.tonyenergy.entity.common.enums.OCPPActionEnum;
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
                MessageTypeEnum.CALL.getCode(),
                uniqueId,
                OCPPActionEnum.GetConfiguration.name(),
                this
        };
    }
}
