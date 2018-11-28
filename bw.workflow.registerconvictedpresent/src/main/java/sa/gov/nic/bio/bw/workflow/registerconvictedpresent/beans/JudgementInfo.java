package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class JudgementInfo extends JavaBean
{
	private String judgIssuer;
	private long judgDate;
	private String judgNum;
	private int judgTazeerLashesCount;
	private int judgHadLashesCount;
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
	private String policeFileNum;
	private Long arrestDate;
	
	public JudgementInfo(String judgIssuer, long judgDate, String judgNum, int judgTazeerLashesCount,
	                     int judgHadLashesCount, int judgFine, String judgOthers, int jailYearCount, int jailMonthCount,
	                     int jailDayCount, int trvlBanDayCount, int trvlBanMonthCount, int trvlBanYearCount,
	                     int deportDayCount, int deportMonthCount, int deportYearCount, int exileDayCount,
	                     int exileMonthCount, int exileYearCount, boolean finalDeport, boolean covenant, boolean libel,
	                     String policeFileNum, Long arrestDate)
	{
		this.judgIssuer = judgIssuer;
		this.judgDate = judgDate;
		this.judgNum = judgNum;
		this.judgTazeerLashesCount = judgTazeerLashesCount;
		this.judgHadLashesCount = judgHadLashesCount;
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
		this.policeFileNum = policeFileNum;
		this.arrestDate = arrestDate;
	}
	
	public String getJudgIssuer(){return judgIssuer;}
	public void setJudgIssuer(String judgIssuer){this.judgIssuer = judgIssuer;}
	
	public long getJudgDate(){return judgDate;}
	public void setJudgDate(long judgDate){this.judgDate = judgDate;}
	
	public String getJudgNum(){return judgNum;}
	public void setJudgNum(String judgNum){this.judgNum = judgNum;}
	
	public int getJudgTazeerLashesCount(){return judgTazeerLashesCount;}
	public void setJudgTazeerLashesCount(int judgTazeerLashesCount){this.judgTazeerLashesCount = judgTazeerLashesCount;}
	
	public int getJudgHadLashesCount(){return judgHadLashesCount;}
	public void setJudgHadLashesCount(int judgHadLashesCount){this.judgHadLashesCount = judgHadLashesCount;}
	
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
	
	public String getPoliceFileNum(){return policeFileNum;}
	public void setPoliceFileNum(String policeFileNum){this.policeFileNum = policeFileNum;}
	
	public Long getArrestDate(){return arrestDate;}
	public void setArrestDate(Long arrestDate){this.arrestDate = arrestDate;}
}