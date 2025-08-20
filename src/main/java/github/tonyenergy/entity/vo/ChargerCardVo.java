package github.tonyenergy.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Charger card vo
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
    public String userEmail;
}
