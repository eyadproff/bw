package sa.gov.nic.bio.bw.workflow.convictedreportinquiry.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.ConvictedReport;

import java.util.List;
import java.util.Map;

@FxmlFile("convictedReportInquiry.fxml")
public class ConvictedReportInquiryPaneFxController extends BodyFxControllerBase
{
	@Input private List<Pair<ConvictedReport, Map<Integer, String>>> convictedReports;
	@Output private Long generalFileNumber;
	
	@FXML private TableView<Pair<ConvictedReport, Map<Integer, String>>> tvConvictedReports;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, Pair<ConvictedReport, Map<Integer, String>>>
																											tcSequence;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tvName;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcSamisId;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcSamisIdType;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcNationality;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcOperatorId;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcRegistrationDate;
	@FXML private BorderPane paneConvictedReports;
	@FXML private ProgressIndicator piLookup;
	@FXML private ProgressIndicator piInquiry;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private Button btnInquiry;
	@FXML private Button btnShowReport;
	
	@SuppressWarnings({"unchecked", "deprecation"})
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtGeneralFileNumber, "\\d*", "[^\\d]",
		                                   10);
		
		btnInquiry.disableProperty().bind(txtGeneralFileNumber.textProperty().isEmpty()
				                                                                    .or(piInquiry.visibleProperty()));
		btnShowReport.disableProperty().bind(Bindings.size(tvConvictedReports.getSelectionModel().getSelectedItems())
			                                                                                    .isEqualTo(0));
		
		tvConvictedReports.setOnKeyReleased(keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnShowReport.fire();
		});
		tvConvictedReports.setOnMouseClicked(mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnShowReport.fire();
			}
		});
		
		GuiUtils.initSequenceTableColumn(tcSequence);
		tcSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		
		tvName.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue().getKey();
		    Name name = convictedReport.getSubjtName();
		    return new SimpleStringProperty(AppUtils.constructName(name));
		});
		
		tcSamisId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue().getKey();
			
		    Long subjSamisId = convictedReport.getSubjSamisId();
			if(subjSamisId == null) return null;
			
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(subjSamisId)));
		});
		
		tcSamisIdType.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue().getKey();
			
			@SuppressWarnings("unchecked")
			List<PersonType> personTypes = (List<PersonType>)
														Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
			
			Integer samisIdTypeInteger = convictedReport.getSubjSamisType();
			if(samisIdTypeInteger != null)
			{
				PersonType thePersonType = null;
				
				for(PersonType type : personTypes)
				{
					if(type.getCode() == samisIdTypeInteger)
					{
						thePersonType = type;
						break;
					}
				}
				
				if(thePersonType != null)
				{
					boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
					String samisIdType = AppUtils.localizeNumbers(arabic ? thePersonType.getDescriptionAR() :
							                                               thePersonType.getDescriptionEN());
					return new SimpleStringProperty(samisIdType);
				}
			}
		    
		    return null;
		});
		
		tcNationality.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue().getKey();
		
			@SuppressWarnings("unchecked")
			List<Country> countries = (List<Country>)
														Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Integer nationalityCode = convictedReport.getSubjNationalityCode();
			
			if(nationalityCode == 0)
			{
				return new SimpleStringProperty(resources.getString("combobox.unknownNationality"));
			}
			
			Country countryBean = null;
			
			for(Country country : countries)
			{
				if(country.getCode() == nationalityCode)
				{
					countryBean = country;
					break;
				}
			}
			
			if(countryBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				String nationality = arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN();
				return new SimpleStringProperty(nationality);
			}
		
		    return null;
		});
		
		tcOperatorId.setCellValueFactory(param ->
		{
			ConvictedReport convictedReport = param.getValue().getKey();
			return new SimpleStringProperty(AppUtils.localizeNumbers(convictedReport.getOperatorId()));
		});
		
		tcRegistrationDate.setCellValueFactory(param ->
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		    ConvictedReport convictedReport = param.getValue().getKey();
			return new SimpleStringProperty(
							AppUtils.formatHijriDateSimple(convictedReport.getReportDate() * 1000, rtl));
		});
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		tvConvictedReports.requestFocus();
		
		if(successfulResponse)
		{
			tvConvictedReports.getItems().setAll(convictedReports);
			tvConvictedReports.requestFocus();
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		txtGeneralFileNumber.setDisable(bShow);
		tvConvictedReports.setDisable(bShow);
		
		GuiUtils.showNode(btnInquiry, !bShow);
		GuiUtils.showNode(piInquiry, bShow);
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		tvConvictedReports.getItems().clear();
		
		String sGeneralFileNumber = txtGeneralFileNumber.getText();
		generalFileNumber = Long.parseLong(sGeneralFileNumber);
		
		continueWorkflow();
	}
	
	@FXML
	private void onShowReportButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		try
		{
			ShowReportDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), ShowReportDialogFxController.class, resources,
					true);
			
			if(controller != null)
			{
				Pair<ConvictedReport, Map<Integer, String>> selectedItem =
															tvConvictedReports.getSelectionModel().getSelectedItem();
				controller.setConvictedReportWithFingerprintImages(selectedItem.getKey(), selectedItem.getValue());
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = ConvictedReportInquiryErrorCodes.C014_00002.getCode();
			String[] errorDetails = {"Failed to load (" + ShowReportDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
		}
	}
}