package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
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
		
		Rectangle rect1 = new Rectangle(200, 100);
		rect1.setFill(Color.RED.deriveColor(1, 1, 1, 0.2));
		rect1.relocate(100,100);
		makeNodeDraggable(selectionLayer, rect1, rtl);
		
		Rectangle rect2 = new Rectangle(200,100);
		rect2.setFill(Color.AQUA.deriveColor(1, 1, 1, 0.2));
		rect2.relocate(300,300);
		makeNodeDraggable(selectionLayer, rect2, rtl);
		
		objectLayer.getChildren().addAll(rect1, rect2);
		spFingerprintCardImage.setOnMouseClicked(mouseEvent -> selectionLayer.getChildren().clear());
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
	
	private static void makeNodeDraggable(Group selectionLayer, Rectangle rectangle, boolean rtl)
	{
		DragContext dragDelta = new DragContext();
		
		// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
		rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
		
		rectangle.setOnMousePressed(mouseEvent ->
		{
			SelectionOverlay selectionOverlay = new SelectionOverlay(rectangle, rtl);
			
			// prevent bubbling up the mouse-click event to the parent (spFingerprintCardImage)
			selectionOverlay.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
			
			selectionLayer.getChildren().setAll(selectionOverlay);
		
		    dragDelta.x = rectangle.getTranslateX() - mouseEvent.getSceneX();
		    dragDelta.y = rectangle.getTranslateY() - mouseEvent.getSceneY();
		});
		
		rectangle.setOnMouseDragged(mouseEvent ->
		{
			rectangle.setTranslateX((mouseEvent.getSceneX() + dragDelta.x) * (rtl ? -1 : 1));
			rectangle.setTranslateY(mouseEvent.getSceneY() + dragDelta.y);
		});
		
		rectangle.setOnMouseReleased(mouseEvent ->
		{
		    double x = rectangle.getTranslateX();
		    double y = rectangle.getTranslateY();
			
			rectangle.relocate(rectangle.getLayoutX() + x, rectangle.getLayoutY() + y);
			
			rectangle.setTranslateX(0);
			rectangle.setTranslateY(0);
		});
	}
}