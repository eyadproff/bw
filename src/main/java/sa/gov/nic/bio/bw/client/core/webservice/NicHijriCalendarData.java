package sa.gov.nic.bio.bw.client.core.webservice;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class NicHijriCalendarData implements Serializable
{
	@JsonProperty("isostartDate")
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
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		NicHijriCalendarData that = (NicHijriCalendarData) o;
		return isoStartDate == that.isoStartDate && Objects.equals(hijriYears, that.hijriYears);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(isoStartDate, hijriYears);
	}
	
	@Override
	public String toString()
	{
		return "NicHijriCalendarData{" + "isoStartDate=" + isoStartDate + ", hijriYears=" + hijriYears + '}';
	}
}