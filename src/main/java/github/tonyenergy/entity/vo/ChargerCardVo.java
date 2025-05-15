package github.tonyenergy.entity.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Charger card
 *
 * @Author: Tony
 * @Date: 2025/5/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ChargerCardVo {
    public String chargerId;
    public int ratedCurrent;
    public int ratedPower;
    public int ratedVoltage;
    public String type;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.info("❌ Get charger card json failed!");
            return "{}";
        }
    }
}
