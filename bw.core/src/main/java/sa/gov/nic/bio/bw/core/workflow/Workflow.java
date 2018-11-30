package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The workflow that manages business processes and monitors their state. The workflow starts by starting its
 * <code>onProcess()</code> method. <code>renderUiAndWaitForUserInput()</code> is called from within <code>onProcess()</code>
 * to wait for user tasks which can be submitted by a different thread/context using <code>submitUserInput()</code>.
 * Workflows can be nested, i.e. workflow A's onProcess() can invoke workflow B's onProcess().
 * <code>renderUiAndWaitForUserInput()</code> can throw a signal which is used to carry and propagate a message to
 * a parent workflow.
 *
 * @author Fouad Almalki
 */
public interface Workflow extends AppLogger
{
	String KEY_WORKFLOW_LOGIN = "WORKFLOW_LOGIN";
	String KEY_WORKFLOW_HOME = "WORKFLOW_HOME";
	String KEY_MENU_WORKFLOW_CLASS = "MENU_WORKFLOW_CLASS";
	String KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE = "WORKFLOW_TASK_NEGATIVE_RESPONSE";
	String KEY_SIGNAL_TYPE = "SIGNAL_TYPE";
	String KEY_ERROR_CODE = "ERROR_CODE";
	String KEY_ERROR_DETAILS = "ERROR_DETAILS";
	String KEY_EXCEPTION = "EXCEPTION";
	
	default String getId(){return null;}
	
	/**
	 * The body of the workflow.
	 *
	 * @throws InterruptedException thrown upon interrupting the caller thread
	 * @throws Signal thrown to carry and propagate a message to a parent workflow
	 */
	void onProcess() throws InterruptedException, Signal;
	
	/**
	 * Submit a user task to the workflow.
	 *
	 * @param uiDataMap the data submitted by the user when filling the form
	 */
	void submitUserInput(Map<String, Object> uiDataMap);
	
	/**
	 * Renders the UI based on the passed controllerClass and then waits until the controller submits user input.
	 *
	 * @param controllerClass the controller class that will be used to render the new UI
	 *
	 * @throws InterruptedException thrown upon interrupting the workflow thread
	 * @throws Signal thrown in case the submitted user task is a signal
	 */
	void renderUiAndWaitForUserInput(Class<? extends BodyFxControllerBase> controllerClass) throws InterruptedException,
	                                                                                               Signal;
	
	void executeWorkflowTask(Class<? extends WorkflowTask> taskClass) throws InterruptedException, Signal;
	
	void interrupt(Signal interruptionSignal);
	
	static void loadWorkflowInputs(Object instance, Map<String, Object> uiInputData, boolean includeOutputs,
	                               boolean onReturn) throws IllegalAccessException, Signal
	{
		Class<?> controllerClass = instance.getClass();
		List<Field> declaredFields = AppUtils.getAllFields(new ArrayList<>(), controllerClass);
		
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
				
				String[] requirements = input.requiredOnlyIf();
				boolean requiredOnReturn = input.requiredOnReturn();
				
				if(input.alwaysRequired() || requirements.length > 0 || (requiredOnReturn && onReturn))
				{
					boolean allRequirementsMet = true;
					
					for(String requirement : requirements)
					{
						String[] split = requirement.split("=");
						String requirementName = split[0];
						String requirementValue = split[1];
						
						Field field;
						try
						{
							field = controllerClass.getDeclaredField(requirementName);
						}
						catch(NoSuchFieldException e)
						{
							LOGGER.warning("The field (" + requirementName + " is not found inside the class (" +
									               controllerClass + ")!");
							allRequirementsMet = false;
							break;
						}
						
						field.setAccessible(true);
						Object fieldValue = field.get(instance);
						if(!String.valueOf(fieldValue).equals(requirementValue))
						{
							allRequirementsMet = false;
							break;
						}
					}
					
					if(allRequirementsMet)
					{
						if(value == null)
						{
							String errorCode = CoreErrorCodes.C002_00024.getCode();
							String[] errorDetails = {"The value of the alwaysRequired input (" + fieldName +
																										") is null!"};
							Map<String, Object> payload = new HashMap<>();
							payload.put(KEY_ERROR_CODE, errorCode);
							payload.put(KEY_ERROR_DETAILS, errorDetails);
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
		List<Field> declaredFields = AppUtils.getAllFields(new ArrayList<>(), controllerClass);
		
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