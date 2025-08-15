package github.tonyenergy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("charger_card")
public class ChargerCard {
    @TableId(type = IdType.ASSIGN_ID)
    public Long id;
    public String chargerId;
    public int ratedCurrent;
    public int ratedPower;
    public int ratedVoltage;
    public String type;
    public String status;
    public String userEmail;
    public String firmwareVersion;
    public Boolean offlineEnable;
}
