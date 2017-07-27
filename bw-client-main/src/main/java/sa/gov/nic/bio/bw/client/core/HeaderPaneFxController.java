package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.interfaces.VisibilityControl;

import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class HeaderPaneFxController implements VisibilityControl
{
	@FXML private ResourceBundle resources;
	@FXML private Pane headerPane;
	
	@FXML
	private void initialize()
	{
	
	}
	
	@Override
	public Pane getRootPane()
	{
		return headerPane;
	}
}