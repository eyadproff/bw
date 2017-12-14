package sa.gov.nic.bio.bw.client.core.utils;

import sa.gov.nic.bio.bw.client.AppEntryPoint;

import java.time.ZoneId;
import java.util.Locale;

public interface AppConstants
{
	interface Locales
	{
		Locale SAUDI_AR_LOCALE = new Locale.Builder().setLanguageTag("ar-SA-u-nu-arab").build(); // nu is for numbers
		Locale SAUDI_EN_LOCALE = new Locale("en", "SA");
	}
	
	Class<?> PREF_NODE_CLASS = AppEntryPoint.class;
	String UI_LANGUAGE_PREF_NAME = "sa.gov.nic.bio.bw.ui.language";
	ZoneId SAUDI_ZONE = ZoneId.of("GMT+3");
	String LOGS_FOLDER_PATH = "C:/bio/apps/bw/logs";
	String TEMP_FOLDER_PATH = "C:/bio/apps/bw/temp";
}