package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.control.ProgressIndicator;
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
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.gui.SelectionOverlay;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@FxmlFile("specifyFingerprintCoordinates.fxml")
public class SpecifyFingerprintCoordinatesPaneFxController extends WizardStepFxControllerBase
{
	private static final double[][] DEFAULT_BOUNDS = {{0.935, 0.350, 0.160, 0.117}, // x, y, width, height
													  {0.755, 0.350, 0.160, 0.117},
													  {0.570, 0.350, 0.160, 0.117},
													  {0.380, 0.350, 0.160, 0.117},
													  {0.200, 0.350, 0.160, 0.117},
													  {0.935, 0.500, 0.160, 0.117},
													  {0.755, 0.500, 0.160, 0.117},
													  {0.570, 0.500, 0.160, 0.117},
													  {0.380, 0.500, 0.160, 0.117},
													  {0.200, 0.500, 0.160, 0.117}};
	
	private static final double TITLED_PANE_TITLE_HEIGHT = 25.0;
	
	private static class Point
	{
		private double x;
		private double y;
		
		private Point(double x, double y)
		{
			this.x = x;
			this.y = y;
		}
	}
	private static class Dimension
	{
		private double width;
		private double height;
		
		private Dimension(double width, double height)
		{
			this.width = width;
			this.height = height;
		}
	}
	
	@Input(alwaysRequired = true) private Image cardImage;
	@Output private Map<Integer, String> fingerprintBase64Images;
	@Output private List<Integer> missingFingerprints;
	@Output private Map<Integer, Dimension> fingerprintsDimensions;
	@Output private Map<Integer, Point> fingerprintsCoordinates;
	@Output private Dimension imagesViewDimension;
	@Output private Point imagesViewCoordinates;
	
	@FXML private StackPane spFingerprintCardImage;
	@FXML private ImageView ivFingerprintCardImage;
	@FXML private ImageView ivFingerprintImageAfterCropping;
	@FXML private ImageView ivFingerprintImageAfterCroppingPlaceHolder;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private Rectangle[] rectangles = new Rectangle[10];
	private Label[] labels = new Label[10];
	private Rectangle selectedRectangle;
	
	@Override
	protected void onAttachedToScene()
	{
		if(fingerprintBase64Images == null) fingerprintBase64Images = new HashMap<>();
		if(missingFingerprints == null) missingFingerprints = new ArrayList<>();
		if(fingerprintsCoordinates == null) fingerprintsCoordinates = new HashMap<>();
		if(fingerprintsDimensions == null) fingerprintsDimensions = new HashMap<>();
		
		Double oldImageViewX = imagesViewCoordinates != null ? imagesViewCoordinates.x : null;
		Double oldImageViewY = imagesViewCoordinates != null ? imagesViewCoordinates.y : null;
		Double oldImageViewWidth = imagesViewDimension != null ? imagesViewDimension.width : null;
		
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Pane root = new Pane();
		spFingerprintCardImage.getChildren().add(root);
		
		Pane objectLayer = new Pane();
		root.getChildren().add(objectLayer);
		
		// selection layer on top of object layer
		Group selectionLayer = new Group();
		root.getChildren().add(selectionLayer);
		
		for(int i = 0; i < rectangles.length; i++)
		{
			final int index;
			
			if(rtl)
			{
				if(i < rectangles.length / 2) index = (rectangles.length / 2) - i - 1;
				else index = rectangles.length - i - 1 + rectangles.length / 2;
			}
			else index = i;
			
			labels[index] = new Label(AppUtils.localizeNumbers(String.valueOf(index + 1), Locale.getDefault(),
			                                                   false));
			labels[index].setMouseTransparent(true);
			labels[index].setTextAlignment(TextAlignment.CENTER);
			labels[index].setAlignment(Pos.CENTER);
			
			rectangles[index] = new Rectangle(25.0, 25.0);
			rectangles[index].setUserData(Boolean.FALSE); // not skipped
			rectangles[index].setFocusTraversable(true);
			rectangles[index].getStyleClass().add("fingerprint-rectangle");
			rectangles[index].setManaged(false);
			
			objectLayer.getChildren().add(rectangles[index]);
			objectLayer.getChildren().add(labels[index]);
			
			labels[index].layoutXProperty().bind(rectangles[index].layoutXProperty());
			labels[index].layoutYProperty().bind(rectangles[index].layoutYProperty());
			labels[index].translateXProperty().bind(rectangles[index].translateXProperty());
			labels[index].translateYProperty().bind(rectangles[index].translateYProperty());
			labels[index].prefWidthProperty().bind(rectangles[index].widthProperty());
			labels[index].prefHeightProperty().bind(rectangles[index].heightProperty());
			
			makeNodeDraggable(ivFingerprintCardImage, selectionLayer, rectangles[index], rtl);
			
			ivFingerprintCardImage.boundsInParentProperty().addListener((observable, oldValue, newValue) ->
			{
			    double strokeWidth = rectangles[index].getStrokeWidth();
			    double scale = newValue.getWidth() / oldValue.getWidth();
			    rectangles[index].setWidth(rectangles[index].getWidth() * scale);
			    rectangles[index].setHeight(rectangles[index].getHeight() * scale);
			    rectangles[index].relocate(newValue.getMinX() +
                                    scale * (rectangles[index].getLayoutX() - strokeWidth / 2.0 - oldValue.getMinX()),
			                                TITLED_PANE_TITLE_HEIGHT + newValue.getMinY() +
                                    scale * (rectangles[index].getLayoutY() - strokeWidth / 2.0 -
                                            (TITLED_PANE_TITLE_HEIGHT + oldValue.getMinY())));
			});
			
			Point point = fingerprintsCoordinates.get(index + 1);
			Dimension dimension = fingerprintsDimensions.get(index + 1);
			
			int finalI = i;
			ivFingerprintCardImage.imageProperty().addListener((observable, oldValue, newValue) ->
				                                                                                Platform.runLater(() ->
			{
				if(newValue != null)
				{
					double imageViewX = ivFingerprintCardImage.getBoundsInParent().getMinX();
					double imageViewY = ivFingerprintCardImage.getBoundsInParent().getMinY();
					double imageViewWidth = ivFingerprintCardImage.getBoundsInParent().getWidth();
					double imageViewHeight = ivFingerprintCardImage.getBoundsInParent().getHeight();
					
					if(oldImageViewX != null && oldImageViewWidth != null && point != null && dimension != null)
					{
						double strokeWidth = rectangles[index].getStrokeWidth();
						double scale = ivFingerprintCardImage.getBoundsInParent().getWidth() / oldImageViewWidth;
						
						rectangles[index].setWidth(scale * dimension.width);
						rectangles[index].setHeight(scale * dimension.height);
						
						// TODO: wrong relocation if the window is resized
						rectangles[index].relocate(scale * point.x - strokeWidth / 2.0 +
								                                                        (imageViewX - oldImageViewX),
						                           scale * point.y - strokeWidth / 2.0 +
						                                                                (imageViewY - oldImageViewY));
						
						String fingerprintBase64Image = fingerprintBase64Images.get(index + 1);
						boolean disabled = fingerprintBase64Image == null;
						if(disabled)
						{
							rectangles[index].getStyleClass().remove("fingerprint-rectangle");
							rectangles[index].getStyleClass().add("fingerprint-rectangle-disabled");
							rectangles[index].setUserData(Boolean.TRUE); // skipped
						}
					}
					else
					{
						double rectX = imageViewWidth * DEFAULT_BOUNDS[finalI][0];
						double rectY = imageViewHeight * DEFAULT_BOUNDS[finalI][1];
						double rectWidth = imageViewWidth * DEFAULT_BOUNDS[finalI][2];
						double rectHeight = imageViewHeight * DEFAULT_BOUNDS[finalI][3];
						
						rectangles[index].setWidth(rectWidth);
						rectangles[index].setHeight(rectHeight);
						rectangles[index].relocate(imageViewWidth - (rectX - imageViewX), rectY - imageViewY);
					}
				}
			}));
		}
		
		ivFingerprintCardImage.setImage(cardImage);
		
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
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		fingerprintBase64Images = null;
		missingFingerprints = null;
		fingerprintsDimensions = null;
		fingerprintsCoordinates = null;
		imagesViewDimension = null;
		imagesViewCoordinates = null;
	}
	
	@Override
	protected void onNextButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(btnPrevious, false);
		GuiUtils.showNode(btnNext, false);
		GuiUtils.showNode(piProgress, true);
		
		Task<Void> task = new Task<>()
		{
			@Override
			protected Void call() throws Exception
			{
				boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
				
				for(int i = 0; i < rectangles.length; i++)
				{
					if(Boolean.FALSE.equals(rectangles[i].getUserData())) // not skipped
					{
						Image image = GuiUtils.extractImageByRectangleBounds(ivFingerprintCardImage, rectangles[i], rtl,
						                                                     TITLED_PANE_TITLE_HEIGHT);
						fingerprintBase64Images.put(i + 1, AppUtils.imageToBase64(image));
					}
					else missingFingerprints.add(i + 1);
					
					fingerprintsDimensions.put(i + 1, new Dimension(rectangles[i].getBoundsInParent().getWidth(),
					                                                rectangles[i].getBoundsInParent().getHeight()));
					fingerprintsCoordinates.put((i + 1), new Point(rectangles[i].getBoundsInParent().getMinX(),
					                                               rectangles[i].getBoundsInParent().getMinY()));
				}
				
				imagesViewDimension = new Dimension(ivFingerprintCardImage.getBoundsInParent().getWidth(),
				                                    ivFingerprintCardImage.getBoundsInParent().getHeight());
				imagesViewCoordinates = new Point(ivFingerprintCardImage.getBoundsInParent().getMinX(),
				                                  ivFingerprintCardImage.getBoundsInParent().getMinY());
				
				return null;
			}
		};
		task.setOnSucceeded(event -> goNext());
		task.setOnFailed(event ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnPrevious, true);
			GuiUtils.showNode(btnNext, true);
			
		    Throwable exception = task.getException();
		    if(exception instanceof ExecutionException) exception = exception.getCause();
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00007.getCode();
		    String[] errorDetails = {"failed to convert the image to base64 encoding!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(task);
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
		Point dragDelta = new Point(0.0, 0.0);
		
		// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
		rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
		
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem menuSkipFingerprint = new MenuItem(resources.getString("menu.skipFingerprint"));
		MenuItem menuEnableFingerprint = new MenuItem(resources.getString("menu.enableFingerprint"));
		
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
		
		contextMenu.setOnShowing(event ->
		{
			menuEnableFingerprint.setVisible(Boolean.TRUE.equals(rectangle.getUserData()));
			menuSkipFingerprint.setVisible(!menuEnableFingerprint.isVisible());
		});
		
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