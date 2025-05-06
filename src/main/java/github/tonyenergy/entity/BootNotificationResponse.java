package github.tonyenergy.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootNotificationResponse {
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC") // OCPP Using ISO8601 Format
    private Date timestamp;

    private int interval;

    public String getAll(String uniqueId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object[] ocppResponse = new Object[]{
                    3, // CALL RESULT
                    uniqueId,
                    this // this object as payload, will transfer to Json automatically
            };
            return mapper.writeValueAsString(ocppResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
