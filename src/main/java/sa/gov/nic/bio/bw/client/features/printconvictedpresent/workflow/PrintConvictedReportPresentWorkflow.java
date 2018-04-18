package sa.gov.nic.bio.bw.client.features.printconvictedpresent.workflow;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvictedReportLookupService;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.InquiryPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerCoordinate;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PrintConvictedReportPresentWorkflow extends WorkflowBase<Void, Void>
{
	public PrintConvictedReportPresentWorkflow(AtomicReference<FormRenderer> formRenderer,
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
						
						// STEP2: submit the fingerprints inquiry
						// TODO: temp data
						
						List<Finger> collectedFingerprints = new ArrayList<>();
						
						try
						{
							collectedFingerprints.add(new Finger(1, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\1.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(2, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\2.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(3, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\3.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(4, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\4.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(5, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\5.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(6, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\6.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(7, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\7.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(8, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\8.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(9, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\9.wsq"))), new FingerCoordinate()));
							//collectedFingerprints.add(new Finger(10, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\10.wsq"))), new FingerCoordinate()));
							
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
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