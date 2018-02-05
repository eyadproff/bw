package sa.gov.nic.bio.bw.client.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.webservice.NicHijriCalendarData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class AppUtils
{
	private static final Logger LOGGER = Logger.getLogger(AppUtils.class.getName());
	private static final String FONT_AWESOME_FILE = "sa/gov/nic/bio/bw/client/core/fonts/fontawesome-webfont-4.7.0.2016.ttf";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a - EEEE dd MMMM yyyy G");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy G");
	private static final DateTimeFormatter FORMAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final HijrahChronology nicChronology = HijrahChronology.INSTANCE;
	private static final FontAwesome FONTAWESOME_INSTANCE = new FontAwesome(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(FONT_AWESOME_FILE)).toExternalForm());
	
	public static Glyph createFontAwesomeIcon(FontAwesome.Glyph icon)
	{
		return FONTAWESOME_INSTANCE.create(icon);
	}
	
	public static Glyph createFontAwesomeIcon(char character)
	{
		return FONTAWESOME_INSTANCE.create(character);
	}
	
	/*public static String getMachineIpAddress() throws SocketException
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
	}*/
	
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
	
	public static LocalDate parseFormalDate(String sDate)
	{
		return LocalDate.parse(sDate, FORMAL_DATE_FORMATTER);
	}
	
	/*public static String replaceNumbersWithCommas(long number, Locale locale)
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		return numberFormat.format(number);
	}*/
	
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
					JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();
					String exp = jsonObject.get("exp").getAsString();
					
					if(exp == null) LOGGER.warning("The payload has no \"exp\"!");
					else
					{
						LocalDateTime expirationDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(exp) * 1000L), AppConstants.SAUDI_ZONE);
						LOGGER.fine("The expiration time for the new JWT is " + expirationDateTime);
						return expirationDateTime;
					}
				}
				catch(Exception e)
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
	
	public static String saveBase64PhotoToTemp(String photoBase64, String fileExtension)
	{
		byte[] photoByteArray = Base64.getDecoder().decode(photoBase64);
		InputStream is = new ByteArrayInputStream(photoByteArray);
		String fileName = System.nanoTime() + fileExtension;
		String filePath = AppConstants.TEMP_FOLDER_PATH + "/" + fileName;
		
		try
		{
			Files.copy(is, Paths.get(filePath));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return filePath;
	}
	
	public static String[] decodeJWT(String jwt)
	{
		String[] parts = jwt.split("\\.");
		
		parts[0] = "header = " + new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
		parts[1] = "payload = " + new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
		parts[2] = "signature = " + parts[2];
		
		return parts;
	}
}