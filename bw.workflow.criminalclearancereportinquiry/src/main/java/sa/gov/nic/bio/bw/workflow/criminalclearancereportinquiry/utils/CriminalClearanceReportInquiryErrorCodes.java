package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.utils;

public enum CriminalClearanceReportInquiryErrorCodes {

    C022_00001, C022_00002, C022_00003, C022_00004,C022_00005, C022_00006, C022_00007, C022_00008;

    public final String getCode()
    {
        return name().replace("_", "-");
    }
}
