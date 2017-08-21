package sa.gov.nic.bio.bw.client.core.utils;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class AppUtils
{
	private static final Logger LOGGER = Logger.getLogger(AppUtils.class.getName());
	private static final String FONT_AWESOME_FILE = "sa/gov/nic/bio/bw/client/core/fonts/fontawesome-webfont-4.7.0.2016.ttf";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a - EEEE dd MMMM yyyy G");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy G");
	private static final HijrahChronology nicChronology = HijrahChronology.INSTANCE;
	private static final FontAwesome FONTAWESOME_INSTANCE = new FontAwesome(AppUtils.getResourceString(FONT_AWESOME_FILE));
	
	public static Glyph createFontAwesomeIcon(FontAwesome.Glyph icon)
	{
		return FONTAWESOME_INSTANCE.create(icon);
	}
	
	public static URL getResourceURL(String resourceName)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResource(resourceName);
	}
	
	public static String getResourceString(String resourceName)
	{
		URL url = getResourceURL(resourceName);
		return url != null ? url.toExternalForm() : null;
	}
	
	public static InputStream getResourceAsStream(String resourceName)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResourceAsStream(resourceName);
	}
	
	public static ResourceBundle getResourceBundle(String resourceBundlePath, GuiLanguage language)
	{
		if(language == null) return ResourceBundle.getBundle(resourceBundlePath, UTF8Control.INSTANCE);
		else return ResourceBundle.getBundle(resourceBundlePath, language.getLocale(), UTF8Control.INSTANCE);
	}
	
	public static String getMachineIpAddress() throws SocketException
	{
		for(NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces()))
		{
			for(InetAddress address : Collections.list(nic.getInetAddresses()))
			{
				if(!address.isLoopbackAddress() && address instanceof Inet4Address)
				{
					return address.getHostAddress();
				}
			}
		}
		
		return null;
	}
	
	public static List<String> listResourceFiles(ProtectionDomain protectionDomain, String endsWith) throws IOException
	{
		List<String> resources = new ArrayList<>();
		
		URL jar = protectionDomain.getCodeSource().getLocation();
		ZipInputStream zip = new ZipInputStream(jar.openStream());
		
		while(true)
		{
			ZipEntry e = zip.getNextEntry();
			if(e == null) break;
			String name = e.getName();
			
			if(name.endsWith(endsWith)) resources.add(name);
		}
		
		return resources;
	}
	
	public static double computeTextWidth(String s, Font font)
	{
		Text text = new Text(s);
		text.setFont(font);
		return text.getLayoutBounds().getWidth();
	}
	
	public static String formatDateTime(TemporalAccessor temporal)
	{
		return replaceNumbers(DATE_TIME_FORMATTER.withLocale(Locale.getDefault()).format(temporal), Locale.getDefault());
		
	}
	
	public static String formatDate(TemporalAccessor temporal)
	{
		return replaceNumbers(DATE_FORMATTER.withLocale(Locale.getDefault()).format(temporal), Locale.getDefault());
		
	}
	
	public static String replaceNumbers(long number, Locale locale)
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		return numberFormat.format(number);
	}
	
	public static String replaceNumbers(String text, Locale locale)
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		
		StringBuilder sb = new StringBuilder();
		for(char c : text.toCharArray()) // not valid for 4-byte chars?
		{
			if(Character.isDigit(c))
			{
				sb.append(numberFormat.format(Integer.parseInt(String.valueOf(c))));
			}
			else sb.append(c);
		}
		
		return sb.toString();
	}
	
	public static void injectNicHijriCalendarData(NicHijriCalendarData nicHijriCalendarData) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		Map<Integer, int[]> years =  nicHijriCalendarData.getHijriYears();
		
		int minYear = Integer.MAX_VALUE;
		int maxYear = Integer.MIN_VALUE;
		
		for(int year : years.keySet())
		{
			maxYear = Math.max(maxYear, year);
			minYear = Math.min(minYear, year);
		}
		
		int isoStart = (int) LocalDateTime.ofInstant(Instant.ofEpochMilli(nicHijriCalendarData.getIsoStartDate()), AppConstants.SAUDI_ZONE).toLocalDate().toEpochDay();
		
		Field initCompleteField = HijrahChronology.class.getDeclaredField("initComplete");
		initCompleteField.setAccessible(true);
		initCompleteField.set(nicChronology, true);

        /*Field typeIdField = HijrahChronology.class.getDeclaredField("typeId");
        typeIdField.setAccessible(true);
        typeIdField.set(nicChronology, "Hijrah-NIC");

        Field calendarTypeField = HijrahChronology.class.getDeclaredField("calendarType");
        calendarTypeField.setAccessible(true);
        calendarTypeField.set(nicChronology, "NIC");*/
		
		Field hijrahStartEpochMonthField = HijrahChronology.class.getDeclaredField("hijrahStartEpochMonth");
		hijrahStartEpochMonthField.setAccessible(true);
		hijrahStartEpochMonthField.set(nicChronology, minYear * 12);
		
		Field minEpochDayField = HijrahChronology.class.getDeclaredField("minEpochDay");
		minEpochDayField.setAccessible(true);
		minEpochDayField.set(nicChronology, isoStart);
		
		Method createEpochMonthsMethod = HijrahChronology.class.getDeclaredMethod("createEpochMonths", int.class, int.class, int.class, Map.class);
		createEpochMonthsMethod.setAccessible(true);
		int[] hijrahEpochMonthStartDays = (int[]) createEpochMonthsMethod.invoke(nicChronology, isoStart, minYear, maxYear, years);
		
		Field hijrahEpochMonthStartDaysField = HijrahChronology.class.getDeclaredField("hijrahEpochMonthStartDays");
		hijrahEpochMonthStartDaysField.setAccessible(true);
		hijrahEpochMonthStartDaysField.set(nicChronology, hijrahEpochMonthStartDays);
		
		Field maxEpochDayField = HijrahChronology.class.getDeclaredField("maxEpochDay");
		maxEpochDayField.setAccessible(true);
		maxEpochDayField.set(nicChronology, hijrahEpochMonthStartDays[hijrahEpochMonthStartDays.length - 1]);
		
		Method getYearLengthMethod = HijrahChronology.class.getDeclaredMethod("getYearLength", int.class);
		getYearLengthMethod.setAccessible(true);
		
		Field minYearLengthField = HijrahChronology.class.getDeclaredField("minYearLength");
		minYearLengthField.setAccessible(true);
		
		Field maxYearLengthField = HijrahChronology.class.getDeclaredField("maxYearLength");
		maxYearLengthField.setAccessible(true);
		
		for(int year = minYear; year < maxYear; year++)
		{
			int length = (int) getYearLengthMethod.invoke(nicChronology, year);
			int minYearLength = (int) minYearLengthField.get(nicChronology);
			int maxYearLength = (int) maxYearLengthField.get(nicChronology);
			minYearLengthField.set(nicChronology, Math.min(minYearLength, length));
			maxYearLengthField.set(nicChronology, Math.max(maxYearLength, length));
		}
	}
	
	public static ChronoZonedDateTime<HijrahDate> milliSecondsToHijriDataTime(long milliSeconds)
	{
		return nicChronology.zonedDateTime(Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE));
	}
	
	public static ZonedDateTime milliSecondsToGregorianDataTime(long milliSeconds)
	{
		return Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE);
	}
	
	public static String formatHijriGregorianDateTime(long milliSeconds)
	{
		ChronoZonedDateTime<HijrahDate> hijriDateTime = AppUtils.milliSecondsToHijriDataTime(milliSeconds);
		ZonedDateTime gregorianDateTime = AppUtils.milliSecondsToGregorianDataTime(milliSeconds);
		return AppUtils.formatDateTime(hijriDateTime) + " - " + AppUtils.formatDate(gregorianDateTime);
	}
}