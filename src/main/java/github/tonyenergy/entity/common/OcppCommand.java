package github.tonyenergy.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class OcppCommand {
    private Long id;
    private String title;
    private Integer messageType;
    private String action;
    private String jsonSchema;
    private String description;
}
