package sa.gov.nic.bio.bw.core.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class NicHijriCalendarData extends JavaBean
{
	private long isoStartDate;
	private Map<Integer, int[]> hijriYears;
	
	public long getIsoStartDate()
	{
		return isoStartDate;
	}
	public void setIsoStartDate(long isoStartDate)
	{
		this.isoStartDate = isoStartDate;
	}
	
	public Map<Integer, int[]> getHijriYears()
	{
		return hijriYears;
	}
	public void setHijriYears(Map<Integer, int[]> hijriYears)
	{
		this.hijriYears = hijriYears;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(System.lineSeparator());
		for(Entry<Integer, int[]> entry : hijriYears.entrySet())
		{
			sb.append("    ");
			sb.append(entry.getKey());
			sb.append("={");
			sb.append(Arrays.stream(entry.getValue()).mapToObj(String::valueOf).collect(
																				Collectors.joining(", ")));
			sb.append("}");
			sb.append(System.lineSeparator());
		}
		sb.append("  }");
		
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
							.append("isoStartDate", isoStartDate)
							.append("hijriYears", sb)
							.toString();
	}
}