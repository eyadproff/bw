package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.interfaces.BodyFxController;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;

/**
 * An instance of this class would store the current state of GUI related stuff.
 */
public class GuiState
{
	private GuiLanguage language;
	private BodyFxController bodyController;
	
	GuiState(){}
	
	public GuiLanguage getLanguage(){return language;}
	public void setLanguage(GuiLanguage language){this.language = language;}
	public BodyFxController getBodyController(){return bodyController;}
	public void setBodyController(BodyFxController bodyController){this.bodyController = bodyController;}
}