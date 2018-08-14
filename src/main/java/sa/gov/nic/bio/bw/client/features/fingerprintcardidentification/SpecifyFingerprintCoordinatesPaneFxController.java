package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.gui.SelectionOverlay;

import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class SpecifyFingerprintCoordinatesPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_PREFIX_FINGERPRINT_IMAGE = "FINGERPRINT_IMAGE_";
	private static final String KEY_PREFIX_FINGERPRINT_RECT_X = "FINGERPRINT_RECT_X_";
	private static final String KEY_PREFIX_FINGERPRINT_RECT_Y = "FINGERPRINT_RECT_Y_";
	private static final String KEY_PREFIX_FINGERPRINT_RECT_WIDTH = "FINGERPRINT_RECT_WIDTH_";
	private static final String KEY_PREFIX_FINGERPRINT_RECT_HEIGHT = "FINGERPRINT_RECT_HEIGHT_";
	private static final String KEY_IMAGE_VIEW_X = "IMAGE_VIEW_X";
	private static final String KEY_IMAGE_VIEW_Y = "IMAGE_VIEW_Y";
	
	
	private static final double[][] DEFAULT_BOUNDS = {{0.935, 0.265, 0.160, 0.117}, // x, y, width, height
													  {0.755, 0.265, 0.160, 0.117},
													  {0.570, 0.265, 0.160, 0.117},
													  {0.380, 0.265, 0.160, 0.117},
													  {0.200, 0.265, 0.160, 0.117},
													  {0.935, 0.420, 0.160, 0.117},
													  {0.755, 0.420, 0.160, 0.117},
													  {0.570, 0.420, 0.160, 0.117},
													  {0.380, 0.420, 0.160, 0.117},
													  {0.200, 0.420, 0.160, 0.117}};
	
	private static final double TITLED_PANE_TITLE_HEIGHT = 25.0;
	private static class DragContext
	{
		
		private double x;
		private double y;
	}
	
	@FXML private StackPane spFingerprintCardImage;
	@FXML private ImageView ivFingerprintCardImage;
	@FXML private ImageView ivFingerprintImageAfterCropping;
	@FXML private ImageView ivFingerprintImageAfterCroppingPlaceHolder;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private Rectangle[] rectangles = new Rectangle[10];
	private Label[] labels = new Label[10];
	private Rectangle selectedRectangle;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/specifyFingerprintCoordinates.fxml");
	}
	
	@Override
	protected void initialize()
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Pane root = new Pane();
		spFingerprintCardImage.getChildren().add(root);
		
		Pane objectLayer = new Pane();
		root.getChildren().add(objectLayer);
		
		// selection layer on top of object layer
		Group selectionLayer = new Group();
		root.getChildren().add(selectionLayer);
		
		double imageViewX = ivFingerprintCardImage.getLayoutX();
		double imageViewY = ivFingerprintCardImage.getLayoutY();
		double imageViewWidth = ivFingerprintCardImage.getBoundsInParent().getWidth();
		double imageViewHeight = ivFingerprintCardImage.getBoundsInParent().getHeight();
		
		for(int i = 0; i < rectangles.length; i++)
		{
			final int finalI;
			
			if(rtl)
			{
				if(i < rectangles.length / 2) finalI = (rectangles.length / 2) - i - 1;
				else finalI = rectangles.length - i - 1 + rectangles.length / 2;
			}
			else finalI = i;
			
			double rectX = imageViewWidth * DEFAULT_BOUNDS[i][0];
			double rectY = imageViewHeight * DEFAULT_BOUNDS[i][1];
			double rectWidth = imageViewWidth * DEFAULT_BOUNDS[i][2];
			double rectHeight = imageViewHeight * DEFAULT_BOUNDS[i][3];
			
			labels[finalI] = new Label(AppUtils.replaceNumbersOnly(String.valueOf(finalI + 1), Locale.getDefault()));
			labels[finalI].setMouseTransparent(true);
			labels[finalI].setTextAlignment(TextAlignment.CENTER);
			labels[finalI].setAlignment(Pos.CENTER);
			
			rectangles[finalI] = new Rectangle(rectWidth, rectHeight);
			rectangles[finalI].setFocusTraversable(true);
			rectangles[finalI].getStyleClass().add("fingerprint-rectangle");
			rectangles[finalI].relocate(imageViewWidth - (rectX - imageViewX), rectY - imageViewY);
			
			objectLayer.getChildren().add(rectangles[finalI]);
			objectLayer.getChildren().add(labels[finalI]);
			
			labels[finalI].layoutXProperty().bind(rectangles[finalI].layoutXProperty());
			labels[finalI].layoutYProperty().bind(rectangles[finalI].layoutYProperty());
			labels[finalI].translateXProperty().bind(rectangles[finalI].translateXProperty());
			labels[finalI].translateYProperty().bind(rectangles[finalI].translateYProperty());
			labels[finalI].prefWidthProperty().bind(rectangles[finalI].widthProperty());
			labels[finalI].prefHeightProperty().bind(rectangles[finalI].heightProperty());
			
			makeNodeDraggable(ivFingerprintCardImage, selectionLayer, rectangles[finalI], rtl);
			
			
			ivFingerprintCardImage.boundsInParentProperty().addListener((observable, oldValue, newValue) ->
			{
				double strokeWidth = rectangles[finalI].getStrokeWidth();
			    double scale = newValue.getWidth() / oldValue.getWidth();
			    rectangles[finalI].setWidth(rectangles[finalI].getWidth() * scale);
			    rectangles[finalI].setHeight(rectangles[finalI].getHeight() * scale);
			    rectangles[finalI].relocate(newValue.getMinX() +
	                                scale * (rectangles[finalI].getLayoutX() - strokeWidth / 2.0 - oldValue.getMinX()),
			                               TITLED_PANE_TITLE_HEIGHT + newValue.getMinY() +
	                                scale * (rectangles[finalI].getLayoutY() - strokeWidth / 2.0 -
			                                                        (TITLED_PANE_TITLE_HEIGHT + oldValue.getMinY())));
			});
		}
		
		spFingerprintCardImage.setOnMousePressed(mouseEvent ->
		{
			if(selectedRectangle != null)
			{
				selectedRectangle.getStyleClass().remove("fingerprint-rectangle-selected");
				selectedRectangle.getStyleClass().add("fingerprint-rectangle");
				selectedRectangle = null;
			}
			
			ivFingerprintImageAfterCropping.setImage(null);
			GuiUtils.showNode(ivFingerprintImageAfterCropping, false);
			GuiUtils.showNode(ivFingerprintImageAfterCroppingPlaceHolder, true);
			selectionLayer.getChildren().clear();
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Double imageViewX = (Double) uiInputData.get(KEY_IMAGE_VIEW_X);
			Double imageViewY = (Double) uiInputData.get(KEY_IMAGE_VIEW_Y);
			
			for(int i = 0; i < rectangles.length; i++)
			{
				Double x = (Double) uiInputData.get(KEY_PREFIX_FINGERPRINT_RECT_X + (i + 1));
				Double y = (Double) uiInputData.get(KEY_PREFIX_FINGERPRINT_RECT_Y + (i + 1));
				Double width = (Double) uiInputData.get(KEY_PREFIX_FINGERPRINT_RECT_WIDTH + (i + 1));
				Double height = (Double) uiInputData.get(KEY_PREFIX_FINGERPRINT_RECT_HEIGHT + (i + 1));
				
				if(x != null && y != null && width != null && height != null)
				{
					rectangles[i].relocate(x * ivFingerprintCardImage.getBoundsInParent().getWidth() -
			                       ivFingerprintCardImage.getLayoutX() - rectangles[i].getStrokeWidth() / 2.0 +
							                       2 * (ivFingerprintCardImage.getLayoutX() - imageViewX),
				                           y * ivFingerprintCardImage.getBoundsInParent().getHeight() -
		                           ivFingerprintCardImage.getLayoutY() - rectangles[i].getStrokeWidth() / 2.0 +
						                           2 * (ivFingerprintCardImage.getLayoutY() - imageViewY));
					rectangles[i].setWidth(width * ivFingerprintCardImage.getBoundsInParent().getWidth() -
							                       rectangles[i].getStrokeWidth());
					rectangles[i].setHeight(height * ivFingerprintCardImage.getBoundsInParent().getHeight() -
							                        rectangles[i].getStrokeWidth());
				}
			}
		}
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		for(int i = 0; i < rectangles.length; i++)
		{
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_IMAGE + (i + 1), null);
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_X + (i + 1), null);
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_Y + (i + 1), null);
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_WIDTH + (i + 1), null);
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_HEIGHT + (i + 1), null);
			uiDataMap.put(KEY_IMAGE_VIEW_X, null);
			uiDataMap.put(KEY_IMAGE_VIEW_Y, null);
		}
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		for(int i = 0; i < rectangles.length; i++)
		{
			if(Boolean.FALSE.equals(rectangles[i].getUserData())) // not skipped
			{
				Image image = GuiUtils.extractImageByRectangleBounds(ivFingerprintCardImage, rectangles[i], rtl,
				                                                     TITLED_PANE_TITLE_HEIGHT);
				uiDataMap.put(KEY_PREFIX_FINGERPRINT_IMAGE + (i + 1), image);
			}
			else uiDataMap.put(KEY_PREFIX_FINGERPRINT_IMAGE + (i + 1), null);
			
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_X + (i + 1), (rectangles[i].getLayoutX() +
						ivFingerprintCardImage.getLayoutX()) / ivFingerprintCardImage.getBoundsInParent().getWidth());
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_Y + (i + 1), (rectangles[i].getLayoutY() +
						ivFingerprintCardImage.getLayoutY()) / ivFingerprintCardImage.getBoundsInParent().getHeight());
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_WIDTH + (i + 1), rectangles[i].getBoundsInParent().getWidth() /
																ivFingerprintCardImage.getBoundsInParent().getWidth());
			uiDataMap.put(KEY_PREFIX_FINGERPRINT_RECT_HEIGHT + (i + 1), rectangles[i].getBoundsInParent().getHeight() /
																ivFingerprintCardImage.getBoundsInParent().getHeight());
		}
		
		uiDataMap.put(KEY_IMAGE_VIEW_X, ivFingerprintCardImage.getLayoutX());
		uiDataMap.put(KEY_IMAGE_VIEW_Y, ivFingerprintCardImage.getLayoutY());
	}
	
	@FXML
	private void onPreviousButtonClicked(ActionEvent actionEvent)
	{
		goPrevious();
	}
	
	@FXML
	private void onNextButtonClicked(ActionEvent actionEvent)
	{
		goNext();
	}
	
	private Void updatePreviewImage(Rectangle rectangle, boolean rtl)
	{
		GuiUtils.showNode(ivFingerprintImageAfterCroppingPlaceHolder, false);
		GuiUtils.showNode(ivFingerprintImageAfterCropping, true);
		
		Image image = GuiUtils.extractImageByRectangleBounds(ivFingerprintCardImage, rectangle, rtl,
		                                                     TITLED_PANE_TITLE_HEIGHT);
		
		ivFingerprintImageAfterCropping.setImage(image);
		return null;
	}
	
	private void makeNodeDraggable(ImageView ivCard, Group selectionLayer, Rectangle rectangle, boolean rtl)
	{
		DragContext dragDelta = new DragContext();
		
		// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
		rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
		
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem menuSkipFingerprint = new MenuItem(resources.getString("menu.skipFingerprint"));
		MenuItem menuEnableFingerprint = new MenuItem(resources.getString("menu.enableFingerprint"));
		
		menuEnableFingerprint.setVisible(false);
		
		menuSkipFingerprint.setOnAction(e ->
		{
			rectangle.getStyleClass().remove("fingerprint-rectangle");
			rectangle.getStyleClass().add("fingerprint-rectangle-disabled");
			rectangle.setUserData(Boolean.TRUE); // skipped
			
			menuSkipFingerprint.setVisible(false);
			menuEnableFingerprint.setVisible(true);
		});
		menuEnableFingerprint.setOnAction(e ->
		{
			rectangle.getStyleClass().remove("fingerprint-rectangle-disabled");
			rectangle.getStyleClass().add("fingerprint-rectangle");
			rectangle.setUserData(Boolean.FALSE); // not skipped
			
			menuEnableFingerprint.setVisible(false);
			menuSkipFingerprint.setVisible(true);
		});
		
		Glyph skipIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.TIMES_CIRCLE);
		menuSkipFingerprint.setGraphic(skipIcon);
		
		Glyph enableIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.CHECK_CIRCLE);
		menuEnableFingerprint.setGraphic(enableIcon);
		
		contextMenu.getItems().setAll(menuSkipFingerprint, menuEnableFingerprint);
		
		rectangle.setOnMousePressed(mouseEvent ->
		{
			if(contextMenu.isShowing()) contextMenu.hide();
			
			if(mouseEvent.isSecondaryButtonDown())
			{
				if(contextMenu.isShowing()) contextMenu.hide();
				contextMenu.show(rectangle, mouseEvent.getScreenX(), mouseEvent.getScreenY());
			}
			
			if(selectedRectangle != null)
			{
				selectedRectangle.getStyleClass().remove("fingerprint-rectangle-selected");
				selectedRectangle.getStyleClass().add("fingerprint-rectangle");
			}
			selectedRectangle = rectangle;
			selectedRectangle.getStyleClass().remove("fingerprint-rectangle");
			selectedRectangle.getStyleClass().add("fingerprint-rectangle-selected");
			
			rectangle.requestFocus();
			
			SelectionOverlay selectionOverlay = new SelectionOverlay(ivCard, rectangle, this::updatePreviewImage,
			                                                         rtl);
			
			// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
			selectionOverlay.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
			
			selectionLayer.getChildren().setAll(selectionOverlay);
			
			dragDelta.x = rectangle.getTranslateX() - 1 - mouseEvent.getSceneX();
			dragDelta.y = rectangle.getTranslateY() - 1 - mouseEvent.getSceneY();
			
			mouseEvent.consume();
		});
		
		rectangle.setOnMouseDragged(mouseEvent ->
		{
			if(contextMenu.isShowing()) contextMenu.hide();
			if(mouseEvent.isSecondaryButtonDown()) return;
			
			double newX = (mouseEvent.getSceneX() + dragDelta.x) * (rtl ? -1 : 1);
			double newY = mouseEvent.getSceneY() + dragDelta.y;
			
			boolean tooRight = ivCard.getLayoutX() + ivCard.getLayoutBounds().getWidth() <=
																newX + rectangle.getLayoutX() + rectangle.getWidth();
			boolean tooLeft = ivCard.getLayoutX() >= newX + rectangle.getLayoutX();
			boolean tooDown = ivCard.getLayoutY() + TITLED_PANE_TITLE_HEIGHT +
					ivCard.getLayoutBounds().getHeight() <= newY + rectangle.getLayoutY() + rectangle.getHeight();
			boolean tooUp = ivCard.getLayoutY() + TITLED_PANE_TITLE_HEIGHT >= newY + rectangle.getLayoutY();
			
			if(tooRight)
			{
				rectangle.setTranslateX(ivCard.getLayoutX() + ivCard.getLayoutBounds().getWidth()
						                                            - rectangle.getWidth() - rectangle.getLayoutX());
				if(!tooUp && !tooDown) rectangle.setTranslateY(newY);
				return;
			}
			
			if(tooLeft)
			{
				rectangle.setTranslateX(ivCard.getLayoutX() - rectangle.getLayoutX());
				if(!tooUp && !tooDown) rectangle.setTranslateY(newY);
				return;
			}
			
			if(tooDown)
			{
				rectangle.setTranslateX(newX);
				rectangle.setTranslateY(ivCard.getLayoutY() + TITLED_PANE_TITLE_HEIGHT +
	                            ivCard.getLayoutBounds().getHeight() - rectangle.getHeight() - rectangle.getLayoutY());
				return;
			}
			
			if(tooUp)
			{
				rectangle.setTranslateX(newX);
				rectangle.setTranslateY(ivCard.getLayoutY() + TITLED_PANE_TITLE_HEIGHT - rectangle.getLayoutY());
				return;
			}
			
			rectangle.setTranslateX(newX);
			rectangle.setTranslateY(newY);
		});
		
		rectangle.setOnMouseReleased(mouseEvent ->
		{
			double strokeWidth = rectangle.getStrokeWidth();
		    double x = rectangle.getTranslateX();
		    double y = rectangle.getTranslateY();
			
			rectangle.relocate(rectangle.getLayoutX() - strokeWidth / 2.0 + x,
			                   rectangle.getLayoutY() - strokeWidth / 2.0 + y);
			
			rectangle.setTranslateX(0);
			rectangle.setTranslateY(0);
			
			updatePreviewImage(rectangle, rtl);
		});
		
		rectangle.setOnKeyPressed(keyEvent ->
		{
			if(contextMenu.isShowing()) contextMenu.hide();
			
			double strokeWidth = rectangle.getStrokeWidth();
			KeyCode keyCode = keyEvent.getCode();
			
			switch(keyCode)
			{
				case UP:
				{
					if(rectangle.getLayoutY() - strokeWidth / 2.0 <= ivCard.getLayoutY() +
																						TITLED_PANE_TITLE_HEIGHT) break;
					rectangle.relocate(rectangle.getLayoutX() - strokeWidth / 2.0,
					                   rectangle.getLayoutY() - strokeWidth / 2.0 - 1);
					break;
				}
				case RIGHT:
				{
					if(rtl)
					{
						if(rectangle.getLayoutX() - strokeWidth / 2.0 <= ivCard.getLayoutX()) break;
					}
					else
					{
						if(rectangle.getLayoutX() + strokeWidth / 2.0 + rectangle.getWidth() >= ivCard.getLayoutX() +
																		ivCard.getBoundsInParent().getWidth()) break;
					}
					
					rectangle.relocate(rectangle.getLayoutX() - strokeWidth / 2.0 + (rtl ? -1 : 1),
					                   rectangle.getLayoutY() - strokeWidth / 2.0);
					break;
				}
				case DOWN:
				{
					if(rectangle.getLayoutY() + strokeWidth / 2.0 + rectangle.getHeight() >= ivCard.getLayoutY() +
											TITLED_PANE_TITLE_HEIGHT + ivCard.getBoundsInParent().getHeight()) break;
					rectangle.relocate(rectangle.getLayoutX() - strokeWidth / 2.0,
					                   rectangle.getLayoutY() - strokeWidth / 2.0 + 1);
					break;
				}
				case LEFT:
				{
					if(rtl)
					{
						if(rectangle.getLayoutX() + strokeWidth / 2.0 + rectangle.getWidth() >= ivCard.getLayoutX() +
																		ivCard.getBoundsInParent().getWidth()) break;
					}
					else
					{
						if(rectangle.getLayoutX() - strokeWidth / 2.0 <= ivCard.getLayoutX()) break;
					}
					
					rectangle.relocate(rectangle.getLayoutX() - strokeWidth / 2.0 + (rtl ? 1 : -1),
					                   rectangle.getLayoutY() - strokeWidth / 2.0);
					break;
				}
			}
			
			keyEvent.consume(); // prevent navigating to other GUI controls on the screen
			updatePreviewImage(rectangle, rtl);
		});
	}
}