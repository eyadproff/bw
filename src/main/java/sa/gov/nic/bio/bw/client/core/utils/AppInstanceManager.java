package sa.gov.nic.bio.bw.client.core.utils;

import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.logging.Level;

public class AppInstanceManager implements AppLogger
{
	public static boolean checkIfAlreadyRunning()
	{
		String userHomeDirectory = System.getProperty("user.home");
		String fileToBeLocked = userHomeDirectory + File.separator + "bw-lock-file";
		
		File file = new File(fileToBeLocked);
		
		try
		{
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			FileLock fileLock = randomAccessFile.getChannel().tryLock();
			
			if(fileLock != null)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(() ->
				{
					try
					{
						fileLock.release();
						randomAccessFile.close();
						boolean deleted = file.delete();
						LOGGER.info("Deleted the lock file (" + file.getAbsolutePath() + ")? " + deleted);
					}
					catch(Exception e)
					{
						LOGGER.log(Level.SEVERE, "Unable to delete the lock file: " + file.getAbsolutePath(), e);
					}
				}));
				
				return false;
			}
		}
		catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create and/or lock file: " + file.getAbsolutePath(), e);
		}
		
		return true;
	}
}