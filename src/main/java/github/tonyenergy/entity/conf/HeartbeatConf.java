package github.tonyenergy.entity.conf;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * HeartbeatConf, this object as payload, will transfer to Json automatically, then response charger
 *
 * @Author: Tony
 * @Date: 2025/5/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HeartbeatConf {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC") // OCPP Using ISO8601 Format
    private Date currentTime;
}
