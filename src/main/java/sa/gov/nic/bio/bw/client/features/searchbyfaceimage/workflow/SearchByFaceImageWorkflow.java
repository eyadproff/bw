package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ConfirmImageFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ImageSourceFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.SearchFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ShowResultFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.UploadImageFileFxController;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice.Candidate;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SearchByFaceImageWorkflow extends WorkflowBase<Void, Void>
{
	public static final String KEY_IMAGE_SOURCE = "IMAGE_SOURCE";
	public static final String KEY_UPLOADED_IMAGE = "UPLOADED_IMAGE";
	public static final String KEY_FINAL_IMAGE_BASE64 = "FINAL_IMAGE_BASE64";
	public static final String KEY_FINAL_IMAGE = "FINAL_IMAGE";
	public static final String KEY_CANDIDATES = "CANDIDATES";
	public static final String VALUE_IMAGE_SOURCE_UPLOAD = "IMAGE_SOURCE_UPLOAD";
	public static final String VALUE_IMAGE_SOURCE_CAMERA = "IMAGE_SOURCE_CAMERA";
	
	public SearchByFaceImageWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                 BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		
		while(true)
		{
			int step = 1;
			
			while(true)
			{
				Map<String, Object> uiOutputData = null;
				
				switch(step)
				{
					case 1:
					{
						formRenderer.get().renderForm(ImageSourceFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 2:
					{
						String imageInput = (String) uiInputData.get(KEY_IMAGE_SOURCE);
						
						if(VALUE_IMAGE_SOURCE_CAMERA.equals(imageInput))
						{
							uiInputData.put(FaceCapturingFxController.KEY_ICAO_REQUIRED, Boolean.FALSE);
							formRenderer.get().renderForm(FaceCapturingFxController.class, uiInputData);
						}
						else if(VALUE_IMAGE_SOURCE_UPLOAD.equals(imageInput))
						{
							formRenderer.get().renderForm(UploadImageFileFxController.class, uiInputData);
						}
						
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 3:
					{
						formRenderer.get().renderForm(ConfirmImageFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 4:
					{
						// show progress indicator here
						formRenderer.get().renderForm(SearchFxController.class, uiInputData);
						
						String imageBase64 = (String) uiInputData.get(SearchByFaceImageWorkflow.KEY_FINAL_IMAGE_BASE64);
						ServiceResponse<List<Candidate>> response = SearchByFaceImageService.execute(imageBase64);
						uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
						
						// if success, ask for goNext() automatically. Otherwise, show failure message and retry button
						formRenderer.get().renderForm(SearchFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
					case 5:
					{
						formRenderer.get().renderForm(ShowResultFxController.class, uiInputData);
						uiOutputData = waitForUserTask();
						uiInputData.putAll(uiOutputData);
						break;
					}
				}
				
				if(uiOutputData != null)
				{
					Object direction = uiOutputData.get("direction");
					if("backward".equals(direction)) step--;
					else if("forward".equals(direction)) step++;
					else if("startOver".equals(direction)) step = 1;
				}
			}
		}
	}
}