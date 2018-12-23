package sa.gov.nic.bio.bw.core.workflow;

import java.util.Map;

public interface DataConveyor
{
	Map<String, Object> getDataMap();
	
	@SuppressWarnings("unchecked")
	default <T> T getData(Class<?> outputClass, String outputName)
	{
		return (T) getDataMap().get(outputClass.getName() + "#" + outputName);
	}
	
	default void passData(Class<?> outputClass, Class<?> inputClass, String... inputOutputNames)
	{
		for(String inputOutputName : inputOutputNames) passData(outputClass, inputClass, inputOutputName);
	}
	
	default void passData(Class<?> outputClass, Class<?> inputClass, String inputOutputName)
	{
		passData(outputClass, inputOutputName, inputClass, inputOutputName, null);
	}
	
	default void passData(Class<?> outputClass, String outputName, Class<?> inputClass, String inputName)
	{
		passData(outputClass, outputName, inputClass, inputName, null);
	}
	
	@SuppressWarnings("unchecked")
	default <T1, T2> void passData(Class<?> outputClass, String outputName, Class<?> inputClass, String inputName,
                                   Converter<T1, T2> converter)
	{
		Object value = getDataMap().get(outputClass.getName() + "#" + outputName);
		if(converter != null) value = converter.convert((T1) value);
		setData(inputClass, inputName, value);
	}
	
	default void setData(Class<?> inputClass, String inputName, Object value)
	{
		getDataMap().put(inputClass.getName() + "#" + inputName, value);
	}
	
	default void removeData(Class<?> inputClass, String inputName)
	{
		getDataMap().remove(inputClass.getName() + "#" + inputName);
	}
}