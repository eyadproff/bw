package sa.gov.nic.bio.bw.core.workflow;

import javafx.application.Platform;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.TaskInput;
import sa.gov.nic.bio.bw.core.controllers.MockTaskDialogFxController;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public abstract class WorkflowTask implements AppLogger
{
	@Input protected Integer workflowId;
	@Input protected Long workflowTcn;
	
	private final Map<Integer, Runnable> mocks = new HashMap<>();
	
	public Map<Integer, Runnable> getMocks(){return mocks;}
	
	public abstract void execute() throws Throwable;
	
	public void mockExecute() throws Throwable
	{
		Integer[] selectedIndex = {-1};
		CountDownLatch latch = new CountDownLatch(1);
		
		Platform.runLater(() ->
		{
			try
			{
				MockTaskDialogFxController captureFingerprintDialogFxController =
						DialogUtils.buildCustomDialogByFxml(Context.getCoreFxController().getStage(),
						                                    MockTaskDialogFxController.class,
						                                    false);
		
				List<TaskInput<?>> taskInputs = new ArrayList<>();
				java.lang.reflect.Field[] declaredFields = getClass().getDeclaredFields();
		
				for(java.lang.reflect.Field declaredField : declaredFields)
				{
					declaredField.setAccessible(true);
		
					Input input = declaredField.getAnnotation(Input.class);
		
					if(input != null)
					{
						String name = declaredField.getName();
						Class<?> type = declaredField.getType();
						boolean alwaysRequired = input.alwaysRequired();
						String[] strings = input.requiredOnlyIf();
						String requirementConditions = strings.length > 0 ? Arrays.toString(strings) : null;
						Object value = declaredField.get(this);
						taskInputs.add( new TaskInput<>(name, type, alwaysRequired, requirementConditions, value));
					}
				}
				
				captureFingerprintDialogFxController.setTaskName(getClass().getSimpleName());
				captureFingerprintDialogFxController.setTaskInputs(taskInputs);
				selectedIndex[0] = captureFingerprintDialogFxController.showDialogAndWait();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			latch.countDown();
		});
		
		try
		{
			latch.await();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		if(selectedIndex[0] == -1) execute(); // real task
		else // mock task
		{
			Runnable runnable = mocks.get(selectedIndex[0]);
			runnable.run();
		}
	}
	
	protected <T> void resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse<T> taskResponse) throws Signal
	{
		if(!taskResponse.isSuccess())
		{
			Map<String, Object> payload = new HashMap<>();
			payload.put(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
			throw new Signal(SignalType.RESET_WORKFLOW_STEP, payload);
		}
		
		if(taskResponse.getResult() == null)
		{
			Map<String, Object> payload = new HashMap<>();
			payload.put(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE,
			            TaskResponse.failure(CoreErrorCodes.C002_00023.getCode(),
			                                 new String[]{"Null response from the task: " + getClass().getName()}));
			throw new Signal(SignalType.RESET_WORKFLOW_STEP, payload);
		}
	}
}