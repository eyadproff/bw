package sa.gov.nic.bio.bw.workflow.criminaltransactions.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransaction;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransactionType;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.lookups.CriminalTransactionTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalWorkflowSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@FxmlFile("criminalRecordsTransactions.fxml")
public class CriminalTransactionsPaneFxController extends ContentFxControllerBase
{
	@Input private Integer resultsTotalCount;
	@Input private List<CriminalTransaction> criminalTransactions;
	@Output private Long criminalBiometricsId;
	@Output private Long reportNumber;
	@Output private Integer location;
	@Output private Long operatorId;
	@Output private Long criminalDelinkId;
	@Output private Integer transactionType;
	@Output private Integer recordsPerPage;
	@Output private Integer pageIndex;
	
	@FXML private TitledPane tpSearchResults;
	@FXML private Pane paneTable;
	@FXML private CheckBox cbCriminalBiometricsId;
	@FXML private CheckBox cbReportNumber;
	@FXML private CheckBox cbLocation;
	@FXML private CheckBox cbOperatorId;
	@FXML private CheckBox cbCriminalDelinkId;
	@FXML private CheckBox cbTransactionType;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private TextField txtReportNumber;
	@FXML private TextField txtLocation;
	@FXML private TextField txtOperatorId;
	@FXML private TextField txtCriminalDelinkId;
	@FXML private ComboBox<ComboBoxItem<CriminalTransactionType>> cboTransactionType;
	@FXML private Pagination pagination;
	@FXML private TableView<CriminalTransaction> tvCriminalTransactions;
	@FXML private TableColumn<CriminalTransaction, String> tcSequence;
	@FXML private TableColumn<CriminalTransaction, String> tcTransactionType;
	@FXML private TableColumn<CriminalTransaction, String> tcTransactionId;
	@FXML private TableColumn<CriminalTransaction, String> tcCriminalId;
	@FXML private TableColumn<CriminalTransaction, String> tcDateAndTime;
	@FXML private TableColumn<CriminalTransaction, String> tcOperatorId;
	@FXML private TableColumn<CriminalTransaction, String> tcLocation;
	@FXML private TableColumn<CriminalTransaction, String> tcConvictedReportId;
	@FXML private TableColumn<CriminalTransaction, String> tcResult;
	@FXML private TableColumn<CriminalTransaction, String> tcPersonPresence;
	@FXML private TableColumn<CriminalTransaction, String> tcFingerprintsSource;
	@FXML private Label lblCriminalTransactionsPlaceHolder;
	@FXML private ProgressIndicator piCriminalTransactionsPlaceHolder;
	@FXML private Button btnInquiry;
	@FXML private Button btnClearFields;
	
	private Node paginationControlBox;
	private boolean tableInitialized = false;
	private boolean newQuery = false;
	private BooleanProperty disableInquiryButtonProperty = new SimpleBooleanProperty(false);
	
	@Override
	protected void onAttachedToScene()
	{
		txtCriminalBiometricsId.disableProperty().bind(cbCriminalBiometricsId.selectedProperty().not());
		txtReportNumber.disableProperty().bind(cbReportNumber.selectedProperty().not());
		txtLocation.disableProperty().bind(cbLocation.selectedProperty().not());
		txtOperatorId.disableProperty().bind(cbOperatorId.selectedProperty().not());
		txtCriminalDelinkId.disableProperty().bind(cbCriminalDelinkId.selectedProperty().not());
		cboTransactionType.disableProperty().bind(cbTransactionType.selectedProperty().not());
		
		BooleanBinding criminalBiometricsIdBinding = createTextFieldNotCompleteBooleanBinding(txtCriminalBiometricsId);
		BooleanBinding reportNumberBinding = createTextFieldNotCompleteBooleanBinding(txtReportNumber);
		BooleanBinding locationBinding = createTextFieldNotCompleteBooleanBinding(txtLocation);
		BooleanBinding operatorIdBinding = createTextFieldNotCompleteBooleanBinding(txtOperatorId);
		BooleanBinding criminalDelinkIdBinding = createTextFieldNotCompleteBooleanBinding(txtCriminalDelinkId);
		BooleanBinding transactionTypeBinding = createComboBoxNotCompleteBooleanBinding(cboTransactionType);
		BooleanBinding allDisabled = txtCriminalBiometricsId.disableProperty().and(txtReportNumber.disableProperty())
		                                                    .and(txtLocation.disableProperty()).and(txtOperatorId.disableProperty())
		                                                    .and(txtCriminalDelinkId.disableProperty()).and(cboTransactionType.disableProperty());
		
		btnInquiry.disableProperty().bind(criminalBiometricsIdBinding.or(locationBinding).or(reportNumberBinding).or(locationBinding).or(operatorIdBinding)
		                                                             .or(criminalDelinkIdBinding).or(transactionTypeBinding).or(allDisabled).or(disableInquiryButtonProperty));
			
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
		
		pagination.setMaxPageIndicatorCount(AppConstants.TABLE_PAGINATION_PAGES_PER_ITERATION);
		GuiUtils.localizePagination(pagination);
		pagination.setPageFactory(this::createPage);
		paginationControlBox = pagination.lookup(".control-box");
		pagination.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			// disable controlling the pagination by keyboard
			if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) event.consume();
		});
		
		GuiUtils.applyValidatorToTextField(txtCriminalBiometricsId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtReportNumber, "\\d*", "[^\\d]", 18);
		GuiUtils.applyValidatorToTextField(txtLocation, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtOperatorId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCriminalDelinkId, "\\d*", "[^\\d]", 10);
		
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		@SuppressWarnings("unchecked")
		var criminalTransactionTypes = (List<CriminalTransactionType>)
														Context.getUserSession().getAttribute(CriminalTransactionTypesLookup.KEY);
		
		var criminalTransactionTypesMap = criminalTransactionTypes.stream()
		                                                          .collect(Collectors.toMap(CriminalTransactionType::getCode, Function.identity()));
		
		var successLabel = resources.getString("label.success");
		var failureLabel = resources.getString("label.failure");
		var presentLabel = resources.getString("label.present");
		var notPresentLabel = resources.getString("label.notPresent");
		var byEnteringPersonIdLabel = resources.getString("label.byEnteringPersonId");
		var byEnteringCivilBiometricsIdLabel = resources.getString("label.byEnteringCivilBiometricsId");
		var byEnteringCriminalBiometricsIdLabel = resources.getString("label.byEnteringCriminalBiometricsId");
		var byScanningFingerprintsCardLabel = resources.getString("label.byScanningFingerprintsCard");
		var byUploadingNistFileLabel = resources.getString("label.byUploadingNistFile");
		var byCapturingFingerprintsViaScannerLabel = resources.getString("label.byCapturingFingerprintsViaScanner");
		
		ObservableList<ComboBoxItem<CriminalTransactionType>> criminalTransactionTypeItems = FXCollections.observableArrayList();
		criminalTransactionTypes.forEach(idType -> criminalTransactionTypeItems.add(new ComboBoxItem<>(idType, arabic ?
		                                                                                                       idType.getDescriptionAr() : idType.getDescriptionEn())));
		cboTransactionType.setItems(criminalTransactionTypeItems);
		
		tcSequence.setCellValueFactory(param ->
		{
			var record = param.getValue();
			int sequence = record.getSequence();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(sequence)));
		});
		
		tcTransactionType.setCellValueFactory(param ->
		{
		    var record = param.getValue();
			var transactionType = criminalTransactionTypesMap.get(record.getEventType());
			if(transactionType == null) return null;
			return new SimpleStringProperty(arabic ? transactionType.getArabicText() : transactionType.getEnglishText());
		});
		
		tcTransactionId.setCellValueFactory(param ->
		{
			var record = param.getValue();
			var tcn = record.getTcn();
			if(tcn == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(tcn)));
		});
		
		tcCriminalId.setCellValueFactory(param ->
		{
			var record = param.getValue();
			var criminalId = record.getCriminalId();
			if(criminalId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(criminalId));
		});
		
		tcDateAndTime.setCellValueFactory(param ->
		{
			var record = param.getValue();
			var timestamp = record.getTimestamp();
			if(timestamp == null) return null;
			return new SimpleStringProperty(AppUtils.formatHijriGregorianDateTimeSimple(timestamp, rtl));
		});
		
		tcOperatorId.setCellValueFactory(param ->
		{
			var record = param.getValue();
			var operatorId = record.getOperatorId();
			if(operatorId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(operatorId)));
		});
		
		tcLocation.setCellValueFactory(param ->
		{
			var record = param.getValue();
			var locationId = record.getLocationId();
			if(locationId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(locationId));
		});
		
		tcConvictedReportId.setCellValueFactory(param ->
		{
			var record = param.getValue();
			var criminalReportId = record.getCriminalReportId();
			if(criminalReportId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(criminalReportId));
		});
		
		tcResult.setCellValueFactory(param ->
		{
		    var record = param.getValue();
			var successful = record.getSuccessful();
			if(successful == null) return null;
			return new SimpleStringProperty(successful ? successLabel : failureLabel);
		});
		
		tcPersonPresence.setCellValueFactory(param ->
		{
		    var record = param.getValue();
			var workflowSource = record.getCriminalWorkflowSource();
			if(workflowSource == null) return null;
			
			if(workflowSource.equals(CriminalWorkflowSource.CRIMINAL_PRESENT.name()))
			{
				return new SimpleStringProperty(presentLabel);
			}
			else if(workflowSource.equals(CriminalWorkflowSource.CRIMINAL_NOT_PRESENT.name()))
			{
				return new SimpleStringProperty(notPresentLabel);
			}
			else return null;
		});
		
		tcFingerprintsSource.setCellValueFactory(param ->
		{
		    var record = param.getValue();
			var fingerprintSource = record.getCriminalFingerprintSource();
			if(fingerprintSource == null) return null;
			
			if(fingerprintSource.equals(CriminalFingerprintSource.LIVE_CAPTURE.name()))
			{
				return new SimpleStringProperty(byCapturingFingerprintsViaScannerLabel);
			}
			else if(fingerprintSource.equals(CriminalFingerprintSource.IMPORT_BY_SAMIS_ID.name()))
			{
				return new SimpleStringProperty(byEnteringPersonIdLabel);
			}
			else if(fingerprintSource.equals(CriminalFingerprintSource.IMPORT_BY_BIO_ID.name()))
			{
				return new SimpleStringProperty(byEnteringCivilBiometricsIdLabel);
			}
			else if(fingerprintSource.equals(CriminalFingerprintSource.IMPORT_BY_CRIMINAL_ID.name()))
			{
				return new SimpleStringProperty(byEnteringCriminalBiometricsIdLabel);
			}
			else if(fingerprintSource.equals(CriminalFingerprintSource.IMPORT_BY_FINGERPRINTS_CARD_SCANNER.name()))
			{
				return new SimpleStringProperty(byScanningFingerprintsCardLabel);
			}
			else if(fingerprintSource.equals(CriminalFingerprintSource.IMPORT_BY_NIST_FILE.name()))
			{
				return new SimpleStringProperty(byUploadingNistFileLabel);
			}
			else return null;
		});
		
		txtCriminalBiometricsId.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			piCriminalTransactionsPlaceHolder.setVisible(false);
			lblCriminalTransactionsPlaceHolder.setVisible(true);
			
			if(resultsTotalCount != null)
			{
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers(String.valueOf(resultsTotalCount)) + ")");
				
				int pageCount = (resultsTotalCount - 1) / AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE + 1;
				if(pageIndex > pageCount) pageIndex = pageCount;
				Platform.runLater(() ->
				{
				    pagination.setPageCount(pageCount);
				    pagination.setCurrentPageIndex(pageIndex);
				
				    if(newQuery) newQuery = false;
				});
			}
			
			if(criminalTransactions != null)
			{
				for(int i = 0; i < criminalTransactions.size(); i++)
				{
					int sequence = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE * pageIndex + i + 1;
					var record = criminalTransactions.get(i);
					record.setSequence(sequence);
				}
				
				tvCriminalTransactions.getItems().setAll(criminalTransactions);
				tvCriminalTransactions.requestFocus();
			}
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		if(paginationControlBox != null) paginationControlBox.setDisable(bShow);
		disableInquiryButtonProperty.setValue(bShow);
		piCriminalTransactionsPlaceHolder.setVisible(bShow);
		lblCriminalTransactionsPlaceHolder.setVisible(!bShow);
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		if(btnInquiry.isDisabled() || !btnInquiry.isVisible()) return;
		
		reportNumber = null;
		criminalBiometricsId = null;
		location = null;
		operatorId = null;
		criminalDelinkId = null;
		transactionType = null;
		
		if(!txtReportNumber.isDisabled()) reportNumber = Long.parseLong(txtReportNumber.getText());
		if(!txtCriminalBiometricsId.isDisabled()) criminalBiometricsId = Long.parseLong(txtCriminalBiometricsId.getText());
		if(!txtLocation.isDisabled()) location = Integer.parseInt(txtLocation.getText());
		if(!txtOperatorId.isDisabled()) operatorId = Long.parseLong(txtOperatorId.getText());
		if(!txtCriminalDelinkId.isDisabled()) criminalDelinkId = Long.parseLong(txtCriminalDelinkId.getText());
		if(!cboTransactionType.isDisabled()) transactionType = cboTransactionType.getValue().getItem().getCode();
		
		pagination.setPageCount(1);
		tvCriminalTransactions.getItems().clear();
		recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
		pageIndex = 0;
		resultsTotalCount = null;
		criminalTransactions = null;
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
		
		newQuery = true;
		continueWorkflow();
	}
	
	@FXML
	private void onClearFieldsButtonClicked(ActionEvent actionEvent)
	{
		txtReportNumber.clear();
		txtCriminalBiometricsId.clear();
		txtLocation.clear();
		txtOperatorId.clear();
		txtCriminalDelinkId.clear();
		cboTransactionType.getSelectionModel().selectFirst();
	}
	
	private Node createPage(int pageIndex)
	{
		if(tableInitialized)
		{
			if(!newQuery)
			{
				tvCriminalTransactions.getItems().clear();
				recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
				this.pageIndex = pageIndex;
				resultsTotalCount = null;
				criminalTransactions = null;
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
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
	
	private static <T> BooleanBinding createComboBoxNotCompleteBooleanBinding(ComboBox<T> comboBox)
	{
		return comboBox.disabledProperty().not().and(
				Bindings.createBooleanBinding(() -> comboBox.getSelectionModel().isEmpty(), comboBox.valueProperty()));
	}
}