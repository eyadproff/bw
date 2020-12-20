package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks;

import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.fingerprint.beans.ConvertedFingerprintImagesResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.utils.RegisterCriminalClearanceReportErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SegmentWsqFingerprintsWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true) private List<Finger> fingerprints;
    @Input(alwaysRequired = true) private List<Integer> missingFingerprints;
    @Output private List<Fingerprint> segmentedFingerPrints;
    @Output private Map<Integer, String> fingerprintBase64Images;

    @Override
    public void execute() throws Signal {
        List<Integer> availableFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        availableFingerprints.removeAll(missingFingerprints);

        segmentedFingerPrints = new ArrayList<>();
        Map<Integer, String> fingerprintWsqToBeConvertedMap = new HashMap<>();
        Map<Integer, String> fingerprintImages = new HashMap<>();

        for (Finger finger : fingerprints) {
            int position = finger.getType();

            if (position == FingerPosition.RIGHT_SLAP.getPosition() ||
                position == FingerPosition.LEFT_SLAP.getPosition() ||
                position == FingerPosition.TWO_THUMBS.getPosition() ||
                position == FingerPosition.RIGHT_THUMB_SLAP.getPosition() ||
                position == FingerPosition.LEFT_THUMB_SLAP.getPosition() ||
                position == FingerPosition.RIGHT_LITTLE.getPosition() ||
                position == FingerPosition.RIGHT_RING.getPosition() ||
                position == FingerPosition.RIGHT_MIDDLE.getPosition() ||
                position == FingerPosition.RIGHT_INDEX.getPosition() ||
                position == FingerPosition.LEFT_LITTLE.getPosition() ||
                position == FingerPosition.LEFT_RING.getPosition() ||
                position == FingerPosition.LEFT_MIDDLE.getPosition() ||
                position == FingerPosition.LEFT_INDEX.getPosition() ||
                position == FingerPosition.RIGHT_THUMB.getPosition() ||
                position == FingerPosition.LEFT_THUMB.getPosition()) {


                String slapImageBase64 = finger.getImage();
                String slapImageFormat = "WSQ";
                int expectedFingersCount = 0;

                List<Integer> slapMissingFingers = new ArrayList<>();

                if (position == FingerPosition.RIGHT_SLAP.getPosition()) {
                    for (int i = FingerPosition.RIGHT_INDEX.getPosition();
                         i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++) {
                        if (availableFingerprints.contains(i)) { expectedFingersCount++; }
                        else { slapMissingFingers.add(i); }
                    }
                }
                else if (position == FingerPosition.LEFT_SLAP.getPosition()) {
                    for (int i = FingerPosition.LEFT_INDEX.getPosition();
                         i <= FingerPosition.LEFT_LITTLE.getPosition(); i++) {
                        if (availableFingerprints.contains(i)) { expectedFingersCount++; }
                        else { slapMissingFingers.add(i); }
                    }
                }
                else if (position == FingerPosition.TWO_THUMBS.getPosition()) {

                    if (availableFingerprints.contains(FingerPosition.RIGHT_THUMB.getPosition())) { expectedFingersCount++; }
                    else { slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition()); }

                    if (availableFingerprints.contains(FingerPosition.LEFT_THUMB.getPosition())) { expectedFingersCount++; }
                    else { slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition()); }

                }
                else if (position == FingerPosition.RIGHT_THUMB_SLAP.getPosition() || position == FingerPosition.RIGHT_THUMB.getPosition()) {

                    if (availableFingerprints.contains(FingerPosition.RIGHT_THUMB.getPosition())) { expectedFingersCount++; }
                    else { slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition()); }

                    slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition());
                    position = FingerPosition.TWO_THUMBS.getPosition();

                }
                else if (position == FingerPosition.LEFT_THUMB_SLAP.getPosition() || position == FingerPosition.LEFT_THUMB.getPosition()) {

                    if (availableFingerprints.contains(FingerPosition.LEFT_THUMB.getPosition())) { expectedFingersCount++; }
                    else { slapMissingFingers.add(FingerPosition.LEFT_THUMB.getPosition()); }

                    slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
                    position = FingerPosition.TWO_THUMBS.getPosition();

                }
                else if (position == FingerPosition.LEFT_LITTLE.getPosition() || position == FingerPosition.LEFT_RING.getPosition() ||
                         position == FingerPosition.LEFT_MIDDLE.getPosition() || position == FingerPosition.LEFT_INDEX.getPosition()) {

                    if (availableFingerprints.contains(position)) { expectedFingersCount++; }
                    else { slapMissingFingers.add(position); }

                    for (int i = FingerPosition.LEFT_INDEX.getPosition();
                         i <= FingerPosition.LEFT_LITTLE.getPosition(); i++) {
                        if (position != i) { slapMissingFingers.add(i); }
                    }
                    position = FingerPosition.LEFT_SLAP.getPosition();

                }
                else if (position == FingerPosition.RIGHT_LITTLE.getPosition() || position == FingerPosition.RIGHT_RING.getPosition() ||
                         position == FingerPosition.RIGHT_MIDDLE.getPosition() || position == FingerPosition.RIGHT_INDEX.getPosition()) {

                    if (availableFingerprints.contains(position)) { expectedFingersCount++; }
                    else { slapMissingFingers.add(position); }

                    for (int i = FingerPosition.RIGHT_INDEX.getPosition();
                         i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++) {
                        if (position != i) { slapMissingFingers.add(i); }
                    }
                    position = FingerPosition.RIGHT_SLAP.getPosition();

                }

                Future<TaskResponse<SegmentFingerprintsResponse>>
                        serviceResponseFuture = Context.getBioKitManager()
                        .getFingerprintUtilitiesService()
                        .segmentSlap(slapImageBase64, slapImageFormat,
                                position, expectedFingersCount,
                                slapMissingFingers);

                TaskResponse<SegmentFingerprintsResponse> response;
                try {
                    response = serviceResponseFuture.get();
                } catch (Exception e) {
                    if (e instanceof ExecutionException && e.getCause() instanceof NotConnectedException) {
                        String errorCode = RegisterCriminalClearanceReportErrorCodes.N020_00001.getCode();
                        resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode));
                        return;
                    }

                    String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00007.getCode();
                    String[] errorDetails = {"Failed to call the service for segmenting the fingerprints!"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
                    return;
                }

                resetWorkflowStepIfNegativeOrNullTaskResponse(response);

                SegmentFingerprintsResponse result = response.getResult();
                if (result.getReturnCode() == SegmentFingerprintsResponse.SuccessCodes.SUCCESS) {
                    List<DMFingerData> fingerData = result.getFingerData();

                    fingerData.forEach(dmFingerData ->
                    {
                        fingerprintWsqToBeConvertedMap.put(dmFingerData.getPosition(),
                                dmFingerData.getFingerWsqImage());
                        Fingerprint fingerprint = new Fingerprint(dmFingerData,
                                slapImageBase64, dmFingerData.getFinger());
                        segmentedFingerPrints.add(fingerprint);
                    });
                }
                else if (result.getReturnCode() == SegmentFingerprintsResponse.FailureCodes.SEGMENTATION_FAILED) {
                    String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00008.getCode();
                    String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.SEGMENTATION_FAILED (" +
                                             result.getReturnCode() + ")"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                    return;
                }
                else if (result.getReturnCode() == SegmentFingerprintsResponse.FailureCodes.WRONG_NO_OF_EXPECTED_FINGERS) {
                    String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00009.getCode();
                    String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.WRONG_NO_OF_EXPECTED_FINGERS (" +
                                             result.getReturnCode() + ")"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                    return;
                }
                else {
                    String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00010.getCode();
                    String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.UNKNOWN (" +
                                             result.getReturnCode() + ")"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                    return;
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
                String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00011.getCode();
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
                String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00012.getCode();
                String[] errorDetails =
                        {"ConvertedFingerprintImagesResponse.FailureCodes.FAILED_TO_CONVERT_WSQ_TO_IMAGE (" +
                         result.getReturnCode() + ")"};
                resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                return;
            }
            else
            {
                String errorCode = RegisterCriminalClearanceReportErrorCodes.C020_00013.getCode();
                String[] errorDetails = {"ConvertedFingerprintImagesResponse.FailureCodes.UNKNOWN (" +
                                         result.getReturnCode() + ")"};
                resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                return;
            }
        }

        this.fingerprintBase64Images = fingerprintImages;
    }
}