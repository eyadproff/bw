package sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks;

import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.fingerprint.beans.SegmentFingerprintsResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.FingerCoordinate;
import sa.gov.nic.bio.bw.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SegmentWsqFingerprintsWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true) private List<Finger> fingerprints;
    @Input(alwaysRequired = true) private List<Integer> missingFingerprints;
    @Output private List<Fingerprint> segmentedFingerPrints;

    @Override
    public void execute() throws Signal {
        List<Integer> availableFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        availableFingerprints.removeAll(missingFingerprints);

        segmentedFingerPrints = new ArrayList<>();

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
                    //                    slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
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
                    //                    slapMissingFingers.add(FingerPosition.RIGHT_THUMB.getPosition());
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
                if (result.getReturnCode() == SegmentFingerprintsResponse.SuccessCodes.SUCCESS) {
                    List<DMFingerData> fingerData = result.getFingerData();
//                    List<FingerCoordinate> fingerCoordinates = new ArrayList<>();

                    fingerData.forEach(dmFingerData ->
                    {
                        Fingerprint fingerprint = new Fingerprint(dmFingerData,
                                slapImageBase64, dmFingerData.getFinger());
                        segmentedFingerPrints.add(fingerprint);
                    });
                }
                else if (result.getReturnCode() == SegmentFingerprintsResponse.FailureCodes.SEGMENTATION_FAILED) {
                    String errorCode = CommonsErrorCodes.C008_00020.getCode();
                    String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.SEGMENTATION_FAILED (" +
                                             result.getReturnCode() + ")"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                    return;
                }
                else if (result.getReturnCode() == SegmentFingerprintsResponse.FailureCodes.WRONG_NO_OF_EXPECTED_FINGERS) {
                    String errorCode = CommonsErrorCodes.C008_00021.getCode();
                    String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.WRONG_NO_OF_EXPECTED_FINGERS (" +
                                             result.getReturnCode() + ")"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                    return;
                }
                else {
                    String errorCode = CommonsErrorCodes.C008_00022.getCode();
                    String[] errorDetails = {"SegmentFingerprintsResponse.FailureCodes.UNKNOWN (" +
                                             result.getReturnCode() + ")"};
                    resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, errorDetails));
                    return;
                }
            }
        }

    }
}