package sa.gov.nic.bio.bw.client.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
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
import sa.gov.nic.bio.bw.client.core.interfaces.VisibilityControl;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class MenuPaneFxController implements VisibilityControl
{
	@FXML private ResourceBundle resources;
	@FXML private Pane rootPane;
	@FXML private Accordion accordion;
	@FXML private Pane overlayPane;
	
	private MenuItem selectedMenu;
	private ListView<MenuItem> selectedListView;
	
	@FXML
	private void initialize()
	{
		accordion.setFocusTraversable(false);
	}
	
	@Override
	public Pane getRootPane()
	{
		return rootPane;
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
		
		Font testFont = new Label().getFont();
		testFont = Font.font(testFont.getFamily(), FontWeight.BOLD, testFont.getSize());
		
		MenuItem menuItem = menus.get(0);
		String previousMenuId = menuItem.getMenuId();
		String previousParentMenuId = previousMenuId.substring(0, previousMenuId.lastIndexOf("."));
		String parentMenuLabel = topMenus.get(previousParentMenuId).getLabel();
		
		double maxWidth = AppUtils.computeTextWidth(menuItem.getLabel(), testFont);
		maxWidth = Math.max(maxWidth, AppUtils.computeTextWidth(parentMenuLabel, testFont));
		
		ListView<MenuItem> listView = newListView();
		listView.setFocusTraversable(false);
		listView.getItems().add(menus.get(0));
		TitledPane titledPane = new TitledPane();
		titledPane.setFocusTraversable(false);
		titledPane.setText(topMenus.get(previousParentMenuId).getLabel());
		titledPane.setContent(listView);
		
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
			
			maxWidth = Math.max(maxWidth, AppUtils.computeTextWidth(menuItem.getLabel(), testFont));
			
			if(parentMenuId.equals(previousParentMenuId)) // same group
			{
				listView.getItems().add(menuItem);
			}
			else
			{
				parentMenuLabel = topMenus.get(parentMenuId).getLabel();
				maxWidth = Math.max(maxWidth, AppUtils.computeTextWidth(parentMenuLabel, testFont));
				
				listView = newListView();
				listView.setFocusTraversable(false);
				listView.getItems().add(menuItem);
				titledPane = new TitledPane();
				titledPane.setFocusTraversable(false);
				titledPane.setText(parentMenuLabel);
				titledPane.setContent(listView);
				
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
		
		double cellHeight = 23.0; // default value, hardcoded :(
		
		// move the arrow position to the other side
		for(TitledPane pane : accordion.getPanes())
		{
			Pane arrow = (Pane) pane.lookup(".arrow");
			Text labeledText = (Text) pane.lookup(".title > .text");
			Glyph glyphFont = (Glyph) pane.lookup(".glyph-font");
			ListView<MenuItem> lv = (ListView<MenuItem>) pane.getContent();
			List<MenuItem> items = lv.getItems();
			
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
			
			lv.setPrefWidth(maxWidth * 1.3);
			lv.setPrefHeight(lv.getItems().size() * cellHeight + 30);
		}
	}
	
	private ListView<MenuItem> newListView()
	{
		ListView<MenuItem> listView = new ListView<>();
		ObservableList<MenuItem> items = FXCollections.observableArrayList(MenuItem.extractor());
		listView.setItems(items);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue == null) return; // un-select action
			
			if(selectedMenu != null) selectedMenu.setSelected(false);
			if(selectedListView != null && selectedListView != listView) selectedListView.getSelectionModel().clearSelection();
			
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

                if(empty || item == null)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    applyCss();
	                Text labeledText = (Text) lookup(".text");
                    
                    setText(item.getLabel());

                    Font font = labeledText.getFont();
                    labeledText.fontProperty().unbind();
                    labeledText.fontProperty().set(Font.font(font.getFamily(), item.isSelected() ? FontWeight.EXTRA_BOLD : FontWeight.NORMAL, font.getSize()));
                    
                    ListCell<MenuItem> listCell = this;
                    listCell.setCursor(item.isSelected() ? Cursor.DEFAULT : Cursor.HAND);

                    item.selectedProperty().addListener((observable, oldValue, newValue) ->
                    {
                        Font f = labeledText.getFont();
                        f = Font.font(f.getFamily(), newValue ? FontWeight.EXTRA_BOLD : FontWeight.NORMAL, f.getSize());
                        labeledText.fontProperty().set(f);
                        listCell.setCursor(newValue ? Cursor.DEFAULT : Cursor.HAND);
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
                }
            }
        });
		
		return listView;
	}
	
	private void onSelectMenu(MenuItem menuItem)
	{
		//coreFxController.goToMenu(menuItem.getMenuId());
	}
}