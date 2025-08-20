package github.tonyenergy.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Boot Notification Req
 *
 * @author Tony
 * @date 2025/5/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class BootNotificationReq {
    private String chargeBoxSerialNumber;
    private String chargePointModel;
    private String chargePointSerialNumber;
    private String chargePointVendor;
    private String firmwareVersion;
    private String iccid;
    private String imsi;
    private String meterSerialNumber;
    private String meterType;
}
