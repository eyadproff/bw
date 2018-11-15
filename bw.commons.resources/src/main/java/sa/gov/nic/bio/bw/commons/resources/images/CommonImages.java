package sa.gov.nic.bio.bw.commons.resources.images;

import sa.gov.nic.bio.bw.commons.resources.JavaResource;

public enum CommonImages implements JavaResource
{
	ICON_APP("icon_app.png"),
	ICON_SUCCESS_12PX("icon_success_12px.png"),
	ICON_SUCCESS_16PX("icon_success_16px.png"),
	ICON_SUCCESS_32PX("icon_success_32px.png"),
	ICON_WARNING_12PX("icon_warning_12px.png"),
	ICON_WARNING_16PX("icon_warning_16px.png"),
	ICON_WARNING_32PX("icon_warning_32px.png"),
	ICON_ERROR_16PX("icon_error_16px.png"),
	ICON_ERROR_32PX("icon_error_32px.png"),
	ICON_GREEN_STATUS("icon_green_status.png"),
	ICON_YELLOW_STATUS("icon_yellow_status.png"),
	ICON_RED_STATUS("icon_red_status.png"),
	LOGO_NIC("logo_nic.png"),
	LOGO_SEMAT("logo_semat.png"),
	LOGO_SAUDI_SECURITY("logo_saudi_security.jpg"),
	PLACEHOLDER_AVATAR("placeholder_avatar.jpg"),
	PLACEHOLDER_IMAGE("placeholder_image.png"),
	PLACEHOLDER_IMAGE_SQUARE("placeholder_image_square.png"),
	PLACEHOLDER_SKIP("placeholder_skip.png");
	
	private String fileName;
	
	CommonImages(String fileName)
	{
		this.fileName = fileName;
	}
	
	@Override
	public String getFileName()
	{
		return fileName;
	}
}