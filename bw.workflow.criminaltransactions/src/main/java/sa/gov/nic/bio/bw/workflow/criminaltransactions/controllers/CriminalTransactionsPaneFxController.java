package sa.gov.nic.bio.bw.workflow.criminaltransactions.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransaction;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.beans.CriminalTransactionType;
import sa.gov.nic.bio.bw.workflow.criminaltransactions.lookups.CriminalTransactionTypesLookup;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@FxmlFile("criminalRecordsTransactions.fxml")
public class CriminalTransactionsPaneFxController extends ContentFxControllerBase
{
	@Input private Integer resultsTotalCount;
	@Input private List<CriminalTransaction> criminalTransactions;
	@Output private Long criminalBiometricsId;
	
	@FXML private TitledPane tpSearchResults;
	@FXML private Pane paneTable;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private TableView<CriminalTransaction> tvCriminalTransactions;
	@FXML private TableColumn<CriminalTransaction, String> tcSequence;
	@FXML private TableColumn<CriminalTransaction, String> tcTransactionType;
	@FXML private TableColumn<CriminalTransaction, String> tcTransactionId;
	@FXML private TableColumn<CriminalTransaction, String> tcDateAndTime;
	@FXML private TableColumn<CriminalTransaction, String> tcOperatorId;
	@FXML private TableColumn<CriminalTransaction, String> tcLocation;
	@FXML private TableColumn<CriminalTransaction, String> tcConvictedReportId;
	@FXML private TableColumn<CriminalTransaction, String> tcResult;
	@FXML private Label lblCriminalTransactionsPlaceHolder;
	@FXML private ProgressIndicator piCriminalTransactionsPlaceHolder;
	@FXML private Button btnInquiry;
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;
	
	private BooleanProperty disableInquiryButtonProperty = new SimpleBooleanProperty(false);
	
	@Override
	protected void onAttachedToScene()
	{
		BooleanBinding criminalBiometricsIdBinding = createTextFieldNotCompleteBooleanBinding(txtCriminalBiometricsId);
		btnInquiry.disableProperty().bind(criminalBiometricsIdBinding.or(disableInquiryButtonProperty));
			
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");
		
		GuiUtils.applyValidatorToTextField(txtCriminalBiometricsId, "\\d*", "[^\\d]", 10);
		
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		@SuppressWarnings("unchecked")
		var criminalTransactionTypes = (List<CriminalTransactionType>)
														Context.getUserSession().getAttribute(CriminalTransactionTypesLookup.KEY);
		
		var criminalTransactionTypesMap = criminalTransactionTypes.stream()
		                                                          .collect(Collectors.toMap(CriminalTransactionType::getCode, Function.identity()));
		
		var successLabel = resources.getString("label.success");
		var failureLabel = resources.getString("label.failure");
		
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
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
						                            AppUtils.localizeNumbers(String.valueOf(resultsTotalCount)) + ")");
			}
			
			if(criminalTransactions != null)
			{
				for(int i = 0; i < criminalTransactions.size(); i++)
				{
					int sequence = i + 1;
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
		disableInquiryButtonProperty.setValue(bShow);
		piCriminalTransactionsPlaceHolder.setVisible(bShow);
		lblCriminalTransactionsPlaceHolder.setVisible(!bShow);
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		if(btnInquiry.isDisabled() || !btnInquiry.isVisible()) return;
		
		criminalBiometricsId = null;
		tvCriminalTransactions.getItems().clear();
		resultsTotalCount = null;
		criminalTransactions = null;
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
				                                                            AppUtils.localizeNumbers("0") + ")");
		
		continueWorkflow();
	}
	
	private static BooleanBinding createTextFieldNotCompleteBooleanBinding(TextField textField)
	{
		return textField.disabledProperty().not().and(
						Bindings.createBooleanBinding(() -> textField.getText().isBlank(), textField.textProperty()));
	}
}