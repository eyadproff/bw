package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class SelectionOverlay extends Region
{
	private static final double DRAG_HANDLE_DIAMETER = 5.0;
	private static final double DRAG_HANDLE_RADIUS = DRAG_HANDLE_DIAMETER / 2.0;
	private static final double MIN_WIDTH = 20.0;
	private static final double MIN_HEIGHT = 20.0;
	
	private class Delta
	{
		private double x;
		private double y;
		private double minX;
		private double maxX;
		private double minY;
		private double maxY;
	}
	
	private class DragHandle extends Rectangle
	{
		private DragHandle(double size, Cursor dragCursor)
		{
			init(size, dragCursor);
		}
		
		private void init(double size, Cursor dragCursor)
		{
			setWidth(size);
			setHeight(size);
			getStyleClass().add("selection-drag-handle");
			
			Delta dragDelta = new Delta();
			
			setOnMousePressed(mouseEvent ->
			{
			    // record a delta distance for the drag and drop operation.
			    dragDelta.x = getX() - mouseEvent.getX();
			    dragDelta.y = getY() - mouseEvent.getY();
			
			    dragDelta.minX = rectangle.getBoundsInParent().getMinX();
			    dragDelta.maxX = rectangle.getBoundsInParent().getMaxX();
			    dragDelta.minY = rectangle.getBoundsInParent().getMinY();
			    dragDelta.maxY = rectangle.getBoundsInParent().getMaxY();
			
			    getScene().setCursor(dragCursor);
			
			    mouseEvent.consume();
			});
			
			setOnMouseReleased(mouseEvent ->
			{
			    getScene().setCursor(Cursor.DEFAULT);
			    mouseEvent.consume();
			});
			
			setOnMouseDragged(mouseEvent ->
			{
			    double newX = mouseEvent.getX() + dragDelta.x;
			    double newY = mouseEvent.getY() + dragDelta.y;
			
			    boolean exceedMinDimension = false;
			
			    if(this == dragHandleN)
			    {
			        double newHeight = dragDelta.maxY - newY - DRAG_HANDLE_RADIUS;
			
			        if(newHeight < MIN_HEIGHT) exceedMinDimension = true;
			
			        if(!exceedMinDimension)
			        {
			            setY(newY);
			            rectangle.setHeight(newHeight);
			            rectangle.relocate(dragDelta.minX, newY + DRAG_HANDLE_RADIUS);
			        }
			    }
			    else if(this == dragHandleNE)
			    {
			        double newWidth = newX - dragDelta.minX + DRAG_HANDLE_RADIUS;
			        double newHeight = dragDelta.maxY - newY - DRAG_HANDLE_RADIUS;
			        double heightDelta = 0.0;
			
			        if(newWidth < MIN_WIDTH)
			        {
			            exceedMinDimension = true;
			            newWidth = MIN_WIDTH;
			        }
			        if(newHeight < MIN_HEIGHT)
			        {
			            exceedMinDimension = true;
			            heightDelta = newHeight - MIN_HEIGHT;
			            newHeight = MIN_HEIGHT;
			        }
			
			        rectangle.setWidth(newWidth);
			        rectangle.setHeight(newHeight);
			        rectangle.relocate(dragDelta.minX, newY + DRAG_HANDLE_RADIUS + heightDelta);
			
			        if(!exceedMinDimension)
			        {
			            setX(newX);
			            setY(newY);
			        }
			    }
			    else if(this == dragHandleE)
			    {
			        double newWidth = newX - dragDelta.minX + DRAG_HANDLE_RADIUS;
			        if(newWidth < MIN_WIDTH) exceedMinDimension = true;
			
			        if(!exceedMinDimension)
			        {
			            setX(newX);
			            rectangle.setWidth(newWidth);
			        }
			    }
			    else if(this == dragHandleSE)
			    {
			        double newWidth = newX - dragDelta.minX + DRAG_HANDLE_RADIUS;
			        double newHeight = newY - dragDelta.minY + DRAG_HANDLE_RADIUS;
			
			        if(newWidth < MIN_WIDTH)
			        {
			            exceedMinDimension = true;
			            newWidth = MIN_WIDTH;
			        }
			        if(newHeight < MIN_HEIGHT)
			        {
			            exceedMinDimension = true;
			            newHeight = MIN_HEIGHT;
			        }
			
			        rectangle.setWidth(newWidth);
			        rectangle.setHeight(newHeight);
			
			        if(!exceedMinDimension)
			        {
			            setX(newX);
			            setY(newY);
			        }
			    }
			    else if(this == dragHandleS)
			    {
			        double newHeight = newY - dragDelta.minY + DRAG_HANDLE_RADIUS;
			        if(newHeight < MIN_HEIGHT) exceedMinDimension = true;
			
			        if(!exceedMinDimension)
			        {
			            setY(newY);
			            rectangle.setHeight(newHeight);
			        }
			    }
			    else if(this == dragHandleSW)
			    {
			        double newWidth = dragDelta.maxX - newX - DRAG_HANDLE_RADIUS;
			        double newHeight = newY - dragDelta.minY + DRAG_HANDLE_RADIUS;
			        double widthDelta = 0.0;
			
			        if(newWidth < MIN_WIDTH)
			        {
			            exceedMinDimension = true;
			            widthDelta = newWidth - MIN_WIDTH;
			            newWidth = MIN_WIDTH;
			        }
			        if(newHeight < MIN_HEIGHT)
			        {
			            exceedMinDimension = true;
			            newHeight = MIN_HEIGHT;
			        }
			
			        rectangle.setWidth(newWidth);
			        rectangle.setHeight(newHeight);
			        rectangle.relocate(newX + DRAG_HANDLE_RADIUS + widthDelta, dragDelta.minY);
			
			        if(!exceedMinDimension)
			        {
			            setX(newX);
			            setY(newY);
			        }
			    }
			    else if(this == dragHandleW)
			    {
			        double newWidth = dragDelta.maxX - newX - DRAG_HANDLE_RADIUS;
			        if(newWidth < MIN_WIDTH) exceedMinDimension = true;
			
			        if(!exceedMinDimension)
			        {
			            setX(newX);
			            rectangle.setWidth(newWidth);
			            rectangle.relocate(newX + DRAG_HANDLE_RADIUS, dragDelta.minY);
			        }
			    }
			    else if(this == dragHandleNW)
			    {
			        double newWidth = dragDelta.maxX - newX - DRAG_HANDLE_RADIUS;
			        double newHeight = dragDelta.maxY - newY - DRAG_HANDLE_RADIUS;
			        double widthDelta = 0.0;
			        double heightDelta = 0.0;
			
			        if(newWidth < MIN_WIDTH)
			        {
			            exceedMinDimension = true;
			            widthDelta = newWidth - MIN_WIDTH;
			            newWidth = MIN_WIDTH;
			        }
			        if(newHeight < MIN_HEIGHT)
			        {
			            exceedMinDimension = true;
			            heightDelta = newHeight - MIN_HEIGHT;
			            newHeight = MIN_HEIGHT;
			        }
			
			        rectangle.setWidth(newWidth);
			        rectangle.setHeight(newHeight);
			        rectangle.relocate(newX + DRAG_HANDLE_RADIUS + widthDelta,
			                           newY + DRAG_HANDLE_RADIUS + heightDelta);
			
			        if(!exceedMinDimension)
			        {
			            setX(newX);
			            setY(newY);
			        }
			    }
			
			    mouseEvent.consume();
			});
			
			setOnMouseEntered(mouseEvent -> getScene().setCursor(dragCursor));
			setOnMouseExited(mouseEvent -> getScene().setCursor(Cursor.DEFAULT));
		}
	}
	
	private Rectangle rectangle;
	private Rectangle selectionRectangle = new Rectangle();
	
	// drag handles
	private DragHandle dragHandleNW;
	private DragHandle dragHandleNE;
	private DragHandle dragHandleSE;
	private DragHandle dragHandleSW;
	private DragHandle dragHandleN;
	private DragHandle dragHandleS;
	private DragHandle dragHandleE;
	private DragHandle dragHandleW;
	
	public SelectionOverlay(Rectangle rectangle, boolean rtl)
	{
		this.rectangle = rectangle;
		
		// mouse events only on our drag objects, but not on this node itself
		// note that the selection rectangle is only for visuals and is set to being mouse transparent
		setPickOnBounds(false);
		
		// the rectangle is only for visuals, we don't want any mouse events on it
		selectionRectangle.setMouseTransparent(true);
		
		// set style
		selectionRectangle.getStyleClass().add("selection-rectangle");
		getChildren().add(selectionRectangle);
		
		dragHandleNW = new DragHandle(DRAG_HANDLE_DIAMETER, rtl ? Cursor.NE_RESIZE : Cursor.NW_RESIZE);
		dragHandleNE = new DragHandle(DRAG_HANDLE_DIAMETER, rtl ? Cursor.NW_RESIZE : Cursor.NE_RESIZE);
		dragHandleSE = new DragHandle(DRAG_HANDLE_DIAMETER, rtl ? Cursor.SW_RESIZE : Cursor.SE_RESIZE);
		dragHandleSW = new DragHandle(DRAG_HANDLE_DIAMETER, rtl ? Cursor.SE_RESIZE : Cursor.SW_RESIZE);
		
		dragHandleN = new DragHandle(DRAG_HANDLE_DIAMETER, Cursor.N_RESIZE);
		dragHandleS = new DragHandle(DRAG_HANDLE_DIAMETER, Cursor.S_RESIZE);
		dragHandleE = new DragHandle(DRAG_HANDLE_DIAMETER, rtl ? Cursor.W_RESIZE : Cursor.E_RESIZE);
		dragHandleW = new DragHandle(DRAG_HANDLE_DIAMETER, rtl ? Cursor.E_RESIZE : Cursor.W_RESIZE);
		
		getChildren().addAll(dragHandleNW, dragHandleNE, dragHandleSE, dragHandleSW, dragHandleN, dragHandleS,
		                     dragHandleE, dragHandleW);
		
		rectangle.boundsInParentProperty().addListener((observableValue, oldBounds, newBounds) ->
			                                                                    updateSelectionBounds(newBounds));
		updateSelectionBounds(rectangle.boundsInParentProperty().get());
	}
	
	private void updateSelectionBounds(Bounds newBounds)
	{
		selectionRectangle.setX(newBounds.getMinX());
		selectionRectangle.setY(newBounds.getMinY());
		selectionRectangle.setWidth(newBounds.getWidth());
		selectionRectangle.setHeight(newBounds.getHeight());
		
		dragHandleNW.setX(newBounds.getMinX() - DRAG_HANDLE_RADIUS);
		dragHandleNW.setY(newBounds.getMinY() - DRAG_HANDLE_RADIUS);
		
		dragHandleNE.setX(newBounds.getMaxX() - DRAG_HANDLE_RADIUS);
		dragHandleNE.setY(newBounds.getMinY() - DRAG_HANDLE_RADIUS);
		
		dragHandleSE.setX(newBounds.getMaxX() - DRAG_HANDLE_RADIUS);
		dragHandleSE.setY(newBounds.getMaxY() - DRAG_HANDLE_RADIUS);
		
		dragHandleSW.setX(newBounds.getMinX() - DRAG_HANDLE_RADIUS);
		dragHandleSW.setY(newBounds.getMaxY() - DRAG_HANDLE_RADIUS);
		
		dragHandleN.setX(newBounds.getMinX() + newBounds.getWidth() / 2.0 - DRAG_HANDLE_RADIUS);
		dragHandleN.setY(newBounds.getMinY() - DRAG_HANDLE_RADIUS);
		
		dragHandleS.setX(newBounds.getMinX() + newBounds.getWidth() / 2.0 - DRAG_HANDLE_RADIUS);
		dragHandleS.setY(newBounds.getMaxY() - DRAG_HANDLE_RADIUS);
		
		dragHandleE.setX(newBounds.getMaxX() - DRAG_HANDLE_RADIUS);
		dragHandleE.setY(newBounds.getMinY() + newBounds.getHeight() / 2.0 - DRAG_HANDLE_RADIUS);
		
		dragHandleW.setX(newBounds.getMinX() - DRAG_HANDLE_RADIUS);
		dragHandleW.setY(newBounds.getMinY() + newBounds.getHeight() / 2.0 - DRAG_HANDLE_RADIUS);
	}
}
