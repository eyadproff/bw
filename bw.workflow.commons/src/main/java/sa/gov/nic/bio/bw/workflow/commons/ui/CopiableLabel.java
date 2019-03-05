package sa.gov.nic.bio.bw.workflow.commons.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.utils.AppUtils;

import java.util.Locale;

public class CopiableLabel extends Label
{
	public CopiableLabel()
	{
		addCopyButton();
	}
	
	public CopiableLabel(String text)
	{
		super(text);
		addCopyButton();
	}
	
	public CopiableLabel(String text, Node graphic)
	{
		super(text, graphic);
	}
	
	private void addCopyButton()
	{
		Button button = new Button();
		button.visibleProperty().bind(textProperty().isEmpty().not());
		button.managedProperty().bind(textProperty().isEmpty().not());
		button.setFocusTraversable(false);
		button.setPadding(new Insets(0.0, 4.0, 0.0, 4.0));
		button.setOnAction(actionEvent -> AppUtils.copyToClipboard(AppUtils.localizeNumbers(getText(),
		                                                           Locale.ENGLISH, false)));
		Glyph clipboardIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.CLIPBOARD);
		clipboardIcon.setFontSize(8.0);
		button.setGraphic(clipboardIcon);
		setGraphic(button);
		setContentDisplay(ContentDisplay.RIGHT);
	}
}