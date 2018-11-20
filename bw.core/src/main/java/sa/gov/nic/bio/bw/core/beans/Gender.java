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
		ResourceBundle resourceBundle = AppUtils.getCoreStringsResourceBundle(Locale.getDefault());
		
		switch(this)
		{
			case MALE: return resourceBundle.getString("label.male");
			case FEMALE: return resourceBundle.getString("label.female");
			default: return null;
		}
	}
}