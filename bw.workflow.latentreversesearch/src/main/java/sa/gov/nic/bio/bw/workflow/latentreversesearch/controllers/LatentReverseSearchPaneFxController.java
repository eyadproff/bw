package sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHit;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitProcessingStatus;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.utils.LatentReverseSearchErrorCodes;

import java.util.List;

@FxmlFile("latentReverseSearch.fxml")
public class LatentReverseSearchPaneFxController extends ContentFxControllerBase
{
	@Input private Integer resultsTotalCount;
	@Input private List<LatentHit> latentHits;
	@Output private Long transactionNumber;
	@Output private Long civilBiometricsId;
	@Output private Long personId;
	@Output private Long referenceNumber;
	@Output private Integer locationId;
	@Output private LatentHitProcessingStatus status;
	@Output private Long entryDateFrom;
	@Output private Long entryDateTo;
	@Output private Integer recordsPerPage;
	@Output private Integer pageIndex;
	
	@FXML private TitledPane tpSearchResults;
	@FXML private Pane paneTable;
	@FXML private CheckBox cbTransactionNumber;
	@FXML private CheckBox cbCivilBiometricsId;
	@FXML private CheckBox cbPersonId;
	@FXML private CheckBox cbReferenceNumber;
	@FXML private CheckBox cbLocationId;
	@FXML private CheckBox cbStatus;
	@FXML private CheckBox cbEntryDate;
	@FXML private ComboBox<ComboBoxItem<LatentHitProcessingStatus>> cboStatus;
	@FXML private TextField txtTransactionNumber;
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtPersonId;
	@FXML private TextField txtReferenceNumber;
	@FXML private TextField txtLocationId;
	@FXML private DatePicker dpEntryDateFrom;
	@FXML private DatePicker dpEntryDateTo;
	@FXML private RadioButton rdoLatestNew;
	@FXML private RadioButton rdoOtherSearchCriteria;
	@FXML private RadioButton rdoEntryDateFromUseHijri;
	@FXML private RadioButton rdoEntryDateFromGregorian;
	@FXML private RadioButton rdoEntryDateToUseHijri;
	@FXML private RadioButton rdoEntryDateToGregorian;
	@FXML private Pagination pagination;
	@FXML private TableView<LatentHit> tvLatentHits;
	@FXML private TableColumn<LatentHit, String> tcTransactionNumber;
	@FXML private TableColumn<LatentHit, String> tcCivilBiometricsId;
	@FXML private TableColumn<LatentHit, String> tcPersonId;
	@FXML private TableColumn<LatentHit, String> tcReferenceNumber;
	@FXML private TableColumn<LatentHit, String> tcLocationId;
	@FXML private TableColumn<LatentHit, String> tcStatus;
	@FXML private TableColumn<LatentHit, String> tcEntryDateTime;
	@FXML private Label lblCandidateLatentsPlaceHolder;
	@FXML private ProgressIndicator piCandidateLatentsPlaceHolder;
	@FXML private Button btnInquiry;
	@FXML private Button btnClearFields;
	@FXML private Button btnOpenLatentHitList;
	
	private Node paginationControlBox;
	private boolean tableInitialized = false;
	private boolean newQuery = false;
	private BooleanProperty disableInquiryButtonProperty = new SimpleBooleanProperty(false);
	
	@Override
	protected void onAttachedToScene()
	{
		rdoOtherSearchCriteria.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    cbTransactionNumber.setDisable(!newValue);
		    cbCivilBiometricsId.setDisable(!newValue);
		    cbPersonId.setDisable(!newValue);
		    cbReferenceNumber.setDisable(!newValue);
		    cbLocationId.setDisable(!newValue);
		    cbStatus.setDisable(!newValue);
		    cbEntryDate.setDisable(!newValue);
		
		    if(!newValue) // !selected
		    {
		        cbTransactionNumber.setSelected(false);
		        cbCivilBiometricsId.setSelected(false);
		        cbPersonId.setSelected(false);
		        cbReferenceNumber.setSelected(false);
		        cbLocationId.setSelected(false);
		        cbStatus.setSelected(false);
		        cbEntryDate.setSelected(false);
		    }
		});
		
		txtTransactionNumber.disableProperty().bind(cbTransactionNumber.selectedProperty().not());
		txtCivilBiometricsId.disableProperty().bind(cbCivilBiometricsId.selectedProperty().not());
		txtPersonId.disableProperty().bind(cbPersonId.selectedProperty().not());
		txtReferenceNumber.disableProperty().bind(cbReferenceNumber.selectedProperty().not());
		txtLocationId.disableProperty().bind(cbLocationId.selectedProperty().not());
		cboStatus.disableProperty().bind(cbStatus.selectedProperty().not());
		dpEntryDateFrom.disableProperty().bind(cbEntryDate.selectedProperty().not());
		dpEntryDateTo.disableProperty().bind(cbEntryDate.selectedProperty().not());
		
		BooleanBinding transactionNumberBinding = createTextFieldNotCompleteBooleanBinding(txtTransactionNumber);
		BooleanBinding civilBiometricsIdBinding = createTextFieldNotCompleteBooleanBinding(txtCivilBiometricsId);
		BooleanBinding personIdBinding = createTextFieldNotCompleteBooleanBinding(txtPersonId);
		BooleanBinding referenceNumberBinding = createTextFieldNotCompleteBooleanBinding(txtReferenceNumber);
		BooleanBinding locationIdBinding = createTextFieldNotCompleteBooleanBinding(txtLocationId);
		BooleanBinding statusBinding = createComboBoxNotCompleteBooleanBinding(cboStatus);
		BooleanBinding entryDateFromBinding = createDatePickerNotCompleteBooleanBinding(dpEntryDateFrom);
		BooleanBinding entryDateToBinding = createDatePickerNotCompleteBooleanBinding(dpEntryDateTo);
		BooleanBinding allDisabled = txtTransactionNumber.disableProperty().and(txtCivilBiometricsId.disableProperty())
		                                                 .and(txtPersonId.disableProperty()).and(txtReferenceNumber.disableProperty())
		                                                 .and(txtLocationId.disableProperty()).and(cboStatus.disableProperty())
		                                                 .and(dpEntryDateFrom.disableProperty()).and(dpEntryDateTo.disableProperty());
		
		btnInquiry.disableProperty().bind(rdoOtherSearchCriteria.selectedProperty().and(transactionNumberBinding.or(civilBiometricsIdBinding)
		                                       .or(personIdBinding).or(referenceNumberBinding).or(locationIdBinding).or(statusBinding)
                                               .or(entryDateFromBinding).or(entryDateToBinding).or(allDisabled)).or(disableInquiryButtonProperty));
		
		GuiUtils.initDatePicker(rdoEntryDateFromUseHijri, dpEntryDateFrom, null);
		GuiUtils.initDatePicker(rdoEntryDateToUseHijri, dpEntryDateTo, null);
		
		rdoEntryDateFromUseHijri.setSelected(true);
		rdoEntryDateToUseHijri.setSelected(true);
		
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
		
		GuiUtils.applyValidatorToTextField(txtTransactionNumber, "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(txtCivilBiometricsId, "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtReferenceNumber, "\\d*", "[^\\d]", 18);
		GuiUtils.applyValidatorToTextField(txtLocationId, "\\d*", "[^\\d]", 4);
		
		//btnOpenLatentHitList.disableProperty().bind(Bindings.size(tvLatentHits.getSelectionModel()
		//                                                                      .getSelectedItems()).isEqualTo(0));
		
		EventHandler<? super KeyEvent> keyReleasedEventHandler = keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnOpenLatentHitList.fire();
		};
		EventHandler<? super MouseEvent> mouseEventHandler = mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnOpenLatentHitList.fire();
			}
		};
		
		tvLatentHits.setOnKeyReleased(keyReleasedEventHandler);
		tvLatentHits.setOnMouseClicked(mouseEventHandler);
		
		ObservableList<ComboBoxItem<LatentHitProcessingStatus>> statusItems = FXCollections.observableArrayList();
		for(var status : LatentHitProcessingStatus.values())
		{
			String statusText;
			
			switch(status)
			{
				case NEW: statusText = resources.getString("label.new"); break;
				case IN_PROGRESS: statusText = resources.getString("label.inProgress"); break;
				case DONE: statusText = resources.getString("label.done"); break;
				default: statusText = "";
			}
			
			statusItems.add(new ComboBoxItem<>(status, statusText));
		}
		
		cboStatus.setItems(statusItems);
		cboStatus.getSelectionModel().selectFirst();
		
		tcTransactionNumber.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			long transactionNumber = latentHit.getTransactionNumber();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(transactionNumber)));
		});
		
		tcCivilBiometricsId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			long civilBiometricsId = latentHit.getCivilBiometricsId();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(civilBiometricsId)));
		});
		
		tcPersonId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			long personId = latentHit.getPersonId();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(personId)));
		});
		
		tcReferenceNumber.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			long referenceNumber = latentHit.getReferenceNumber();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(referenceNumber)));
		});
		
		tcLocationId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			int locationId = latentHit.getLocationId();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(locationId)));
		});
		
		tcStatus.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			var status = latentHit.getStatus();
			String statusText;
			
			switch(status)
			{
				case NEW: statusText = resources.getString("label.new"); break;
				case IN_PROGRESS: statusText = resources.getString("label.inProgress"); break;
				case DONE: statusText = resources.getString("label.done"); break;
				default: statusText = "";
			}
			
			return new SimpleStringProperty(statusText);
		});
		
		tcEntryDateTime.setCellValueFactory(param ->
		{
		    boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			var latentHit = param.getValue();
		    long entryDateTime = latentHit.getEntryDateTime();
		    return new SimpleStringProperty(AppUtils.formatHijriDateSimple(entryDateTime, rtl));
		});
		
		btnInquiry.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			piCandidateLatentsPlaceHolder.setVisible(false);
			lblCandidateLatentsPlaceHolder.setVisible(true);
			
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
			
			if(latentHits != null)
			{
				tvLatentHits.getItems().setAll(latentHits);
				tvLatentHits.requestFocus();
			}
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		if(paginationControlBox != null) paginationControlBox.setDisable(bShow);
		disableInquiryButtonProperty.setValue(bShow);
		piCandidateLatentsPlaceHolder.setVisible(bShow);
		lblCandidateLatentsPlaceHolder.setVisible(!bShow);
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		if(btnInquiry.isDisabled() || !btnInquiry.isVisible()) return;
		
		transactionNumber = null;
		civilBiometricsId = null;
		personId = null;
		referenceNumber = null;
		locationId = null;
		status = null;
		entryDateFrom = null;
		entryDateTo = null;
		
		if(!rdoLatestNew.isSelected())
		{
			if(!txtTransactionNumber.isDisabled()) transactionNumber = Long.parseLong(txtTransactionNumber.getText());
			if(!txtCivilBiometricsId.isDisabled()) civilBiometricsId = Long.parseLong(txtCivilBiometricsId.getText());
			if(!txtPersonId.isDisabled()) personId = Long.parseLong(txtPersonId.getText());
			if(!txtReferenceNumber.isDisabled()) referenceNumber = Long.parseLong(txtReferenceNumber.getText());
			if(!txtLocationId.isDisabled()) locationId = Integer.parseInt(txtLocationId.getText());
			if(!cboStatus.isDisabled()) status = cboStatus.getValue().getItem();
			if(!dpEntryDateFrom.isDisabled()) entryDateFrom = AppUtils.gregorianDateToSeconds(dpEntryDateFrom.getValue());
			if(!dpEntryDateTo.isDisabled()) entryDateTo = AppUtils.gregorianDateToSeconds(dpEntryDateTo.getValue());
		}
		
		pagination.setPageCount(1);
		tvLatentHits.getItems().clear();
		recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
		pageIndex = 0;
		resultsTotalCount = null;
		latentHits = null;
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
		
		newQuery = true;
		continueWorkflow();
	}
	
	@FXML
	private void onClearFieldsButtonClicked(ActionEvent actionEvent)
	{
		txtTransactionNumber.clear();
		txtCivilBiometricsId.clear();
		txtPersonId.clear();
		txtReferenceNumber.clear();
		txtLocationId.clear();
		dpEntryDateFrom.setValue(null);
		dpEntryDateTo.setValue(null);
	}
	
	@FXML
	private void onOpenLatentHitListButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		try
		{
			AdjudicatorDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
								Context.getCoreFxController().getStage(), AdjudicatorDialogFxController.class, true);
			
			if(controller != null)
			{
				var selectedItem = tvLatentHits.getSelectionModel().getSelectedItem();
				//controller.setTransactionNumber(selectedItem.getTransactionNumber());
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = LatentReverseSearchErrorCodes.C018_00001.getCode();
			String[] errorDetails = {"Failed to load (" + AdjudicatorDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
		}
	}
	
	private Node createPage(int pageIndex)
	{
		if(tableInitialized)
		{
			if(!newQuery)
			{
				tvLatentHits.getItems().clear();
				recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
				this.pageIndex = pageIndex;
				resultsTotalCount = null;
				latentHits = null;
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
	
	private static BooleanBinding createDatePickerNotCompleteBooleanBinding(DatePicker datePicker)
	{
		return datePicker.disabledProperty().not().and(datePicker.valueProperty().isNull());
	}
}