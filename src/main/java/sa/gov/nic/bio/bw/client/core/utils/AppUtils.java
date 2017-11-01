package sa.gov.nic.bio.bw.client.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.logging.Level;
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
	private static final FontAwesome FONTAWESOME_INSTANCE = new FontAwesome(Thread.currentThread().getContextClassLoader().getResource(FONT_AWESOME_FILE).toExternalForm());
	
	public static Glyph createFontAwesomeIcon(FontAwesome.Glyph icon)
	{
		return FONTAWESOME_INSTANCE.create(icon);
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
	
	public static List<String> listResourceFiles(ProtectionDomain protectionDomain, String matcher) throws IOException
	{
		List<String> resources = new ArrayList<>();
		resources.add("sa/gov/nic/bio/bw/client/cancelcriminal/workflows/cancelCriminal.bpmn20.xml");
		resources.add("sa/gov/nic/bio/bw/client/cancellatent/workflows/cancelLatent.bpmn20.xml");
		resources.add("sa/gov/nic/bio/bw/client/core/workflows/core.bpmn20.xml");
		resources.add("sa/gov/nic/bio/bw/client/home/workflows/home.bpmn20.xml");
		resources.add("sa/gov/nic/bio/bw/client/login/workflows/login.bpmn20.xml");
		resources.add("sa/gov/nic/bio/bw/client/searchbyfaceimage/workflows/searchByFaceImage.bpmn20.xml");
		
		List<String> resources2 = new ArrayList<>();
		URL jar = protectionDomain.getCodeSource().getLocation();
		ZipInputStream zip = new ZipInputStream(jar.openStream());
		
		while(true)
		{
			ZipEntry e = zip.getNextEntry();
			if(e == null) break;
			String name = e.getName();
			
			if(name.matches(matcher)) resources2.add(name);
		}
		
		LOGGER.fine("resources2 = " + resources2);
		
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
		return replaceNumbersOnly(DATE_TIME_FORMATTER.withLocale(Locale.getDefault()).format(temporal), Locale.getDefault());
		
	}
	
	public static String formatDate(TemporalAccessor temporal)
	{
		return replaceNumbersOnly(DATE_FORMATTER.withLocale(Locale.getDefault()).format(temporal), Locale.getDefault());
		
	}
	
	public static String replaceNumbersWithCommas(long number, Locale locale)
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		return numberFormat.format(number);
	}
	
	public static String replaceNumbersOnly(String text, Locale locale)
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
	
	public static ChronoZonedDateTime<HijrahDate> milliSecondsToHijriDataTime(long milliSeconds) throws DateTimeException // thrown in case of "Hijrah date out of range"
	{
		return nicChronology.zonedDateTime(Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE));
	}
	
	public static ZonedDateTime milliSecondsToGregorianDataTime(long milliSeconds)
	{
		return Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE);
	}
	
	public static String formatHijriGregorianDateTime(long milliSeconds) throws DateTimeException // thrown in case of "Hijrah date out of range"
	{
		ChronoZonedDateTime<HijrahDate> hijriDateTime = AppUtils.milliSecondsToHijriDataTime(milliSeconds);
		ZonedDateTime gregorianDateTime = AppUtils.milliSecondsToGregorianDataTime(milliSeconds);
		return AppUtils.formatDateTime(hijriDateTime) + " - " + AppUtils.formatDate(gregorianDateTime);
	}
	
	public static LocalDateTime extractExpirationTimeFromJWT(String jwt)
	{
		String[] tokenParts = jwt.split("\\.");
		
		if(tokenParts.length != 3)
		{
			LOGGER.warning("userToken is not JWT! Failed to extract the expiration time!");
		}
		else
		{
			String payload = null;
			try
			{
				payload = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8.displayName());
			}
			catch(IllegalArgumentException e)
			{
				LOGGER.warning("tokenParts[1] is not in valid Base64 scheme! tokenParts[1] = " + tokenParts[1]);
			}
			catch(UnsupportedEncodingException e) // thrown if UTF-8 is not supported, should never happen!
			{
				LOGGER.log(Level.SEVERE, "UTF-8 is not supported!!", e);
			}
			
			if(payload == null) LOGGER.warning("payload is null!");
			else
			{
				try
				{
					ObjectNode object = new ObjectMapper().readValue(payload, ObjectNode.class);
					JsonNode node = object.get("exp");
					
					if(node == null) LOGGER.warning("The payload has no \"exp\"!");
					else
					{
						String exp = node.asText();
						return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(exp) * 1000L), AppConstants.SAUDI_ZONE);
					}
				}
				catch(IOException e)
				{
					LOGGER.log(Level.SEVERE,"Failed to extract \"exp\" from payload!", e);
				}
			}
		}
		
		return null;
	}
	
	/*public static void saveImageToFile(Image image, String folderPath, String fileName) throws IOException
	{
		if(folderPath == null) throw new IllegalArgumentException("folderPath is null!");
		if(fileName == null) throw new IllegalArgumentException("fileName is null!");
		if(!fileName.matches(".+\\..+")) throw new IllegalArgumentException("fileName has no extension!");
		
		File outputFile = new File(folderPath + "/" + fileName);
		BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
		ImageIO.write(bImage, fileName.substring(fileName.lastIndexOf('.') + 1), outputFile);
	}*/
	
	public static void cleanDirectory(Path directoryPath) throws IOException
	{
		Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
			{
				if(!directoryPath.equals(dir)) Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}