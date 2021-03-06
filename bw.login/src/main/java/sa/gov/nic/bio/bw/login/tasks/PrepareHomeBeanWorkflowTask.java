package sa.gov.nic.bio.bw.login.tasks;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.HomeBean;
import sa.gov.nic.bio.bw.core.beans.UserInfo;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.logging.Level;

public class PrepareHomeBeanWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private UserInfo userInfo;
	
	@Override
	public void execute()
	{
		Context.getUserSession().setAttribute("userInfo", userInfo);
		
		String username = userInfo.getUserName();
		String operatorName = userInfo.getOperatorName();
		long operatorId = userInfo.getOperatorId();
		String locationName = userInfo.getLocationName();
		String locationId = userInfo.getLocationId();
		
		String operator;
		String location;
		
		if(operatorName == null) operatorName = "-";
		if(locationName == null) locationName = "-";
		
		operator = operatorName + (operatorId > 0 ? " (" + userInfo.getOperatorId() + ")" : "");
		location = locationName + (locationId != null && !locationId.isEmpty() &&
												!locationId.equals("0") ? " (" + userInfo.getLocationId() + ")" : "");
		
		String encodedFaceImage = userInfo.getFaceImage();
		byte[] facePhotoByteArray = null;
		Image facePhoto = null;
		
		if(encodedFaceImage != null && !encodedFaceImage.isEmpty())
		{
			try
			{
				facePhotoByteArray = Base64.getDecoder().decode(encodedFaceImage);
			}
			catch(Exception e)
			{
				LOGGER.log(Level.WARNING, "Failed to decode the Base64 string encodedFaceImage = " +
						encodedFaceImage, e);
			}
			
			if(facePhotoByteArray != null)
			{
				try
				{
					facePhoto = new Image(new ByteArrayInputStream(facePhotoByteArray));
				}
				catch(Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed to load the avatar facePhoto!", e);
				}
			}
		}
		
		// remove extra spaces in between and on edges
		username = username.trim().replaceAll("\\s+", " ");
		operator = operator.trim().replaceAll("\\s+", " ");
		location = location.trim().replaceAll("\\s+", " ");
		
		// localize numbers
		operator = AppUtils.localizeNumbers(operator, Locale.getDefault(), false);
		location = AppUtils.localizeNumbers(location, Locale.getDefault(), false);
		
		long loginTime = System.currentTimeMillis();
		long lastLogonTime = userInfo.getLastLogonTime();
		long lastFailedLoginTime = userInfo.getBadPasswordTime();
		int failedLoginCount = userInfo.getBadPasswordCount();
		long lastPasswordChangeTime = userInfo.getPasswordLastSet();
		long passwordExpirationTime = userInfo.getAccountExperiyDate();
		
		String sLoginTime = formatHijriGregorianDateTime(loginTime);
		String sLastLogonTime = formatHijriGregorianDateTime(lastLogonTime);
		String sLastFailedLoginTime = formatHijriGregorianDateTime(lastFailedLoginTime);
		String sLastPasswordChangeTime = formatHijriGregorianDateTime(lastPasswordChangeTime);
		String sPasswordExpirationTime = formatHijriGregorianDateTime(passwordExpirationTime);
		String sFailedLoginCount = failedLoginCount > 0 ?
												AppUtils.localizeNumbers(String.valueOf(failedLoginCount)) : null;
		
		HomeBean homeBean = new HomeBean(username, operator, location, sLoginTime, sLastLogonTime,
		                                 sLastFailedLoginTime, sFailedLoginCount, sLastPasswordChangeTime,
		                                 sPasswordExpirationTime, facePhoto);
		
		Context.getUserSession().setAttribute("homeBean", homeBean);
	}
	
	private String formatHijriGregorianDateTime(long milliSeconds)
	{
		return milliSeconds > 0 ? AppUtils.formatHijriGregorianDateTime(milliSeconds / 1000L) : null;
	}
}