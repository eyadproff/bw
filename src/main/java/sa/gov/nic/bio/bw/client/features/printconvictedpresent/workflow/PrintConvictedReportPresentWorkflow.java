package sa.gov.nic.bio.bw.client.features.printconvictedpresent.workflow;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.ConfirmInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.InquiryPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerCoordinate;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.awt.Point;
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
						formRenderer.get().renderForm(InquiryPaneFxController.class, uiInputData);
						
						// TODO: temp data
						
						List<Finger> collectedFingerprints = new ArrayList<>();
						
						try
						{
							collectedFingerprints.add(new Finger(FingerPosition.RIGHT_SLAP.getPosition(), Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\Fingers\\1523288249433_13\\wsqImage.wsq"))), new FingerCoordinate(new Point(144, 666), new Point(398, 640), new Point(440, 1070), new Point(186, 1096))));
							collectedFingerprints.add(new Finger(FingerPosition.LEFT_SLAP.getPosition(), Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\Fingers\\1523293042039_14\\wsqImage.wsq"))), new FingerCoordinate(new Point(108, 630), new Point(346, 676), new Point(268, 1074), new Point(30, 1028))));
							collectedFingerprints.add(new Finger(FingerPosition.TWO_THUMBS.getPosition(), Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("D:\\dev\\nic\\data\\Fingers\\1523293059551_15\\wsqImage.wsq"))), new FingerCoordinate(new Point(434, 808), new Point(776, 842), new Point(718, 1384), new Point(376, 1350))));
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						ServiceResponse<List<Integer>> serviceResponse =
								FingerprintInquiryService.execute(collectedFingerprints, new ArrayList<>());
						System.out.println("serviceResponse = " + serviceResponse);
						
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 3:
					{
						formRenderer.get().renderForm(ConfirmInfoPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 4:
					{
						formRenderer.get().renderForm(JudgmentDetailsPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 5:
					{
						formRenderer.get().renderForm(ReviewAndSubmitPaneFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 6:
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