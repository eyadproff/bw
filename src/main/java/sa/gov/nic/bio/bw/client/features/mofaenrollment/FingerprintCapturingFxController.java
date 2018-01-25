package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;

public class FingerprintCapturingFxController extends WizardStepFxControllerBase
{
	@FXML private TitledPane tpRightHand;
	@FXML private TitledPane tpLeftHand;
	@FXML private SplitPane spFingerprints;
	@FXML private ImageView ivFingerprintDeviceLivePreview;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivLeftLittle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftThumb;
	@FXML private SVGPath svgRightHand;
	@FXML private SVGPath svgLeftHand;
	@FXML private SVGPath svgRightHandLittle;
	@FXML private SVGPath svgRightHandRing;
	@FXML private SVGPath svgRightHandMiddle;
	@FXML private SVGPath svgRightHandIndex;
	@FXML private SVGPath svgRightHandThumb;
	@FXML private SVGPath svgLeftHandLittle;
	@FXML private SVGPath svgLeftHandRing;
	@FXML private SVGPath svgLeftHandMiddle;
	@FXML private SVGPath svgLeftHandIndex;
	@FXML private SVGPath svgLeftHandThumb;
	@FXML private TitledPane tpRightLittle;
	@FXML private TitledPane tpRightRing;
	@FXML private TitledPane tpRightMiddle;
	@FXML private TitledPane tpRightIndex;
	@FXML private TitledPane tpRightThumb;
	@FXML private TitledPane tpLeftLittle;
	@FXML private TitledPane tpLeftRing;
	@FXML private TitledPane tpLeftMiddle;
	@FXML private TitledPane tpLeftIndex;
	@FXML private TitledPane tpLeftThumb;
	@FXML private CheckBox cbRightLittle;
	@FXML private CheckBox cbRightRing;
	@FXML private CheckBox cbRightMiddle;
	@FXML private CheckBox cbRightIndex;
	@FXML private CheckBox cbRightThumb;
	@FXML private CheckBox cbLeftLittle;
	@FXML private CheckBox cbLeftRing;
	@FXML private CheckBox cbLeftMiddle;
	@FXML private CheckBox cbLeftIndex;
	@FXML private CheckBox cbLeftThumb;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/fingerprintCapturing.fxml");
	}
	
	@FXML
	private void initialize()
	{
		tpLeftHand.prefWidthProperty().bind(spFingerprints.widthProperty().multiply(0.5));
		tpRightHand.prefWidthProperty().bind(spFingerprints.widthProperty().multiply(0.5));
		/*ivRightThumb.fitWidthProperty().bind(tpRightThumb.widthProperty());
		ivRightIndex.fitWidthProperty().bind(tpRightIndex.widthProperty());
		ivRightMiddle.fitWidthProperty().bind(tpRightMiddle.widthProperty());
		ivRightRing.fitWidthProperty().bind(tpRightRing.widthProperty());
		ivRightLittle.fitWidthProperty().bind(tpRightLittle.widthProperty());
		ivLeftThumb.fitWidthProperty().bind(tpLeftThumb.widthProperty());
		ivLeftIndex.fitWidthProperty().bind(tpLeftIndex.widthProperty());
		ivLeftMiddle.fitWidthProperty().bind(tpLeftMiddle.widthProperty());
		ivLeftRing.fitWidthProperty().bind(tpLeftRing.widthProperty());
		ivLeftLittle.fitWidthProperty().bind(tpLeftLittle.widthProperty());*/
		
		svgRightHand.setFill(Color.BLACK);
		svgLeftHand.setFill(Color.BLACK);
		
		svgRightHandLittle.setFill(null);
		svgRightHandRing.setFill(null);
		svgRightHandMiddle.setFill(null);
		svgRightHandIndex.setFill(null);
		svgRightHandThumb.setFill(null);
		svgLeftHandLittle.setFill(null);
		svgLeftHandRing.setFill(null);
		svgLeftHandMiddle.setFill(null);
		svgLeftHandIndex.setFill(null);
		svgLeftHandThumb.setFill(null);
		
		tpRightLittle.disableProperty().bind(cbRightLittle.selectedProperty().not());
		tpRightRing.disableProperty().bind(cbRightRing.selectedProperty().not());
		tpRightMiddle.disableProperty().bind(cbRightMiddle.selectedProperty().not());
		tpRightIndex.disableProperty().bind(cbRightIndex.selectedProperty().not());
		tpRightThumb.disableProperty().bind(cbRightThumb.selectedProperty().not());
		tpLeftLittle.disableProperty().bind(cbLeftLittle.selectedProperty().not());
		tpLeftRing.disableProperty().bind(cbLeftRing.selectedProperty().not());
		tpLeftMiddle.disableProperty().bind(cbLeftMiddle.selectedProperty().not());
		tpLeftIndex.disableProperty().bind(cbLeftIndex.selectedProperty().not());
		tpLeftThumb.disableProperty().bind(cbLeftThumb.selectedProperty().not());
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
	
	}
}