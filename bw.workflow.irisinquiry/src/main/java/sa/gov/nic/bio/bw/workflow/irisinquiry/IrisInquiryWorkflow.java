package sa.gov.nic.bio.bw.workflow.irisinquiry;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.IrisCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DeporteeInfoToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetDeporteeInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.irisinquiry.controllers.InquiryByIrisPaneFxController;
import sa.gov.nic.bio.bw.workflow.irisinquiry.controllers.InquiryByIrisResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.irisinquiry.tasks.IrisInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.irisinquiry.tasks.IrisInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.irisinquiry.tasks.IrisInquiryWorkflowTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AssociatedMenu(workflowId = 1022, menuId = "menu.query.irisInquiry",
				menuTitle = "menu.title", menuOrder = 7, devices = {Device.FINGERPRINT_SCANNER,
				                                                    Device.IRIS_SCANNER, Device.CAMERA})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "eye", title = "wizard.irisCapturing"),
		@Step(iconId = "search", title = "wizard.inquiryByIris"),
		@Step(iconId = "database", title = "wizard.inquiryResult")})
public class IrisInquiryWorkflow extends WizardWorkflowBase
{
	private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
	private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				setData(IrisCapturingFxController.class, "hidePreviousButton", Boolean.TRUE);
				setData(IrisCapturingFxController.class, "hideStartOverButton", Boolean.TRUE);
				IrisCapturingFxController.Request irisCapturingRequest = IrisCapturingFxController.Request.IDENTIFICATION;
				setData(IrisCapturingFxController.class, "irisCapturingRequest", irisCapturingRequest);
				renderUiAndWaitForUserInput(IrisCapturingFxController.class);
				break;
			}
			case 1:
			{
				passData(IrisInquiryStatusCheckerWorkflowTask.class, InquiryByIrisPaneFxController.class,
				         "status");
				
				renderUiAndWaitForUserInput(InquiryByIrisPaneFxController.class);
				
				Long tcn = getData(IrisInquiryWorkflowTask.class, "tcn");
				
				if(tcn == null)
				{
					passData(IrisCapturingFxController.class, "capturedRightIrisCompressedBase64",
					         IrisInquiryWorkflowTask.class, "rightIrisBase64");
					passData(IrisCapturingFxController.class, "capturedLeftIrisCompressedBase64",
					         IrisInquiryWorkflowTask.class, "leftIrisBase64");
					
					executeWorkflowTask(IrisInquiryWorkflowTask.class);
				}
				
				passData(IrisInquiryWorkflowTask.class, IrisInquiryStatusCheckerWorkflowTask.class,
				         "tcn");
				executeWorkflowTask(IrisInquiryStatusCheckerWorkflowTask.class);
				
				Status status = getData(IrisInquiryStatusCheckerWorkflowTask.class, "status");
				if(status == Status.HIT)
				{
					Long civilBiometricsId = getData(IrisInquiryStatusCheckerWorkflowTask.class,
					                                 "civilBiometricsId");
					if(civilBiometricsId != null)
					{
						setData(getClass(), FIELD_CIVIL_HIT, Boolean.TRUE);
						List<Long> civilPersonIds = getData(IrisInquiryStatusCheckerWorkflowTask.class,
						                                    "civilPersonIds");
						if(!civilPersonIds.isEmpty())
						{
							// LinkedHashMap is ordered
							Map<Long, PersonInfo> civilPersonInfoMap = new LinkedHashMap<>();
							
							for(Long civilPersonId : civilPersonIds)
							{
								if(civilPersonId == null) continue;
								
								PersonInfo personInfo;
								
								String sCivilPersonId = String.valueOf(civilPersonId);
								if(sCivilPersonId.length() == 10 && sCivilPersonId.startsWith("9"))
								{
									setData(GetDeporteeInfoByIdWorkflowTask.class, "deporteeId",
									        civilPersonId);
									setData(GetDeporteeInfoByIdWorkflowTask.class,
									        "returnNullResultInCaseNotFound", Boolean.TRUE);
									executeWorkflowTask(GetDeporteeInfoByIdWorkflowTask.class);
									DeporteeInfo deporteeInfo = getData(GetDeporteeInfoByIdWorkflowTask.class,
									                                    "deporteeInfo");
									personInfo = new DeporteeInfoToPersonInfoConverter().convert(deporteeInfo);
								}
								else
								{
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
				}
				
				break;
			}
			case 2:
			{
				passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByIrisResultPaneFxController.class,
				         "civilPersonInfoMap");
				passData(IrisInquiryStatusCheckerWorkflowTask.class,
				         InquiryByIrisResultPaneFxController.class,
				         "status", "civilBiometricsId");
				renderUiAndWaitForUserInput(InquiryByIrisResultPaneFxController.class);
				
				break;
			}
		}
	}
}