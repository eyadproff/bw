package sa.gov.nic.bio.bw.client.features.commons.workflow;

import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.utils.PrintDeadPersonRecordPresentErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

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
	public ServiceResponse<?> execute()
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
					return ServiceResponse.failure(errorCode, e, errorDetails);
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
					return ServiceResponse.failure(response.getErrorCode(), response.getException(), errorDetails);
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
				return ServiceResponse.failure(errorCode, e, errorDetails);
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
				return ServiceResponse.failure(response.getErrorCode(), response.getException(), errorDetails);
			}
		}
		
		this.fingerprintImages = fingerprintImages;
		return ServiceResponse.success();
	}
}