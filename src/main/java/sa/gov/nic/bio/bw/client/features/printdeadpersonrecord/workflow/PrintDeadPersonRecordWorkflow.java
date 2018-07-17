package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.workflow.GetPersonInfoByIdService;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.RecordIdPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.ShowRecordPaneFxController;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PrintDeadPersonRecordWorkflow extends WizardWorkflowBase<Void, Void>
{
	public PrintDeadPersonRecordWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                     BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Map<String, Object> onStep(int step) throws InterruptedException, Signal
	{
		Map<String, Object> uiOutputData;
		
		switch(step)
		{
			case 0:
			{
				formRenderer.get().renderForm(RecordIdPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				
				while(true)
				{
					Long recordId = (Long) uiOutputData.get(RecordIdPaneFxController.KEY_RECORD_ID);
					
					ServiceResponse<DeadPersonRecord> serviceResponse = DeadPersonRecordByIdService.execute(recordId);
					
					if(serviceResponse.isSuccess())
					{
						DeadPersonRecord result = serviceResponse.getResult();
						uiInputData.put(ShowRecordPaneFxController.KEY_DEAD_PERSON_RECORD, result);
						
						Long samisId = result.getSamisId();
						
						if(samisId != null)
						{
							ServiceResponse<PersonInfo> response = GetPersonInfoByIdService.execute(samisId,
							                                                                        0);
							if(response.isSuccess()) // success with person info
							{
								uiInputData.put(ShowRecordPaneFxController.KEY_PERSON_INFO, response.getResult());
								uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
								formRenderer.get().renderForm(RecordIdPaneFxController.class, uiInputData);
								uiOutputData = waitForUserTask();
								uiInputData.putAll(uiOutputData);
								break;
							}
						}
						else // success without person info
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							formRenderer.get().renderForm(RecordIdPaneFxController.class, uiInputData);
							uiOutputData = waitForUserTask();
							uiInputData.putAll(uiOutputData);
							break;
						}
					}
					
					// failure
					
					uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
					formRenderer.get().renderForm(RecordIdPaneFxController.class, uiInputData);
					uiOutputData = waitForUserTask();
					uiInputData.putAll(uiOutputData);
				}
				
				break;
			}
			case 1:
			{
				formRenderer.get().renderForm(ShowRecordPaneFxController.class, uiInputData);
				uiOutputData = waitForUserTask();
				uiInputData.putAll(uiOutputData);
				break;
			}
			default:
			{
				uiOutputData = waitForUserTask();
				break;
			}
		}
		
		return uiOutputData;
	}
}