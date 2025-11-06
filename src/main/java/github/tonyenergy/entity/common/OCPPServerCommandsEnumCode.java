package github.tonyenergy.entity.common;

public enum OCPPServerCommandsEnumCode {
    ClearCache,
    DataTransfer,
    UnlockConnector,
    CancelReservation,
    SendLocalList,
    GetCompositeSchedule,
    GetDiagnostics,
    ChangeAvailability,
    ClearChargingProfile,
    TriggerMessage,
    SetChargingProfile,
    RemoteStartTransaction,
    UpdateFirmware,
    GetLocalListVersion,
    GetConfiguration,
    RemoteStopTransaction,
    ReserveNow,
    ChangeConfiguration,
    Reset;
    public static OCPPServerCommandsEnumCode from(String name) {
        try {
            return OCPPServerCommandsEnumCode.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
