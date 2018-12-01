package sa.gov.nic.bio.bw.core.beans;

import sa.gov.nic.bio.bw.core.utils.AppUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Gender
{
	MALE, FEMALE;
	
	@Override
	public String toString()
	{
		return toString(Locale.getDefault());
	}
	
	public String toString(Locale locale)
	{
		ResourceBundle resourceBundle = AppUtils.getCoreStringsResourceBundle(locale);
		
		switch(this)
		{
			case MALE: return resourceBundle.getString("label.male");
			case FEMALE: return resourceBundle.getString("label.female");
			default: return null;
		}
	}
}