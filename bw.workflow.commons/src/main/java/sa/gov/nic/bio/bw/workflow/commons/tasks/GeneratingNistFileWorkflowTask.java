package sa.gov.nic.bio.bw.workflow.commons.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.NistFileResponse;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.workflow.commons.webservice.NistFileAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class GeneratingNistFileWorkflowTask extends WorkflowTask
{
	@Input private String facePhotoBase64;
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	@Input(alwaysRequired = true) private String nistOutputFilePath;

	@Override
	public void execute() throws Signal
	{
		List<Integer> missingFingerprints = new ArrayList<>();
		for(int i = 1; i <= 10; i++) missingFingerprints.add(i);
		fingerprintBase64Images.keySet().forEach(missingFingerprints::remove);

		var task = new ConvertFingerprintBase64ImagesToWsqWorkflowTask();
		task.setFingerprintBase64Images(fingerprintBase64Images);
		task.execute();

		var fingerprintWsqImages = task.getFingerprintWsqImages();
		var fingerprints = new FingerprintsWsqToFingerConverter().convert(fingerprintWsqImages);

		String fingerprintsJson = AppUtils.toJson(fingerprints);
		String missingFingerprintsJson = AppUtils.toJson(missingFingerprints);

		NistFileAPI api = Context.getWebserviceManager().getApi(NistFileAPI.class);
		Call<NistFileResponse> call = api.generateNistFile(workflowId, workflowTcn, facePhotoBase64, fingerprintsJson,
												 		   missingFingerprintsJson);
		TaskResponse<NistFileResponse> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);

		NistFileResponse nistFileResponse = taskResponse.getResult();
		byte[] bytes;
		try
		{
			bytes = Base64.getDecoder().decode(nistFileResponse.getNist());
			System.out.println("bytes.length = " + bytes.length);
		}
		catch(IllegalArgumentException e)
		{
			String errorCode = CommonsErrorCodes.C008_00048.getCode();
			String[] errorDetails = {"Failed to decode the base64-encoded NIST file!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}

		var path = Path.of(nistOutputFilePath);

		try
		{
			Files.write(path, bytes, StandardOpenOption.CREATE);
		}
		catch(IOException e)
		{
			String errorCode = CommonsErrorCodes.C008_00049.getCode();
			String[] errorDetails = {"Failed save the NIST file to the path (" + path + ")!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
		}
	}
}