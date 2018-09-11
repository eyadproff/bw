package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import javafx.application.Platform;
import javafx.util.Pair;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.ConvictedReportInquiryPaneFxController;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConvictedReportInquiryWorkflow extends WorkflowBase<Void, Void>
{
	public ConvictedReportInquiryWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                      BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> workflowResponse = new HashMap<>();
		Platform.runLater(() -> Context.getCoreFxController().clearWizardBar());
		
		while(true)
		{
			formRenderer.get().renderForm(ConvictedReportInquiryPaneFxController.class, workflowResponse);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			Long generalFileNumber =
							(Long) userTaskDataMap.get(ConvictedReportInquiryPaneFxController.KEY_GENERAL_FILE_NUMBER);
			
			ServiceResponse<List<ConvictedReport>> response = ConvictedReportInquiryService.execute(generalFileNumber);
			
			if(response.isSuccess())
			{
				ServiceResponse<?> serviceResponse = null;
				
				List<Pair<ConvictedReport, Map<Integer, String>>> convictedReports = new ArrayList<>();
				
				List<ConvictedReport> result = response.getResult();
				for(int i = 0; i < result.size(); i++)
				{
					ConvictedReport convictedReport = result.get(i);
					
					List<Finger> fingerprints = convictedReport.getSubjFingers();
					List<Integer> missingFingerprints = convictedReport.getSubjMissingFingers();
					
					List<Integer> availableFingerprints = IntStream.rangeClosed(1, 10)
																   .boxed()
																   .collect(Collectors.toList());
					availableFingerprints.removeAll(missingFingerprints);
					
					Map<Integer, String> fingerprintWsqMap = new HashMap<>();
					Map<Integer, String> fingerprintImages = new HashMap<>();
					
					for(Finger finger : fingerprints)
					{
						int position = finger.getType();
						
						if(position == FingerPosition.RIGHT_SLAP.getPosition() ||
								position == FingerPosition.LEFT_SLAP.getPosition() ||
								position == FingerPosition.TWO_THUMBS.getPosition())
						{
							String slapImageBase64 = finger.getImage();
							if(slapImageBase64 == null)
							{
								serviceResponse = ServiceResponse.success(null);
								continue;
							}
							
							String slapImageFormat = "WSQ";
							int expectedFingersCount = 0;
							List<Integer> slapMissingFingers = new ArrayList<>();
							
							if(position == FingerPosition.RIGHT_SLAP.getPosition())
							{
								for(int j = FingerPosition.RIGHT_INDEX.getPosition();
								    j <= FingerPosition.RIGHT_LITTLE.getPosition(); j++)
								{
									if(availableFingerprints.contains(j)) expectedFingersCount++;
									else slapMissingFingers.add(j);
								}
							}
							else if(position == FingerPosition.LEFT_SLAP.getPosition())
							{
								for(int j = FingerPosition.LEFT_INDEX.getPosition();
								    j <= FingerPosition.LEFT_LITTLE.getPosition(); j++)
								{
									if(availableFingerprints.contains(j)) expectedFingersCount++;
									else slapMissingFingers.add(j);
								}
							}
							else if(position == FingerPosition.TWO_THUMBS.getPosition())
							{
								if(availableFingerprints.contains(FingerPosition.RIGHT_THUMB.getPosition()))
									expectedFingersCount++;
								else slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
								
								if(availableFingerprints.contains(FingerPosition.LEFT_THUMB.getPosition()))
									expectedFingersCount++;
								else slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition());
							}
							
							Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<SegmentFingerprintsResponse>>
									serviceResponseFuture = Context.getBioKitManager()
																   .getFingerprintUtilitiesService()
																   .segmentSlap(slapImageBase64, slapImageFormat,
																                position, expectedFingersCount,
																                slapMissingFingers);
							
							sa.gov.nic.bio.biokit.beans.ServiceResponse<SegmentFingerprintsResponse> response2;
							try
							{
								response2 = serviceResponseFuture.get();
							}
							catch(Exception e)
							{
								String errorCode = ConvictedReportInquiryErrorCodes.C014_00009.getCode();
								String[] errorDetails = {"Failed to call the service for segmenting the fingerprints!"};
								serviceResponse = ServiceResponse.failure(errorCode, e, errorDetails);
								break;
							}
							
							if(response2.isSuccess())
							{
								SegmentFingerprintsResponse result2 = response2.getResult();
								List<DMFingerData> fingerData = result2.getFingerData();
								fingerData.forEach(dmFingerData -> fingerprintImages.put(dmFingerData.getPosition(),
								                                                         dmFingerData.getFinger()));
							}
							else
							{
								String[] errorDetails = {"Failed to segment the fingerprints!"};
								serviceResponse = ServiceResponse.failure(response2.getErrorCode(),
								                                          response2.getException(), errorDetails);
								break;
							}
						}
						else
						{
							if(position == FingerPosition.RIGHT_THUMB_SLAP.getPosition())
								position = FingerPosition.RIGHT_THUMB.getPosition();
							else if(position == FingerPosition.LEFT_THUMB_SLAP.getPosition())
								position = FingerPosition.LEFT_THUMB.getPosition();
							fingerprintWsqMap.put(position, finger.getImage());
						}
					}
					
					if(!fingerprintWsqMap.isEmpty())
					{
						Future<sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintImagesResponse>>
								serviceResponseFuture = Context.getBioKitManager()
															   .getFingerprintUtilitiesService()
															   .convertWsqToImages(fingerprintWsqMap);
						
						sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintImagesResponse> response2;
						try
						{
							response2 = serviceResponseFuture.get();
						}
						catch(Exception e)
						{
							String errorCode = ConvictedReportInquiryErrorCodes.C014_00010.getCode();
							String[] errorDetails = {"Failed to call the service for converting the WSQ!"};
							serviceResponse = ServiceResponse.failure(errorCode, e, errorDetails);
							break;
						}
						
						if(response2.isSuccess())
						{
							ConvertedFingerprintImagesResponse responseResult = response2.getResult();
							fingerprintImages.putAll(responseResult.getFingerprintImagesMap());
						}
						else
						{
							String[] errorDetails = {"Failed to convert the WSQ!"};
							serviceResponse = ServiceResponse.failure(response2.getErrorCode(),
							                                          response2.getException(), errorDetails);
							break;
						}
					}
					
					convictedReports.add(new Pair<>(convictedReport, fingerprintImages));
				}
				
				if(serviceResponse != null && !serviceResponse.isSuccess())
														workflowResponse.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
				else workflowResponse.put(KEY_WEBSERVICE_RESPONSE, ServiceResponse.success(convictedReports));
			}
			else workflowResponse.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}