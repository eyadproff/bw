package sa.gov.nic.bio.bw.client.core.utils;

import java.util.Locale;

public interface AppConstants
{
	interface Locales
	{
		Locale SAUDI_AR_LOCALE = new Locale.Builder().setLanguageTag("ar-SA-u-nu-arab").build(); // nu is for numbers
		Locale SAUDI_EN_LOCALE = new Locale("en", "SA");
	}
}