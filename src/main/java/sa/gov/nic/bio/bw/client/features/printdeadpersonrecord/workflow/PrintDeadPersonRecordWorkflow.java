package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetPersonInfoByIdService;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.FetchingPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.RecordIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.ShowRecordPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.utils.PrintDeadPersonRecordPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
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

@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
public class PrintDeadPersonRecordWorkflow extends WizardWorkflowBase<Void, Void>
{
	public PrintDeadPersonRecordWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                     BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUi(RecordIdPaneFxController.class);
				waitForUserInput();
				
				while(true)
				{
					Long recordId = (Long) uiInputData.get(RecordIdPaneFxController.KEY_RECORD_ID);
					
					ServiceResponse<DeadPersonRecord> serviceResponse = DeadPersonRecordByIdService.execute(recordId);
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					renderUi(RecordIdPaneFxController.class);
					waitForUserInput();
					
					if(serviceResponse.isSuccess())
					{
						uiInputData.put(ShowRecordPaneFxController.KEY_DEAD_PERSON_RECORD, serviceResponse.getResult());
						break;
					}
				}
				
				break;
			}
			case 1:
			{
				renderUi(FetchingPersonInfoPaneFxController.class);
				waitForUserInput();
				
				if(uiInputData.get(FetchingPersonInfoPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING) != Boolean.TRUE)
				{
					break;
				}
				
				DeadPersonRecord deadPersonRecord =
								(DeadPersonRecord) uiInputData.get(ShowRecordPaneFxController.KEY_DEAD_PERSON_RECORD);
				Long samisId = deadPersonRecord.getSamisId();
				
				loop: while(true)
				{
					ServiceResponse<?> serviceResponse;
					
					block:
					{
						if(samisId != null)
						{
							serviceResponse = GetPersonInfoByIdService.execute(samisId, 0);
							
							if(serviceResponse.isSuccess())
							{
								uiInputData.put(ShowRecordPaneFxController.KEY_SAMIS_ID, samisId);
								uiInputData.put(ShowRecordPaneFxController.KEY_PERSON_INFO,
												serviceResponse.getResult());
							}
							else break block;
						}
						
						List<Finger> fingerprints = deadPersonRecord.getSubjFingers();
						
						List<Integer> missingFingerprints = deadPersonRecord.getSubjMissingFingers();
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
								String slapImageFormat = "WSQ";
								int expectedFingersCount = 0;
								List<Integer> slapMissingFingers = new ArrayList<>();
								
								if(position == FingerPosition.RIGHT_SLAP.getPosition())
								{
									for(int i = FingerPosition.RIGHT_INDEX.getPosition();
									    i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
									{
										if(availableFingerprints.contains(i)) expectedFingersCount++;
										else slapMissingFingers.add(i);
									}
								}
								else if(position == FingerPosition.LEFT_SLAP.getPosition())
								{
									for(int i = FingerPosition.LEFT_INDEX.getPosition();
									    i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
									{
										if(availableFingerprints.contains(i)) expectedFingersCount++;
										else slapMissingFingers.add(i);
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
								
								sa.gov.nic.bio.biokit.beans.ServiceResponse<SegmentFingerprintsResponse> response;
								try
								{
									response = serviceResponseFuture.get();
								}
								catch(Exception e)
								{
									String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00001.getCode();
									String[] errorDetails =
														{"Failed to call the service for segmenting the fingerprints!"};
									serviceResponse = ServiceResponse.failure(errorCode, e, errorDetails);
									break block;
								}
								
								if(response.isSuccess())
								{
									SegmentFingerprintsResponse result = response.getResult();
									List<DMFingerData> fingerData = result.getFingerData();
									fingerData.forEach(dmFingerData -> fingerprintImages.put(dmFingerData.getPosition(),
									                                                         dmFingerData.getFinger()));
								}
								else
								{
									String[] errorDetails = {"Failed to segment the fingerprints!"};
									serviceResponse = ServiceResponse.failure(response.getErrorCode(),
									                                          response.getException(), errorDetails);
									break block;
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
							
							sa.gov.nic.bio.biokit.beans.ServiceResponse<ConvertedFingerprintImagesResponse> response;
							try
							{
								response = serviceResponseFuture.get();
							}
							catch(Exception e)
							{
								String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00002.getCode();
								String[] errorDetails = {"Failed to call the service for converting the WSQ!"};
								serviceResponse = ServiceResponse.failure(errorCode, e, errorDetails);
								break block;
							}
							
							if(response.isSuccess())
							{
								ConvertedFingerprintImagesResponse responseResult = response.getResult();
								Map<Integer, String> result = responseResult.getFingerprintImagesMap();
								fingerprintImages.putAll(result);
							}
							else
							{
								String[] errorDetails = {"Failed to convert the WSQ!"};
								serviceResponse = ServiceResponse.failure(response.getErrorCode(),
								                                          response.getException(), errorDetails);
								break block;
							}
						}
						
						uiInputData.put(ShowRecordPaneFxController.KEY_PERSON_FINGERPRINTS_IMAGES, fingerprintImages);
						serviceResponse = ServiceResponse.success(null);
					}
					
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					
					while(true)
					{
						renderUi(FetchingPersonInfoPaneFxController.class);
						waitForUserInput();
						
						Boolean retry = (Boolean) uiInputData.get(
								FetchingPersonInfoPaneFxController.KEY_RETRY_PERSON_INFO_FETCHING);
						uiInputData.remove(FetchingPersonInfoPaneFxController.KEY_RETRY_PERSON_INFO_FETCHING);
						if(retry == null || !retry) break loop;
						
						Boolean running = (Boolean)
									uiInputData.get(FetchingPersonInfoPaneFxController.KEY_DEVICES_RUNNER_IS_RUNNING);
						if(running != null && running) break;
					}
				}
				
				break;
			}
			case 2:
			{
				renderUi(ShowRecordPaneFxController.class);
				waitForUserInput();
				break;
			}
			default:
			{
				waitForUserInput();
				break;
			}
		}
	}
}