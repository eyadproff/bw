package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.interfaces.HideableController;

public abstract class SubFxControllerBase extends FxControllerBase implements HideableController
{
	@FXML protected Pane rootPane;
	protected CoreFxController coreFxController;
	
	@Override
	public Pane getRootPane()
	{
		return rootPane;
	}
	
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
}