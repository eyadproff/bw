package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.workflow.Converter;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonIdInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.util.Date;

public class DisCriminalReportToPersonInfoConverter implements Converter<DisCriminalReport, PersonInfo>
{
	@Override
	public PersonInfo convert(DisCriminalReport disCriminalReport)
	{
		Long samisId = disCriminalReport.getSubjSamisId();
		Name name = disCriminalReport.getSubjtName();
		Long subjBirthDate = disCriminalReport.getSubjBirthDate();
		Date birthDate = subjBirthDate != null ? new Date(subjBirthDate * 1000L) : null;
		String birthPlace = disCriminalReport.getSubjBirthPlace();
		Integer subjSamisType = disCriminalReport.getSubjSamisType();
		String personType = subjSamisType != null ? String.valueOf(subjSamisType) : null;
		Integer nationality = disCriminalReport.getSubjNationalityCode();
		String subjGender = disCriminalReport.getSubjGender();
		Integer gender = subjGender != null ? ("M".equals(subjGender) ? 1 : 2) : null;
		String idNumber = disCriminalReport.getSubjDocId();
		Integer idType = disCriminalReport.getSubjDocType();
		Long subjDocIssDate = disCriminalReport.getSubjDocIssDate();
		Date idIssueDate = subjDocIssDate != null ? new Date(subjDocIssDate * 1000L) : null;
		String occupation = disCriminalReport.getSubjOccupation();
		PersonIdInfo identityInfo = new PersonIdInfo();
		PersonInfo personInfo = new PersonInfo();
		
		identityInfo.setIdNumber(idNumber);
		identityInfo.setIdType(idType);
		identityInfo.setIdIssueDate(idIssueDate);
		identityInfo.setOccupation(occupation);
		
		personInfo.setSamisId(samisId);
		personInfo.setName(name);
		personInfo.setBirthDate(birthDate);
		personInfo.setBirthPlace(birthPlace);
		personInfo.setPersonType(personType);
		personInfo.setNationality(nationality);
		personInfo.setGender(gender);
		
		return personInfo;
	}
}