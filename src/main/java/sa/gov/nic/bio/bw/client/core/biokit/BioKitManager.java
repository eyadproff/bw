package sa.gov.nic.bio.bw.client.core.biokit;

import sa.gov.nic.bio.biokit.BiokitCommander;
import sa.gov.nic.bio.biokit.BiokitCommanderFactory;
import sa.gov.nic.bio.biokit.DeviceServiceFactory;
import sa.gov.nic.bio.biokit.DeviceUtilitiesServiceFactory;
import sa.gov.nic.bio.biokit.exceptions.AlreadyConnectedException;
import sa.gov.nic.bio.biokit.exceptions.ConnectionException;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.face.FaceService;
import sa.gov.nic.bio.biokit.fingerprint.FingerprintService;
import sa.gov.nic.bio.biokit.fingerprint.FingerprintUtilitiesService;
import sa.gov.nic.bio.biokit.utils.JsonMapper;
import sa.gov.nic.bio.biokit.websocket.ClosureListener;
import sa.gov.nic.bio.biokit.websocket.UpdateListener;
import sa.gov.nic.bio.biokit.websocket.WebsocketClient;
import sa.gov.nic.bio.biokit.websocket.WebsocketLogger;
import sa.gov.nic.bio.biokit.websocket.beans.Message;

public class BioKitManager
{
	private String bclId;
	private int websocketPort;
	private WebsocketClient websocketClient;
	private FaceService faceService;
	private FingerprintService fingerprintService;
	private FingerprintUtilitiesService fingerprintUtilitiesService;
	private BiokitCommander biokitCommander;
	
	public BioKitManager(String bclId, int websocketPort, String websocketServerUrl,
	                     int maxTextMessageBufferSizeInBytes, int maxBinaryMessageBufferSizeInBytes,
	                     int responseTimeoutSeconds, JsonMapper<Message> jsonMapper, ClosureListener closureListener,
	                     WebsocketLogger websocketLogger, UpdateListener updateListener)
	{
		this.bclId = bclId;
		this.websocketPort = websocketPort;
		
		websocketClient = new WebsocketClient(websocketServerUrl, maxTextMessageBufferSizeInBytes,
		                                      maxBinaryMessageBufferSizeInBytes, responseTimeoutSeconds, jsonMapper,
		                                      closureListener, websocketLogger, updateListener);
		
		faceService = DeviceServiceFactory.getFaceService(websocketClient);
		fingerprintService = DeviceServiceFactory.getFingerprintService(websocketClient);
		fingerprintUtilitiesService = DeviceUtilitiesServiceFactory.getFingerprintUtilitiesService(websocketClient);
		biokitCommander = BiokitCommanderFactory.getBiokitCommander(websocketClient);
	}
	
	public void setClosureListener(ClosureListener closureListener)
	{
		this.websocketClient.setClosureListener(closureListener);
	}
	
	public void setUpdateListener(UpdateListener updateListener)
	{
		this.websocketClient.setUpdateListener(updateListener);
	}
	
	public void connect() throws ConnectionException, AlreadyConnectedException
	{
		websocketClient.connect();
	}
	
	public void disconnect() throws ConnectionException, NotConnectedException
	{
		websocketClient.disconnect();
	}
	
	public String getBclId(){return bclId;}
	public int getWebsocketPort(){return websocketPort;}
	public FaceService getFaceService(){return faceService;}
	public FingerprintService getFingerprintService(){return fingerprintService;}
	public FingerprintUtilitiesService getFingerprintUtilitiesService(){return fingerprintUtilitiesService;}
	public BiokitCommander getBiokitCommander(){return biokitCommander;}
}