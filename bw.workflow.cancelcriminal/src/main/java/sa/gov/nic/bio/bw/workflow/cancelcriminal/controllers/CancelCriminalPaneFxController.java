package sa.gov.nic.bio.bw.workflow.cancelcriminal.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;

import java.util.List;
import java.util.Locale;

@FxmlFile("cancelCriminal.fxml")
public class CancelCriminalPaneFxController extends BodyFxControllerBase
{
	public enum CancelCriminalMethod
	{
		BY_PERSON_ID, BY_INQUIRY_ID
	}
	
	@Input private Boolean success;
	@Output private CancelCriminalMethod cancelCriminalMethod;
	@Output private Long criminalId;
	@Output private Long inquiryId;
	@Output private Long personId;
	@Output private Integer samisIdType;
	
	@FXML private TabPane tabPane;
	@FXML private Tab tabByPersonId;
	@FXML private Tab tabByInquiryId;
	@FXML private TextField txtPersonId;
	@FXML private ComboBox<PersonType> cboPersonType;
	@FXML private TextField txtCriminalId;
	@FXML private TextField txtInquiryId;
	@FXML private TextField txtCriminalId2;
	@FXML private VBox bottomBox;
	@FXML private Button btnCancelCriminal;
	@FXML private ProgressIndicator piCancelCriminal;
	
	@Override
	protected void onAttachedToScene()
	{
		tabByPersonId.setGraphic(AppUtils.createFontAwesomeIcon('\uf2bb'));
		tabByInquiryId.setGraphic(AppUtils.createFontAwesomeIcon('\uf1c0'));
		
		tabByPersonId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue) Platform.runLater(txtPersonId::requestFocus);
		});
		
		tabByInquiryId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue) Platform.runLater(txtInquiryId::requestFocus);
		});
		
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCriminalId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtInquiryId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCriminalId2, "\\d*", "[^\\d]", 10);
		
		
		BooleanBinding personIdEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding inquiryIdEmptyBinding = txtInquiryId.textProperty().isEmpty();
		BooleanBinding criminalNumberEmptyBinding = txtCriminalId.textProperty().isEmpty();
		BooleanBinding criminal2NumberEmptyBinding = txtCriminalId2.textProperty().isEmpty();
		
		BooleanBinding byPersonIdTabReadyBinding = tabByPersonId.selectedProperty()
														        .and(personIdEmptyBinding.not())
																.and(criminalNumberEmptyBinding.not());
		
		BooleanBinding byInquiryIdTabReadyBinding = tabByInquiryId.selectedProperty()
																  .and(inquiryIdEmptyBinding.not())
																  .and(criminal2NumberEmptyBinding.not());
		
		btnCancelCriminal.disableProperty().bind(byPersonIdTabReadyBinding.not().and(byInquiryIdTabReadyBinding.not()));
		
		
		
		cboPersonType.setConverter(new StringConverter<PersonType>()
		{
			@Override
			public String toString(PersonType object)
			{
				return formatPersonType(object);
			}
			
			@Override
			public PersonType fromString(String string)
			{
				return null;
			}
		});
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>)
													Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
		cboPersonType.getItems().addAll(personTypes);
		cboPersonType.getSelectionModel().select(0);
		
		txtPersonId.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		txtPersonId.requestFocus();
		
		if(successfulResponse)
		{
			String personId = txtPersonId.getText().trim();
			PersonType personType = cboPersonType.getValue();
			String criminalId = txtCriminalId.getText().trim();
			String inquiryId = txtInquiryId.getText().trim();
			String criminalId2 = txtCriminalId2.getText().trim();
			
			if(success)
			{
				if(tabByPersonId.isSelected())
				{
					String message = String.format(resources.getString("cancelCriminal.byPersonId.success"),
					                               criminalId, personId, formatPersonType(personType));
					showSuccessNotification(message);
				}
				else
				{
					String message = String.format(resources.getString("cancelCriminal.byInquiryId.success"),
					                               criminalId2, inquiryId);
					showSuccessNotification(message);
				}
			}
			else
			{
				if(tabByPersonId.isSelected())
				{
					String message = String.format(resources.getString("cancelCriminal.byPersonId.failure"),
					                               criminalId, personId, formatPersonType(personType));
					showWarningNotification(message);
				}
				else
				{
					String message = String.format(resources.getString("cancelCriminal.byInquiryId.failure"),
					                               criminalId2, inquiryId);
					showWarningNotification(message);
				}
			}
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		tabPane.setDisable(bShow);
		txtPersonId.setDisable(bShow);
		txtCriminalId.setDisable(bShow);
		
		GuiUtils.showNode(btnCancelCriminal, !bShow);
		GuiUtils.showNode(piCancelCriminal, bShow);
	}
	
	@FXML
	private void onCancelCriminalButtonClicked(ActionEvent actionEvent)
	{
		if(btnCancelCriminal.isDisabled()) return;
		
		String headerText = resources.getString("cancelCriminal.confirmation.header");
		
		if(tabByPersonId.isSelected())
		{
			String personId = txtPersonId.getText().trim();
			PersonType personType = cboPersonType.getValue();
			String criminalId = txtCriminalId.getText().trim();
			
			String contentText = String.format(
										resources.getString("cancelCriminal.byPersonId.confirmation.message"),
			                            criminalId, personId, formatPersonType(personType));
			boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
			
			this.cancelCriminalMethod = CancelCriminalMethod.BY_PERSON_ID;
			this.personId = Long.parseLong(personId);
			this.samisIdType = personType.getCode();
			this.criminalId = Long.parseLong(criminalId);
			
			continueWorkflow();
		}
		else
		{
			String inquiryId = txtInquiryId.getText().trim();
			String criminalId = txtCriminalId2.getText().trim();
			
			String contentText = String.format(
										resources.getString("cancelCriminal.byInquiryId.confirmation.message"),
										criminalId, inquiryId);
			boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
			
			this.cancelCriminalMethod = CancelCriminalMethod.BY_INQUIRY_ID;
			this.inquiryId = Long.parseLong(inquiryId);
			this.criminalId = Long.parseLong(criminalId);
			
			continueWorkflow();
		}
	}
	
	private static String formatPersonType(PersonType personType)
	{
		StringBuilder sb = new StringBuilder();
		
		if(Context.getGuiLanguage() == GuiLanguage.ARABIC)
		{
			sb.append(personType.getDescriptionAR());
		}
		else sb.append(personType.getDescriptionEN());
		
		return AppUtils.localizeNumbers(sb.toString(), Locale.getDefault(), false);
	}
}