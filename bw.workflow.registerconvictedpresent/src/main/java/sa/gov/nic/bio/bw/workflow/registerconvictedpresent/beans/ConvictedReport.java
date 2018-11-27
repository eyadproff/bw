package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;

public class ConvictedReport extends JavaBean
{
	private long reportNumber;
	private long reportDate;
	private Long generalFileNum;
	private Name subjtName;
	private Integer subjNationalityCode;
	private String subjOccupation;
	private String subjGender; //'M' or 'F'
	private Long subjBirthDate;
	private String subjBirthPlace;
	private Long subjSamisId;
	private Integer subjSamisType;
	private Long subjBioId;
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
	                       Integer subjNationalityCode, String subjOccupation, String subjGender,
	                       Long subjBirthDate, String subjBirthPlace, Long subjSamisId, Integer subjSamisType,
	                       Long subjBioId, String subjDocId, Integer subjDocType, Long subjDocIssDate,
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
		this.subjSamisId = subjSamisId;
		this.subjSamisType = subjSamisType;
		this.subjBioId = subjBioId;
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
	
	public Integer getSubjNationalityCode(){return subjNationalityCode;}
	public void setSubjNationalityCode(Integer subjNationalityCode){this.subjNationalityCode = subjNationalityCode;}
	
	public String getSubjOccupation(){return subjOccupation;}
	public void setSubjOccupation(String subjOccupation){this.subjOccupation = subjOccupation;}
	
	public String getSubjGender(){return subjGender;}
	public void setSubjGender(String subjGender){this.subjGender = subjGender;}
	
	public Long getSubjBirthDate(){return subjBirthDate;}
	public void setSubjBirthDate(Long subjBirthDate){this.subjBirthDate = subjBirthDate;}
	
	public String getSubjBirthPlace(){return subjBirthPlace;}
	public void setSubjBirthPlace(String subjBirthPlace){this.subjBirthPlace = subjBirthPlace;}
	
	public Long getSubjSamisId(){return subjSamisId;}
	public void setSubjSamisId(Long subjSamisId){this.subjSamisId = subjSamisId;}
	
	public Integer getSubjSamisType(){return subjSamisType;}
	public void setSubjSamisType(Integer subjSamisType){this.subjSamisType = subjSamisType;}
	
	public Long getSubjBioId(){return subjBioId;}
	public void setSubjBioId(Long subjBioId){this.subjBioId = subjBioId;}
	
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
}