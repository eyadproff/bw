package sa.gov.nic.bio.bw.client.core.utils;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.CoreFxController;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.login.tasks.LogoutTask;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiUtils
{
	private static final Logger LOGGER = Logger.getLogger(GuiUtils.class.getName());
	
	public static void showNode(Node node, boolean bShow)
	{
		node.setVisible(bShow);
		node.setManaged(bShow);
	}
	
	public static void buildErrorMessage(Throwable throwable, String[] errorDetails, StringBuilder sb)
	{
		if(errorDetails != null)
		{
			for(String s : errorDetails) sb.append(s).append("\n");
		}
		
		if(throwable != null)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			String exceptionText = sw.toString();
			sb.append(exceptionText);
		}
	}
	
	public static EventHandler<WindowEvent> createOnExitHandler(Stage stage, CoreFxController coreFxController)
	{
		return event ->
		{
			event.consume(); // prevent the stage from closing
			
			String message = coreFxController.getResourceBundle().getString("exit.confirm");
			boolean confirmed = coreFxController.showConfirmationDialogAndWait(null, message);
			
			if(confirmed)
			{
				stage.close();
				LOGGER.info("The main window is closed");
				
				Future<?> future = Context.getExecutorService().submit(new LogoutTask());
				try
				{
					int timeout = Integer.parseInt(System.getProperty("jnlp.bio.bw.onExit.logout.timeout.seconds"));
					future.get(timeout, TimeUnit.SECONDS);
				}
				catch(TimeoutException e)
				{
					LOGGER.log(Level.WARNING, "logout service timed out!", e);
				}
				catch(Exception e)
				{
					LOGGER.log(Level.WARNING, "logout service failed!", e);
				}
				
				Context.getScheduledExecutorService().shutdownNow();
				Context.getExecutorService().shutdownNow();
				Context.getWorkflowManager().interruptTheWorkflow();
				
				Platform.exit();
				LOGGER.info("The application is exited!");
				System.exit(0); // last resort
			}
		};
	}
	
	public static void applyValidatorToTextField(TextField textField, String validationRegex, String discardRegex,
	                                             int maxCharCount)
	{
		textField.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue.length() > maxCharCount) textField.setText(oldValue);
			if(validationRegex != null && discardRegex != null && !newValue.matches(validationRegex))
												textField.setText(newValue.replaceAll(discardRegex, ""));
		});
	}
	
	public static void applyValidatorToTextField(TextField textField, int maxCharCount)
	{
		applyValidatorToTextField(textField, null, null, maxCharCount);
	}
	
	public static void makeButtonClickableByPressingEnter(Button button)
	{
		button.setOnKeyPressed(event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				button.fire();
				button.setDisable(true); // prevent "holding ENTER key" from firing the event again
				event.consume();
			}
		});
	}
	
	public static void makeComboBoxOpenableByPressingSpaceBar(ComboBox<?> comboBox)
	{
		comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if(event.getCode() == KeyCode.SPACE)
			{
				if(!comboBox.isShowing()) comboBox.show();
			}
		});
	}
	
	public static <T> void addAutoCompletionSupportToComboBox(ComboBox<HideableItem<T>> comboBox, List<T> items)
	{
		ObservableList<HideableItem<T>> hideableHideableItems = FXCollections.observableArrayList(hideableItem ->
                                                                    new Observable[]{hideableItem.hiddenProperty()});
		
		items.forEach(item ->
		{
			HideableItem<T> hideableItem = new HideableItem<>(item);
			hideableHideableItems.add(hideableItem);
		});
		
		FilteredList<HideableItem<T>> filteredHideableItems = new FilteredList<>(hideableHideableItems,
		                                                                         t -> !t.isHidden());
		
		comboBox.setItems(filteredHideableItems);
		
		@SuppressWarnings("unchecked")
		HideableItem<T>[] selectedItem = (HideableItem<T>[]) new HideableItem[1];
		
		comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if(comboBox.isShowing())
			{
				comboBox.setEditable(true);
				comboBox.getEditor().clear();
			}
			else if(event.getCode() != KeyCode.ESCAPE && event.getCode() != KeyCode.UP &&
					event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.TAB &&
					event.getCode() != KeyCode.ENTER && event.getCode() != KeyCode.SPACE &&
					event.getCode() != KeyCode.RIGHT && event.getCode() != KeyCode.LEFT &&
					event.getCode() != KeyCode.SHIFT)
			{
				comboBox.show();
				comboBox.setEditable(true);
				comboBox.getEditor().clear();
			}
		});
		
		comboBox.showingProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue)
		    {
			    @SuppressWarnings("unchecked")
			    ListView<HideableItem> lv = ((ComboBoxListViewSkin<HideableItem>) comboBox.getSkin()).getListView();
			    lv.scrollTo(comboBox.getValue());
				
		        Platform.runLater(() ->
		        {
		        	if(selectedItem[0] == null)
			        {
				        double cellHeight = ((Control) lv.lookup(".list-cell")).getHeight();
				        lv.setFixedCellSize(cellHeight);
				        lv.setOnKeyPressed(event ->
				        {
				        	if(comboBox.isShowing() && (event.getCode() == KeyCode.ENTER ||
					           event.getCode() == KeyCode.ESCAPE)) comboBox.hide();
				        });
			        }
		        });
		    }
		    else
		    {
		        HideableItem<T> value = comboBox.getValue();
		        if(value != null) selectedItem[0] = value;
		
		        comboBox.setEditable(false);
		
		        Platform.runLater(() ->
		        {
		            comboBox.getSelectionModel().select(selectedItem[0]);
		            comboBox.setValue(selectedItem[0]);
		        });
		    }
		});
		
		comboBox.setOnHidden(event -> hideableHideableItems.forEach(item -> item.setHidden(false)));
		
		comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) ->
	    {
	        if(!comboBox.isShowing()) return;
	
	        Platform.runLater(() ->
			{
				if(comboBox.getSelectionModel().getSelectedItem() == null)
				{
					hideableHideableItems.forEach(item -> item.setHidden(!item.getText().toLowerCase()
																			  .contains(newValue.toLowerCase())));
				}
				else
				{
					boolean validText = false;
					
					for(HideableItem hideableItem : hideableHideableItems)
					{
						if(hideableItem.getText().equals(newValue))
						{
							validText = true;
							break;
						}
					}
					
					if(!validText) comboBox.getSelectionModel().select(null);
				}
			});
	    });
	}

	public static void attachImageDialog(CoreFxController coreFxController, ImageView imageView, String dialogTitle,
	                                     String showImageText)
	{
		Runnable runnable = () ->
		{
			ImageView iv = new ImageView(imageView.getImage());
			iv.setPreserveRatio(true);

			double taskBarHeight = 40.0;
			double rightLeftWindowBorders = 6.0;
			double topBottomWindowBorders = 29.0;
			double padding = 10.0;

			double maxWidth = Screen.getPrimary().getBounds().getWidth() - rightLeftWindowBorders - padding;
			double maxHeight = Screen.getPrimary().getBounds().getHeight() - taskBarHeight - topBottomWindowBorders
																						   - padding;

			double widthDiff = iv.getImage().getWidth() - maxWidth;
			double heightDiff = iv.getImage().getHeight() - maxHeight;

			boolean beyondWidth = widthDiff > 0;
			boolean beyondHeight = heightDiff > 0;

			if(beyondWidth && beyondHeight)
			{
				if(widthDiff >= heightDiff) iv.setFitWidth(maxWidth);
				else iv.setFitHeight(maxHeight);
			}
			else if(beyondWidth) iv.setFitWidth(maxWidth);
			else if(beyondHeight) iv.setFitHeight(maxHeight);

			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(iv);
			Stage stage = DialogUtils.buildCustomDialog(coreFxController.getStage(), dialogTitle, borderPane,
			                                            Context.getGuiLanguage().getNodeOrientation()
					                                            == NodeOrientation.RIGHT_TO_LEFT, false);
			stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent ->
			{
				if(keyEvent.getCode() == KeyCode.ESCAPE) stage.close();
			});
			stage.show();
		};

		imageView.setOnMouseClicked(mouseEvent ->
		{
		    // left-double-click
		    if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2)
		    {
		        runnable.run();
		    }
		});

		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem = new MenuItem(showImageText);
		menuItem.setOnAction(event -> runnable.run());
		contextMenu.getItems().add(menuItem);

		imageView.setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(imageView,
		                                                                         contextMenuEvent.getScreenX(),
		                                                                         contextMenuEvent.getScreenY()));
	}
	
	public static Image scaleImage(Image source, double targetWidth, double targetHeight)
	{
		ImageView imageView = new ImageView(source);
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(targetWidth);
		imageView.setFitHeight(targetHeight);
		return imageView.snapshot(null, null);
	}
	
	public static Image mergeImage(Image right, Image left)
	{
		//do some calculate first
		int offset  = 5;
		double width = left.getWidth() + right.getWidth() + offset;
		double height = Math.max(left.getHeight(),right.getHeight()) + offset;
		//create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color oldColor = g2.getColor();
		//fill background
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, (int) width, (int) height);
		//draw image
		g2.setColor(oldColor);
		g2.drawImage(SwingFXUtils.fromFXImage(left, null), null, 0, 0);
		g2.drawImage(SwingFXUtils.fromFXImage(right, null), null, (int) left.getWidth() + offset, 0);
		g2.dispose();
		
		return SwingFXUtils.toFXImage(newImage, null);
	}
}