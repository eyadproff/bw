package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.workflow.Converter;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonIdInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.util.Date;

public class ConvictedReportToPersonInfoConverter implements Converter<ConvictedReport, PersonInfo>
{
	@Override
	public PersonInfo convert(ConvictedReport convictedReport)
	{
		Long samisId = convictedReport.getSubjSamisId();
		Name name = convictedReport.getSubjtName();
		Long subjBirthDate = convictedReport.getSubjBirthDate();
		Date birthDate = subjBirthDate != null ? new Date(subjBirthDate * 1000L) : null;
		String birthPlace = convictedReport.getSubjBirthPlace();
		Integer subjSamisType = convictedReport.getSubjSamisType();
		String personType = subjSamisType != null ? String.valueOf(subjSamisType) : null;
		Integer nationality = convictedReport.getSubjNationalityCode();
		String subjGender = convictedReport.getSubjGender();
		Integer gender = subjGender != null ? ("F".equals(subjGender) ? 1 : 2) : null;
		String idNumber = convictedReport.getSubjDocId();
		Integer idType = convictedReport.getSubjDocType();
		Long subjDocIssDate = convictedReport.getSubjDocIssDate();
		Date idIssueDate = subjDocIssDate != null ? new Date(subjDocIssDate * 1000L) : null;
		Long subjDocExpDate = convictedReport.getSubjDocExpDate();
		Date idExpirDate = subjDocExpDate != null ? new Date(subjDocExpDate * 1000L) : null;
		String occupation = convictedReport.getSubjOccupation();
		PersonIdInfo identityInfo = new PersonIdInfo();
		String face = convictedReport.getSubjFace();
		PersonInfo personInfo = new PersonInfo();
		
		identityInfo.setIdNumber(idNumber);
		identityInfo.setIdType(idType);
		identityInfo.setIdIssueDate(idIssueDate);
		identityInfo.setIdExpirDate(idExpirDate);
		identityInfo.setOccupation(occupation);
		
		personInfo.setSamisId(samisId);
		personInfo.setName(name);
		personInfo.setBirthDate(birthDate);
		personInfo.setBirthPlace(birthPlace);
		personInfo.setPersonType(personType);
		personInfo.setNationality(nationality);
		personInfo.setGender(gender);
		personInfo.setFace(face);
		
		return personInfo;
	}
}