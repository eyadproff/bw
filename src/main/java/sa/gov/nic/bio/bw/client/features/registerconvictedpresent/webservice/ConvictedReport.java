package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;

import java.util.List;
import java.util.Objects;

public class ConvictedReport
{
	private long reportNumber;
	private long reportDate;
	private Long generalFileNum;
	private Name subjtName;
	private int subjNationalityCode;
	private String subjOccupation;
	private String subjGender; //'M' or 'F'
	private Long subjBirthDate;
	private String subjBirthPlace;
	private String subjDocId;
	private Integer subjDocType;
	private Long subjDocIssDate;
	private Long subjDocExpDate;
	private JudgementInfo subjJudgementInfo;
	private List<Finger> subjFingers;
	private List<Integer> subjMissingFingers;
	private String subjFace;
	private String operatorId;
	
	public ConvictedReport(long reportNumber, long reportDate, Long generalFileNum, Name subjtName,
	                       int subjNationalityCode, String subjOccupation, String subjGender, Long subjBirthDate,
	                       String subjBirthPlace, String subjDocId, Integer subjDocType, Long subjDocIssDate,
	                       Long subjDocExpDate, JudgementInfo subjJudgementInfo, List<Finger> subjFingers,
	                       List<Integer> subjMissingFingers, String subjFace, String operatorId)
	{
		this.reportNumber = reportNumber;
		this.reportDate = reportDate;
		this.generalFileNum = generalFileNum;
		this.subjtName = subjtName;
		this.subjNationalityCode = subjNationalityCode;
		this.subjOccupation = subjOccupation;
		this.subjGender = subjGender;
		this.subjBirthDate = subjBirthDate;
		this.subjBirthPlace = subjBirthPlace;
		this.subjDocId = subjDocId;
		this.subjDocType = subjDocType;
		this.subjDocIssDate = subjDocIssDate;
		this.subjDocExpDate = subjDocExpDate;
		this.subjJudgementInfo = subjJudgementInfo;
		this.subjFingers = subjFingers;
		this.subjMissingFingers = subjMissingFingers;
		this.subjFace = subjFace;
		this.operatorId = operatorId;
	}
	
	public long getReportNumber(){return reportNumber;}
	public void setReportNumber(long reportNumber){this.reportNumber = reportNumber;}
	
	public long getReportDate(){return reportDate;}
	public void setReportDate(long reportDate){this.reportDate = reportDate;}
	
	public Long getGeneralFileNum(){return generalFileNum;}
	public void setGeneralFileNum(Long generalFileNum){this.generalFileNum = generalFileNum;}
	
	public Name getSubjtName(){return subjtName;}
	public void setSubjtName(Name subjtName){this.subjtName = subjtName;}
	
	public int getSubjNationalityCode(){return subjNationalityCode;}
	public void setSubjNationalityCode(int subjNationalityCode){this.subjNationalityCode = subjNationalityCode;}
	
	public String getSubjOccupation(){return subjOccupation;}
	public void setSubjOccupation(String subjOccupation){this.subjOccupation = subjOccupation;}
	
	public String getSubjGender(){return subjGender;}
	public void setSubjGender(String subjGender){this.subjGender = subjGender;}
	
	public Long getSubjBirthDate(){return subjBirthDate;}
	public void setSubjBirthDate(Long subjBirthDate){this.subjBirthDate = subjBirthDate;}
	
	public String getSubjBirthPlace(){return subjBirthPlace;}
	public void setSubjBirthPlace(String subjBirthPlace){this.subjBirthPlace = subjBirthPlace;}
	
	public String getSubjDocId(){return subjDocId;}
	public void setSubjDocId(String subjDocId){this.subjDocId = subjDocId;}
	
	public Integer getSubjDocType(){return subjDocType;}
	public void setSubjDocType(Integer subjDocType){this.subjDocType = subjDocType;}
	
	public Long getSubjDocIssDate(){return subjDocIssDate;}
	public void setSubjDocIssDate(Long subjDocIssDate){this.subjDocIssDate = subjDocIssDate;}
	
	public Long getSubjDocExpDate(){return subjDocExpDate;}
	public void setSubjDocExpDate(Long subjDocExpDate){this.subjDocExpDate = subjDocExpDate;}
	
	public JudgementInfo getSubjJudgementInfo(){return subjJudgementInfo;}
	public void setSubjJudgementInfo(JudgementInfo subjJudgementInfo){this.subjJudgementInfo = subjJudgementInfo;}
	
	public List<Finger> getSubjFingers(){return subjFingers;}
	public void setSubjFingers(List<Finger> subjFingers){this.subjFingers = subjFingers;}
	
	public List<Integer> getSubjMissingFingers(){return subjMissingFingers;}
	public void setSubjMissingFingers(List<Integer> subjMissingFingers){this.subjMissingFingers = subjMissingFingers;}
	
	public String getSubjFace(){return subjFace;}
	public void setSubjFace(String subjFace){this.subjFace = subjFace;}
	
	public String getOperatorId(){return operatorId;}
	public void setOperatorId(String operatorId){this.operatorId = operatorId;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ConvictedReport that = (ConvictedReport) o;
		return reportNumber == that.reportNumber && reportDate == that.reportDate &&
			   subjNationalityCode == that.subjNationalityCode &&
			   Objects.equals(generalFileNum, that.generalFileNum) && Objects.equals(subjtName, that.subjtName) &&
			   Objects.equals(subjOccupation, that.subjOccupation) && Objects.equals(subjGender, that.subjGender) &&
			   Objects.equals(subjBirthDate, that.subjBirthDate) &&
			   Objects.equals(subjBirthPlace, that.subjBirthPlace) && Objects.equals(subjDocId, that.subjDocId) &&
			   Objects.equals(subjDocType, that.subjDocType) && Objects.equals(subjDocIssDate, that.subjDocIssDate) &&
			   Objects.equals(subjDocExpDate, that.subjDocExpDate) &&
			   Objects.equals(subjJudgementInfo, that.subjJudgementInfo) &&
			   Objects.equals(subjFingers, that.subjFingers) &&
			   Objects.equals(subjMissingFingers, that.subjMissingFingers) &&
			   Objects.equals(subjFace, that.subjFace) && Objects.equals(operatorId, that.operatorId);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(reportNumber, reportDate, generalFileNum, subjtName, subjNationalityCode, subjOccupation,
		                    subjGender, subjBirthDate, subjBirthPlace, subjDocId, subjDocType, subjDocIssDate,
		                    subjDocExpDate, subjJudgementInfo, subjFingers, subjMissingFingers, subjFace, operatorId);
	}
	
	@Override
	public String toString()
	{
		return "ConvictedReport{" + "reportNumber=" + reportNumber + ", reportDate=" + reportDate +
			   ", generalFileNum='" + generalFileNum + '\'' + ", subjtName=" + subjtName +
			   ", subjNationalityCode=" + subjNationalityCode + ", subjOccupation='" + subjOccupation + '\'' +
			   ", subjGender='" + subjGender + '\'' + ", subjBirthDate=" + subjBirthDate + ", subjBirthPlace='" +
			   subjBirthPlace + '\'' + ", subjDocId='" + subjDocId + '\'' + ", subjDocType='" + subjDocType + '\'' +
			   ", subjDocIssDate=" + subjDocIssDate + ", subjDocExpDate=" + subjDocExpDate + ", subjJudgementInfo=" +
			   subjJudgementInfo + ", subjFingers=" + subjFingers + ", subjMissingFingers=" + subjMissingFingers +
			   ", subjFace='" + subjFace + '\'' + ", operatorId='" + operatorId + '\'' + '}';
	}
}