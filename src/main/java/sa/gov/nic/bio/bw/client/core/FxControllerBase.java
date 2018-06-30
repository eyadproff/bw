package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A base class for all JavaFX controllers.
 *
 * @author Fouad Almalki
 */
public abstract class FxControllerBase
{
	/*
	 *	location and resources are injected automatically by JavaFX.
	 */
	
	/**
	 * Location of the FXML file.
	 */
	@FXML
	protected URL location;
	
	/**
	 * The resource bundle passed to FXMLLoader at initialization.
	 */
	@FXML
	protected ResourceBundle resources;
	
	/**
	 * Called after the FXML is completely processed and the JavaFX nodes are created. All fields annotated with @FXML
	 * is processed before this method is called.
	 */
	@FXML
	protected void initialize(){}
}