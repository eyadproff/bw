package sa.gov.nic.bio.bw.client.features.commons.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.utils.PrintDeadPersonRecordPresentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask implements WorkflowTask
{
	@Input(required = true) private List<Finger> fingerprints;
	@Input(required = true) private List<Integer> missingFingerprints;
	
	@Output private Map<Integer, String> fingerprintImages;
	
	@Override
	public void execute() throws Signal
	{
		List<Integer> availableFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
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
					if(availableFingerprints.contains(FingerPosition.RIGHT_THUMB.getPosition())) expectedFingersCount++;
					else slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
					
					if(availableFingerprints.contains(FingerPosition.LEFT_THUMB.getPosition())) expectedFingersCount++;
					else slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition());
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
					String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00001.getCode();
					String[] errorDetails =
							{"Failed to call the service for segmenting the fingerprints!"};
					resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
					return;
				}
				
				resetWorkflowStepIfNegativeOrNullTaskResponse(response);
				
				SegmentFingerprintsResponse result = response.getResult();
				List<DMFingerData> fingerData = result.getFingerData();
				fingerData.forEach(dmFingerData -> fingerprintImages.put(dmFingerData.getPosition(),
				                                                         dmFingerData.getFinger()));
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
			Future<TaskResponse<ConvertedFingerprintImagesResponse>>
												serviceResponseFuture = Context.getBioKitManager()
																			   .getFingerprintUtilitiesService()
																			   .convertWsqToImages(fingerprintWsqMap);
			
			TaskResponse<ConvertedFingerprintImagesResponse> response;
			try
			{
				response = serviceResponseFuture.get();
			}
			catch(Exception e)
			{
				String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00002.getCode();
				String[] errorDetails = {"Failed to call the service for converting the WSQ!"};
				resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
				return;
			}
			
			resetWorkflowStepIfNegativeOrNullTaskResponse(response);
			
			ConvertedFingerprintImagesResponse responseResult = response.getResult();
			Map<Integer, String> result = responseResult.getFingerprintImagesMap();
			fingerprintImages.putAll(result);
		}
		
		this.fingerprintImages = fingerprintImages;
	}
}