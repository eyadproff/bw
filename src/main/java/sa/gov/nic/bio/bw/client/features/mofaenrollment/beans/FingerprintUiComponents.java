package sa.gov.nic.bio.bw.client.features.mofaenrollment.beans;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.ui.FourStateTitledPane;

import java.util.Objects;

public class FingerprintUiComponents
{
	private FingerPosition fingerPosition;
	private FingerPosition slapPosition;
	private ImageView imageView;
	private SVGPath svgPath;
	private FourStateTitledPane titledPane;
	private CheckBox checkBox;
	private Button button;
	private String fingerLabel;
	private String handLabel;
	
	public FingerprintUiComponents(FingerPosition fingerPosition, FingerPosition slapPosition, ImageView imageView,
	                               SVGPath svgPath, FourStateTitledPane titledPane, CheckBox checkBox,
	                               Button button, String fingerLabel, String handLabel)
	{
		this.fingerPosition = fingerPosition;
		this.slapPosition = slapPosition;
		this.imageView = imageView;
		this.svgPath = svgPath;
		this.titledPane = titledPane;
		this.checkBox = checkBox;
		this.button = button;
		this.fingerLabel = fingerLabel;
		this.handLabel = handLabel;
	}
	
	public FingerPosition getFingerPosition(){return fingerPosition;}
	public void setFingerPosition(FingerPosition fingerPosition){this.fingerPosition = fingerPosition;}
	
	public FingerPosition getSlapPosition(){return slapPosition;}
	public void setSlapPosition(FingerPosition slapPosition){this.slapPosition = slapPosition;}
	
	public ImageView getImageView(){return imageView;}
	public void setImageView(ImageView imageView){this.imageView = imageView;}
	
	public SVGPath getSvgPath(){return svgPath;}
	public void setSvgPath(SVGPath svgPath){this.svgPath = svgPath;}
	
	public FourStateTitledPane getTitledPane(){return titledPane;}
	public void setTitledPane(FourStateTitledPane titledPane){this.titledPane = titledPane;}
	
	public CheckBox getCheckBox(){return checkBox;}
	public void setCheckBox(CheckBox checkBox){this.checkBox = checkBox;}
	
	public Button getButton(){return button;}
	public void setButton(Button button){this.button = button;}
	
	public String getFingerLabel(){return fingerLabel;}
	public void setFingerLabel(String fingerLabel){this.fingerLabel = fingerLabel;}
	
	public String getHandLabel(){return handLabel;}
	public void setHandLabel(String handLabel){this.handLabel = handLabel;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerprintUiComponents that = (FingerprintUiComponents) o;
		return fingerPosition == that.fingerPosition && slapPosition == that.slapPosition &&
			   Objects.equals(imageView, that.imageView) && Objects.equals(svgPath, that.svgPath) &&
			   Objects.equals(titledPane, that.titledPane) && Objects.equals(checkBox, that.checkBox) &&
			   Objects.equals(button, that.button) && Objects.equals(fingerLabel, that.fingerLabel) &&
			   Objects.equals(handLabel, that.handLabel);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(fingerPosition, slapPosition, imageView, svgPath, titledPane, checkBox, button,
		                    fingerLabel, handLabel);
	}
	
	@Override
	public String toString()
	{
		return "FingerprintUiComponents{" + "fingerPosition=" + fingerPosition + ", slapPosition=" + slapPosition +
			   ", imageView=" + imageView + ", svgPath=" + svgPath + ", titledPane=" + titledPane + ", checkBox=" +
			   checkBox + ", button=" + button + ", fingerLabel='" + fingerLabel + '\'' + ", handLabel='" +
			   handLabel + '\'' + '}';
	}
}