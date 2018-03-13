package sa.gov.nic.bio.bw.client.core;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.beans.MenuItem;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class MenuPaneFxController extends RegionFxControllerBase
{
	private static final double MAX_MENU_WIDTH = 166.0;
	private static final double MENU_SINGLE_LINE_CELL_HEIGHT = 24.0;
	private static final double MENU_MULTI_LINES_CELL_HEIGHT = 18.0;
	
	@FXML private Accordion accordion;
	@FXML private Pane overlayPane;

	private MenuItem selectedMenu;
	private ListView<MenuItem> selectedListView;
	
	// Integer -> index of listView
	// Pair<Integer, Double> -> pair of menu index with its current height
	private Map<Integer, List<Pair<Integer, Double>>> cellsHeightMap = new HashMap<>();
	
	@Override
	protected void initialize()
	{
		accordion.setFocusTraversable(false);
		accordion.setMaxWidth(MAX_MENU_WIDTH);
	}
	
	public void showOverlayPane(boolean bShow)
	{
		overlayPane.setVisible(bShow);
	}
	
	@SuppressWarnings("unchecked")
	public void setMenus(List<MenuItem> menus, Map<String, MenuItem> topMenus)
	{
		accordion.getPanes().clear();
		
		if(menus.isEmpty()) return;
		
		List<Pair<TitledPane, Integer>> titledPanes = new ArrayList<>();
		
		MenuItem menuItem = menus.get(0);
		String previousMenuId = menuItem.getMenuId();
		String previousParentMenuId = previousMenuId.substring(0, previousMenuId.lastIndexOf("."));
		
		int listViewIndex = 0;
		int itemIndex = 0;
		
		cellsHeightMap.put(listViewIndex, new ArrayList<>());
		menuItem.setIndex(itemIndex++);
		
		ListView<MenuItem> listView = newListView(listViewIndex++);
		listView.setFocusTraversable(false);
		listView.getItems().add(menuItem);
		TitledPane titledPane = new TitledPane();
		titledPane.setFocusTraversable(false);
		titledPane.setText(topMenus.get(previousParentMenuId).getLabel());
		titledPane.setWrapText(true);
		titledPane.setContent(listView);
		
		listView.setMaxWidth(MAX_MENU_WIDTH);
		
		String iconId = topMenus.get(previousParentMenuId).getIconId();
		FontAwesome.Glyph glyph = FontAwesome.Glyph.valueOf(iconId.toUpperCase());
		Glyph icon = AppUtils.createFontAwesomeIcon(glyph);
		
		titledPane.setGraphic(icon);
		titledPanes.add(new Pair<>(titledPane, topMenus.get(previousParentMenuId).getOrder()));
		
		for(int i = 1; i < menus.size(); i++)
		{
			menuItem = menus.get(i);
			String menuId = menuItem.getMenuId();
			String parentMenuId = menuId.substring(0, menuId.lastIndexOf("."));
			
			if(parentMenuId.equals(previousParentMenuId)) // same group
			{
				menuItem.setIndex(itemIndex++);
				listView.getItems().add(menuItem);
			}
			else
			{
				String parentMenuLabel = topMenus.get(parentMenuId).getLabel();
				
				cellsHeightMap.put(listViewIndex, new ArrayList<>());
				itemIndex = 0;
				menuItem.setIndex(itemIndex++);
				
				listView = newListView(listViewIndex++);
				listView.setFocusTraversable(false);
				listView.getItems().add(menuItem);
				titledPane = new TitledPane();
				titledPane.setFocusTraversable(false);
				titledPane.setText(parentMenuLabel);
				titledPane.setWrapText(true);
				titledPane.setContent(listView);
				
				listView.setMaxWidth(MAX_MENU_WIDTH);
				
				iconId = topMenus.get(parentMenuId).getIconId();
				glyph = FontAwesome.Glyph.valueOf(iconId.toUpperCase());
				icon = AppUtils.createFontAwesomeIcon(glyph);
				
				titledPane.setGraphic(icon);
				titledPanes.add(new Pair<>(titledPane, topMenus.get(parentMenuId).getOrder()));
			}
			
			previousParentMenuId = parentMenuId;
		}
		
		titledPanes.sort(Comparator.comparingInt(Pair::getValue));
		titledPanes.forEach(titledPaneIntegerPair -> accordion.getPanes().add(titledPaneIntegerPair.getKey()));
		
		// lookups do not work until CSS is applied
		rootPane.applyCss();
		
		double cellHeight = 24.0; // default value, hardcoded :(
		
		// move the arrow position to the other side
		for(TitledPane pane : accordion.getPanes())
		{
			Pane arrow = (Pane) pane.lookup(".arrow");
			Text labeledText = (Text) pane.lookup(".title > .text");
			Glyph glyphFont = (Glyph) pane.lookup(".glyph-font");
			ListView<MenuItem> lv = (ListView<MenuItem>) pane.getContent();
			List<MenuItem> items = lv.getItems();
			items.sort(Comparator.comparingInt(MenuItem::getOrder));
			
			for(MenuItem item : items)
			{
				labeledText.fontProperty().unbind();
				
				// initial value
				Font font = labeledText.getFont();
				font = Font.font(font.getFamily(), item.isSelected() ?
						FontWeight.EXTRA_BOLD : FontWeight.NORMAL, font.getSize());
				labeledText.fontProperty().set(font);
				
				item.selectedProperty().addListener((observable, oldValue, newValue) ->
                {
                    Font f = labeledText.getFont();
	                f = Font.font(f.getFamily(), newValue ? FontWeight.EXTRA_BOLD : FontWeight.NORMAL, f.getSize());
                    labeledText.fontProperty().set(f);
                });
			}
			
			arrow.translateXProperty().bind(pane.widthProperty().subtract(arrow.widthProperty().multiply(3)));
			labeledText.translateXProperty().bind(arrow.widthProperty().multiply(2).negate());
			glyphFont.translateXProperty().bind(arrow.widthProperty().multiply(2).negate());
			
			lv.setPrefHeight(lv.getItems().size() * cellHeight + 30);
		}
	}
	
	private ListView<MenuItem> newListView(int index)
	{
		ListView<MenuItem> listView = new ListView<>();
		ObservableList<MenuItem> items = FXCollections.observableArrayList(MenuItem.extractor());
		listView.setItems(items);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue == null) return; // un-select action
			
			if(selectedMenu != null) selectedMenu.setSelected(false);
			if(selectedListView != null && selectedListView != listView)
															selectedListView.getSelectionModel().clearSelection();
			
			selectedMenu = newValue;
			selectedListView = listView;
			selectedMenu.setSelected(true);
			
			onSelectMenu(selectedMenu);
		});
		
		listView.setCellFactory(param -> new ListCell<MenuItem>()
        {
        	@Override
            protected void updateItem(MenuItem item, boolean empty)
            {
                super.updateItem(item, empty);
	            setWrapText(true);
	            
	            if(empty || item == null)
                {
	                setText(null);
	                setGraphic(null);
                }
                else
                {
	                applyCss();
	                LabeledText labeledText = (LabeledText) lookup(".text");
	                
                    setText(item.getLabel());

                    Font font = labeledText.getFont();
                    labeledText.fontProperty().unbind();
                    labeledText.fontProperty().set(Font.font(font.getFamily(), item.isSelected() ?
	                                                        FontWeight.EXTRA_BOLD : FontWeight.NORMAL, font.getSize()));
                    
                    ListCell<MenuItem> listCell = this;
                    listCell.setCursor(item.isSelected() ? Cursor.DEFAULT : Cursor.HAND);
	                listCell.prefWidthProperty().bind(listView.widthProperty().subtract(5.0));
	
	                item.selectedProperty().addListener((observable, oldValue, newValue) ->
                    {
                        Font f = labeledText.getFont();
                        f = Font.font(f.getFamily(), newValue ? FontWeight.EXTRA_BOLD : FontWeight.NORMAL, f.getSize());
                        labeledText.fontProperty().set(f);
                        listCell.setCursor(newValue ? Cursor.DEFAULT : Cursor.HAND);
	                    updateListViewHeight(listView, index, item);
                    });
                    
                    if(item.isSelected())
                    {
                        AnchorPane arrowPane = new AnchorPane();
                        Polygon polygon = new Polygon();
                        polygon.getPoints().addAll(0.0, 5.0, 5.0, 0.0, 5.0, 10.0);
                        polygon.setFill(Color.rgb(0x39, 0x87,0x55));
                        arrowPane.getChildren().add(polygon);
                        setGraphic(arrowPane);

                        arrowPane.translateXProperty().bind(widthProperty().subtract(
		                    arrowPane.widthProperty().add(paddingProperty().getValue().getRight()))
                        );
                        labeledText.translateXProperty().bind(arrowPane.widthProperty().add(4).negate());
                    }
                    else if(getGraphic() != null) getGraphic().setVisible(false); // hide the arrow due un-select
	
	                updateListViewHeight(listView, index, item);
                }
            }
        });
		
		return listView;
	}
	
	private void updateListViewHeight(ListView<?> listView, int listViewIndex, MenuItem menuItem)
	{
		List<Pair<Integer, Double>> listViewHeights = cellsHeightMap.get(listViewIndex);
		int count = listViewHeights.size();
		double height = MENU_SINGLE_LINE_CELL_HEIGHT + MENU_MULTI_LINES_CELL_HEIGHT * (menuItem.getLines() - 1);
		
		boolean newItemToIncludeIntoMap = menuItem.getIndex() >= count;
		if(newItemToIncludeIntoMap) listViewHeights.add(new Pair<>(menuItem.getIndex(), height));
		else listViewHeights.set(menuItem.getIndex(), new Pair<>(menuItem.getIndex(),height));
		
		double[] totalHeights = {0.0};
		listViewHeights.forEach(h -> totalHeights[0] += h.getValue());
		listView.setPrefHeight(totalHeights[0] + MENU_SINGLE_LINE_CELL_HEIGHT); // extra empty cell
	}
	
	private void onSelectMenu(MenuItem menuItem)
	{
		Context.getCoreFxController().goToMenu(menuItem.getWorkflowClass());
	}
}