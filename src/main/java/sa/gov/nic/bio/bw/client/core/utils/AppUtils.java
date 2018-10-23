package sa.gov.nic.bio.bw.client.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class AppUtils implements AppLogger
{
	private static final String FONT_AWESOME_FILE = "sa/gov/nic/bio/bw/client/core/fonts/" +
													"fontawesome-webfont-4.7.0.2016.ttf";
	private static final DateTimeFormatter DATE_TIME_FORMATTER =
													DateTimeFormatter.ofPattern("hh:mm:ss a (Z) - EEEE dd MMMM yyyy G");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy G");
	private static final DateTimeFormatter DATE_FORMATTER_SIMPLE = DateTimeFormatter.ofPattern("dd/MM/yyyy G");
	private static final DateTimeFormatter DATE_FORMATTER_SIMPLE_RTL = DateTimeFormatter.ofPattern("yyyy/MM/dd G");
	private static final DateTimeFormatter DATE_WTH_WEEK_DAY_FORMATTER =
																	DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy G");
	private static final DateTimeFormatter FORMAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final HijrahChronology nicChronology = HijrahChronology.INSTANCE;
	private static final FontAwesome FONTAWESOME_INSTANCE = new FontAwesome(Objects.requireNonNull(
					Thread.currentThread().getContextClassLoader().getResource(FONT_AWESOME_FILE)).toExternalForm());
	
	public static Glyph createFontAwesomeIcon(FontAwesome.Glyph icon)
	{
		return FONTAWESOME_INSTANCE.create(icon);
	}
	
	public static Glyph createFontAwesomeIcon(char character)
	{
		return FONTAWESOME_INSTANCE.create(character);
	}
	
	public static List<Class<?>> listClasspathClasses(ProtectionDomain protectionDomain,
	                                                RuntimeEnvironment runtimeEnvironment)
																				throws IOException, URISyntaxException
	{
		List<Class<?>> classes = new ArrayList<>();
		URL location = protectionDomain.getCodeSource().getLocation();
		String matcher = "^.*\\.class$";
		
		if(runtimeEnvironment == RuntimeEnvironment.LOCAL)
		{
			// location.toURI() is ./build/classes/java/main
			Path classesPath = Paths.get(location.toURI()).normalize();
			
			Files.walk(classesPath)
					.filter(Files::isRegularFile) // we want files only
					.map(path -> classesPath.relativize(path).toString()) // form absolute path to relative path string
					.map(path -> path.replace("\\", ".")) // replace back-slashes with dots
					.filter(path -> path.matches(matcher))
					.map(path -> path.substring(0, path.lastIndexOf('.')))
					.map(name ->
					{
						try
						{
							return Class.forName(name);
						}
						catch(ClassNotFoundException e)
						{
							LOGGER.log(Level.SEVERE, "failed to load the class (" + name + ")!", e);
							return null;
						}
					})
					.filter(Objects::nonNull)
					.forEach(classes::add);
		}
		else
		{
			ZipInputStream zip = new ZipInputStream(location.openStream());
			
			while(true)
			{
				ZipEntry entry = zip.getNextEntry();
				if(entry == null) break;
				String name = entry.getName();
				
				if(name.matches(matcher))
				{
					try
					{
						classes.add(Class.forName(name.substring(0, name.lastIndexOf('.'))));
					}
					catch(ClassNotFoundException e)
					{
						LOGGER.log(Level.SEVERE, "failed to load the class (" + name + ")!", e);
					}
				}
			}
		}
		
		return classes;
	}
	
	public static List<String> listResourceFiles(ProtectionDomain protectionDomain, String matcher,
	                                             boolean removeFileExtensions, RuntimeEnvironment runtimeEnvironment)
																				throws IOException, URISyntaxException
	{
		List<String> resources = new ArrayList<>();
		URL location = protectionDomain.getCodeSource().getLocation();
		
		if(runtimeEnvironment == RuntimeEnvironment.LOCAL)
		{
			// location.toURI() is ./build/classes/java/main
			// we want ./build/resources/main
			// the following line gets the absolute path of the folder "resources/main"
			Path resourcesPath = Paths.get(location.toURI()).resolve("../../../resources/main").normalize();
			
			Files.walk(resourcesPath)
				 .filter(Files::isRegularFile) // we want files only
				 .map(path -> resourcesPath.relativize(path).toString()) // form absolute path to relative path string
				 .map(path -> path.replace("\\", "/")) // replace back-slashes with forward-slashes
				 .filter(path -> path.matches(matcher))
				 .map(path -> removeFileExtensions ? path.substring(0, path.lastIndexOf('.')) : path)
				 .forEach(resources::add);
		}
		else
		{
			ZipInputStream zip = new ZipInputStream(location.openStream());
			
			while(true)
			{
				ZipEntry e = zip.getNextEntry();
				if(e == null) break;
				String name = e.getName();
				
				if(name.matches(matcher)) resources.add(removeFileExtensions ?
						                                name.substring(0, name.lastIndexOf('.')) : name);
			}
		}
		
		return resources;
	}
	
	public static String formatDateTime(TemporalAccessor temporal)
	{
		return localizeNumbers(DATE_TIME_FORMATTER.withLocale(Locale.getDefault()).format(temporal),
		                       Locale.getDefault(), false);
	}
	
	public static String formatDate(TemporalAccessor temporal)
	{
		return localizeNumbers(DATE_FORMATTER.withLocale(Locale.getDefault()).format(temporal), Locale.getDefault(),
		                       false);
	}
	
	public static String formatFullDate(TemporalAccessor temporal)
	{
		return localizeNumbers(DATE_WTH_WEEK_DAY_FORMATTER.withLocale(Locale.getDefault()).format(temporal),
		                       Locale.getDefault(), false);
	}
	
	public static String formatDateSimple(TemporalAccessor temporal, boolean rtl)
	{
		if(rtl) return localizeNumbers(DATE_FORMATTER_SIMPLE_RTL.withLocale(
							Locale.getDefault()).format(temporal), Locale.getDefault(), false);
		else return localizeNumbers(DATE_FORMATTER_SIMPLE.withLocale(
							Locale.getDefault()).format(temporal), Locale.getDefault(), false);
	}
	
	public static LocalDate parseFormalDate(String sDate)
	{
		return LocalDate.parse(sDate, FORMAL_DATE_FORMATTER);
	}
	
	public static String localizeNumbers(String text, Locale locale, boolean textContainsNumbersOnly)
	{
		if(text == null) return null;
		
		if(textContainsNumbersOnly && !text.matches("\\d+")) return text;
		
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
	
	public static String localizeNumbers(String text)
	{
		return localizeNumbers(text, Locale.getDefault(), true);
	}
	
	public static void injectNicHijriCalendarData(NicHijriCalendarData nicHijriCalendarData)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		Map<Integer, int[]> years =  nicHijriCalendarData.getHijriYears();
		
		int minYear = Integer.MAX_VALUE;
		int maxYear = Integer.MIN_VALUE;
		
		for(int year : years.keySet())
		{
			maxYear = Math.max(maxYear, year);
			minYear = Math.min(minYear, year);
		}
		
		int isoStart = (int) LocalDateTime.ofInstant(Instant.ofEpochMilli(nicHijriCalendarData.getIsoStartDate()),
		                                             AppConstants.SAUDI_ZONE).toLocalDate().toEpochDay();
		
		Field initCompleteField = HijrahChronology.class.getDeclaredField("initComplete");
		initCompleteField.setAccessible(true);
		initCompleteField.set(nicChronology, true);
		
		Field hijrahStartEpochMonthField = HijrahChronology.class.getDeclaredField("hijrahStartEpochMonth");
		hijrahStartEpochMonthField.setAccessible(true);
		hijrahStartEpochMonthField.set(nicChronology, minYear * 12);
		
		Field minEpochDayField = HijrahChronology.class.getDeclaredField("minEpochDay");
		minEpochDayField.setAccessible(true);
		minEpochDayField.set(nicChronology, isoStart);
		
		Method createEpochMonthsMethod = HijrahChronology.class.getDeclaredMethod("createEpochMonths", int.class,
		                                                                          int.class, int.class, Map.class);
		createEpochMonthsMethod.setAccessible(true);
		int[] hijrahEpochMonthStartDays = (int[]) createEpochMonthsMethod.invoke(nicChronology, isoStart, minYear,
		                                                                         maxYear, years);
		
		Field hijrahEpochMonthStartDaysField =
											HijrahChronology.class.getDeclaredField("hijrahEpochMonthStartDays");
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
	
	public static ChronoZonedDateTime<HijrahDate> milliSecondsToHijriDateTime(long milliSeconds)
											throws DateTimeException // thrown in case of "Hijrah date out of range"
	{
		return nicChronology.zonedDateTime(Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE));
	}
	
	public static HijrahDate milliSecondsToHijriDate(long milliSeconds)
	{
		return nicChronology.zonedDateTime(Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE))
																			 .toLocalDate();
	}
	
	public static ZonedDateTime milliSecondsToGregorianDateTime(long milliSeconds)
	{
		return Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE);
	}
	
	public static LocalDate milliSecondsToGregorianDate(long milliSeconds)
	{
		return Instant.ofEpochMilli(milliSeconds).atZone(AppConstants.SAUDI_ZONE).toLocalDate();
	}
	
	public static String formatHijriGregorianDateTime(long milliSeconds)
	{
		ChronoZonedDateTime<HijrahDate> hijriDateTime = null;
		
		try
		{
			hijriDateTime = AppUtils.milliSecondsToHijriDateTime(milliSeconds);
		}
		catch(DateTimeException e)
		{
			// thrown in case of "Hijrah date out of range"
		}
		
		ZonedDateTime gregorianDateTime = AppUtils.milliSecondsToGregorianDateTime(milliSeconds);
		
		if(hijriDateTime != null)
		{
			return AppUtils.formatDateTime(hijriDateTime) + " - " + AppUtils.formatDate(gregorianDateTime);
		}
		else return AppUtils.formatDateTime(gregorianDateTime);
	}
	
	public static String formatHijriDateSimple(long milliSeconds, boolean rtl)
	{
		HijrahDate hijriDate = null;
		
		try
		{
			hijriDate = AppUtils.milliSecondsToHijriDate(milliSeconds);
		}
		catch(DateTimeException e)
		{
			// thrown in case of "Hijrah date out of range"
		}
		
		if(hijriDate != null) return AppUtils.formatDateSimple(hijriDate, rtl);
		else return "";
	}
	
	public static String formatHijriGregorianDate(long milliSeconds)
	{
		HijrahDate hijriDate = null;
		
		try
		{
			hijriDate = AppUtils.milliSecondsToHijriDate(milliSeconds);
		}
		catch(DateTimeException e)
		{
			// thrown in case of "Hijrah date out of range"
		}
		
		LocalDate gregorianDate = AppUtils.milliSecondsToGregorianDate(milliSeconds);
		
		if(hijriDate != null)
		{
			return AppUtils.formatFullDate(hijriDate) + " - " + AppUtils.formatDate(gregorianDate);
		}
		else return AppUtils.formatFullDate(gregorianDate);
	}
	
	public static String formatGregorianDate(long milliSeconds)
	{
		LocalDate localDate = AppUtils.milliSecondsToGregorianDate(milliSeconds);
		return AppUtils.formatFullDate(localDate);
	}
	
	public static long gregorianDateToMilliSeconds(LocalDate gregorianDate)
	{
		return gregorianDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond() * 1000;
	}
	
	public static LocalDateTime extractIssueTimeFromJWT(String jwt)
	{
		String iat = extractFieldFromJWT(jwt, "iat");
		
		if(iat != null && !iat.isEmpty())
		{
			LocalDateTime issueDateTime = LocalDateTime.ofInstant(
										Instant.ofEpochMilli(Long.parseLong(iat) * 1000L), AppConstants.SAUDI_ZONE);
			LOGGER.fine("The issue time for the JWT is " + issueDateTime);
			return issueDateTime;
		}
		
		return null;
	}
	
	public static LocalDateTime extractExpirationTimeFromJWT(String jwt)
	{
		String exp = extractFieldFromJWT(jwt, "exp");
		
		if(exp != null && !exp.isEmpty())
		{
			LocalDateTime expirationDateTime = LocalDateTime.ofInstant(
										Instant.ofEpochMilli(Long.parseLong(exp) * 1000L), AppConstants.SAUDI_ZONE);
			LOGGER.fine("The expiration time for the JWT is " + expirationDateTime);
			return expirationDateTime;
		}
		
		return null;
	}
	
	private static String extractFieldFromJWT(String jwt, String field)
	{
		String[] tokenParts = jwt.split("\\.");
		
		if(tokenParts.length != 3)
		{
			LOGGER.warning("userToken is not JWT! Failed to extract \"" + field + "\"!");
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
					JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();
					String exp = jsonObject.get(field).getAsString();
					
					if(exp == null) LOGGER.warning("The payload has no \"" + field + "\"!");
					else return exp;
				}
				catch(Exception e)
				{
					LOGGER.log(Level.SEVERE,"Failed to extract \"" + field + "\" from payload!", e);
				}
			}
		}
		
		return null;
	}
	
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
	
	public static String[] decodeJWT(String jwt)
	{
		String[] parts = jwt.split("\\.");
		
		parts[0] = "header = " + new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
		parts[1] = "payload = " + new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
		parts[2] = "signature = " + parts[2];
		
		return parts;
	}
	
	public static boolean isEnglishText(String text)
	{
		if(text == null) return false;
		return StandardCharsets.US_ASCII.newEncoder().canEncode(text);
	}
	
	public static String buildNamePart(String namePart, String namePartTranslated, boolean combined)
	{
		if((namePart == null || namePart.trim().isEmpty() || namePart.trim().equals("-")) &&
		(namePartTranslated == null || namePartTranslated.trim().isEmpty() || namePartTranslated.trim().equals("-")))
		{
			return null;
		}
		else if((namePart == null || namePart.trim().isEmpty() || namePart.trim().equals("-")))
		{
			return namePartTranslated;
		}
		else if(namePartTranslated == null || namePartTranslated.trim().isEmpty()
																			|| namePartTranslated.trim().equals("-"))
		{
			return namePart;
		}
		else // both have value
		{
			if(AppUtils.isEnglishText(namePart))
			{
				return namePartTranslated + (combined ? " - " + namePart : "");
			}
			else return namePart + (combined ? " - " + namePartTranslated : "");
		}
	}
	
	public static String imageToBase64(Image image, String imageExtension) throws IOException
	{
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), imageExtension, byteOutput);
		byte[] bytes = byteOutput.toByteArray();
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	public static String imageToBase64(Image image) throws IOException
	{
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		
		// convert to greyscale
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		int[] rgb = bufferedImage.getRGB(0, 0, w, h, null, 0, w);
		newImage.setRGB(0, 0, w, h, rgb, 0, w);
		
		ImageIO.write(newImage, "png", byteOutput);
		byte[] bytes = byteOutput.toByteArray();
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	public static Image imageFromBase64(String base64Image)
	{
		if(base64Image == null) return null;
		
		byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
		return new Image(new ByteArrayInputStream(imageByteArray));
	}
	
	public static String constructName(Name name)
	{
		if(name == null) return null;
		
		String firstName = name.getFirstName();
		String fatherName = name.getFatherName();
		String grandfatherName = name.getGrandfatherName();
		String familyName = name.getFamilyName();
		
		return constructName(firstName, fatherName, grandfatherName, familyName);
	}
	
	public static String constructName(String firstName, String fatherName, String grandfatherName, String familyName)
	{
		if(firstName == null || firstName.trim().equals("-")) firstName = "";
		if(fatherName == null || fatherName.trim().equals("-")) fatherName = "";
		if(grandfatherName == null || grandfatherName.trim().equals("-")) grandfatherName = "";
		if(familyName == null || familyName.trim().equals("-")) familyName = "";
		
		String fullName = firstName + " " + fatherName + " " + grandfatherName + " " + familyName;
		return fullName.trim().replaceAll("\\s+", " "); // remove extra spaces
	}
}