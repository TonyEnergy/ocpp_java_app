package github.tonyenergy.entity.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Reset type
 *
 * @author Tony
 * @date 2025/5/11
 */
@Getter
@AllArgsConstructor
public enum ResetTypeEnum implements BaseEnum<String, String> {
    /**
     * Reset Type, from Open Charge Point Protocol 1.6
     */
    HARD("Hard", "Hard"),
    SOFT("Soft", "Soft");

    private final String code;
    private final String desc;

    public static ResetTypeEnum from(String key) {
        try {
            return ResetTypeEnum.valueOf(key);
        } catch (Exception e) {
            return null;
        }
    }
}
