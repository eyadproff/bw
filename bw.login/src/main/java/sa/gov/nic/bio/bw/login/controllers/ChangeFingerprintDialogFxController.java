package sa.gov.nic.bio.bw.login.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.core.controllers.FxControllerBase;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;

import java.util.Optional;

@FxmlFile("changeFingerprintDialog.fxml")
public class ChangeFingerprintDialogFxController extends FxControllerBase
{
	@FXML private Dialog<ButtonType> dialog;
	@FXML private SVGPath svgLeftLittle;
	@FXML private SVGPath svgLeftRing;
	@FXML private SVGPath svgLeftMiddle;
	@FXML private SVGPath svgLeftIndex;
	@FXML private SVGPath svgLeftThumb;
	@FXML private SVGPath svgRightLittle;
	@FXML private SVGPath svgRightRing;
	@FXML private SVGPath svgRightMiddle;
	@FXML private SVGPath svgRightIndex;
	@FXML private SVGPath svgRightThumb;
	@FXML private RadioButton rdoLeftLittle;
	@FXML private RadioButton rdoLeftRing;
	@FXML private RadioButton rdoLeftMiddle;
	@FXML private RadioButton rdoLeftIndex;
	@FXML private RadioButton rdoLeftThumb;
	@FXML private RadioButton rdoRightLittle;
	@FXML private RadioButton rdoRightRing;
	@FXML private RadioButton rdoRightMiddle;
	@FXML private RadioButton rdoRightIndex;
	@FXML private RadioButton rdoRightThumb;
	@FXML private ButtonType btConfirm;
	@FXML private ButtonType btCancel;
	
	private FingerPosition currentFingerPosition;
	
	public FingerPosition getCurrentFingerPosition(){return currentFingerPosition;}
	public void setCurrentFingerPosition(FingerPosition currentFingerPosition)
	{
		this.currentFingerPosition = currentFingerPosition;
		
		switch(currentFingerPosition)
		{
			case LEFT_LITTLE: rdoLeftLittle.setSelected(true); break;
			case LEFT_RING: rdoLeftRing.setSelected(true); break;
			case LEFT_MIDDLE: rdoLeftMiddle.setSelected(true); break;
			case LEFT_INDEX: rdoLeftIndex.setSelected(true); break;
			case LEFT_THUMB: rdoLeftThumb.setSelected(true); break;
			case RIGHT_LITTLE: rdoRightLittle.setSelected(true); break;
			case RIGHT_RING: rdoRightRing.setSelected(true); break;
			case RIGHT_MIDDLE: rdoRightMiddle.setSelected(true); break;
			case RIGHT_INDEX: rdoRightIndex.setSelected(true); break;
			case RIGHT_THUMB: rdoRightThumb.setSelected(true); break;
		}
	}
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event ->
		{
			rdoLeftLittle.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.LEFT_LITTLE);});
			rdoLeftRing.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.LEFT_RING);});
			rdoLeftMiddle.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.LEFT_MIDDLE);});
			rdoLeftIndex.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.LEFT_INDEX);});
			rdoLeftThumb.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.LEFT_THUMB);});
			rdoRightLittle.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.RIGHT_LITTLE);});
			rdoRightRing.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.RIGHT_RING);});
			rdoRightMiddle.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.RIGHT_MIDDLE);});
			rdoRightIndex.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.RIGHT_INDEX);});
			rdoRightThumb.selectedProperty().addListener((observable, oldValue, newValue) ->{
													if(newValue) activateFingerprint(FingerPosition.RIGHT_THUMB);});
			
			activateFingerprint(currentFingerPosition);
		});
	}
	
	public boolean showDialogAndWait()
	{
		Optional<ButtonType> buttonTypeOptional = dialog.showAndWait();
		return buttonTypeOptional.isPresent() && buttonTypeOptional.get() == btConfirm;
	}
	
	private void activateFingerprint(FingerPosition fingerPosition)
	{
		currentFingerPosition = fingerPosition;
		
		GuiUtils.showNode(svgLeftLittle, fingerPosition == FingerPosition.LEFT_LITTLE);
		GuiUtils.showNode(svgLeftRing, fingerPosition == FingerPosition.LEFT_RING);
		GuiUtils.showNode(svgLeftMiddle, fingerPosition == FingerPosition.LEFT_MIDDLE);
		GuiUtils.showNode(svgLeftIndex, fingerPosition == FingerPosition.LEFT_INDEX);
		GuiUtils.showNode(svgLeftThumb, fingerPosition == FingerPosition.LEFT_THUMB);
		GuiUtils.showNode(svgRightLittle, fingerPosition == FingerPosition.RIGHT_LITTLE);
		GuiUtils.showNode(svgRightRing, fingerPosition == FingerPosition.RIGHT_RING);
		GuiUtils.showNode(svgRightMiddle, fingerPosition == FingerPosition.RIGHT_MIDDLE);
		GuiUtils.showNode(svgRightIndex, fingerPosition == FingerPosition.RIGHT_INDEX);
		GuiUtils.showNode(svgRightThumb, fingerPosition == FingerPosition.RIGHT_THUMB);
	}
}