package sa.gov.nic.bio.bw.client.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UpdateChecker
{
	private static final Logger LOGGER = Logger.getLogger(UpdateChecker.class.getName());
	
	private static class FileInfo
	{
		private String fileRelativePath;
		private String fileHash;
		private long fileSize;
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			
			FileInfo fileInfo = (FileInfo) o;
			
			return fileSize == fileInfo.fileSize && (fileRelativePath != null ? fileRelativePath.equals(fileInfo.fileRelativePath) : fileInfo.fileRelativePath == null) &&
				  (fileHash != null ? fileHash.equals(fileInfo.fileHash) : fileInfo.fileHash == null);
		}
		
		@Override
		public int hashCode()
		{
			int result = fileRelativePath != null ? fileRelativePath.hashCode() : 0;
			result = 31 * result + (fileHash != null ? fileHash.hashCode() : 0);
			result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
			return result;
		}
		
		@Override
		public String toString()
		{
			return "FileInfo{" + "fileRelativePath='" + fileRelativePath + '\'' + ", fileHash='" + fileHash + '\'' + ", fileSize='" + fileSize + '\'' + '}';
		}
	}
	
	private String serverBaseUrl;
	private String hashingAlgorithm;
	private String bioFolderPath;
	private int serverConnectTimeoutSeconds;
	private int serverReadTimeoutSeconds;
	
	public UpdateChecker(String serverBaseUrl, String hashingAlgorithm, String bioFolderPath, int serverConnectTimeoutSeconds, int serverReadTimeoutSeconds)
	{
		this.serverBaseUrl = serverBaseUrl;
		this.hashingAlgorithm = hashingAlgorithm;
		this.bioFolderPath = bioFolderPath;
		this.serverConnectTimeoutSeconds = serverConnectTimeoutSeconds;
		this.serverReadTimeoutSeconds = serverReadTimeoutSeconds;
	}
	
	public Boolean checkForUpdates()
	{
		LOGGER.info("Checking for new updates...");
		
		List<FileInfo> localJars = getLocalFilesInfo(new File(bioFolderPath + "/apps/bw/jars"), hashingAlgorithm);
		List<FileInfo> remoteJars = getRemoteFilesInfo(serverBaseUrl + "/bw/jars.hashes", serverConnectTimeoutSeconds, serverReadTimeoutSeconds);
		if(localJars == null || remoteJars == null) return null;
		
		List<FileInfo> jarsToBeRemoved = new ArrayList<>(localJars);
		jarsToBeRemoved.removeAll(remoteJars);
		
		List<FileInfo> jarsToBeDownloaded = new ArrayList<>(remoteJars);
		jarsToBeDownloaded.removeAll(localJars);
		
		LOGGER.info("localJars = " + localJars);
		LOGGER.info("remoteJars = " + remoteJars);
		LOGGER.info("jarsToBeRemoved = " + jarsToBeRemoved);
		LOGGER.info("jarsToBeDownloaded = " + jarsToBeDownloaded);
		
		List<FileInfo> localResources = getLocalFilesInfo(new File(bioFolderPath + "/apps/bw/resources"), hashingAlgorithm);
		List<FileInfo> remoteResources = getRemoteFilesInfo(serverBaseUrl + "/bw/resources.hashes", serverConnectTimeoutSeconds, serverReadTimeoutSeconds);
		if(localResources == null || remoteResources == null) return null;
		
		List<FileInfo> resourcesToBeRemoved = new ArrayList<>(localResources);
		resourcesToBeRemoved.removeAll(remoteResources);
		
		List<FileInfo> resourcesToBeDownloaded = new ArrayList<>(remoteResources);
		resourcesToBeDownloaded.removeAll(localResources);
		
		LOGGER.info("localResources = " + localResources);
		LOGGER.info("remoteResources = " + remoteResources);
		LOGGER.info("resourcesToBeRemoved = " + resourcesToBeRemoved);
		LOGGER.info("resourcesToBeDownloaded = " + resourcesToBeDownloaded);
		
		return jarsToBeRemoved.size() > 0 || jarsToBeDownloaded.size() > 0 || resourcesToBeRemoved.size() > 0 || resourcesToBeDownloaded.size() > 0;
	}
	
	private static List<FileInfo> getLocalFilesInfo(File folderPath, String hashingAlgorithm)
	{
		List<FileInfo> fileInfoList = new ArrayList<>();
		
		if(folderPath != null && (folderPath.isDirectory() || !folderPath.exists()))
		{
			if(folderPath.exists())
			{
				File[] files = folderPath.listFiles();
				
				if(files != null) for(File file : files)
				{
					FileInfo fileInfo = new FileInfo();
					fileInfo.fileRelativePath = folderPath.toURI().relativize(file.toURI()).getPath();
					fileInfo.fileHash = getFileHash(file, hashingAlgorithm);
					fileInfo.fileSize = file.length();
					fileInfoList.add(fileInfo);
				}
				else
				{
					LOGGER.severe("Listing the folder return null: " + folderPath.getAbsolutePath()); // should never happen
					return null;
				}
			}
			else
			{
				boolean folderCreated = folderPath.mkdirs();
				LOGGER.info("Created the folder (" + folderPath.getAbsolutePath() + "): " + folderCreated);
			}
		}
		else
		{
			LOGGER.severe("The following path is not a directory: " + (folderPath != null ? folderPath.getAbsolutePath() : "null"));
			return null;
		}
		
		return fileInfoList;
	}
	
	@SuppressWarnings("unchecked")
	private static List<FileInfo> getRemoteFilesInfo(String hashesUrl, int serverConnectTimeoutSeconds, int serverReadTimeoutSeconds)
	{
		List<FileInfo> filesInfo = new ArrayList<>();
		
		URL serviceUrl;
		try
		{
			serviceUrl = new URL(hashesUrl);
		}
		catch(MalformedURLException e)
		{
			LOGGER.log(Level.SEVERE, "The following URL is not valid: " + hashesUrl, e);
			return null;
		}
		
		HttpURLConnection httpConnection;
		try
		{
			httpConnection = (HttpURLConnection) serviceUrl.openConnection();
		}
		catch(IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to open the connection with the URL: " + hashesUrl, e);
			return null;
		}
		
		httpConnection.setConnectTimeout(serverConnectTimeoutSeconds * 1000);
		httpConnection.setReadTimeout(serverReadTimeoutSeconds * 1000);
		httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
		
		try
		{
			httpConnection.connect();
		}
		catch(IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to connect to the URL: " + hashesUrl, e);
			return null;
		}
		
		int status;
		try
		{
			status = httpConnection.getResponseCode();
		}
		catch(IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to get a response code from the connection to the URL: " + hashesUrl, e);
			return null;
		}
		
		if(status == 200)
		{
			Map<String, Map<String, String>> result;
			try
			{
				InputStream response = httpConnection.getInputStream();
				Reader reader = new InputStreamReader(response, "UTF-8");
				result = new ObjectMapper().readValue(reader, Map.class);
			}
			catch(Exception e)
			{
				LOGGER.log(Level.SEVERE, "Failed to parse the result from the URL: " + hashesUrl, e);
				return null;
			}
			
			if(result != null) for(Map.Entry<String, Map<String, String>> entry : result.entrySet())
			{
				Map<String, String> fileProperties = entry.getValue();
				FileInfo fileInfo = new FileInfo();
				fileInfo.fileRelativePath = entry.getKey();
				fileInfo.fileHash = fileProperties.get("fileHash");
				fileInfo.fileSize = Long.parseLong(fileProperties.get("fileSize"));
				filesInfo.add(fileInfo);
			}
		}
		else if(status == 404) // folder not found
		{
			LOGGER.info("webservice = " + hashesUrl + ", responseCode = 404");
		}
		else
		{
			LOGGER.severe("webservice = " + hashesUrl + ", responseCode = " + status);
			return null;
		}
		
		return filesInfo;
	}
	
	private static String getFileHash(File file, String hashingAlgorithm)
	{
		RandomAccessFile f = null;
		try
		{
			MessageDigest digest = MessageDigest.getInstance(hashingAlgorithm);
			f = new RandomAccessFile(file, "r");
			byte[] bytes = new byte[(int) f.length()];
			f.readFully(bytes);
			byte[] hash = digest.digest(bytes);
			return DatatypeConverter.printHexBinary(hash);
		}
		catch(NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "The hashing algorithm does not exist: " + hashingAlgorithm, e);
		}
		catch(IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to read the file: " + file.getAbsolutePath(), e);
		}
		finally
		{
			if(f != null) try
			{
				f.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
}