package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.SelectionOverlay;

import java.net.URL;
import java.util.Map;

public class SpecifyFingerprintCoordinatesPaneFxController extends WizardStepFxControllerBase
{
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
		
		for(int i = 0; i < rectangles.length; i++)
		{
			rectangles[i] = new Rectangle(400, 500);
			rectangles[i].setFocusTraversable(true);
			rectangles[i].getStyleClass().add("fingerprint-rectangle");
			rectangles[i].relocate(100,100);
			objectLayer.getChildren().add(rectangles[i]);
			makeNodeDraggable(ivFingerprintCardImage, selectionLayer, rectangles[i], rtl);
			
			int finalI = i;
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
		
		}
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
		
		double strokeWidth = rectangle.getStrokeWidth();
		double imageScaledWidth = ivFingerprintCardImage.getBoundsInParent().getWidth();
		double rectWidth = rectangle.getWidth() - strokeWidth;
		double rectHeight = rectangle.getHeight() - strokeWidth;
		double imageOriginalWidth = ivFingerprintCardImage.getImage().getWidth();
		double rectX = rectangle.getLayoutX();
		double rectY = rectangle.getLayoutY();
		double imageX = ivFingerprintCardImage.getLayoutX();
		double imageY = TITLED_PANE_TITLE_HEIGHT + ivFingerprintCardImage.getLayoutY();
		
		double scale = imageOriginalWidth / imageScaledWidth;
		double scaledRectWidth = scale * rectWidth;
		double scaledRectHeight = scale * rectHeight;
		double scaledRectX = scale * (rectX - imageX);
		double scaledRectY = scale * (rectY - imageY);
		
		if(rtl) scaledRectX = imageOriginalWidth - scaledRectX - scaledRectWidth;
		
		WritableImage newImage = new WritableImage(ivFingerprintCardImage.getImage().getPixelReader(),
		                                           (int) scaledRectX, (int) scaledRectY,
		                                           (int) scaledRectWidth, (int) scaledRectHeight);
		
		ivFingerprintImageAfterCropping.setImage(newImage);
		return null;
	}
	
	private void makeNodeDraggable(ImageView ivCard, Group selectionLayer, Rectangle rectangle, boolean rtl)
	{
		DragContext dragDelta = new DragContext();
		
		// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
		rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
		
		rectangle.setOnMousePressed(mouseEvent ->
		{
			rectangle.requestFocus();
			
			SelectionOverlay selectionOverlay = new SelectionOverlay(ivCard, rectangle, this::updatePreviewImage, rtl);
			selectedRectangle = rectangle;
			
			// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
			selectionOverlay.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
			
			selectionLayer.getChildren().setAll(selectionOverlay);
		
		    dragDelta.x = rectangle.getTranslateX() - 1 - mouseEvent.getSceneX();
		    dragDelta.y = rectangle.getTranslateY() - 1 - mouseEvent.getSceneY();
			
			mouseEvent.consume();
		});
		
		rectangle.setOnMouseDragged(mouseEvent ->
		{
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
			double strokeWidth = rectangle.getStrokeWidth();
			KeyCode keyCode = keyEvent.getCode();
			
			switch(keyCode)
			{
				case UP:
				{
					if(rectangle.getLayoutY() - strokeWidth / 2.0 <= ivCard.getLayoutY() + TITLED_PANE_TITLE_HEIGHT) break;
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