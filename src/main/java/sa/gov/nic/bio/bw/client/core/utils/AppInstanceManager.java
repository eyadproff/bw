package sa.gov.nic.bio.bw.client.core.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AppInstanceManager
{
	private static final InetAddress LOCALHOST = InetAddress.getLoopbackAddress();
	private static final int PORT = 55321;
	
	public static boolean checkIfAlreadyRunning()
	{
		try(Socket ignored = new Socket(LOCALHOST, PORT))
		{
			return true;
		}
		catch(IOException ignored)
		{
			return false;
		}
	}
	
	public static void registerInstance()
	{
		Thread thread = new Thread(() ->
		{
			try(ServerSocket socket = new ServerSocket(PORT, 10, LOCALHOST))
			{
				socket.accept();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		});
		
		thread.setDaemon(true); // to shutdown the application on closing the GUI (on stopping all non-daemon threads)
		thread.start();
	}
}