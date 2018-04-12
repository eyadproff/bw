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
						// STEP1: show progress only
						formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
						
						// STEP2: submit the fingerprints inquiry
						// TODO: temp data
						
						outer: while(true)
						{
							List<Finger> collectedFingerprints = new ArrayList<>();
							
							try
							{
								collectedFingerprints.add(new Finger(1, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\1.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(2, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\2.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(3, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\3.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(4, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\4.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(5, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\5.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(6, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\6.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(7, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\7.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(8, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\8.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(9, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\9.wsq"))), new FingerCoordinate()));
								collectedFingerprints.add(new Finger(10, Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\wsq dinesh\\10.wsq"))), new FingerCoordinate()));
								
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
							//collectedFingerprints.add(new Finger(1, "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", new FingerCoordinate()));
							
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
											Thread.sleep(1000);
											continue;
										}
										else if(result.getStatus() ==
																FingerprintInquiryStatusResult.STATUS_INQUIRY_NO_HIT)
										{
											uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_NO_HIT,
											                Boolean.TRUE);
											formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
										}
										else if(result.getStatus() == FingerprintInquiryStatusResult.STATUS_INQUIRY_HIT)
										{
											uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT,
											                Boolean.TRUE);
											uiInputData.put(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT_RESULT,
											                result);
											formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
										}
										else // report the error
										{
											System.out.println("unknown status = " + result.getStatus());
										}
										
										uiOutputData = waitForUserTask();
										
										if(!uiOutputData.containsKey(
												InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY))
										{
											uiInputData.putAll(uiOutputData);
											break outer;
										}
										else break;
									}
									else // report the error
									{
										uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
										formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
										uiOutputData = waitForUserTask();
										
										if(!uiOutputData.containsKey(
																InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY))
										{
											uiInputData.putAll(uiOutputData);
											break outer;
										}
										else break;
									}
								}
							}
							else // report the error
							{
								uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
								formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
								uiOutputData = waitForUserTask();
								
								if(!uiOutputData.containsKey(InquiryPaneFxController.KEY_RETRY_FINGERPRINT_INQUIRY))
								{
									uiInputData.putAll(uiOutputData);
									break;
								}
							}
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
						formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 7:
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