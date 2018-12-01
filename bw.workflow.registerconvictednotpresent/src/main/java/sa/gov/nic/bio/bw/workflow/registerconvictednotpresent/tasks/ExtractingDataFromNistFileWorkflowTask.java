package sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.tasks;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.beans.CriminalNistFile;
import sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.utils.RegisterConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.webservice.NistFileAPI;
import sa.gov.nic.bio.commons.TaskResponse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExtractingDataFromNistFileWorkflowTask implements WorkflowTask
{
	@Input(alwaysRequired = true) private String nistFilePath;
	
	@Output private PersonInfo personInfo;
	@Output private List<Finger> fingerprints;
	@Output private List<Integer> missingFingerprints;
	
	@Override
	public void execute(Integer workflowId, Long workflowTcn) throws Signal
	{
		byte[] bytes;
		try
		{
			bytes = Files.readAllBytes(Paths.get(nistFilePath));
		}
		catch(Exception e)
		{
			String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00002.getCode();
			String[] errorDetails = {"Failed to read the nist file (" + nistFilePath + ")!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}
		
		String nistFileBase64;
		try
		{
			nistFileBase64 = AppUtils.bytesToBase64(bytes);
		}
		catch(Exception e)
		{
			String errorCode = RegisterConvictedNotPresentErrorCodes.C009_00003.getCode();
			String[] errorDetails = {"Failed to convert the nist bytes to base64 (" + nistFilePath + ")!"};
			resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse.failure(errorCode, e, errorDetails));
			return;
		}
		
		NistFileAPI api = Context.getWebserviceManager().getApi(NistFileAPI.class);
		Call<CriminalNistFile> call = api.extractDataFromNistFile(workflowId, workflowTcn, nistFileBase64);
		TaskResponse<CriminalNistFile> taskResponse = Context.getWebserviceManager().executeApi(call);
		resetWorkflowStepIfNegativeOrNullTaskResponse(taskResponse);
		
		CriminalNistFile criminalNistFile = taskResponse.getResult();
		fingerprints = criminalNistFile.getFingerList();
		missingFingerprints = criminalNistFile.getMissing();
		
		String facePhotoBase64 = criminalNistFile.getFace();
		personInfo = new PersonInfo();
		if(facePhotoBase64 != null && !facePhotoBase64.isBlank()) personInfo.setFace(facePhotoBase64);
	}
}