package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintsWsqToFingerConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers.ExtendedInquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers.SpecifyFingerprintCoordinatesPaneFxController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@AssociatedMenu(workflowId = 1005, menuId = "menu.query.fingerprintCardIdentification", menuTitle = "menu.title", menuOrder = 5,
				devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "file", title = "wizard.scanFingerprintCard"),
		@Step(iconId = "\\uf247", title = "wizard.specifyFingerprintCoordinates"),
		@Step(iconId = "scissors", title = "wizard.fingerprintsAfterCropping"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult")})
public class FingerprintCardIdentificationWorkflow extends WizardWorkflowBase
{
	private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
	private static final String FIELD_CRIMINAL_PERSON_INFO_MAP = "CRIMINAL_PERSON_INFO_MAP";
	
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				setData(ScanFingerprintCardPaneFxController.class, "hidePreviousButton", Boolean.TRUE);
				renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				break;
			}
			case 1:
			{
				passData(ScanFingerprintCardPaneFxController.class, SpecifyFingerprintCoordinatesPaneFxController.class,
				         "cardImage");
				
				renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
				break;
			}
			case 2:
			{
				passData(SpecifyFingerprintCoordinatesPaneFxController.class,
				         ShowingFingerprintsPaneFxController.class,
				         "fingerprintBase64Images");
				
				renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
				break;
			}
			case 3:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryByFingerprintsPaneFxController.class,
				         "status");
				
				renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
				
				Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");
				
				if(inquiryId == null)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
					         "fingerprintBase64Images");
					executeWorkflowTask(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class);
					
					passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
					         "fingerprintWsqImages", FingerprintInquiryWorkflowTask.class,
					         "fingerprints", new FingerprintsWsqToFingerConverter());
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         FingerprintInquiryWorkflowTask.class, "missingFingerprints");
					
					executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
				}
				
				passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				
				Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");
				if(status == Status.HIT)
				{
					Long civilBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
					                                 "civilBiometricsId");
					Long criminalBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
					                                    "criminalBiometricsId");
					if(civilBiometricsId != null)
					{
						List<Long> civilPersonIds = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
						                                    "civilPersonIds");
						if(!civilPersonIds.isEmpty())
						{
							// LinkedHashMap is ordered
							Map<Long, PersonInfo> civilPersonInfoMap = new LinkedHashMap<>();
							
							for(Long civilPersonId : civilPersonIds)
							{
								setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
								setData(GetPersonInfoByIdWorkflowTask.class,
								        "returnNullResultInCaseNotFound", Boolean.TRUE);
								executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
								civilPersonInfoMap.put(civilPersonId, getData(GetPersonInfoByIdWorkflowTask.class,
								                                              "personInfo"));
							}
							
							setData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, civilPersonInfoMap);
						}
					}
					
					if(criminalBiometricsId != null)
					{
						setData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class, "criminalBiometricsId",
						        criminalBiometricsId);
						setData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
						        "returnNullResultInCaseNotFound", Boolean.TRUE);
						executeWorkflowTask(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class);
						List<ConvictedReport> convictedReports = getData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
						                                                 "convictedReports");
						ConvictedReportToPersonInfoConverter converter = new ConvictedReportToPersonInfoConverter();
						Map<Long, PersonInfo> criminalPersonInfoMap;
						if(convictedReports != null) criminalPersonInfoMap = convictedReports.stream().collect(
								Collectors.toMap(ConvictedReport::getReportNumber, converter::convert,
								                 (k1, k2) -> k1, LinkedHashMap::new));
						else criminalPersonInfoMap = new LinkedHashMap<>();
						setData(getClass(), FIELD_CRIMINAL_PERSON_INFO_MAP, criminalPersonInfoMap);
					}
				}
				
				break;
			}
			case 4:
			{
				passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP,
				         ExtendedInquiryByFingerprintsResultPaneFxController.class, "civilPersonInfoMap");
				passData(getClass(), FIELD_CRIMINAL_PERSON_INFO_MAP,
				         ExtendedInquiryByFingerprintsResultPaneFxController.class, "criminalPersonInfoMap");
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         ExtendedInquiryByFingerprintsResultPaneFxController.class,
				         "status", "civilBiometricsId", "criminalBiometricsId");
				passData(SpecifyFingerprintCoordinatesPaneFxController.class,
				         ExtendedInquiryByFingerprintsResultPaneFxController.class,
				         "fingerprintBase64Images");
				renderUiAndWaitForUserInput(ExtendedInquiryByFingerprintsResultPaneFxController.class);
				
				break;
			}
		}
	}
}