package sa.gov.nic.bio.bw.core.utils;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

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
	
	// lowest resolution we support is 1024x768, subtract from that the windows task bar 40px
	double STAGE_WIDTH = 1024.0;
	double STAGE_HEIGHT = 768.0 - 40.0;
	
	String APP_CODE = "BW";
	Class<?> PREF_NODE_CLASS = AppConstants.class;
	String APP_BASE_PACKAGE = "sa.gov.nic.bio.bw";
	String UI_LANGUAGE_PREF_NAME = "sa.gov.nic.bio.bw.ui.language";
	String LOGIN_FINGERPRINT_POSITION_PREF_NAME = "sa.gov.nic.bio.bw.login.fingerprint";
	ZoneId SAUDI_ZONE = ZoneId.of("GMT+3");
	String APP_FOLDER_PATH = "C:/bio/user-apps/" + System.getProperty("user.name") + "/bw";
	String LOGS_FOLDER_PATH = APP_FOLDER_PATH + "/logs";
	String TEMP_FOLDER_PATH = APP_FOLDER_PATH + "/temp";
	String DEV_SERVER_URL = "10.0.73.80:8080"; // TODO: TEMP
	KeyCombination SCENIC_VIEW_KEY_COMBINATION = new KeyCodeCombination(KeyCode.D,
	                                                                    KeyCombination.CONTROL_DOWN,
	                                                                    KeyCombination.SHIFT_DOWN,
	                                                                    KeyCombination.ALT_DOWN);
	KeyCombination SHOWING_MOCK_TASKS_KEY_COMBINATION = new KeyCodeCombination(KeyCode.M,
                                                                               KeyCombination.CONTROL_DOWN,
                                                                               KeyCombination.SHIFT_DOWN,
                                                                               KeyCombination.ALT_DOWN);
	KeyCombination CHANGING_SERVER_KEY_COMBINATION = new KeyCodeCombination(KeyCode.S,
	                                                                        KeyCombination.CONTROL_DOWN,
	                                                                        KeyCombination.SHIFT_DOWN,
	                                                                        KeyCombination.ALT_DOWN);
	KeyCombination OPEN_APP_FOLDER_KEY_COMBINATION = new KeyCodeCombination(KeyCode.A,
	                                                                        KeyCombination.CONTROL_DOWN,
	                                                                        KeyCombination.SHIFT_DOWN,
	                                                                        KeyCombination.ALT_DOWN);
	
	// some columns in SAMIS DB use 01-01-0001, 04-04-0004, or so instead of null
	long SAMIS_DB_DATE_EPOCH_MS_NOT_SET_VALUE = -60000000000000L; // Monday, September 3, 0068 1:20:00 PM
	int TABLE_PAGINATION_PAGES_PER_ITERATION = 10;
	int TABLE_PAGINATION_RECORDS_PER_PAGE = 10;
}