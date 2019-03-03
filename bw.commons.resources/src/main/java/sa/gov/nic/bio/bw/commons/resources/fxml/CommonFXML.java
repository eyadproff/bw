package sa.gov.nic.bio.bw.commons.resources.fxml;

import sa.gov.nic.bio.bw.commons.resources.JavaResource;

public enum CommonFXML implements JavaResource
{
	FACE_3D_MODEL("face3DModel.fxml");
	
	private String fileName;
	
	CommonFXML(String fileName)
	{
		this.fileName = fileName;
	}
	
	@Override
	public String getFileName()
	{
		return fileName;
	}
}