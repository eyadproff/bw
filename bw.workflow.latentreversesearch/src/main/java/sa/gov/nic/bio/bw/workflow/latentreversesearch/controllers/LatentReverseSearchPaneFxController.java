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
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Decision;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.DecisionHistory;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobStatus;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJob;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.utils.LatentReverseSearchErrorCodes;

import java.util.List;

@FxmlFile("latentReverseSearch.fxml")
public class LatentReverseSearchPaneFxController extends ContentFxControllerBase
{
	public enum Request
	{
		SEARCH,
		SUBMIT_DECISION
	}
	
	@Input private Integer resultsTotalCount;
	@Input private List<LatentJob> latentJobs;
	@Output private Request request;
	@Output private Long operatorId;
	@Output private Long jobId;
	@Output private Long civilBiometricsId;
	@Output private Long personId;
	@Output private Long tcn;
	@Output private Integer locationId;
	@Output private LatentJobStatus status;
	@Output private Long createDateFrom;
	@Output private Long createDateTo;
	@Output private Integer recordsPerPage;
	@Output private Integer pageIndex;
	@Output private DecisionHistory decisionHistory;
	
	@FXML private TitledPane tpSearchResults;
	@FXML private Pane paneTable;
	@FXML private CheckBox cbTransactionNumber;
	@FXML private CheckBox cbCivilBiometricsId;
	@FXML private CheckBox cbPersonId;
	@FXML private CheckBox cbReferenceNumber;
	@FXML private CheckBox cbLocationId;
	@FXML private CheckBox cbOperatorId;
	@FXML private CheckBox cbStatus;
	@FXML private CheckBox cbCreateDate;
	@FXML private ComboBox<ComboBoxItem<LatentJobStatus>> cboStatus;
	@FXML private TextField txtOperatorId;
	@FXML private TextField txtTransactionNumber;
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtPersonId;
	@FXML private TextField txtReferenceNumber;
	@FXML private TextField txtLocationId;
	@FXML private DatePicker dpCreateDateFrom;
	@FXML private DatePicker dpCreateDateTo;
	@FXML private RadioButton rdoLatestNew;
	@FXML private RadioButton rdoOtherSearchCriteria;
	@FXML private RadioButton rdoEntryDateFromUseHijri;
	@FXML private RadioButton rdoEntryDateFromGregorian;
	@FXML private RadioButton rdoEntryDateToUseHijri;
	@FXML private RadioButton rdoEntryDateToGregorian;
	@FXML private Pagination pagination;
	@FXML private TableView<LatentJob> tvLatentHits;
	@FXML private TableColumn<LatentJob, String> tcJobId;
	@FXML private TableColumn<LatentJob, String> tcCivilBiometricsId;
	@FXML private TableColumn<LatentJob, String> tcPersonId;
	@FXML private TableColumn<LatentJob, String> tcReferenceNumber;
	@FXML private TableColumn<LatentJob, String> tcLocationId;
	@FXML private TableColumn<LatentJob, String> tcStatus;
	@FXML private TableColumn<LatentJob, String> tcCreateDateTime;
	@FXML private TableColumn<LatentJob, String> tcUpdateDateTime;
	@FXML private Label lblCandidateLatentsPlaceHolder;
	@FXML private ProgressIndicator piCandidateLatentsPlaceHolder;
	@FXML private ProgressIndicator piSubmitting;
	@FXML private Button btnInquiry;
	@FXML private Button btnClearFields;
	@FXML private Button btnOpenLatentHitsWindow;
	
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
		    cbOperatorId.setDisable(!newValue);
		    cbStatus.setDisable(!newValue);
		    cbCreateDate.setDisable(!newValue);
		
		    if(!newValue) // !selected
		    {
		        cbTransactionNumber.setSelected(false);
		        cbCivilBiometricsId.setSelected(false);
		        cbPersonId.setSelected(false);
		        cbReferenceNumber.setSelected(false);
		        cbLocationId.setSelected(false);
			    cbOperatorId.setSelected(false);
		        cbStatus.setSelected(false);
		        cbCreateDate.setSelected(false);
		    }
		});
		
		txtTransactionNumber.disableProperty().bind(cbTransactionNumber.selectedProperty().not());
		txtCivilBiometricsId.disableProperty().bind(cbCivilBiometricsId.selectedProperty().not());
		txtPersonId.disableProperty().bind(cbPersonId.selectedProperty().not());
		txtReferenceNumber.disableProperty().bind(cbReferenceNumber.selectedProperty().not());
		txtLocationId.disableProperty().bind(cbLocationId.selectedProperty().not());
		txtOperatorId.disableProperty().bind(cbOperatorId.selectedProperty().not());
		cboStatus.disableProperty().bind(cbStatus.selectedProperty().not());
		dpCreateDateFrom.disableProperty().bind(cbCreateDate.selectedProperty().not());
		dpCreateDateTo.disableProperty().bind(cbCreateDate.selectedProperty().not());
		
		BooleanBinding transactionNumberBinding = createTextFieldNotCompleteBooleanBinding(txtTransactionNumber);
		BooleanBinding civilBiometricsIdBinding = createTextFieldNotCompleteBooleanBinding(txtCivilBiometricsId);
		BooleanBinding personIdBinding = createTextFieldNotCompleteBooleanBinding(txtPersonId);
		BooleanBinding referenceNumberBinding = createTextFieldNotCompleteBooleanBinding(txtReferenceNumber);
		BooleanBinding locationIdBinding = createTextFieldNotCompleteBooleanBinding(txtLocationId);
		BooleanBinding operatorIdBinding = createTextFieldNotCompleteBooleanBinding(txtOperatorId);
		BooleanBinding statusBinding = createComboBoxNotCompleteBooleanBinding(cboStatus);
		BooleanBinding entryDateFromBinding = createDatePickerNotCompleteBooleanBinding(dpCreateDateFrom);
		BooleanBinding entryDateToBinding = createDatePickerNotCompleteBooleanBinding(dpCreateDateTo);
		BooleanBinding allDisabled = txtTransactionNumber.disableProperty().and(txtCivilBiometricsId.disableProperty())
		                                                 .and(txtPersonId.disableProperty()).and(txtReferenceNumber.disableProperty())
		                                                 .and(txtLocationId.disableProperty()).and(txtOperatorId.disableProperty())
		                                                 .and(cboStatus.disableProperty()).and(dpCreateDateFrom.disableProperty())
		                                                 .and(dpCreateDateTo.disableProperty());
		
		btnInquiry.disableProperty().bind(rdoOtherSearchCriteria.selectedProperty().and(transactionNumberBinding
                                                                .or(civilBiometricsIdBinding).or(personIdBinding).or(referenceNumberBinding).or(locationIdBinding).or(operatorIdBinding)
                                                                .or(statusBinding).or(entryDateFromBinding).or(entryDateToBinding).or(allDisabled)).or(disableInquiryButtonProperty));
		
		GuiUtils.initDatePicker(rdoEntryDateFromUseHijri, dpCreateDateFrom, null);
		GuiUtils.initDatePicker(rdoEntryDateToUseHijri, dpCreateDateTo, null);
		
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
		
		GuiUtils.applyValidatorToTextField(txtOperatorId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtTransactionNumber, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCivilBiometricsId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtReferenceNumber, "\\d*", "[^\\d]", 18);
		GuiUtils.applyValidatorToTextField(txtLocationId, "\\d*", "[^\\d]", 4);
		
		btnOpenLatentHitsWindow.disableProperty().bind(Bindings.size(tvLatentHits.getSelectionModel()
		                                                                      .getSelectedItems()).isEqualTo(0));
		
		EventHandler<? super KeyEvent> keyReleasedEventHandler = keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnOpenLatentHitsWindow.fire();
		};
		EventHandler<? super MouseEvent> mouseEventHandler = mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnOpenLatentHitsWindow.fire();
			}
		};
		
		tvLatentHits.setOnKeyReleased(keyReleasedEventHandler);
		tvLatentHits.setOnMouseClicked(mouseEventHandler);
		
		ObservableList<ComboBoxItem<LatentJobStatus>> statusItems = FXCollections.observableArrayList();
		for(var status : LatentJobStatus.values())
		{
			String statusText;
			
			switch(status)
			{
				case NEW: statusText = resources.getString("label.new"); break;
				case IN_PROGRESS: statusText = resources.getString("label.inProgress"); break;
				case COMPLETED: statusText = resources.getString("label.done"); break;
				default: statusText = "";
			}
			
			statusItems.add(new ComboBoxItem<>(status, statusText));
		}
		
		cboStatus.setItems(statusItems);
		cboStatus.getSelectionModel().selectFirst();
		
		tcJobId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			Long jobId = latentHit.getJobId();
			if(jobId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(jobId)));
		});
		
		tcCivilBiometricsId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			Long civilBiometricsId = latentHit.getBioId();
			if(civilBiometricsId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(civilBiometricsId)));
		});
		
		tcPersonId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			Long samisId = latentHit.getSamisId();
			if(samisId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(samisId)));
		});
		
		tcReferenceNumber.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			Long tcn = latentHit.getTcn();
			if(tcn == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(tcn)));
		});
		
		tcLocationId.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			Long locationId = latentHit.getLocationId();
			if(locationId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(locationId)));
		});
		
		tcStatus.setCellValueFactory(param ->
		{
			var latentHit = param.getValue();
			var statusCode = latentHit.getStatus();
			if(statusCode == null) return null;
			
			LatentJobStatus status = LatentJobStatus.findByCode(statusCode);
			if(status == null) return null;
			
			String statusText;
			
			switch(status)
			{
				case NEW: statusText = resources.getString("label.new"); break;
				case IN_PROGRESS: statusText = resources.getString("label.inProgress"); break;
				case COMPLETED: statusText = resources.getString("label.done"); break;
				default: statusText = "";
			}
			
			return new SimpleStringProperty(statusText);
		});
		
		tcCreateDateTime.setCellValueFactory(param ->
		{
		    var latentHit = param.getValue();
		    Long createDateTime = latentHit.getCreateTime();
			if(createDateTime == null) return null;
		    return new SimpleStringProperty(AppUtils.formatHijriGregorianDateTime(createDateTime));
		});
		
		tcUpdateDateTime.setCellValueFactory(param ->
		{
		    var latentHit = param.getValue();
		    Long updateDateTime = latentHit.getUpdateTime();
		    if(updateDateTime == null) return null;
		    return new SimpleStringProperty(AppUtils.formatHijriGregorianDateTime(updateDateTime));
		});
		
		btnInquiry.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			if(request == Request.SEARCH)
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
				
				if(latentJobs != null)
				{
					tvLatentHits.getItems().setAll(latentJobs);
					tvLatentHits.requestFocus();
				}
			}
			else if(request == Request.SUBMIT_DECISION)
			{
				Integer decisionCode = decisionHistory.getDecision();
				Decision decision = Decision.findByCode(decisionCode);
				
				if(decision != null)
				{
					switch(decision)
					{
						case ASSOCIATE_LATENT:
						{
							String latentNumber = AppUtils.localizeNumbers(String.valueOf(decisionHistory.getLinkedLatentBioId()));
							String civilBiometricsId = AppUtils.localizeNumbers(String.valueOf(decisionHistory.getLinkedCivilBioID()));
							
							String message = resources.getString("linkingLatent.success.message");
							message = String.format(message, latentNumber, civilBiometricsId);
							showSuccessNotification(message);
							break;
						}
						case COMPLETE_WITHOUT_ASSOCIATING_LATENT:
						{
							String jobId = AppUtils.localizeNumbers(String.valueOf(decisionHistory.getJobId()));
							
							String message = resources.getString("finishWithoutLinkingLatent.success.message");
							message = String.format(message, jobId);
							showSuccessNotification(message);
							break;
						}
					}
				}
			}
			
			this.decisionHistory = null;
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
		
		jobId = null;
		civilBiometricsId = null;
		personId = null;
		tcn = null;
		locationId = null;
		status = null;
		createDateFrom = null;
		createDateTo = null;
		
		if(rdoOtherSearchCriteria.isSelected())
		{
			if(!txtTransactionNumber.isDisabled()) jobId = Long.parseLong(txtTransactionNumber.getText());
			if(!txtCivilBiometricsId.isDisabled()) civilBiometricsId = Long.parseLong(txtCivilBiometricsId.getText());
			if(!txtPersonId.isDisabled()) personId = Long.parseLong(txtPersonId.getText());
			if(!txtReferenceNumber.isDisabled()) tcn = Long.parseLong(txtReferenceNumber.getText());
			if(!txtLocationId.isDisabled()) locationId = Integer.parseInt(txtLocationId.getText());
			if(!txtOperatorId.isDisabled()) operatorId = Long.parseLong(txtOperatorId.getText());
			if(!cboStatus.isDisabled()) status = cboStatus.getValue().getItem();
			if(!dpCreateDateFrom.isDisabled()) createDateFrom = AppUtils.gregorianDateToSeconds(dpCreateDateFrom.getValue());
			if(!dpCreateDateTo.isDisabled()) createDateTo = AppUtils.gregorianDateToSeconds(dpCreateDateTo.getValue());
		}
		else
		{
			status = LatentJobStatus.NEW;
		}
		
		pagination.setPageCount(1);
		tvLatentHits.getItems().clear();
		recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
		pageIndex = 0;
		resultsTotalCount = null;
		latentJobs = null;
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
		
		newQuery = true;
		request = Request.SEARCH;
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
		txtOperatorId.clear();
		dpCreateDateFrom.setValue(null);
		dpCreateDateTo.setValue(null);
	}
	
	@FXML
	private void onOpenLatentHitsWindowButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		try
		{
			LatentJobDetailsDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), LatentJobDetailsDialogFxController.class, true);
			
			if(controller != null)
			{
				var selectedItem = tvLatentHits.getSelectionModel().getSelectedItem();
				controller.setJobId(selectedItem.getJobId());
				controller.setTcn(selectedItem.getTcn());
				controller.setCivilBiometricsId(selectedItem.getBioId());
				var resultOptional = controller.showAndWait();
				resultOptional.ifPresent(decisionHistory ->
				{
					request = Request.SUBMIT_DECISION;
					this.decisionHistory = decisionHistory;
					continueWorkflow();
				});
			}
		}
		catch(Exception e)
		{
			String errorCode = LatentReverseSearchErrorCodes.C018_00001.getCode();
			String[] errorDetails = {"Failed to load (" + LatentJobDetailsDialogFxController.class.getName() + ")!"};
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
				latentJobs = null;
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
				request = Request.SEARCH;
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