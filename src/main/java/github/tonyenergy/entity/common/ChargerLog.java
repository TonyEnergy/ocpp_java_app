package github.tonyenergy.entity.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@TableName("charger_log")
public class ChargerLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String chargerId;
    private String action;
    private String ocppCallPayload;
    private String ocppCallResultPayload;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime actionTime;
}
