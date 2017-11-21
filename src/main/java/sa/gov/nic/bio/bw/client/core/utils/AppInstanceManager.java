package sa.gov.nic.bio.bw.client.core.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInstanceManager
{
	private static final Logger LOGGER = Logger.getLogger(AppInstanceManager.class.getName());
	private static final InetAddress LOCALHOST = InetAddress.getLoopbackAddress();
	private static final int PORT = 55321;
	
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
						LOGGER.warning("Deleted the lock file (" + file.getAbsolutePath() + ")? " + deleted);
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