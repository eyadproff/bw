package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvictedReportLookupService;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.FetchingFingerprintsPaneFxController;
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

public class RegisterConvictedReportPresentWorkflow extends WorkflowBase<Void, Void>
{
	public RegisterConvictedReportPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
				Map<String, Object> uiOutputData;
				
				switch(step)
				{
					case 0:
					{
						boolean acceptBadQualityFingerprint = "true".equals(System.getProperty(
										"jnlp.bio.bw.printConvictedReport.fingerprint.acceptBadQualityFingerprint"));
						int acceptedBadQualityFingerprintMinRetries = Integer.parseInt(System.getProperty(
							"jnlp.bio.bw.printConvictedReport.fingerprint.acceptedBadQualityFingerprintMinRetries"));
						
						uiInputData.put(FingerprintCapturingFxController.KEY_HIDE_FINGERPRINT_PREVIOUS_BUTTON,
						                Boolean.TRUE);
						uiInputData.put(FingerprintCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FINGERPRINT,
						                acceptBadQualityFingerprint);
						uiInputData.put(
								FingerprintCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES,
								acceptedBadQualityFingerprintMinRetries);
						
						formRenderer.get().renderForm(FingerprintCapturingFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 1:
					{
						boolean acceptBadQualityFace = "true".equals(System.getProperty(
													"jnlp.bio.bw.printConvictedReport.face.acceptBadQualityFace"));
						int acceptedBadQualityFaceMinRetries = Integer.parseInt(System.getProperty(
										"jnlp.bio.bw.printConvictedReport.face.acceptedBadQualityFaceMinRetries"));
						
						uiInputData.put(FaceCapturingFxController.KEY_ACCEPT_BAD_QUALITY_FACE, acceptBadQualityFace);
						uiInputData.put(FaceCapturingFxController.KEY_ACCEPTED_BAD_QUALITY_FACE_MIN_RETIRES,
										acceptedBadQualityFaceMinRetries);
						
						formRenderer.get().renderForm(FaceCapturingFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 2:
					{
						Boolean retry = (Boolean) uiInputData.get(
								InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
						uiInputData.remove(InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY);
						
						if(retry == null || !retry)
						{
							// show progress only
							formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
						}
						
						List<Finger> collectedFingerprints = new ArrayList<>();
						List<Integer> missingFingerprints = new ArrayList<>();
						
						@SuppressWarnings("unchecked")
						Map<Integer, Fingerprint> capturedFingerprints = (Map<Integer, Fingerprint>)
										uiInputData.get(FingerprintCapturingFxController.KEY_CAPTURED_FINGERPRINTS);
						capturedFingerprints.forEach((position, fingerprint) ->
						{
							DMFingerData fingerData = fingerprint.getDmFingerData();
							if(fingerData == null) // skipped fingerprint
							{
								missingFingerprints.add(position);
								return;
							}
							
							collectedFingerprints.add(new Finger(position, fingerData.getFingerWsqImage(),
							                                    null));
						});
						
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
										uiInputData.put(FetchingFingerprintsPaneFxController.KEY_PERSON_FINGERPRINTS,
										                collectedFingerprints);
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
					case 3:
					{
						formRenderer.get().renderForm(InquiryResultPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
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
							uiInputData.remove(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
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
					default:
					{
						uiOutputData = waitForUserTask();
						break;
					}
				}
				
				if(uiOutputData != null)
				{
					String direction = (String) uiOutputData.get("direction");
					
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