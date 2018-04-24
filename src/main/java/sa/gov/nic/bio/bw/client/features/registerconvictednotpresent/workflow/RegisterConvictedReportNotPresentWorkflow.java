package sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.workflow;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvictedReportLookupService;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.FetchingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.InquiryPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.FingerprintInquiryService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.FingerprintInquiryStatusCheckerService;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.SubmittingConvictedReportService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterConvictedReportNotPresentWorkflow extends WorkflowBase<Void, Void>
{
	public RegisterConvictedReportNotPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		String basePackage = getClass().getPackage().getName().replace(".", "/");
		basePackage = basePackage.substring(0, basePackage.lastIndexOf('/'));
		
		Map<String, Object> uiInputData = new HashMap<>();
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = ResourceBundle.getBundle(basePackage + "/bundles/strings",
			                                         Context.getGuiLanguage().getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			e.printStackTrace();
			return null;
		}
		
		URL wizardFxmlLocation = Thread.currentThread().getContextClassLoader()
				.getResource(basePackage + "/fxml/wizard.fxml");
		FXMLLoader wizardPaneLoader = new FXMLLoader(wizardFxmlLocation, stringsBundle);
		wizardPaneLoader.setClassLoader(Context.getFxClassLoader());
		Context.getCoreFxController().loadWizardBar(wizardPaneLoader);
		
		while(true)
		{
			while(true)
			{
				formRenderer.get().renderForm(LookupFxController.class, uiInputData);
				waitForUserTask();
				ServiceResponse<Void> serviceResponse = ConvictedReportLookupService.execute();
				if(serviceResponse.isSuccess()) break;
				else uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
			}
			
			uiInputData.clear();
			int step = 0;
			
			while(true)
			{
				Map<String, Object> uiOutputData = null;
				
				switch(step)
				{
					case 0:
					{
						formRenderer.get().renderForm(PersonIdPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						
						while(true)
						{
							Long personId = (Long) uiOutputData.get(
									PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
							Integer personType = (Integer) uiOutputData.get(
									PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_TYPE);
							
							ServiceResponse<PersonInfo> serviceResponse = GetPersonInfoByIdService.execute(personId,
							                                                                               personType);
							
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(PersonIdPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							
							if(serviceResponse.isSuccess() && serviceResponse.getResult() != null) break;
						}
						
						break;
					}
					case 1:
					{
						formRenderer.get().renderForm(InquiryResultPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 2:
					{
						formRenderer.get().renderForm(FetchingFingerprintsPaneFxController.class, uiInputData);
						
						while(true)
						{
							
							Long personId = (Long) uiInputData.get(
									PersonIdPaneFxController.KEY_PERSON_INFO_INQUIRY_PERSON_ID);
							
							ServiceResponse<List<Finger>> serviceResponse = FetchingFingerprintsService.execute(personId);
							
							uiInputData.remove(FetchingFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_FETCHING);
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(FetchingFingerprintsPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							
							Boolean retry = (Boolean) uiInputData.get(
												FetchingFingerprintsPaneFxController.KEY_RETRY_FINGERPRINT_FETCHING);
							
							if(retry == null || !retry) break;
						}
						
						break;
					}
					case 3:
					{
						Boolean retry = (Boolean) uiInputData.get(
								InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
						uiInputData.remove(InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
						
						if(retry == null || !retry)
						{
							// show progress only
							formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
						}
						
						@SuppressWarnings("unchecked")
						List<Finger> collectedFingerprints = (List<Finger>)
									uiInputData.get(FetchingFingerprintsPaneFxController.KEY_PERSON_FINGERPRINTS);
						List<Integer> missingFingerprints = new ArrayList<>();
						for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
						collectedFingerprints.forEach(finger -> missingFingerprints.remove((Integer) finger.getType()));
						
						ServiceResponse<Integer> serviceResponse =
								FingerprintInquiryService.execute(collectedFingerprints, missingFingerprints);
						Integer inquiryId = serviceResponse.getResult();
						
						if(serviceResponse.isSuccess() && inquiryId != null)
						{
							while(true)
							{
								ServiceResponse<FingerprintInquiryStatusResult> response =
															FingerprintInquiryStatusCheckerService.execute(inquiryId);
								
								FingerprintInquiryStatusResult result = response.getResult();
								
								if(response.isSuccess() && result != null)
								{
									if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_PENDING)
									{
										uiInputData.put(InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY,
										                Boolean.TRUE);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
										uiOutputData = waitForUserTask();
										Boolean cancelled = (Boolean) uiOutputData.get(
												InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
										if(cancelled == null || !cancelled) continue;
										else uiInputData.remove(
												InquiryPaneFxController.KEY_WAITING_FINGERPRINT_INQUIRY_CANCELLED);
									}
									else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
									{
										uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
										                Boolean.FALSE);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									}
									else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
									{
										uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
										                Boolean.TRUE);
										uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT,
										                result);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									}
									else // report the error
									{
										uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_UNKNOWN_STATUS,
										                result.getStatus());
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									}
									
									uiOutputData = waitForUserTask();
									uiInputData.putAll(uiOutputData);
									break;
								}
								else // report the error
								{
									uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
									formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
									uiOutputData = waitForUserTask();
									uiInputData.putAll(uiOutputData);
									break;
								}
							}
						}
						else // report the error
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
						}
						
						break;
					}
					case 4:
					{
						formRenderer.get().renderForm(PersonInfoPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 5:
					{
						formRenderer.get().renderForm(JudgmentDetailsPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 6:
					{
						formRenderer.get().renderForm(PunishmentDetailsPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 7:
					{
						formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						
						while(true)
						{
							ConvictedReport convictedReport = (ConvictedReport)
									uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
							if(convictedReport == null) break;
							
							ServiceResponse<Long> serviceResponse =
									SubmittingConvictedReportService.execute(convictedReport);
							
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							
							if(serviceResponse.isSuccess() && serviceResponse.getResult() != null) break;
						}
						
						break;
					}
					case 8:
					{
						formRenderer.get().renderForm(ShowReportPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
				}
				
				if(uiOutputData != null)
				{
					Object direction = uiOutputData.get("direction");
					if("backward".equals(direction))
					{
						Platform.runLater(() -> Context.getCoreFxController().moveWizardBackward());
						step--;
					}
					else if("forward".equals(direction))
					{
						Platform.runLater(() -> Context.getCoreFxController().moveWizardForward());
						step++;
					}
					else if("startOver".equals(direction))
					{
						Platform.runLater(() -> Context.getCoreFxController().moveWizardToTheBeginning());
						uiInputData.clear();
						step = 0;
					}
				}
			}
		}
	}
}