package sa.gov.nic.bio.bw.workflow.commons.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.utils.AppConstants.Locales;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


public class BuildNumberOfCaptureAttemptsReportTask extends Task<JasperPrint> {
    private static final String DATE = "DATE";
    private static final String LOCATION = "LOCATION";
    private static final String PARAMETER_NAME = "NAME";
    private static final String PARAMETER_SAMIS_ID = "SAMIS_ID";
    private static final String PARAMETER_THUMB_RIGHT = "THUMB_RIGHT";
    private static final String PARAMETER_INDEX_RIGHT = "INDEX_RIGHT";
    private static final String PARAMETER_MIDDLE_RIGHT = "MIDDLE_RIGHT";
    private static final String PARAMETER_RING_RIGHT = "RING_RIGHT";
    private static final String PARAMETER_LITTLE_RIGHT = "LITTLE_RIGHT";
    private static final String PARAMETER_THUMB_LEFT = "THUMB_LEFT";
    private static final String PARAMETER_INDEX_LEFT = "INDEX_LEFT";
    private static final String PARAMETER_MIDDLE_LEFT = "MIDDLE_LEFT";
    private static final String PARAMETER_RING_LEFT = "RING_LEFT";
    private static final String PARAMETER_LITTLE_LEFT = "LITTLE_LEFT";

    private static final String OPERATOR_ID = "OPERATOR_ID";
    private static final String PARAMETER_LOGO = "LOGO";
    private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/workflow/commons/reports/" +
                                                       "numberofcaptureattempts_report.jrxml";

    private Map<Integer, Integer> fingerprintNumOfTriesMap;
    private PersonInfo personInfo;


    public BuildNumberOfCaptureAttemptsReportTask(Map<Integer, Integer> fingerprintNumOfTriesMap, PersonInfo personInfo) {
        this.fingerprintNumOfTriesMap = fingerprintNumOfTriesMap;
        this.personInfo = personInfo;

    }

    @Override
    protected JasperPrint call() throws Exception {
        InputStream reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        HashMap<String, Object> params = new HashMap<>();


        params.put(DATE, AppUtils.formatHijriDateSimple(Instant.now().getEpochSecond(), false));

        if(personInfo != null)
        {
            Name name = personInfo.getName();
            StringBuilder sb = new StringBuilder();

            String firstName = name.getFirstName();
            String fatherName = name.getFatherName();
            String grandFatherName = name.getGrandfatherName();
            String familyName = name.getFamilyName();

            if (firstName != null) { sb.append(firstName).append(" "); }
            if (fatherName != null) { sb.append(fatherName).append(" "); }
            if (grandFatherName != null) { sb.append(grandFatherName).append(" "); }
            if (familyName != null) { sb.append(familyName); }

            String fullName = sb.toString().stripTrailing();

            params.put(PARAMETER_NAME, fullName);


            Long subjSamisId = personInfo.getSamisId();
            params.put(PARAMETER_SAMIS_ID, AppUtils.localizeNumbers(String.valueOf(subjSamisId),
                    Locales.SAUDI_AR_LOCALE,
                    true));
        }


        params.put(PARAMETER_THUMB_RIGHT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(1)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_INDEX_RIGHT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(2)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_MIDDLE_RIGHT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(3)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_RING_RIGHT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(4)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_LITTLE_RIGHT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(5)),
                Locales.SAUDI_AR_LOCALE,
                true));

        params.put(PARAMETER_THUMB_LEFT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(6)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_INDEX_LEFT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(7)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_MIDDLE_LEFT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(8)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_RING_LEFT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(9)),
                Locales.SAUDI_AR_LOCALE,
                true));
        params.put(PARAMETER_LITTLE_LEFT, AppUtils.localizeNumbers(String.valueOf(fingerprintNumOfTriesMap.get(10)),
                Locales.SAUDI_AR_LOCALE,
                true));

        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");

        params.put(OPERATOR_ID, AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()),
                Locales.SAUDI_AR_LOCALE, true));

        params.put(LOCATION, AppUtils.localizeNumbers(String.valueOf(userInfo.getLocationName()),
                Locales.SAUDI_AR_LOCALE, true));

        params.put(PARAMETER_LOGO, CommonImages.LOGO_CIVIL_AFFAIRS.getAsInputStream());

        return JasperFillManager.fillReport(jasperReport, params);
    }
}