package sa.gov.nic.bio.bw.client.features.commons.ui;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ImageViewPaneWithAlignment extends VBox
{
	private ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<>();
	
	public ImageViewPaneWithAlignment(@NamedArg("imageView") ImageView imageView)
	{
		imageViewProperty.addListener((arg0, oldIV, newIV) ->
		{
		    if(oldIV != null) getChildren().remove(oldIV);
		    if(newIV != null) getChildren().add(newIV);
		});
		
		this.imageViewProperty.set(imageView);
	}
	
	public ObjectProperty<ImageView> imageViewProperty()
	{
		return imageViewProperty;
	}
	
	public ImageView getImageView()
	{
		return imageViewProperty.get();
	}
	
	public void setImageView(ImageView imageView)
	{
		this.imageViewProperty.set(imageView);
	}
	
	public ImageViewPaneWithAlignment()
	{
		this(new ImageView());
	}
	
	@Override
	protected void layoutChildren()
	{
		ImageView imageView = imageViewProperty.get();
		
		if(imageView != null)
		{
			imageView.setFitWidth(getWidth());
			imageView.setFitHeight(getHeight());
			layoutInArea(imageView, 0, 0, getWidth(), getHeight(),
			             0, HPos.CENTER, VPos.CENTER);
		}
		
		super.layoutChildren();
	}
}