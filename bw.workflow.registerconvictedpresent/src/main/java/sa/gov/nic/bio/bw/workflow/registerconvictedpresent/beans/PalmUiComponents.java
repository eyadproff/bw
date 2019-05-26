package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;

public class PalmUiComponents extends JavaBean
{
	private FingerPosition palmPosition;
	private ImageView imageView;
	private SVGPath svgPath;
	private FourStateTitledPane titledPane;
	private CheckBox checkBox;
	private String fingerLabel;
	private String handLabel;
	
	public PalmUiComponents(FingerPosition palmPosition, ImageView imageView,
	                        SVGPath svgPath, FourStateTitledPane titledPane, CheckBox checkBox,
	                        String fingerLabel, String handLabel)
	{
		this.palmPosition = palmPosition;
		this.imageView = imageView;
		this.svgPath = svgPath;
		this.titledPane = titledPane;
		this.checkBox = checkBox;
		this.fingerLabel = fingerLabel;
		this.handLabel = handLabel;
	}
	
	public FingerPosition getPalmPosition(){return palmPosition;}
	public void setPalmPosition(FingerPosition palmPosition){this.palmPosition = palmPosition;}
	
	public ImageView getImageView(){return imageView;}
	public void setImageView(ImageView imageView){this.imageView = imageView;}
	
	public SVGPath getSvgPath(){return svgPath;}
	public void setSvgPath(SVGPath svgPath){this.svgPath = svgPath;}
	
	public FourStateTitledPane getTitledPane(){return titledPane;}
	public void setTitledPane(FourStateTitledPane titledPane){this.titledPane = titledPane;}
	
	public CheckBox getCheckBox(){return checkBox;}
	public void setCheckBox(CheckBox checkBox){this.checkBox = checkBox;}
	
	public String getFingerLabel(){return fingerLabel;}
	public void setFingerLabel(String fingerLabel){this.fingerLabel = fingerLabel;}
	
	public String getHandLabel(){return handLabel;}
	public void setHandLabel(String handLabel){this.handLabel = handLabel;}
}