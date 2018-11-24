package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.HashMap;
import java.util.Map;

public interface WorkflowTask extends AppLogger
{
	void execute(Integer workflowId, Long workflowTcn) throws Signal, InterruptedException;
	
	default void mockExecute() throws Signal, InterruptedException
	{
		execute(null, null);
		
		
		//CountDownLatch latch = new CountDownLatch(1);
		//
		//Platform.runLater(() ->
		//{
		//	try
		//	{
		//		MockTaskDialogFxController captureFingerprintDialogFxController =
		//				DialogUtils.buildCustomDialogByFxml(Context.getCoreFxController().getStage(),
		//				                                    MockTaskDialogFxController.class,
		//				                                    AppUtils.getCoreStringsResourceBundle(Locale.getDefault()),
		//				                                    false);
		//
		//		List<TaskInput<?>> taskInputs = new ArrayList<>();
		//		Field[] declaredFields = getClass().getDeclaredFields();
		//
		//		for(Field declaredField : declaredFields)
		//		{
		//			declaredField.setAccessible(true);
		//
		//			Input input = declaredField.getAnnotation(Input.class);
		//
		//			if(input != null)
		//			{
		//				String name = declaredField.getName();
		//				Class<?> type = declaredField.getType();
		//				boolean alwaysRequired = input.alwaysRequired();
		//				String[] strings = input.requiredOnlyIf();
		//				String requirementConditions = strings.length > 0 ? Arrays.toString(strings) : null;
		//				Object value = declaredField.get(this);
		//				taskInputs.add( new TaskInput<>(name, type, alwaysRequired, requirementConditions, value));
		//			}
		//		}
		//
		//		captureFingerprintDialogFxController.setTaskName(getClass().getSimpleName());
		//		captureFingerprintDialogFxController.setTaskInputs(taskInputs);
		//		captureFingerprintDialogFxController.showDialogAndWait();
		//	}
		//	catch(Exception e)
		//	{
		//		e.printStackTrace();
		//	}
		//	latch.countDown();
		//});
		//
		//try
		//{
		//	latch.await();
		//}
		//catch(InterruptedException e)
		//{
		//	e.printStackTrace();
		//}
	}
	
	default <T> void resetWorkflowStepIfNegativeOrNullTaskResponse(TaskResponse<T> taskResponse) throws Signal
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
			            TaskResponse.failure(CoreErrorCodes.C002_00027.getCode(),
			                                 new String[]{"Null response from the task: " + getClass().getName()}));
			throw new Signal(SignalType.RESET_WORKFLOW_STEP, payload);
		}
	}
}