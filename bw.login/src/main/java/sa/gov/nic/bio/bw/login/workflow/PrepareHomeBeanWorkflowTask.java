package sa.gov.nic.bio.bw.login.workflow;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.features.commons.beans.HomeBean;
import sa.gov.nic.bio.bw.core.beans.UserInfo;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.logging.Level;

public class PrepareHomeBeanWorkflowTask implements WorkflowTask
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
		byte[] faceImageByteArray = null;
		Image faceImage = null;
		
		if(encodedFaceImage != null && !encodedFaceImage.isEmpty())
		{
			try
			{
				faceImageByteArray = Base64.getDecoder().decode(encodedFaceImage);
			}
			catch(Exception e)
			{
				LOGGER.log(Level.WARNING, "Failed to decode the Base64 string encodedFaceImage = " +
						encodedFaceImage, e);
			}
			
			if(faceImageByteArray != null)
			{
				try
				{
					faceImage = new Image(new ByteArrayInputStream(faceImageByteArray));
				}
				catch(Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed to load the avatar faceImage!", e);
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
		
		String sLoginTime = loginTime > 0 ? AppUtils.formatHijriGregorianDateTime(loginTime) : null;
		String sLastLogonTime = lastLogonTime > 0 ? AppUtils.formatHijriGregorianDateTime(lastLogonTime) : null;
		String sLastFailedLoginTime = lastFailedLoginTime > 0 ?
													AppUtils.formatHijriGregorianDateTime(lastFailedLoginTime) : null;
		String sFailedLoginCount = failedLoginCount > 0 ? AppUtils.localizeNumbers(String.valueOf(failedLoginCount))
																												: null;
		String sLastPasswordChangeTime = lastPasswordChangeTime > 0 ?
												AppUtils.formatHijriGregorianDateTime(lastPasswordChangeTime) : null;
		String sPasswordExpirationTime =
					passwordExpirationTime > 0 ? AppUtils.formatHijriGregorianDateTime(passwordExpirationTime) : null;
		
		HomeBean homeBean = new HomeBean(username, operator, location, sLoginTime, sLastLogonTime,
		                                 sLastFailedLoginTime, sFailedLoginCount, sLastPasswordChangeTime,
		                                 sPasswordExpirationTime, faceImage);
		
		Context.getUserSession().setAttribute("homeBean", homeBean);
	}
}