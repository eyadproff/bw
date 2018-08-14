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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sa.gov.nic.bio.bw.client.core.Context;
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
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private Rectangle rect;
	
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
		
		rect = new Rectangle(400, 500);
		rect.setFocusTraversable(true);
		rect.setFill(Color.RED.deriveColor(1, 1, 1, 0.2));
		rect.relocate(100,100);
		
		objectLayer.getChildren().addAll(rect);
		spFingerprintCardImage.setOnMousePressed(mouseEvent -> selectionLayer.getChildren().clear());
		
		ivFingerprintCardImage.boundsInParentProperty().addListener((observable, oldValue, newValue) ->
		{
			
			
			double scale = newValue.getWidth() / oldValue.getWidth();
			rect.setWidth(rect.getWidth() * scale);
			rect.setHeight(rect.getHeight() * scale);
			
			rect.relocate(newValue.getMinX() + scale * (rect.getLayoutX() - oldValue.getMinX()),
			              TITLED_PANE_TITLE_HEIGHT + newValue.getMinY() + scale * (rect.getLayoutY() - (TITLED_PANE_TITLE_HEIGHT + oldValue.getMinY())));
		});
		
		makeNodeDraggable(ivFingerprintCardImage, selectionLayer, rect, rtl);
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
		double imageScaledWidth = ivFingerprintCardImage.getBoundsInParent().getWidth();
		double rectWidth = rectangle.getWidth();
		double rectHeight = rectangle.getHeight();
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
			//rectangle.boundsInParentProperty().addListener(changeListener);
			
			SelectionOverlay selectionOverlay = new SelectionOverlay(ivCard, rectangle, this::updatePreviewImage, rtl);
			
			// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
			selectionOverlay.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
			
			selectionLayer.getChildren().setAll(selectionOverlay);
		
		    dragDelta.x = rectangle.getTranslateX() - mouseEvent.getSceneX();
		    dragDelta.y = rectangle.getTranslateY() - mouseEvent.getSceneY();
			
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
		    double x = rectangle.getTranslateX();
		    double y = rectangle.getTranslateY();
			
			rectangle.relocate(rectangle.getLayoutX() + x, rectangle.getLayoutY() + y);
			
			rectangle.setTranslateX(0);
			rectangle.setTranslateY(0);
			
			updatePreviewImage(rectangle, rtl);
		});
		
		rectangle.setOnKeyPressed(keyEvent ->
		{
			KeyCode keyCode = keyEvent.getCode();
			
			switch(keyCode)
			{
				case UP:
				{
					if(rectangle.getLayoutY() <= ivCard.getLayoutY() + TITLED_PANE_TITLE_HEIGHT) break;
					rectangle.relocate(rectangle.getLayoutX(), rectangle.getLayoutY() - 1);
					break;
				}
				case RIGHT:
				{
					if(rtl)
					{
						if(rectangle.getLayoutX() <= ivCard.getLayoutX()) break;
					}
					else
					{
						if(rectangle.getLayoutX() + rectangle.getWidth() >= ivCard.getLayoutX() +
																		ivCard.getBoundsInParent().getWidth()) break;
					}
					
					rectangle.relocate(rectangle.getLayoutX() + (rtl ? -1 : 1), rectangle.getLayoutY());
					break;
				}
				case DOWN:
				{
					if(rectangle.getLayoutY() + rectangle.getHeight() >= ivCard.getLayoutY() +
											TITLED_PANE_TITLE_HEIGHT + ivCard.getBoundsInParent().getHeight()) break;
					rectangle.relocate(rectangle.getLayoutX(), rectangle.getLayoutY() + 1);
					break;
				}
				case LEFT:
				{
					if(rtl)
					{
						if(rectangle.getLayoutX() + rectangle.getWidth() >= ivCard.getLayoutX() +
																		ivCard.getBoundsInParent().getWidth()) break;
					}
					else
					{
						if(rectangle.getLayoutX() <= ivCard.getLayoutX()) break;
					}
					
					rectangle.relocate(rectangle.getLayoutX() + (rtl ? 1 : -1), rectangle.getLayoutY());
					break;
				}
			}
			
			keyEvent.consume(); // prevent navigating to other GUI controls on the screen
			updatePreviewImage(rectangle, rtl);
		});
	}
}