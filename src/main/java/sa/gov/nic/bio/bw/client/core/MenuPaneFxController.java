package sa.gov.nic.bio.bw.client.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.beans.MenuItem;
import sa.gov.nic.bio.bw.client.core.interfaces.AttachableController;
import sa.gov.nic.bio.bw.client.core.interfaces.VisibilityControl;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class MenuPaneFxController implements VisibilityControl, AttachableController
{
	@FXML private ResourceBundle resources;
	@FXML private Pane rootPane;
	@FXML private Accordion accordion;
	@FXML private Pane overlayPane;
	
	private CoreFxController coreFxController;
	private List<MenuItem> menus = new ArrayList<>();
	private MenuItem selectedMenu;
	private ListView<MenuItem> selectedListView;
	
	@Override
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
	
	@Override
	public void attachInitialResources(ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon)
	{
		// Not Used!
	}
	
	@FXML
	private void initialize() throws IOException
	{
	
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
	public void setMenus(List<String> menus, Map<String, Node> icons)
	{
		accordion.getPanes().clear();
		this.menus.clear();
		
		if(menus.isEmpty()) return;
		
		Font testFont = new Label().getFont();
		testFont = Font.font(testFont.getFamily(), FontWeight.BOLD, testFont.getSize());
		
		String previousMenuId = menus.get(0);
		String previousParentMenuId = previousMenuId.substring(0, previousMenuId.lastIndexOf("."));
		
		MenuItem menuItem = new MenuItem();
		this.menus.add(menuItem);
		menuItem.setLabel(resources.getString(previousMenuId));
		menuItem.setMenuId(previousMenuId);
		String parentMenuLabel = resources.getString(previousParentMenuId);
		
		double maxWidth = AppUtils.computeTextWidth(menuItem.getLabel(), testFont);
		maxWidth = Math.max(maxWidth, AppUtils.computeTextWidth(parentMenuLabel, testFont));
		
		ListView<MenuItem> listView = newListView();
		listView.getItems().add(menuItem);
		TitledPane titledPane = new TitledPane();
		titledPane.setText(parentMenuLabel);
		titledPane.setContent(listView);
		titledPane.setGraphic(icons.get(previousParentMenuId));
		accordion.getPanes().add(titledPane);
		
		for(int i = 1; i < menus.size(); i++)
		{
			String menuId = menus.get(i);
			String parentMenuId = menuId.substring(0, menuId.lastIndexOf("."));
			
			menuItem = new MenuItem();
			this.menus.add(menuItem);
			menuItem.setLabel(resources.getString(menuId));
			menuItem.setMenuId(menuId);
			
			maxWidth = Math.max(maxWidth, AppUtils.computeTextWidth(menuItem.getLabel(), testFont));
			
			if(parentMenuId.equals(previousParentMenuId)) // same group
			{
				listView.getItems().add(menuItem);
			}
			else
			{
				parentMenuLabel = resources.getString(parentMenuId);
				maxWidth = Math.max(maxWidth, AppUtils.computeTextWidth(parentMenuLabel, testFont));
				
				listView = newListView();
				listView.getItems().add(menuItem);
				titledPane = new TitledPane();
				titledPane.setText(parentMenuLabel);
				titledPane.setContent(listView);
				titledPane.setGraphic(icons.get(parentMenuId));
				accordion.getPanes().add(titledPane);
			}
			
			previousParentMenuId = parentMenuId;
		}
		
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
						FontWeight.BOLD : FontWeight.NORMAL, font.getSize());
				labeledText.fontProperty().set(font);
				
				item.selectedProperty().addListener((observable, oldValue, newValue) ->
                {
                    Font f = labeledText.getFont();
	                f = Font.font(f.getFamily(), newValue ?
                            FontWeight.BOLD : FontWeight.NORMAL, f.getSize());
                    labeledText.fontProperty().set(f);
                });
			}
			
			arrow.translateXProperty().bind(
					pane.widthProperty().subtract(arrow.widthProperty().multiply(3))
			);
			labeledText.translateXProperty().bind(arrow.widthProperty().multiply(2).negate());
			glyphFont.translateXProperty().bind(arrow.widthProperty().multiply(2).negate());
			
			lv.setPrefWidth(maxWidth * 1.5);
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
                    labeledText.fontProperty().set(Font.font(font.getFamily(), item.isSelected() ? FontWeight.BOLD : FontWeight.NORMAL, font.getSize()));
                    
                    ListCell<MenuItem> listCell = this;
                    listCell.setCursor(item.isSelected() ? Cursor.DEFAULT : Cursor.HAND);

                    item.selectedProperty().addListener((observable, oldValue, newValue) ->
                    {
                        Font f = labeledText.getFont();
                        f = Font.font(f.getFamily(), newValue ? FontWeight.BOLD : FontWeight.NORMAL, f.getSize());
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
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("menuId", menuItem.getMenuId());
		
		coreFxController.submitFormTask(uiDataMap);
	}
}