package sa.gov.nic.bio.bw.workflow.commons.tasks;

import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.workflow.Converter;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.util.Date;

public class DeporteeInfoToPersonInfoConverter implements Converter<DeporteeInfo, PersonInfo>
{
	@Override
	public PersonInfo convert(DeporteeInfo deporteeInfo)
	{
		if(deporteeInfo == null) return null;
		
		Long samisId = deporteeInfo.getSamisId();
		Name name = deporteeInfo.getName();
		Long subjBirthDate = deporteeInfo.getBirthDate();
		Date birthDate = subjBirthDate != null ? new Date(subjBirthDate * 1000L) : null;
		Integer nationality = deporteeInfo.getNationality();
		String subjGender = deporteeInfo.getGender();
		Integer gender = subjGender != null ? ("M".equals(subjGender) ? 1 : 2) : null;
		String face = deporteeInfo.getFace();
		
		PersonInfo personInfo = new PersonInfo();
		
		personInfo.setSamisId(samisId);
		personInfo.setName(name);
		personInfo.setBirthDate(birthDate);
		personInfo.setNationality(nationality);
		personInfo.setGender(gender);
		personInfo.setFace(face);
		
		return personInfo;
	}
}