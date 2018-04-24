package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import java.util.List;
import java.util.Objects;

public class JudgementInfo
{
	private String judgIssuer;
	private long judgDate;
	private String judgNum;
	private int judgLashesCount;
	private int judgFine;
	private String judgOthers;
	private int jailYearCount;
	private int jailMonthCount;
	private int jailDayCount;
	private int trvlBanDayCount;
	private int trvlBanMonthCount;
	private int trvlBanYearCount;
	private int deportDayCount;
	private int deportMonthCount;
	private int deportYearCount;
	private int exileDayCount;
	private int exileMonthCount;
	private int exileYearCount;
	private boolean finalDeport;
	private boolean covenant;
	private boolean libel;
	private List<CrimeCode> crimeCodes;
	private String policeFileNum;
	private long arrestDate;
	
	public JudgementInfo(String judgIssuer, long judgDate, String judgNum, int judgLashesCount, int judgFine,
	                     String judgOthers, int jailYearCount, int jailMonthCount, int jailDayCount,
	                     int trvlBanDayCount, int trvlBanMonthCount, int trvlBanYearCount, int deportDayCount,
	                     int deportMonthCount, int deportYearCount, int exileDayCount, int exileMonthCount,
	                     int exileYearCount, boolean finalDeport, boolean covenant, boolean libel,
	                     List<CrimeCode> crimeCodes, String policeFileNum, long arrestDate)
	{
		this.judgIssuer = judgIssuer;
		this.judgDate = judgDate;
		this.judgNum = judgNum;
		this.judgLashesCount = judgLashesCount;
		this.judgFine = judgFine;
		this.judgOthers = judgOthers;
		this.jailYearCount = jailYearCount;
		this.jailMonthCount = jailMonthCount;
		this.jailDayCount = jailDayCount;
		this.trvlBanDayCount = trvlBanDayCount;
		this.trvlBanMonthCount = trvlBanMonthCount;
		this.trvlBanYearCount = trvlBanYearCount;
		this.deportDayCount = deportDayCount;
		this.deportMonthCount = deportMonthCount;
		this.deportYearCount = deportYearCount;
		this.exileDayCount = exileDayCount;
		this.exileMonthCount = exileMonthCount;
		this.exileYearCount = exileYearCount;
		this.finalDeport = finalDeport;
		this.covenant = covenant;
		this.libel = libel;
		this.crimeCodes = crimeCodes;
		this.policeFileNum = policeFileNum;
		this.arrestDate = arrestDate;
	}
	
	public String getJudgIssuer(){return judgIssuer;}
	public void setJudgIssuer(String judgIssuer){this.judgIssuer = judgIssuer;}
	
	public long getJudgDate(){return judgDate;}
	public void setJudgDate(long judgDate){this.judgDate = judgDate;}
	
	public String getJudgNum(){return judgNum;}
	public void setJudgNum(String judgNum){this.judgNum = judgNum;}
	
	public int getJudgLashesCount(){return judgLashesCount;}
	public void setJudgLashesCount(int judgLashesCount){this.judgLashesCount = judgLashesCount;}
	
	public int getJudgFine(){return judgFine;}
	public void setJudgFine(int judgFine){this.judgFine = judgFine;}
	
	public String getJudgOthers(){return judgOthers;}
	public void setJudgOthers(String judgOthers){this.judgOthers = judgOthers;}
	
	public int getJailYearCount(){return jailYearCount;}
	public void setJailYearCount(int jailYearCount){this.jailYearCount = jailYearCount;}
	
	public int getJailMonthCount(){return jailMonthCount;}
	public void setJailMonthCount(int jailMonthCount){this.jailMonthCount = jailMonthCount;}
	
	public int getJailDayCount(){return jailDayCount;}
	public void setJailDayCount(int jailDayCount){this.jailDayCount = jailDayCount;}
	
	public int getTrvlBanDayCount(){return trvlBanDayCount;}
	public void setTrvlBanDayCount(int trvlBanDayCount){this.trvlBanDayCount = trvlBanDayCount;}
	
	public int getTrvlBanMonthCount(){return trvlBanMonthCount;}
	public void setTrvlBanMonthCount(int trvlBanMonthCount){this.trvlBanMonthCount = trvlBanMonthCount;}
	
	public int getTrvlBanYearCount(){return trvlBanYearCount;}
	public void setTrvlBanYearCount(int trvlBanYearCount){this.trvlBanYearCount = trvlBanYearCount;}
	
	public int getDeportDayCount(){return deportDayCount;}
	public void setDeportDayCount(int deportDayCount){this.deportDayCount = deportDayCount;}
	
	public int getDeportMonthCount(){return deportMonthCount;}
	public void setDeportMonthCount(int deportMonthCount){this.deportMonthCount = deportMonthCount;}
	
	public int getDeportYearCount(){return deportYearCount;}
	public void setDeportYearCount(int deportYearCount){this.deportYearCount = deportYearCount;}
	
	public int getExileDayCount(){return exileDayCount;}
	public void setExileDayCount(int exileDayCount){this.exileDayCount = exileDayCount;}
	
	public int getExileMonthCount(){return exileMonthCount;}
	public void setExileMonthCount(int exileMonthCount){this.exileMonthCount = exileMonthCount;}
	
	public int getExileYearCount(){return exileYearCount;}
	public void setExileYearCount(int exileYearCount){this.exileYearCount = exileYearCount;}
	
	public boolean isFinalDeport(){return finalDeport;}
	public void setFinalDeport(boolean finalDeport){this.finalDeport = finalDeport;}
	
	public boolean isCovenant(){return covenant;}
	public void setCovenant(boolean covenant){this.covenant = covenant;}
	
	public boolean isLibel(){return libel;}
	public void setLibel(boolean libel){this.libel = libel;}
	
	public List<CrimeCode> getCrimeCodes(){return crimeCodes;}
	public void setCrimeCodes(List<CrimeCode> crimeCodes){this.crimeCodes = crimeCodes;}
	
	public String getPoliceFileNum(){return policeFileNum;}
	public void setPoliceFileNum(String policeFileNum){this.policeFileNum = policeFileNum;}
	
	public long getArrestDate(){return arrestDate;}
	public void setArrestDate(long arrestDate){this.arrestDate = arrestDate;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		JudgementInfo that = (JudgementInfo) o;
		return judgDate == that.judgDate && judgLashesCount == that.judgLashesCount && judgFine == that.judgFine &&
			   jailYearCount == that.jailYearCount && jailMonthCount == that.jailMonthCount &&
			   jailDayCount == that.jailDayCount && trvlBanDayCount == that.trvlBanDayCount &&
			   trvlBanMonthCount == that.trvlBanMonthCount && trvlBanYearCount == that.trvlBanYearCount &&
			   deportDayCount == that.deportDayCount && deportMonthCount == that.deportMonthCount &&
			   deportYearCount == that.deportYearCount && exileDayCount == that.exileDayCount &&
			   exileMonthCount == that.exileMonthCount && exileYearCount == that.exileYearCount &&
			   finalDeport == that.finalDeport && covenant == that.covenant && libel == that.libel &&
			   arrestDate == that.arrestDate && Objects.equals(judgIssuer, that.judgIssuer) &&
			   Objects.equals(judgNum, that.judgNum) && Objects.equals(judgOthers, that.judgOthers) &&
			   Objects.equals(crimeCodes, that.crimeCodes) && Objects.equals(policeFileNum, that.policeFileNum);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(judgIssuer, judgDate, judgNum, judgLashesCount, judgFine, judgOthers, jailYearCount,
		                    jailMonthCount, jailDayCount, trvlBanDayCount, trvlBanMonthCount, trvlBanYearCount,
		                    deportDayCount, deportMonthCount, deportYearCount, exileDayCount, exileMonthCount,
		                    exileYearCount, finalDeport, covenant, libel, crimeCodes, policeFileNum, arrestDate);
	}
	
	@Override
	public String toString()
	{
		return "JudgementInfo{" + "judgIssuer='" + judgIssuer + '\'' + ", judgDate=" + judgDate + ", judgNum='" +
			   judgNum + '\'' + ", judgLashesCount=" + judgLashesCount + ", judgFine=" + judgFine + ", judgOthers='" +
			   judgOthers + '\'' + ", jailYearCount=" + jailYearCount + ", jailMonthCount=" + jailMonthCount +
			   ", jailDayCount=" + jailDayCount + ", trvlBanDayCount=" + trvlBanDayCount + ", trvlBanMonthCount=" +
			   trvlBanMonthCount + ", trvlBanYearCount=" + trvlBanYearCount + ", deportDayCount=" + deportDayCount +
			   ", deportMonthCount=" + deportMonthCount + ", deportYearCount=" + deportYearCount + ", exileDayCount=" +
			   exileDayCount + ", exileMonthCount=" + exileMonthCount + ", exileYearCount=" + exileYearCount +
			   ", finalDeport=" + finalDeport + ", covenant=" + covenant + ", libel=" + libel + ", crimeCodes=" +
			   crimeCodes + ", policeFileNum='" + policeFileNum + '\'' + ", arrestDate=" + arrestDate + '}';
	}
}