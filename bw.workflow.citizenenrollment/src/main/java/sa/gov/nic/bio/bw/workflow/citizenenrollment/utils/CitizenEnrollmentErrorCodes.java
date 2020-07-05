package sa.gov.nic.bio.bw.workflow.citizenenrollment.utils;

public enum CitizenEnrollmentErrorCodes {
    B018_00001,
    B018_00002,
    B018_00003,
    C018_00010,
    C018_00011;

    public final String getCode() {
        return name().replace("_", "-");
    }
}