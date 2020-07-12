package sa.gov.nic.bio.bw.workflow.biometricsexception.utils;

public enum BiometricsExceptionErrorCodes {
    B003_0079,
    B003_0078,
    B003_0027;

    public final String getCode() {
        return name().replace("_", "-");
    }
}