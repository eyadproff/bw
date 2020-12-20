package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.utils;

public enum RegisterCriminalClearanceReportErrorCodes {
    C020_00001,
    C020_00002,
    C020_00003,
    C020_00004,
    C020_00005,
    C020_00006,
    C020_00007,
    C020_00008,
    C020_00009,
    C020_00010,
    C020_00011,
    C020_00012,
    C020_00013,
    C020_00014,
    C020_00015,

    N020_00001;

    public final String getCode() {
        return name().replace("_", "-");
    }
}