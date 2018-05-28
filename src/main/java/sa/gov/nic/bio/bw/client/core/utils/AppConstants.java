package sa.gov.nic.bio.bw.client.core.utils;

import sa.gov.nic.bio.bw.client.AppEntryPoint;

import java.time.ZoneId;
import java.util.Locale;

public interface AppConstants
{
	interface Locales
	{
		Locale SAUDI_AR_LOCALE = new Locale.Builder().setLanguage("ar").setRegion("SA")
													 .setExtension('u', "nu-arab").build(); // nu is for numbers
		Locale SAUDI_EN_LOCALE = new Locale("en", "SA");
	}
	
	// lowest resolution we support is 800x600, subtract from that the windows task bar 40px
	double STAGE_WIDTH = 800.0;
	double STAGE_HEIGHT = 560.0;
	
	Class<?> PREF_NODE_CLASS = AppEntryPoint.class;
	String UI_LANGUAGE_PREF_NAME = "sa.gov.nic.bio.bw.ui.language";
	ZoneId SAUDI_ZONE = ZoneId.of("GMT+3");
	String LOGS_FOLDER_PATH = "C:/bio/user-apps/" + System.getProperty("user.name") + "/bw/logs";
	String TEMP_FOLDER_PATH = "C:/bio/user-apps/" + System.getProperty("user.name") + "/bw/temp";
	String LOCAL_SERVER_URL = "10.0.73.80:8080";
}