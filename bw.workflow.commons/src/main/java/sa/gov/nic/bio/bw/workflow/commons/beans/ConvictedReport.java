package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;

import java.util.List;

public class ConvictedReport extends JavaBean
{
	public interface FingerprintsSource
	{
		String LIVE_SCAN = "LIVE_SCAN";
		String ARCHIVE_DB = "ARCHIVE_DB";
		String CARD_SCAN = "CARD_SCAN";
		String NIST_FILE = "NIST_FILE";
	}
	
	public interface Status
	{
		Integer ACTIVE = 0;
		Integer UPDATED = 1;
		Integer DELETED = 2;
	}
	
	private Integer sequence; // used in TableView
	private Long reportNumber;
	private Long reportDate;
	private Long generalFileNumber;
	private Name subjtName;
	private Integer subjNationalityCode;
	private String subjNationalityIsoCode;
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
	private List<CrimeCode> crimeCodes;
	private Integer locationId;
	private Long deleterId;
	private Long rootReportNumber;
	private Long prevReportNumber;
	private String sourceSystem;
	private Integer status;
	
	public ConvictedReport(){}
	
	public ConvictedReport(Long reportNumber, Long reportDate, Long generalFileNumber, Name subjtName,
	                       Integer subjNationalityCode, String subjNationalityIsoCode, String subjOccupation,
	                       String subjGender, Long subjBirthDate, String subjBirthPlace, Long subjSamisId,
	                       Integer subjSamisType, Long subjBioId, String subjDocId, Integer subjDocType,
	                       Long subjDocIssDate, Long subjDocExpDate, JudgementInfo subjJudgementInfo,
	                       List<Finger> subjFingers, List<Integer> subjMissingFingers, String subjFace,
	                       String operatorId, List<CrimeCode> crimeCodes, Integer locationId, Long deleterId,
	                       Long rootReportNumber, Long prevReportNumber, String sourceSystem, Integer status)
	{
		this.reportNumber = reportNumber;
		this.reportDate = reportDate;
		this.generalFileNumber = generalFileNumber;
		this.subjtName = subjtName;
		this.subjNationalityCode = subjNationalityCode;
		this.subjNationalityIsoCode = subjNationalityIsoCode;
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
		this.crimeCodes = crimeCodes;
		this.locationId = locationId;
		this.deleterId = deleterId;
		this.rootReportNumber = rootReportNumber;
		this.prevReportNumber = prevReportNumber;
		this.sourceSystem = sourceSystem;
		this.status = status;
	}
	
	public Integer getSequence(){return sequence;}
	public void setSequence(Integer sequence){this.sequence = sequence;}
	
	public Long getReportNumber(){return reportNumber;}
	public void setReportNumber(Long reportNumber){this.reportNumber = reportNumber;}
	
	public Long getReportDate(){return reportDate;}
	public void setReportDate(Long reportDate){this.reportDate = reportDate;}
	
	public Long getGeneralFileNumber(){return generalFileNumber;}
	public void setGeneralFileNumber(Long generalFileNumber){this.generalFileNumber = generalFileNumber;}
	
	public Name getSubjtName(){return subjtName;}
	public void setSubjtName(Name subjtName){this.subjtName = subjtName;}
	
	public Integer getSubjNationalityCode(){return subjNationalityCode;}
	public void setSubjNationalityCode(Integer subjNationalityCode){this.subjNationalityCode = subjNationalityCode;}
	
	public String getSubjNationalityIsoCode(){return subjNationalityIsoCode;}
	public void setSubjNationalityIsoCode(String subjNationalityIsoCode)
																{this.subjNationalityIsoCode = subjNationalityIsoCode;}
	
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
	
	public List<CrimeCode> getCrimeCodes(){return crimeCodes;}
	public void setCrimeCodes(List<CrimeCode> crimeCodes){this.crimeCodes = crimeCodes;}
	
	public Integer getLocationId(){return locationId;}
	public void setLocationId(Integer locationId){this.locationId = locationId;}
	
	public Long getDeleterId(){return deleterId;}
	public void setDeleterId(Long deleterId){this.deleterId = deleterId;}
	
	public Long getRootReportNumber(){return rootReportNumber;}
	public void setRootReportNumber(Long rootReportNumber){this.rootReportNumber = rootReportNumber;}
	
	public Long getPrevReportNumber(){return prevReportNumber;}
	public void setPrevReportNumber(Long prevReportNumber){this.prevReportNumber = prevReportNumber;}
	
	public String getSourceSystem(){return sourceSystem;}
	public void setSourceSystem(String sourceSystem){this.sourceSystem = sourceSystem;}
	
	public Integer getStatus(){return status;}
	public void setStatus(Integer status){this.status = status;}
}