package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.FingerCoordinate;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private List<Finger> fingerprints;
	@Input(alwaysRequired = true) private List<Integer> missingFingerprints;
	@Output private Map<Integer, String> fingerprintBase64Images;
	@Output private List<Finger> combinedFingerprints; // segmented + unsegmented
	
	@Override
	public void execute() throws Signal
	{
		List<Integer> availableFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
		availableFingerprints.removeAll(missingFingerprints);
		
		Map<Integer, String> fingerprintWsqToBeConvertedMap = new HashMap<>();
		Map<Integer, String> fingerprintImages = new HashMap<>();
		combinedFingerprints = new ArrayList<>();
		
		boolean segmentRightSlap = true;
		boolean segmentLeftSlap = true;
		boolean segmentThumbSlap = true;
		boolean takeRightThumbSlapAsRightThumb = true;
		boolean takeLeftThumbSlapAsLeftThumb = true;
		
		for(Finger finger : fingerprints)
		{
			int position = finger.getType();
			
			if(position >= FingerPosition.RIGHT_INDEX.getPosition() &&
			   position <= FingerPosition.RIGHT_LITTLE.getPosition()) segmentRightSlap = false;
			
			else if(position >= FingerPosition.LEFT_INDEX.getPosition() &&
			        position <= FingerPosition.LEFT_LITTLE.getPosition()) segmentLeftSlap = false;
			
			else if(position == FingerPosition.RIGHT_THUMB.getPosition() ||
			        position == FingerPosition.LEFT_THUMB.getPosition() ||
			        position == FingerPosition.RIGHT_THUMB_SLAP.getPosition() ||
			        position == FingerPosition.LEFT_THUMB_SLAP.getPosition()) segmentThumbSlap = false;
			
			if(position == FingerPosition.RIGHT_THUMB.getPosition()) takeRightThumbSlapAsRightThumb = false;
			if(position == FingerPosition.LEFT_THUMB.getPosition()) takeLeftThumbSlapAsLeftThumb = false;
		}
		
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
					
					if(!segmentRightSlap) continue;
				}
				else if(position == FingerPosition.LEFT_SLAP.getPosition())
				{
					for(int i = FingerPosition.LEFT_INDEX.getPosition();
				                                                i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
					{
						if(availableFingerprints.contains(i)) expectedFingersCount++;
						else slapMissingFingers.add(i);
					}
					
					if(!segmentLeftSlap) continue;
				}
				else /*if(position == FingerPosition.TWO_THUMBS.getPosition())*/
				{
					if(availableFingerprints.contains(FingerPosition.RIGHT_THUMB.getPosition())) expectedFingersCount++;
					else slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
					
					if(availableFingerprints.contains(FingerPosition.LEFT_THUMB.getPosition())) expectedFingersCount++;
					else slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition());
					
					if(!segmentThumbSlap) continue;
				}
				
				Future<TaskResponse<SegmentFingerprintsResponse>>
										 serviceResponseFuture = Context.getBioKitManager()
																		.getFingerprintUtilitiesService()
																		.segmentSlap(slapImageBase64, slapImageFormat,
																		             position, expectedFingersCount,
																		             slapMissingFingers);
				
				TaskResponse<SegmentFingerprintsResponse> response;
				try
				{
					response = serviceResponseFuture.get();
				}
				catch(Exception e)
				{
					if(e instanceof ExecutionException && e.getCause() instanceof NotConnectedException)
					{
						String errorCode = CommonsErrorCodes.N008_00001.getCode();
						resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode));
						return;
					}
					
					String errorCode = CommonsErrorCodes.C008_00019.getCode();
					String[] errorDetails = {"Failed to call the service for segmenting the fingerprints!"};
					resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
					return;
				}
				
				resetWorkflowStepIfNegativeOrNullTaskResponse(response);
				
				SegmentFingerprintsResponse result = response.getResult();
				if(result.getReturnCode() == SegmentFingerprintsResponse.SuccessCodes.SUCCESS)
				{
					List<DMFingerData> fingerData = result.getFingerData();
					List<FingerCoordinate> fingerCoordinates = new ArrayList<>();
					
					fingerData.forEach(dmFingerData ->
					{
						fingerprintWsqToBeConvertedMap.put(dmFingerData.getPosition(),
														   dmFingerData.getFingerWsqImage());
						String roundingBox = dmFingerData.getRoundingBox();
						FingerCoordinate fingerCoordinate = AppUtils.constructFingerCoordinates(roundingBox);
						fingerCoordinates.add(fingerCoordinate);
						
						Finger segmentedFinger = new Finger(dmFingerData.getPosition(),
						                                    dmFingerData.getFingerWsqImage(), null);
						combinedFingerprints.add(segmentedFinger); // segmented
					});
					
					if(finger.getFingerCoordinates() == null) finger.setFingerCoordinates(fingerCoordinates);
					combinedFingerprints.add(new Finger(finger)); // slap
				}
				else if(result.getReturnCode() == SegmentFingerprintsResponse.FailureCodes.SEGMENTATION_FAILED)
				{
					String errorCode = CommonsErrorCodes.C008_00020.getCode();
					String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.SEGMENTATION_FAILED (" +
																						result.getReturnCode() + ")"};
					resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
					return;
				}
				else if(result.getReturnCode() == SegmentFingerprintsResponse.FailureCodes.WRONG_NO_OF_EXPECTED_FINGERS)
				{
					String errorCode = CommonsErrorCodes.C008_00021.getCode();
					String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.WRONG_NO_OF_EXPECTED_FINGERS (" +
																						result.getReturnCode() + ")"};
					resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
					return;
				}
				else
				{
					String errorCode = CommonsErrorCodes.C008_00022.getCode();
					String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.UNKNOWN (" +
																						result.getReturnCode() + ")"};
					resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
					return;
				}
			}
			else
			{
				if(position == FingerPosition.RIGHT_THUMB_SLAP.getPosition())
				{
					position = FingerPosition.RIGHT_THUMB.getPosition();
					
					if(takeRightThumbSlapAsRightThumb)
					{
						Finger segmentedRightThumb = new Finger(finger);
						segmentedRightThumb.setType(position);
						combinedFingerprints.add(segmentedRightThumb); // the segmented
					}
					
					combinedFingerprints.add(new Finger(finger)); // the slap
					fingerprintWsqToBeConvertedMap.put(position, finger.getImage());
				}
				else if(position == FingerPosition.LEFT_THUMB_SLAP.getPosition())
				{
					position = FingerPosition.LEFT_THUMB.getPosition();
					
					if(takeLeftThumbSlapAsLeftThumb)
					{
						Finger segmentedLeftThumb = new Finger(finger);
						segmentedLeftThumb.setType(position);
						combinedFingerprints.add(segmentedLeftThumb); // the segmented
					}
					
					combinedFingerprints.add(new Finger(finger)); // the slap
					fingerprintWsqToBeConvertedMap.put(position, finger.getImage());
				}
				else
				{
					combinedFingerprints.add(new Finger(finger)); // the segmented
					fingerprintWsqToBeConvertedMap.put(position, finger.getImage());
				}
			}
		}
		
		if(!fingerprintWsqToBeConvertedMap.isEmpty())
		{
			Future<TaskResponse<ConvertedFingerprintImagesResponse>>
									serviceResponseFuture = Context.getBioKitManager()
																   .getFingerprintUtilitiesService()
																   .convertWsqToImages(fingerprintWsqToBeConvertedMap);
			
			TaskResponse<ConvertedFingerprintImagesResponse> response;
			try
			{
				response = serviceResponseFuture.get();
			}
			catch(Exception e)
			{
				String errorCode = CommonsErrorCodes.C008_00023.getCode();
				String[] errorDetails = {"Failed to call the service for converting the WSQ!"};
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
				return;
			}
			
			resetWorkflowStepIfNegativeOrNullTaskResponse(response);
			ConvertedFingerprintImagesResponse result = response.getResult();
			
			if(result.getReturnCode() == ConvertedFingerprintImagesResponse.SuccessCodes.SUCCESS)
			{
				fingerprintImages.putAll(result.getFingerprintImagesMap());
			}
			else if(result.getReturnCode() ==
										ConvertedFingerprintImagesResponse.FailureCodes.FAILED_TO_CONVERT_WSQ_TO_IMAGE)
			{
				String errorCode = CommonsErrorCodes.C008_00024.getCode();
				String[] errorDetails =
						{"ConvertedFingerprintImagesResponse.FailureCodes.FAILED_TO_CONVERT_WSQ_TO_IMAGE (" +
																						result.getReturnCode() + ")"};
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
				return;
			}
			else
			{
				String errorCode = CommonsErrorCodes.C008_00025.getCode();
				String[] errorDetails = {"ConvertedFingerprintImagesResponse.FailureCodes.UNKNOWN (" +
																						result.getReturnCode() + ")"};
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
				return;
			}
		}
		
		this.fingerprintBase64Images = fingerprintImages;
	}
}