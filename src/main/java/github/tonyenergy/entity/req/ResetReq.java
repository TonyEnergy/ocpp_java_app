package github.tonyenergy.entity.req;

import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.common.OCPPActionEnumCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * ResetReq
 *
 * @author Tony
 * @date 2025/5/11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ResetReq {

    private String type;

    public Object[] getRequest(String uniqueId) {
        return new Object[]{
                MessageTypeEnumCode.CALL.getMessageTypeNumber(),
                uniqueId,
                OCPPActionEnumCode.Reset.name(),
                this
        };
    }
}
