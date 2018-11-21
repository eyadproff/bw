package sa.gov.nic.bio.bw.core.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.controllers.CoreFxController;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class GuiUtils implements AppLogger
{
	public static class OrElse<T>
	{
		private T object;
		private boolean executeOrElse;
		
		public OrElse(T object, boolean executeOrElse)
		{
			this.object = object;
			this.executeOrElse = executeOrElse;
		}
		
		public void orElse(Consumer<T> consumer)
		{
			if(executeOrElse) consumer.accept(object);
		}
	}
	
	private static final DateTimeFormatter dateFormatterForParsing =
							DateTimeFormatter.ofPattern("d/M/yyyy", AppConstants.Locales.SAUDI_EN_LOCALE);
	private static final DateTimeFormatter dateFormatterForFormatting =
							DateTimeFormatter.ofPattern("dd/MM/yyyy", AppConstants.Locales.SAUDI_EN_LOCALE);
	
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
	
	public static EventHandler<WindowEvent> createOnExitHandler(Stage stage, CoreFxController coreFxController,
	                                                            Task<?> logoutTask)
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
				
				Future<?> future = Context.getExecutorService().submit(logoutTask);
				int timeout = Integer.parseInt(Context.getConfigManager().getProperty("onExit.logout.timeout.seconds"));
				try
				{
					future.get(timeout, TimeUnit.SECONDS);
				}
				catch(TimeoutException e)
				{
					LOGGER.warning("logout service timed out (" + timeout + " seconds)!");
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
			int oldCaretPosition = textField.getCaretPosition();
			
			if(newValue.length() > maxCharCount)
			{
				textField.setText(oldValue);
				textField.positionCaret(maxCharCount);
			}
			
			if(validationRegex != null && discardRegex != null && !newValue.matches(validationRegex))
						Platform.runLater(() -> {
							textField.setText(newValue.replaceAll(discardRegex, ""));
							textField.positionCaret(oldCaretPosition);
						});
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
				//button.setDisable(true); // prevent "holding ENTER key" from firing the event again
				event.consume();
			}
		});
	}
	
	public static void makeComboBoxOpenableByPressingEnter(ComboBox<?> comboBox)
	{
		comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				if(!comboBox.isShowing()) comboBox.show();
			}
		});
	}
	
	public static <T> void addAutoCompletionSupportToComboBox(ComboBox<ComboBoxItem<T>> comboBox, List<T> items)
	{
		addAutoCompletionSupportToComboBox(comboBox, items, null,
		                                   null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> void addAutoCompletionSupportToComboBox(ComboBox<ComboBoxItem<T>> comboBox, List<T> items,
                                                      ChangeListener<Boolean>[] showingPropertyChangeListenerReference,
                                                      ChangeListener<String>[] textPropertyChangeListenerReference)
	{
		ObservableList<ComboBoxItem<T>> comboBoxItems = FXCollections.observableArrayList(comboBoxItem ->
                                                                new Observable[]{comboBoxItem.hiddenProperty()});
		
		items.forEach(item ->
		{
			ComboBoxItem<T> comboBoxItem = new ComboBoxItem<>(item);
			comboBoxItems.add(comboBoxItem);
		});
		
		FilteredList<ComboBoxItem<T>> filteredComboBoxItems = new FilteredList<>(comboBoxItems, t -> !t.isHidden());
		comboBox.setItems(filteredComboBoxItems);
		
		ComboBoxItem<T>[] selectedItem = (ComboBoxItem<T>[]) new ComboBoxItem[1];
		
		EventHandler<KeyEvent> keyPressedEventHandler = event ->
		{
			if(comboBox.isShowing())
			{
				if(!event.isShiftDown() && !event.isControlDown() && !event.isAltDown())
				{
					comboBox.setEditable(true);
					comboBox.getEditor().clear();
				}
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
		};
		EventHandler<Event> onHiddenEventHandler = event ->
														comboBoxItems.forEach(item -> item.setHidden(false));
		ChangeListener<Boolean> showingPropertyChangeListener = (observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				selectedItem[0] = comboBox.getValue();
				
				
				ListView<ComboBoxItem> lv = (ListView<ComboBoxItem>)
												((ComboBoxListViewSkin<?>) comboBox.getSkin()).getPopupContent();
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
				ComboBoxItem<T> value = comboBox.getValue();
				if(value != null) selectedItem[0] = value;
				
				comboBox.setEditable(false);
				
				Platform.runLater(() ->
				{
				    comboBox.getSelectionModel().select(selectedItem[0]);
				    comboBox.setValue(selectedItem[0]);
				});
			}
		};
		ChangeListener<String> textPropertyChangeListener = (observable, oldValue, newValue) ->
		{
			if(!comboBox.isShowing()) return;
			
			Platform.runLater(() ->
			{
			    if(comboBox.getSelectionModel().getSelectedItem() == null)
			    {
			        comboBoxItems.forEach(item -> item.setHidden(!item.getText().toLowerCase()
			                .contains(newValue.toLowerCase())));
			    }
			    else
			    {
			        boolean validText = false;
			
			        for(ComboBoxItem hideableItem : comboBoxItems)
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
		};
		
		if(showingPropertyChangeListenerReference != null)
		{
			if(showingPropertyChangeListenerReference[0] != null)
			{
				comboBox.showingProperty().removeListener(showingPropertyChangeListenerReference[0]);
			}
			
			showingPropertyChangeListenerReference[0] = showingPropertyChangeListener;
		}
		if(textPropertyChangeListenerReference != null)
		{
			if(textPropertyChangeListenerReference[0] != null)
			{
				comboBox.getEditor().textProperty().removeListener(textPropertyChangeListenerReference[0]);
			}
			
			textPropertyChangeListenerReference[0] = textPropertyChangeListener;
		}
		
		comboBox.setOnKeyPressed(keyPressedEventHandler);
		comboBox.setOnHidden(onHiddenEventHandler);
		comboBox.showingProperty().addListener(showingPropertyChangeListener);
		comboBox.getEditor().textProperty().addListener(textPropertyChangeListener);
	}

	public static void attachImageDialog(CoreFxController coreFxController, ImageView imageView, String dialogTitle,
	                                     String showImageText, boolean blur)
	{
		Runnable runnable = () ->
		{
			ImageView iv = new ImageView(imageView.getImage());
			iv.setPreserveRatio(true);
			
			if(blur) blurImageView(imageView);

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
	
	public static void showMessageTooltip(CheckBox checkBox, String message)
	{
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(8.0));
		
		Label lblMessage = new Label(message);
		lblMessage.setTextAlignment(TextAlignment.CENTER);
		lblMessage.setWrapText(true);
		vBox.getChildren().add(lblMessage);
		
		PopOver popOver = new PopOver(vBox);
		popOver.setArrowIndent(5.0);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		popOver.setAutoHide(true);
		popOver.show(checkBox);
		
		// fix the position
		popOver.setY(popOver.getY() - 7.0);
		popOver.setX(popOver.getX() - (Context.getGuiLanguage().getNodeOrientation() ==
																		NodeOrientation.RIGHT_TO_LEFT ? 7.0 : 5.0));
		
		// auto-hide after 2 seconds
		PauseTransition pause = new PauseTransition(Duration.seconds(2.0));
		pause.setOnFinished(e -> popOver.hide());
		pause.play();
		
		// catch the mouse click
		checkBox.setOnAction(event -> popOver.hide());
		popOver.setOnHidden(event -> checkBox.setOnAction(null));
		popOver.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent ->
		{
			if(mouseEvent.getButton() == MouseButton.PRIMARY)
			{
				popOver.hide();
				checkBox.fire();
			}
		});
	}
	
	public static void initDatePicker(RadioButton rdoUseHijri, DatePicker datePicker,
	                                  Predicate<LocalDate> dateValidator)
	{
		datePicker.setChronology(HijrahChronology.INSTANCE);
		
		Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell()
		{
			@Override
			public void updateItem(LocalDate item, boolean empty)
			{
				super.updateItem(item, empty);
				
				if(dateValidator != null && !dateValidator.test(item))
				{
					Platform.runLater(() -> setDisable(true));
				}
			}
		};
		
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>()
		{
			@Override
			public String toString(LocalDate date)
			{
				Chronology chronology = rdoUseHijri.isSelected() ? HijrahChronology.INSTANCE : IsoChronology.INSTANCE;
				
				if(date != null) return dateFormatterForFormatting.withChronology(chronology).format(date);
				else return "";
			}
			
			@Override
			public LocalDate fromString(String string)
			{
				if(string != null && !string.isEmpty())
				{
					// support "/", "\" and "-" as date separators
					string = string.replace("\\", "/");
					string = string.replace("-", "/");
					
					// if the input is 8 characters long, we will add the separators
					if(string.length() == 8 && !string.contains("/"))
					{
						string = string.substring(0, 2) + "/" + string.substring(2, 4) + "/" + string.substring(4, 8);
					}
					// if the input is 6 characters long, we will consider day and month are both single digits
					// and then add the separators
					else if(string.length() == 6 && !string.contains("/"))
					{
						string = string.substring(0, 1) + "/" + string.substring(1, 2) + "/" + string.substring(2, 6);
					}
					
					Chronology chronology = rdoUseHijri.isSelected() ?
															HijrahChronology.INSTANCE : IsoChronology.INSTANCE;
					
					try
					{
						TemporalAccessor temporalAccessor = dateFormatterForParsing.withChronology(chronology)
																				   .parse(string);
						
						LocalDate date = IsoChronology.INSTANCE.date(temporalAccessor);
						
						if(dateValidator != null && !dateValidator.test(date)) return null;
						else return date;
					}
					catch(DateTimeException e)
					{
						LOGGER.log(Level.FINE, "failed to convert the date (" + string + ") to "
																							+ chronology.getId(), e);
					}
					
					return null;
				}
				else return null;
			}
		};
		
		datePicker.setDayCellFactory(dayCellFactory);
		datePicker.setConverter(converter);
		
		rdoUseHijri.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			// refresh
			LocalDate date = datePicker.getValue();
			datePicker.setValue(null);
			datePicker.setValue(date);
		});
	}
	
	public static Image extractImageByRectangleBounds(ImageView imageView, Rectangle rectangle, boolean rtl,
	                                                   double extraImageY)
	{
		double strokeWidth = rectangle.getStrokeWidth();
		double imageScaledWidth = imageView.getBoundsInParent().getWidth();
		double rectWidth = rectangle.getWidth() - strokeWidth;
		double rectHeight = rectangle.getHeight() - strokeWidth;
		double imageOriginalWidth = imageView.getImage().getWidth();
		double rectX = rectangle.getLayoutX();
		double rectY = rectangle.getLayoutY();
		double imageX = imageView.getLayoutX();
		double imageY = extraImageY + imageView.getLayoutY();
		
		double scale = imageOriginalWidth / imageScaledWidth;
		double scaledRectWidth = scale * rectWidth;
		double scaledRectHeight = scale * rectHeight;
		double scaledRectX = scale * (rectX - imageX);
		double scaledRectY = scale * (rectY - imageY);
		
		if(rtl) scaledRectX = imageOriginalWidth - scaledRectX - scaledRectWidth;
		
		return new WritableImage(imageView.getImage().getPixelReader(),
		                         (int) scaledRectX, (int) scaledRectY,
		                         (int) scaledRectWidth, (int) scaledRectHeight);
	}
	
	public static void ensureNodeVisibilityInHorizontalScrollPane(ScrollPane pane, Node node)
	{
		double width = pane.getContent().getBoundsInLocal().getWidth();
		double x = node.getBoundsInParent().getMaxX();
		
		Animation animation = new Timeline(new KeyFrame(Duration.seconds(0.5),
                                           new KeyValue(pane.hvalueProperty(), x / width)));
		animation.play();
	}
	
	public static void attachFingerprintImages(Map<Integer, String> fingerprintBase64Images, ImageView ivRightThumb,
	                                           ImageView ivRightIndex, ImageView ivRightMiddle, ImageView ivRightRing,
	                                           ImageView ivRightLittle, ImageView ivLeftThumb, ImageView ivLeftIndex,
	                                           ImageView ivLeftMiddle, ImageView ivLeftRing, ImageView ivLeftLittle)
	{
		if(fingerprintBase64Images == null) return;
		
		ResourceBundle resourceBundle = AppUtils.getCoreStringsResourceBundle(Locale.getDefault());
		Map<Integer, String> dialogTitleMap = new HashMap<>();
		
		dialogTitleMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
		                   resourceBundle.getString("label.fingers.thumb") + " (" +
				                   resourceBundle.getString("label.rightHand") + ")");
		dialogTitleMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
		                   resourceBundle.getString("label.fingers.index") + " (" +
				                   resourceBundle.getString("label.rightHand") + ")");
		dialogTitleMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
		                   resourceBundle.getString("label.fingers.middle") + " (" +
				                   resourceBundle.getString("label.rightHand") + ")");
		dialogTitleMap.put(FingerPosition.RIGHT_RING.getPosition(),
		                   resourceBundle.getString("label.fingers.ring") + " (" +
				                   resourceBundle.getString("label.rightHand") + ")");
		dialogTitleMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
		                   resourceBundle.getString("label.fingers.little") + " (" +
				                   resourceBundle.getString("label.rightHand") + ")");
		dialogTitleMap.put(FingerPosition.LEFT_THUMB.getPosition(),
		                   resourceBundle.getString("label.fingers.thumb") + " (" +
				                   resourceBundle.getString("label.leftHand") + ")");
		dialogTitleMap.put(FingerPosition.LEFT_INDEX.getPosition(),
		                   resourceBundle.getString("label.fingers.index") + " (" +
				                   resourceBundle.getString("label.leftHand") + ")");
		dialogTitleMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
		                   resourceBundle.getString("label.fingers.middle") + " (" +
				                   resourceBundle.getString("label.leftHand") + ")");
		dialogTitleMap.put(FingerPosition.LEFT_RING.getPosition(),
		                   resourceBundle.getString("label.fingers.ring") + " (" +
				                   resourceBundle.getString("label.leftHand") + ")");
		dialogTitleMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
		                   resourceBundle.getString("label.fingers.little") + " (" +
				                   resourceBundle.getString("label.leftHand") + ")");
		
		Map<Integer, ImageView> imageViewMap = new HashMap<>();
		
		imageViewMap.put(FingerPosition.RIGHT_THUMB.getPosition(), ivRightThumb);
		imageViewMap.put(FingerPosition.RIGHT_INDEX.getPosition(), ivRightIndex);
		imageViewMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(), ivRightMiddle);
		imageViewMap.put(FingerPosition.RIGHT_RING.getPosition(), ivRightRing);
		imageViewMap.put(FingerPosition.RIGHT_LITTLE.getPosition(), ivRightLittle);
		imageViewMap.put(FingerPosition.LEFT_THUMB.getPosition(), ivLeftThumb);
		imageViewMap.put(FingerPosition.LEFT_INDEX.getPosition(), ivLeftIndex);
		imageViewMap.put(FingerPosition.LEFT_MIDDLE.getPosition(), ivLeftMiddle);
		imageViewMap.put(FingerPosition.LEFT_RING.getPosition(), ivLeftRing);
		imageViewMap.put(FingerPosition.LEFT_LITTLE.getPosition(), ivLeftLittle);
		
		fingerprintBase64Images.forEach((position, fingerprintImage) ->
		{
		    ImageView imageView = imageViewMap.get(position);
		    String dialogTitle = dialogTitleMap.get(position);
		
		    byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
		    imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
		    GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, dialogTitle,
		                               resourceBundle.getString("label.contextMenu.showImage"), false);
		});
	}
	
	public static <T> boolean selectComboBoxItem(ComboBox<ComboBoxItem<T>> comboBox, T item)
	{
		Optional<ComboBoxItem<T>> optional = comboBox.getItems()
													 .stream()
													 .filter(o -> o.getItem().equals(item))
													 .findFirst();
		optional.ifPresent(comboBox::setValue);
		return optional.isPresent();
	}
	
	public static void blurImageView(ImageView imageView)
	{
		int radius = Integer.parseInt(Context.getConfigManager().getProperty("image.blur.radius"));
		imageView.setEffect(new GaussianBlur(radius));
	}
	
	public static void attachFacePhotoBase64(ImageView imageView, String facePhotoBase64, boolean blur,
	                                         Gender subjectGender)
	{
		if(facePhotoBase64 != null)
		{
			UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
			boolean maleOperator = userInfo != null && userInfo.getGender() > 0 &&
															Gender.values()[userInfo.getGender() - 1] == Gender.MALE;
			boolean femaleSubject = subjectGender == Gender.FEMALE;
			blur &= maleOperator && femaleSubject;
			
			@SuppressWarnings("unchecked")
			List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
			String maleSeeFemaleRole = Context.getConfigManager().getProperty("face.roles.maleSeeFemale");
			boolean authorized = userRoles.contains(maleSeeFemaleRole);
			
			byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
			imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
			ResourceBundle resourceBundle = AppUtils.getCoreStringsResourceBundle(Locale.getDefault());
			attachImageDialog(Context.getCoreFxController(), imageView,
			                           resourceBundle.getString("label.personPhoto"),
			                           resourceBundle.getString("label.contextMenu.showImage"),
			                           blur && !authorized);
			
			if(blur && !authorized) blurImageView(imageView);
		}
	}
	
	public static <T> OrElse<Label> setLabelText(Label label, T value)
	{
		return setLabelText(label, value, true);
	}
	
	public static <T> OrElse<Label> setLabelText(Label label, T value, boolean localizedNumbers)
	{
		if(value != null)
		{
			if(localizedNumbers) label.setText(AppUtils.localizeNumbers(String.valueOf(value)));
			else label.setText(String.valueOf(value));
			return new OrElse<>(null, false);
		}
		else return new OrElse<>(label, true);
	}
	
	public static OrElse<Label> setLabelText(Label label, LocalizedText localizedText)
	{
		if(localizedText != null)
		{
			label.setText(localizedText.getLocalizedText());
			return new OrElse<>(null, false);
		}
		else return new OrElse<>(label, true);
	}
	
	public static OrElse<Label> setLabelText(Label label, LocalDate localDate)
	{
		if(localDate != null)
		{
			label.setText(AppUtils.formatHijriGregorianDate(localDate));
			return new OrElse<>(null, false);
		}
		else return new OrElse<>(label, true);
	}
	
	public static OrElse<CheckBox> setCheckBoxSelection(CheckBox checkBox, Boolean value)
	{
		if(value != null)
		{
			checkBox.setSelected(value);
			return new OrElse<>(null, false);
		}
		else return new OrElse<>(checkBox, true);
	}
	
	public static void autoFitTableViewColumns(TableView<?> tableView)
	{
		try
		{
			//Class<?> clazz = Class.forName("javafx.scene.control.skin.TableSkinUtils");
			//Method resizeColumnToFitContentMethod = clazz.getMethod("resizeColumnToFitContent",
			//                                                        TableViewSkinBase.class,
			//                                                        TableColumnBase.class, int.class);
			//resizeColumnToFitContentMethod.setAccessible(true);
			//ObservableList<? extends TableColumn<?, ?>> columns = tableView.getColumns();
			//TableColumnHeader header = (TableColumnHeader) tableView.lookup("TableColumnHeader");
			//System.out.println("header = " + header);
			//
			//Method getTableSkinMethod = TableColumnHeader.class.getDeclaredMethod("getTableSkin");
			//getTableSkinMethod.setAccessible(true);
			//
			//TableViewSkinBase<?, ?, ?, ?, ?> skin = (TableViewSkinBase<?, ?, ?, ?, ?>) getTableSkinMethod.invoke(header);
			//for(TableColumn<?, ?> column : columns)
			//{
			//	column.get
			//	try
			//	{
			//		resizeColumnToFitContentMethod.invoke(null, skin, column, -1);
			//	}
			//	catch(InvocationTargetException e)
			//	{
			//		e.printStackTrace();
			//	}
			//}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}