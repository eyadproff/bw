package sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson;


import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.*;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.*;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.FingerprintInquiryCriminalStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.FingerprintInquiryCriminalWorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.SubmitCriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.controllers.UpdatePersonInfoPaneFxController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AssociatedMenu(workflowId = 1032, menuId = "menu.query.criminalclearancereportanonymousperson",
                menuTitle = "menu.title", menuOrder = 9, devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
                @Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
                @Step(iconId = "search", title = "wizard.inquiryByFingerprintsInCivil"),
                @Step(iconId = "database", title = "wizard.inquiryResult"),
                @Step(iconId = "user", title = "wizard.personInfo"),
                @Step(iconId = "search", title = "wizard.inquiryByFingerprintsInCriminal"),
                @Step(iconId = "\\uf022", title = "wizard.addCriminalClearanceDetails"),
                @Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
                @Step(iconId = "save", title = "wizard.registerReport"),
                @Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class CriminalClearanceReportAnonymousPersonWorkflow extends WizardWorkflowBase {

    private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
    private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
    //    private static final String FIELD_OLD_CRIMINAL_PERSON_INFO_MAP = "OLD_CRIMINAL_PERSON_INFO_MAP";
    //    private static final String FIELD_NEW_CRIMINAL_PERSON_INFO_MAP = "NEW_CRIMINAL_PERSON_INFO_MAP";

    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0: {

                setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
                        Boolean.TRUE);
                setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                        0);

                renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);

                break;
            }
            //            case 1: {
            //
            //              //  renderUiAndWaitForUserInput(FaceCapturingFxController.class);
            //
            //                break;
            //            }
            case 1: {
                FingerprintInquiryStatusCheckerWorkflowTask.Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");

                setData(InquiryByFingerprintsPaneFxController.class, "status", status);

                renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);

                Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");

                if (inquiryId == null) {
                    passData(SlapFingerprintsCapturingFxController.class, "slapFingerprints",
                            FingerprintInquiryWorkflowTask.class, "fingerprints");
                    passData(SlapFingerprintsCapturingFxController.class, FingerprintInquiryWorkflowTask.class,
                            "missingFingerprints");
                    executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
                }

                setData(FingerprintInquiryStatusCheckerWorkflowTask.class, "ignoreCriminalFingerprintsInquiryResult", Boolean.TRUE);
                passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
                        "inquiryId");
                executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);

                status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");

                if (status == FingerprintInquiryStatusCheckerWorkflowTask.Status.HIT) {
                    Long civilBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                            "civilBiometricsId");
                    Long criminalBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                            "criminalBiometricsId");
                    if (civilBiometricsId != null) {
                        setData(getClass(), FIELD_CIVIL_HIT, Boolean.TRUE);
                        List<Long> civilPersonIds = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                                "civilPersonIds");
                        if (!civilPersonIds.isEmpty()) {
                            // LinkedHashMap is ordered
                            Map<Long, PersonInfo> civilPersonInfoMap = new LinkedHashMap<>();

                            for (Long civilPersonId : civilPersonIds) {
                                if (civilPersonId == null) { continue; }

                                PersonInfo personInfo;

                                String sCivilPersonId = String.valueOf(civilPersonId);
                                if (sCivilPersonId.length() == 10 && sCivilPersonId.startsWith("9")) {
                                    setData(GetDeporteeInfoByIdWorkflowTask.class, "deporteeId",
                                            civilPersonId);
                                    setData(GetDeporteeInfoByIdWorkflowTask.class,
                                            "returnNullResultInCaseNotFound", Boolean.TRUE);
                                    executeWorkflowTask(GetDeporteeInfoByIdWorkflowTask.class);
                                    DeporteeInfo deporteeInfo = getData(GetDeporteeInfoByIdWorkflowTask.class,
                                            "deporteeInfo");
                                    personInfo = new DeporteeInfoToPersonInfoConverter().convert(deporteeInfo);
                                }
                                else {
                                    setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
                                    setData(GetPersonInfoByIdWorkflowTask.class,
                                            "returnNullResultInCaseNotFound", Boolean.TRUE);
                                    executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
                                    personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
                                }

                                civilPersonInfoMap.put(civilPersonId, personInfo);
                            }

                            setData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, civilPersonInfoMap);
                        }
                    }

                    //                    if(criminalBiometricsId != null)
                    //                    {
                    //                        setData(ConvictedReportInquiryFromDisWorkflowTask.class,
                    //                                "criminalBiometricsId", criminalBiometricsId);
                    //                        setData(ConvictedReportInquiryFromDisWorkflowTask.class,
                    //                                "returnNullResultInCaseNotFound", Boolean.TRUE);
                    //                        executeWorkflowTask(ConvictedReportInquiryFromDisWorkflowTask.class);
                    //                        List<DisCriminalReport> disCriminalReports =
                    //                                getData(ConvictedReportInquiryFromDisWorkflowTask.class,
                    //                                        "disCriminalReports");
                    //                        DisCriminalReportToPersonInfoConverter converter = new DisCriminalReportToPersonInfoConverter();
                    //                        Map<Integer, PersonInfo> oldCriminalPersonInfoMap;
                    //                        if(disCriminalReports != null) oldCriminalPersonInfoMap = disCriminalReports.stream().collect(
                    //                                Collectors.toMap(DisCriminalReport::getSequenceNumber, converter::convert,
                    //                                        (k1, k2) -> k1, LinkedHashMap::new));
                    //                        else oldCriminalPersonInfoMap = new LinkedHashMap<>();
                    //                        setData(getClass(), FIELD_OLD_CRIMINAL_PERSON_INFO_MAP, oldCriminalPersonInfoMap);
                    //
                    //                        setData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
                    //                                "criminalBiometricsId", criminalBiometricsId);
                    //                        setData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
                    //                                "returnNullResultInCaseNotFound", Boolean.TRUE);
                    //                        executeWorkflowTask(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class);
                    //                        List<ConvictedReport> convictedReports =
                    //                                getData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
                    //                                        "convictedReports");
                    //                        ConvictedReportToPersonInfoConverter converter2 = new ConvictedReportToPersonInfoConverter();
                    //                        Map<Long, PersonInfo> newCriminalPersonInfoMap;
                    //                        if(convictedReports != null) newCriminalPersonInfoMap = convictedReports.stream().collect(
                    //                                Collectors.toMap(ConvictedReport::getReportNumber, converter2::convert,
                    //                                        (k1, k2) -> k1, LinkedHashMap::new));
                    //                        else newCriminalPersonInfoMap = new LinkedHashMap<>();
                    //                        setData(getClass(), FIELD_NEW_CRIMINAL_PERSON_INFO_MAP, newCriminalPersonInfoMap);
                    //                    }
                }


                break;
            }
            case 2: {

                passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByFingerprintsResultPaneFxController.class,
                        "civilPersonInfoMap");
                //                setData(InquiryByFingerprintsResultPaneFxController.class, "hideRegisterUnknownButton",
                //                        Boolean.TRUE);
                //                setData(InquiryByFingerprintsResultPaneFxController.class, "hideConfirmationButton",
                //                        Boolean.TRUE);
                setData(InquiryByFingerprintsResultPaneFxController.class, "ignoreCriminalFingerprintsInquiryResult",
                        Boolean.TRUE);
                passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
                        InquiryByFingerprintsResultPaneFxController.class, "status", "civilBiometricsId");


                renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);

                break;
            }
            case 3: {

                passData(getClass(), FIELD_CIVIL_HIT, UpdatePersonInfoPaneFxController.class, "civilHit");
                passData(InquiryByFingerprintsResultPaneFxController.class, UpdatePersonInfoPaneFxController.class,
                        "normalizedPersonInfo");
                renderUiAndWaitForUserInput(UpdatePersonInfoPaneFxController.class);
                break;
            }
            case 4: {

                FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status status = getData(
                        FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "status");

                setData(InquiryCriminalByFingerprintsPaneFxController.class, "status", status);
                renderUiAndWaitForUserInput(InquiryCriminalByFingerprintsPaneFxController.class);
                Integer inquiryId = getData(FingerprintInquiryCriminalWorkflowTask.class, "inquiryId");

                if (inquiryId == null) {

                    passData(SlapFingerprintsCapturingFxController.class, "slapFingerprints",
                            FingerprintInquiryCriminalWorkflowTask.class, "fingerprints");
                    passData(SlapFingerprintsCapturingFxController.class, FingerprintInquiryCriminalWorkflowTask.class,
                            "missingFingerprints");
                    executeWorkflowTask(FingerprintInquiryCriminalWorkflowTask.class);

                }

                passData(FingerprintInquiryCriminalWorkflowTask.class,
                        FingerprintInquiryCriminalStatusCheckerWorkflowTask.class,
                        "inquiryId");
                //                setData(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "ignoreCriminalFingerprintsInquiryResult", Boolean.TRUE);
                executeWorkflowTask(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class);

                status = getData(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "status");
                if (status == FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status.HIT) {
                    Long criminalBiometricsId = getData(
                            FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "criminalBiometricsId");
                    setData(InquiryCriminalByFingerprintsPaneFxController.class, "criminalBiometricsId", criminalBiometricsId);
                }
                break;
            }
            case 5: {
                renderUiAndWaitForUserInput(CriminalClearanceDetailsPaneFxController.class);
                break;
            }
            case 6: {

                passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                        "fingerprintBase64Images");

                passData(UpdatePersonInfoPaneFxController.class, ReviewAndSubmitPaneFxController.class,
                        "facePhotoBase64", "firstName", "fatherName", "grandfatherName",
                        "familyName", "gender", "nationality", "occupation", "birthPlace", "birthDate",
                        "birthDateUseHijri", "personId", "personType", "documentId", "documentType",
                        "documentIssuanceDate", "documentExpiryDate");

                passData(CriminalClearanceDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class, "whoRequestedTheReport");
                passData(CriminalClearanceDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class, "purposeOfTheReport");
                renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
                break;
            }
            case 7: {

                passData(ReviewAndSubmitPaneFxController.class, SubmitCriminalClearanceReport.class, "criminalClearanceReport");

                renderUiAndWaitForUserInput(RegisteringCriminalClearanceReportPaneFxController.class);

                executeWorkflowTask(SubmitCriminalClearanceReport.class);

                break;
            }
            case 8: {

                sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.InquiryByFingerprintsPaneFxController.ServiceType serviceType = getData(
                        sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.InquiryByFingerprintsPaneFxController.class, "serviceType");
                if (serviceType == sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.InquiryByFingerprintsPaneFxController.ServiceType.TAKEFINGERPRINTS) {
                    passData(SlapFingerprintsCapturingFxController.class, ShowReportPaneFxController.class,
                            "fingerprintBase64Images");
                }
                else {
                    passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                            ShowReportPaneFxController.class,
                            "fingerprintBase64Images");
                }

                passData(GetPersonInfoByIdWorkflowTask.class, ShowReportPaneFxController.class,
                        "personInfo");
                passData(ReviewAndSubmitPaneFxController.class, ShowReportPaneFxController.class,
                        "criminalClearanceReport");
                passData(SubmitCriminalClearanceReport.class, ShowReportPaneFxController.class,
                        "criminalClearanceResponse");

                renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
                break;
            }

            default: {
                break;
            }
        }
    }
}
