package sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowReportDialogFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.utils.ConvictedReportInquiryErrorCodes;

import java.util.List;

@FxmlFile("convictedReportInquiry.fxml")
public class ConvictedReportInquiryPaneFxController extends BodyFxControllerBase
{
	@Input private Integer resultsTotalCount;
	@Input private List<ConvictedReport> convictedReports;
	@Output private Long reportNumber;
	@Output private Long criminalBiometricsId;
	@Output private Long location;
	@Output private Long personId;
	@Output private String documentId;
	@Output private String firstName;
	@Output private String fatherName;
	@Output private String grandfatherName;
	@Output private String familyName;
	@Output private String judgementNumber;
	@Output private Long prisonerNumber;
	@Output private Long judgmentDateFrom;
	@Output private Long judgmentDateTo;
	@Output private Boolean showOldReports;
	@Output private Boolean showDeletedReports;
	@Output private Integer recordsPerPage;
	@Output private Integer pageIndex;
	
	@FXML private TitledPane tpSearchResults;
	@FXML private Pane paneTable;
	@FXML private CheckBox cbCriminalBiometricsId;
	@FXML private CheckBox cbLocation;
	@FXML private CheckBox cbPersonId;
	@FXML private CheckBox cbDocumentId;
	@FXML private CheckBox cbFirstName;
	@FXML private CheckBox cbFatherName;
	@FXML private CheckBox cbGrandfatherName;
	@FXML private CheckBox cbFamilyName;
	@FXML private CheckBox cbJudgmentNumber;
	@FXML private CheckBox cbPrisonerNumber;
	@FXML private CheckBox cbJudgmentDate;
	@FXML private CheckBox cbShowOldReports;
	@FXML private CheckBox cbShowDeletedReports;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private TextField txtLocation;
	@FXML private TextField txtReportNumber;
	@FXML private TextField txtPersonId;
	@FXML private TextField txtDocumentId;
	@FXML private TextField txtFirstName;
	@FXML private TextField txtFatherName;
	@FXML private TextField txtGrandfatherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtJudgmentNumber;
	@FXML private TextField txtPrisonerNumber;
	@FXML private DatePicker dpJudgmentDateFrom;
	@FXML private DatePicker dpJudgmentDateTo;
	@FXML private RadioButton rdoReportNumber;
	@FXML private RadioButton rdoOtherSearchCriteria;
	@FXML private RadioButton rdoJudgmentDateFromUseHijri;
	@FXML private RadioButton rdoJudgmentDateFromGregorian;
	@FXML private RadioButton rdoJudgmentDateToUseHijri;
	@FXML private RadioButton rdoJudgmentDateToGregorian;
	@FXML private Pagination pagination;
	@FXML private TableView<ConvictedReport> tvConvictedReports;
	@FXML private TableColumn<ConvictedReport, String> tcSequence;
	@FXML private TableColumn<ConvictedReport, String> tcName;
	@FXML private TableColumn<ConvictedReport, String> tcPersonId;
	@FXML private TableColumn<ConvictedReport, String> tcDocumentId;
	@FXML private TableColumn<ConvictedReport, String> tcNationality;
	@FXML private TableColumn<ConvictedReport, String> tcGender;
	@FXML private TableColumn<ConvictedReport, String> tcRegistrationDate;
	@FXML private Label lblConvictedReportsPlaceHolder;
	@FXML private ProgressIndicator piConvictedReportsPlaceHolder;
	@FXML private Button btnInquiry;
	@FXML private Button btnClearFields;
	@FXML private Button btnShowReport;
	
	private Node paginationControlBox;
	private boolean tableInitialized = false;
	private boolean newQuery = false;
	private BooleanProperty disableInquiryButtonProperty = new SimpleBooleanProperty(false);
	
	@Override
	protected void onAttachedToScene()
	{
		txtReportNumber.disableProperty().bind(rdoReportNumber.selectedProperty().not());
		rdoOtherSearchCriteria.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			cbCriminalBiometricsId.setDisable(!newValue);
			cbLocation.setDisable(!newValue);
			cbPersonId.setDisable(!newValue);
			cbDocumentId.setDisable(!newValue);
			cbFirstName.setDisable(!newValue);
			cbFatherName.setDisable(!newValue);
			cbGrandfatherName.setDisable(!newValue);
			cbFamilyName.setDisable(!newValue);
			cbJudgmentNumber.setDisable(!newValue);
			cbPrisonerNumber.setDisable(!newValue);
			cbJudgmentDate.setDisable(!newValue);
			cbShowOldReports.setDisable(!newValue);
			cbShowDeletedReports.setDisable(!newValue);
			
			if(!newValue) // !selected
			{
				cbCriminalBiometricsId.setSelected(false);
				cbLocation.setSelected(false);
				cbPersonId.setSelected(false);
				cbDocumentId.setSelected(false);
				cbFirstName.setSelected(false);
				cbFatherName.setSelected(false);
				cbGrandfatherName.setSelected(false);
				cbFamilyName.setSelected(false);
				cbJudgmentNumber.setSelected(false);
				cbPrisonerNumber.setSelected(false);
				cbJudgmentDate.setSelected(false);
				cbShowOldReports.setSelected(true);
				cbShowDeletedReports.setSelected(true);
			}
		});
		
		txtCriminalBiometricsId.disableProperty().bind(cbCriminalBiometricsId.selectedProperty().not());
		txtLocation.disableProperty().bind(cbLocation.selectedProperty().not());
		txtPersonId.disableProperty().bind(cbPersonId.selectedProperty().not());
		txtDocumentId.disableProperty().bind(cbDocumentId.selectedProperty().not());
		txtFirstName.disableProperty().bind(cbFirstName.selectedProperty().not());
		txtFatherName.disableProperty().bind(cbFatherName.selectedProperty().not());
		txtGrandfatherName.disableProperty().bind(cbGrandfatherName.selectedProperty().not());
		txtFamilyName.disableProperty().bind(cbFamilyName.selectedProperty().not());
		txtJudgmentNumber.disableProperty().bind(cbJudgmentNumber.selectedProperty().not());
		txtPrisonerNumber.disableProperty().bind(cbPrisonerNumber.selectedProperty().not());
		dpJudgmentDateFrom.disableProperty().bind(cbJudgmentDate.selectedProperty().not());
		dpJudgmentDateTo.disableProperty().bind(cbJudgmentDate.selectedProperty().not());
		
		BooleanBinding reportNumberBinding = createTextFieldNotCompleteBooleanBinding(txtReportNumber);
		BooleanBinding criminalBiometricsIdBinding = createTextFieldNotCompleteBooleanBinding(txtCriminalBiometricsId);
		BooleanBinding locationBinding = createTextFieldNotCompleteBooleanBinding(txtLocation);
		BooleanBinding personIdBinding = createTextFieldNotCompleteBooleanBinding(txtPersonId);
		BooleanBinding documentIdBinding = createTextFieldNotCompleteBooleanBinding(txtDocumentId);
		BooleanBinding firstNameBinding = createTextFieldNotCompleteBooleanBinding(txtFirstName);
		BooleanBinding fatherNameBinding = createTextFieldNotCompleteBooleanBinding(txtFatherName);
		BooleanBinding grandfatherNameBinding = createTextFieldNotCompleteBooleanBinding(txtGrandfatherName);
		BooleanBinding familyNameBinding = createTextFieldNotCompleteBooleanBinding(txtFamilyName);
		BooleanBinding judgmentNumberBinding = createTextFieldNotCompleteBooleanBinding(txtJudgmentNumber);
		BooleanBinding prisonerNumberBinding = createTextFieldNotCompleteBooleanBinding(txtPrisonerNumber);
		BooleanBinding judgmentDateFromBinding = createDatePickerNotCompleteBooleanBinding(dpJudgmentDateFrom);
		BooleanBinding judgmentDateToBinding = createDatePickerNotCompleteBooleanBinding(dpJudgmentDateTo);
		BooleanBinding allDisabled = txtCriminalBiometricsId.disableProperty().and(txtLocation.disableProperty())
									.and(txtPersonId.disableProperty()).and(txtDocumentId.disableProperty())
									.and(txtFirstName.disableProperty()).and(txtFatherName.disableProperty())
									.and(txtGrandfatherName.disableProperty()).and(txtFamilyName.disableProperty())
									.and(txtJudgmentNumber.disableProperty()).and(txtPrisonerNumber.disableProperty())
									.and(dpJudgmentDateFrom.disableProperty()).and(dpJudgmentDateTo.disableProperty());
		
		btnInquiry.disableProperty().bind(rdoReportNumber.selectedProperty().and(reportNumberBinding)
                      .or(rdoOtherSearchCriteria.selectedProperty().and(criminalBiometricsIdBinding.or(locationBinding)
                      .or(personIdBinding).or(documentIdBinding).or(firstNameBinding).or(fatherNameBinding)
                      .or(grandfatherNameBinding).or(familyNameBinding).or(judgmentNumberBinding)
                      .or(prisonerNumberBinding).or(judgmentDateFromBinding).or(judgmentDateToBinding).or(allDisabled)))
                      .or(disableInquiryButtonProperty));
		
		GuiUtils.initDatePicker(rdoJudgmentDateFromUseHijri, dpJudgmentDateFrom, null);
		GuiUtils.initDatePicker(rdoJudgmentDateToUseHijri, dpJudgmentDateTo, null);
		
		rdoJudgmentDateFromUseHijri.setSelected(true);
		rdoJudgmentDateToUseHijri.setSelected(true);
		
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
				                                                            AppUtils.localizeNumbers("0") + ")");
		
		pagination.setMaxPageIndicatorCount(AppConstants.TABLE_PAGINATION_PAGES_PER_ITERATION);
		GuiUtils.localizePagination(pagination);
		pagination.setPageFactory(this::createPage);
		paginationControlBox = pagination.lookup(".control-box");
		pagination.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			// disable controlling the pagination by keyboard
			if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) event.consume();
		});
		
		GuiUtils.applyValidatorToTextField(txtCriminalBiometricsId, "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(txtLocation, "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(txtReportNumber, "\\d*", "[^\\d]",
		                                   18);
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtDocumentId, null, null, 10);
		GuiUtils.applyValidatorToTextField(txtFirstName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtFatherName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtGrandfatherName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtFamilyName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtJudgmentNumber, null, null, 20);
		GuiUtils.applyValidatorToTextField(txtPrisonerNumber, "\\d*", "[^\\d]",
		                                   12);
		
		btnShowReport.disableProperty().bind(Bindings.size(tvConvictedReports.getSelectionModel()
				                                                             .getSelectedItems()).isEqualTo(0));
		
		EventHandler<? super KeyEvent> keyReleasedEventHandler = keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnShowReport.fire();
		};
		EventHandler<? super MouseEvent> mouseEventHandler = mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnShowReport.fire();
			}
		};
		
		tvConvictedReports.setOnKeyReleased(keyReleasedEventHandler);
		tvConvictedReports.setOnMouseClicked(mouseEventHandler);
		
		tcSequence.setCellValueFactory(param ->
		{
			ConvictedReport convictedReport = param.getValue();
			int sequence = convictedReport.getSequence();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(sequence)));
		});
		
		tcName.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    Name name = convictedReport.getSubjtName();
		    return new SimpleStringProperty(AppUtils.constructName(name));
		});
		
		tcPersonId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    Long subjSamisId = convictedReport.getSubjSamisId();
		    if(subjSamisId == null) return null;
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(subjSamisId)));
		});
		
		tcDocumentId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
			String documentId = convictedReport.getSubjDocId();
			if(documentId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(documentId));
		});
		
		tcNationality.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		
			@SuppressWarnings("unchecked")
			List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Integer nationalityCode = convictedReport.getSubjNationalityCode();
			if(nationalityCode == null) return null;
			
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
		
		tcGender.setCellValueFactory(param ->
		{
			ConvictedReport convictedReport = param.getValue();
			String subjGender = convictedReport.getSubjGender();
			if(subjGender == null) return null;
			
			Gender gender = "F".equals(subjGender) ? Gender.FEMALE : Gender.MALE;
			return new SimpleStringProperty(gender.toString());
		});
		
		tcRegistrationDate.setCellValueFactory(param ->
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		    ConvictedReport convictedReport = param.getValue();
			Long reportDate = convictedReport.getReportDate();
			if(reportDate == null) return null;
			return new SimpleStringProperty(AppUtils.formatHijriDateSimple(reportDate, rtl));
		});
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
		
		txtCriminalBiometricsId.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			piConvictedReportsPlaceHolder.setVisible(false);
			lblConvictedReportsPlaceHolder.setVisible(true);
			
			if(resultsTotalCount != null)
			{
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
						                            AppUtils.localizeNumbers(String.valueOf(resultsTotalCount)) + ")");
				
				int pageCount = (resultsTotalCount - 1) / AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE + 1;
				if(pageIndex > pageCount) pageIndex = pageCount;
				Platform.runLater(() ->
				{
					pagination.setPageCount(pageCount);
					pagination.setCurrentPageIndex(pageIndex);
					
					if(newQuery) newQuery = false;
				});
			}
			
			if(convictedReports != null)
			{
				for(int i = 0; i < convictedReports.size(); i++)
				{
					int sequence = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE * pageIndex + i + 1;
					ConvictedReport convictedReport = convictedReports.get(i);
					convictedReport.setSequence(sequence);
				}
				
				tvConvictedReports.getItems().setAll(convictedReports);
				tvConvictedReports.requestFocus();
			}
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		if(paginationControlBox != null) paginationControlBox.setDisable(bShow);
		disableInquiryButtonProperty.setValue(bShow);
		piConvictedReportsPlaceHolder.setVisible(bShow);
		lblConvictedReportsPlaceHolder.setVisible(!bShow);
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		if(btnInquiry.isDisabled() || !btnInquiry.isVisible()) return;
		
		reportNumber = null;
		criminalBiometricsId = null;
		location = null;
		personId = null;
		documentId = null;
		firstName = null;
		fatherName = null;
		grandfatherName = null;
		familyName = null;
		judgementNumber = null;
		prisonerNumber = null;
		judgmentDateFrom = null;
		judgmentDateTo = null;
		showOldReports = cbShowOldReports.isSelected();
		showDeletedReports = cbShowDeletedReports.isSelected();
		
		if(rdoReportNumber.isSelected())
		{
			if(!txtReportNumber.isDisabled()) reportNumber = Long.parseLong(txtReportNumber.getText());
		}
		else
		{
			if(!txtCriminalBiometricsId.isDisabled()) criminalBiometricsId =
														Long.parseLong(txtCriminalBiometricsId.getText());
			if(!txtLocation.isDisabled()) location = Long.parseLong(txtLocation.getText());
			if(!txtPersonId.isDisabled()) personId = Long.parseLong(txtPersonId.getText());
			if(!txtDocumentId.isDisabled()) documentId = txtDocumentId.getText();
			if(!txtFirstName.isDisabled()) firstName = txtFirstName.getText();
			if(!txtFatherName.isDisabled()) fatherName = txtFatherName.getText();
			if(!txtGrandfatherName.isDisabled()) grandfatherName = txtGrandfatherName.getText();
			if(!txtFamilyName.isDisabled()) familyName = txtFamilyName.getText();
			if(!txtJudgmentNumber.isDisabled()) judgementNumber = txtJudgmentNumber.getText();
			if(!txtPrisonerNumber.isDisabled()) prisonerNumber = Long.parseLong(txtPrisonerNumber.getText());
			if(!dpJudgmentDateFrom.isDisabled()) judgmentDateFrom =
														AppUtils.gregorianDateToSeconds(dpJudgmentDateFrom.getValue());
			if(!dpJudgmentDateTo.isDisabled()) judgmentDateTo =
														AppUtils.gregorianDateToSeconds(dpJudgmentDateTo.getValue());
		}
		
		pagination.setPageCount(1);
		tvConvictedReports.getItems().clear();
		recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
		pageIndex = 0;
		resultsTotalCount = null;
		convictedReports = null;
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
				                                                            AppUtils.localizeNumbers("0") + ")");
		
		newQuery = true;
		continueWorkflow();
	}
	
	@FXML
	private void onClearFieldsButtonClicked(ActionEvent actionEvent)
	{
		txtReportNumber.clear();
		txtCriminalBiometricsId.clear();
		txtLocation.clear();
		txtPersonId.clear();
		txtDocumentId.clear();
		txtFirstName.clear();
		txtFatherName.clear();
		txtGrandfatherName.clear();
		txtFamilyName.clear();
		txtJudgmentNumber.clear();
		txtPrisonerNumber.clear();
		dpJudgmentDateFrom.setValue(null);
		dpJudgmentDateTo.setValue(null);
	}
	
	@FXML
	private void onShowReportButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		try
		{
			ShowReportDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), ShowReportDialogFxController.class, true);
			
			if(controller != null)
			{
				ConvictedReport selectedItem = tvConvictedReports.getSelectionModel().getSelectedItem();
				controller.setReportNumber(selectedItem.getReportNumber());
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = ConvictedReportInquiryErrorCodes.C014_00001.getCode();
			String[] errorDetails = {"Failed to load (" + ShowReportDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
		}
	}
	
	private Node createPage(int pageIndex)
	{
		if(tableInitialized)
		{
			if(!newQuery)
			{
				tvConvictedReports.getItems().clear();
				recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
				this.pageIndex = pageIndex;
				resultsTotalCount = null;
				convictedReports = null;
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
					                                                        AppUtils.localizeNumbers("0") + ")");
				continueWorkflow();
			}
		}
		else tableInitialized = true;
		
		return paneTable;
	}
	
	private static BooleanBinding createTextFieldNotCompleteBooleanBinding(TextField textField)
	{
		return textField.disabledProperty().not().and(
						Bindings.createBooleanBinding(() -> textField.getText().isBlank(), textField.textProperty()));
	}
	
	private static BooleanBinding createDatePickerNotCompleteBooleanBinding(DatePicker datePicker)
	{
		return datePicker.disabledProperty().not().and(datePicker.valueProperty().isNull());
	}
}