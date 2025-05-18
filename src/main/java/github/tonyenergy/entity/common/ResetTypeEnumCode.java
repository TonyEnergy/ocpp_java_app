package github.tonyenergy.entity.common;


/**
 * Reset type
 *
 * @author Tony
 * @date 2025/5/11
 */
public enum ResetTypeEnumCode {
    /**
     * Reset Type, from Open Charge Point Protocol 1.6
     */
    Hard,
    Soft;

    public static ResetTypeEnumCode from(String key) {
        try {
            return ResetTypeEnumCode.valueOf(key);
        } catch (Exception e) {
            return null;
        }
    }
}
