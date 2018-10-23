package sa.gov.nic.bio.bw.client.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import sa.gov.nic.bio.bw.client.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A base class for all JavaFX controllers.
 *
 * @author Fouad Almalki
 */
public abstract class FxControllerBase implements AppLogger
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
	
	/**
	 * Called after <code>initialize()</code>. It is used to apply global customization for all nodes in the GUI.
	 *
	 * @param rootNode the root node that is associated with this controller
	 */
	final void postInitialization(Node rootNode)
	{
		Set<Node> buttons = rootNode.lookupAll(".button");
		buttons.forEach(node ->
		{
			if(node instanceof Button)
			{
				Button button = (Button) node;
				GuiUtils.makeButtonClickableByPressingEnter(button);
			}
		});
	}
}