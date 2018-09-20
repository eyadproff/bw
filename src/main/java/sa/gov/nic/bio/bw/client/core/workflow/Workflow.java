package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.controllers.BodyFxControllerBase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * The workflow that manages business processes and monitors their state. The workflow starts by starting its
 * <code>onProcess()</code> method. <code>waitForUserInput()</code> is called from within <code>onProcess()</code>
 * to wait for user tasks which can be submitted by a different thread/context using <code>submitUserInput()</code>.
 * Workflows can be nested, i.e. workflow A's onProcess() can invoke workflow B's onProcess().
 * <code>waitForUserInput()</code> can throw a signal which is used to carry and propagate a message to
 * a parent workflow.
 *
 * @param <I> type of the workflow's input. Use <code>Void</code> in case of not input
 * @param <O> type of the workflow's output. Use <code>Void</code> in case of no output
 *
 * @author Fouad Almalki
 */
public interface Workflow<I, O>
{
	String KEY_WEBSERVICE_RESPONSE = "WEBSERVICE_RESPONSE";
	String KEY_SIGNAL_TYPE = "SIGNAL_TYPE";
	String KEY_ERROR_CODE = "ERROR_CODE";
	String KEY_ERROR_DETAILS = "ERROR_DETAILS";
	String KEY_EXCEPTION = "EXCEPTION";
	
	/**
	 * The body of the workflow.
	 *
	 * @param input the workflow's input if any, otherwise <code>null</code>
	 *
	 * @return the workflow's output if any, otherwise <code>null</code>
	 * @throws InterruptedException thrown upon interrupting the caller thread
	 * @throws Signal thrown to carry and propagate a message to a parent workflow
	 */
	O onProcess(I input) throws InterruptedException, Signal;
	
	/**
	 * Submit a user task to the workflow.
	 *
	 * @param uiDataMap the data submitted by the user when filling the form
	 */
	void submitUserInput(Map<String, Object> uiDataMap);
	
	/**
	 * Waits until some other thread submits a user task.
	 *
	 * @throws InterruptedException thrown upon interrupting the workflow thread
	 * @throws Signal thrown in case the submitted user task is a signal
	 */
	void waitForUserInput() throws InterruptedException, Signal;
	
	void renderUi(Class<? extends BodyFxControllerBase> controllerClass) throws InterruptedException, Signal;
	
	void executeTask(Class<? extends WorkflowTask> taskClass) throws InterruptedException, Signal;
	
	static void loadWorkflowInputs(Object instance, Map<String, Object> uiInputData, boolean includeOutputs)
														throws IllegalAccessException, InstantiationException, Signal
	{
		Class<?> controllerClass = instance.getClass();
		Field[] declaredFields = instance.getClass().getDeclaredFields();
		
		for(Field declaredField : declaredFields)
		{
			declaredField.setAccessible(true);
			
			String fieldName = controllerClass.getName() + "#" + declaredField.getName();
			Class<?> declaredType = declaredField.getType();
			Input input = declaredField.getAnnotation(Input.class);
			
			if(input != null)
			{
				Object value = uiInputData.get(fieldName);
				if(value != null || !declaredType.isPrimitive()) declaredField.set(instance, value);
				
				if(input.required())
				{
					Class<? extends BooleanSupplier> requirementConditionClass = input.requirementCondition();
					BooleanSupplier requirementCondition = requirementConditionClass.newInstance();
					
					if(requirementCondition.getAsBoolean())
					{
						if(value == null)
						{
							String errorMessage = "The value of the required input (" + fieldName + ") is null!";
							Map<String, Object> payload = new HashMap<>();
							payload.put(Workflow.KEY_ERROR_DETAILS, new String[]{errorMessage});
							throw new Signal(SignalType.INVALID_STATE, payload);
						}
					}
				}
				
				continue;
			}
			
			Output output = declaredField.getAnnotation(Output.class);
			
			if(output != null)
			{
				Object value = uiInputData.get(fieldName);
				if(includeOutputs) declaredField.set(instance, value);
				else declaredField.set(instance, null);
			}
		}
	}
	
	static void saveWorkflowOutputs(Object instance, Map<String, Object> uiInputData) throws IllegalAccessException
	{
		Class<?> controllerClass = instance.getClass();
		Field[] declaredFields = controllerClass.getDeclaredFields();
		
		for(Field declaredField : declaredFields)
		{
			declaredField.setAccessible(true);
			
			String fieldName = controllerClass.getName() + "#" + declaredField.getName();
			Output output = declaredField.getAnnotation(Output.class);
			
			if(output != null)
			{
				Object value = declaredField.get(instance);
				uiInputData.put(fieldName, value);
			}
		}
	}
}