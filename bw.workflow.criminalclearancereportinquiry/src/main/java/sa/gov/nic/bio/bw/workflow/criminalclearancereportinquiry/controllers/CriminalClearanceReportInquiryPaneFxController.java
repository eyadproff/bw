package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.controllers;

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
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.utils.*;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.utils.CriminalClearanceReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;

import java.time.Instant;
import java.util.List;

@FxmlFile("criminalClearanceReportInquiry.fxml")
public class CriminalClearanceReportInquiryPaneFxController extends ContentFxControllerBase
{
	@Input private Integer resultsTotalCount;
	@Input private List<CriminalClearanceReport> criminalClearanceReports;

	@Output private Long reportNumber;
	@Output private Long personId;
//	@Output private Integer pageIndex;
	@Output private ServiceType serviceType;

	@FXML private TitledPane tpSearchResults;
//	@FXML private Pane paneTable;
	@FXML private CheckBox cbShowExpiredReports;
	@FXML private TextField txtReportNumber;
	@FXML private TextField txtPersonId;
	@FXML private RadioButton rdoReportNumber;
	@FXML private RadioButton rdoPersonId;
//	@FXML private Pagination pagination;
	@FXML private TableView<CriminalClearanceReport> tvCriminalClearanceReports;
	@FXML private TableColumn<CriminalClearanceReport, String> tcSequence;
	@FXML private TableColumn<CriminalClearanceReport, String> tcName;
	@FXML private TableColumn<CriminalClearanceReport, String> tcPersonId;
	@FXML private TableColumn<CriminalClearanceReport, String> tcPassportId;
	@FXML private TableColumn<CriminalClearanceReport, String> tcReportId;
	@FXML private TableColumn<CriminalClearanceReport, String> tcNationality;
	@FXML private TableColumn<CriminalClearanceReport, String> tcRequestedBy;
	@FXML private TableColumn<CriminalClearanceReport, String> tcPurposeOfTheReport;
	@FXML private TableColumn<CriminalClearanceReport, String> tcRegistrationDate;
	@FXML private TableColumn<CriminalClearanceReport, String> tcExpireDate;
	@FXML private Label lblCriminalClearanceReportsPlaceHolder;
	@FXML private ProgressIndicator piCriminalClearanceReportsPlaceHolder;
	@FXML private Button btnInquiry;
	@FXML private Button btnClearFields;
	@FXML private Button btnShowReport;

	private Node paginationControlBox;
	private boolean tableInitialized = false;
	private boolean newQuery = false;
	private BooleanProperty disableInquiryButtonProperty = new SimpleBooleanProperty(false);


	public enum ServiceType{
		BY_REPORT_NUMBER,
		BY_ID_NUMBER
	}

	@Override
	protected void onAttachedToScene()
	{
		txtReportNumber.disableProperty().bind(rdoReportNumber.selectedProperty().not());
		txtPersonId.disableProperty().bind(rdoPersonId.selectedProperty().not());

		BooleanBinding reportNumberBinding = createTextFieldNotCompleteBooleanBinding(txtReportNumber);
		BooleanBinding personIdBinding = createTextFieldNotCompleteBooleanBinding(txtPersonId,10);

		btnInquiry.disableProperty().bind(rdoReportNumber.selectedProperty().and(reportNumberBinding)
                      .or(rdoPersonId.selectedProperty().and(personIdBinding)));


		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");

//		pagination.setMaxPageIndicatorCount(AppConstants.TABLE_PAGINATION_PAGES_PER_ITERATION);
//		GuiUtils.localizePagination(pagination);
//		pagination.setPageFactory(this::createPage);
//		paginationControlBox = pagination.lookup(".control-box");
//		pagination.addEventFilter(KeyEvent.KEY_PRESSED, event ->
//		{
//			// disable controlling the pagination by keyboard
//			if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) event.consume();
//		});

		GuiUtils.applyValidatorToTextField(txtReportNumber, "\\d*", "[^\\d]",
		                                   18);
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);


		btnShowReport.disableProperty().bind(Bindings.size(tvCriminalClearanceReports.getSelectionModel()
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

		tvCriminalClearanceReports.setOnKeyReleased(keyReleasedEventHandler);
		tvCriminalClearanceReports.setOnMouseClicked(mouseEventHandler);



		tcSequence.setCellValueFactory(param ->
		{
			CriminalClearanceReport criminalClearanceReport = param.getValue();
			int sequence = criminalClearanceReport.getSequence();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(sequence)));
		});

		tcSequence.setCellFactory(column -> new TableCell<>()
		{
			@Override
			protected void updateItem(String item, boolean empty)
			{
				super.updateItem(item, empty);

				setText(empty ? "" : getItem());
				setGraphic(null);

				TableRow<CriminalClearanceReport> currentRow = getTableRow();

				CriminalClearanceReport criminalClearanceReport = currentRow.getItem();

				currentRow.getStyleClass().remove("active-record");
				currentRow.getStyleClass().remove("old-record");

				if(!isEmpty() && criminalClearanceReport != null)
				{
					if(criminalClearanceReport.getExpireDate() >= Instant.now().getEpochSecond())
					{
						currentRow.getStyleClass().add("active-record");
					}
					else
					{
						currentRow.getStyleClass().add("old-record");
					}

				}

				currentRow.applyCss();
			}
		});

		tcName.setCellValueFactory(param ->
		{
		    CriminalClearanceReport criminalClearanceReport = param.getValue();
		    Name name = criminalClearanceReport.getFullName();
		    return new SimpleStringProperty(AppUtils.constructName(name));
		});

		tcPersonId.setCellValueFactory(param ->
		{
		    CriminalClearanceReport criminalClearanceReport = param.getValue();
		    Long subjSamisId = criminalClearanceReport.getSamisId();
		    if(subjSamisId == null || subjSamisId == 0) return null;
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(subjSamisId)));
		});

		tcPassportId.setCellValueFactory(param ->
		{
			CriminalClearanceReport criminalClearanceReport = param.getValue();
			String subjPassportId = criminalClearanceReport.getPassportNumber();
			if(subjPassportId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(subjPassportId));
		});

		tcReportId.setCellValueFactory(param ->
		{
			CriminalClearanceReport criminalClearanceReport = param.getValue();
			Long subjReportId = criminalClearanceReport.getReportNumber();
			if(subjReportId == null) return null;
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(subjReportId)));
		});

		tcNationality.setCellValueFactory(param ->
		{
		    CriminalClearanceReport criminalClearanceReport = param.getValue();

			@SuppressWarnings("unchecked")
			List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);

			Integer nationalityCode = criminalClearanceReport.getNationality();
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

		tcRequestedBy.setCellValueFactory(param ->
		{
			CriminalClearanceReport criminalClearanceReport = param.getValue();
			String requestedName = criminalClearanceReport.getRequestedName();
			return new SimpleStringProperty(requestedName);
		});

		tcPurposeOfTheReport.setCellValueFactory(param ->
		{
			CriminalClearanceReport criminalClearanceReport = param.getValue();
			String requestedName = criminalClearanceReport.getReason();
			return new SimpleStringProperty(requestedName);
		});

		tcRegistrationDate.setCellValueFactory(param ->
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		    CriminalClearanceReport criminalClearanceReport = param.getValue();
			Long reportDate = criminalClearanceReport.getCreateDate();
			if(reportDate == null) return null;
			return new SimpleStringProperty(AppUtils.formatHijriDateSimple(reportDate, rtl));
		});

		tcExpireDate.setCellValueFactory(param ->
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			CriminalClearanceReport criminalClearanceReport = param.getValue();
			Long expireDate = criminalClearanceReport.getExpireDate();
			if(expireDate == null) return null;
			return new SimpleStringProperty(AppUtils.formatHijriDateSimple(expireDate, rtl));
		});

		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();

		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}

		txtReportNumber.requestFocus();
	}

	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			piCriminalClearanceReportsPlaceHolder.setVisible(false);
			lblCriminalClearanceReportsPlaceHolder.setVisible(true);

			if(resultsTotalCount != null)
			{
				tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
						                            AppUtils.localizeNumbers(String.valueOf(resultsTotalCount)) + ")");

				int pageCount = (resultsTotalCount - 1) / AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE + 1;
//				if(pageIndex > pageCount) pageIndex = pageCount;
				Platform.runLater(() ->
				{
//					pagination.setPageCount(pageCount);
//					pagination.setCurrentPageIndex(pageIndex);

//					if(newQuery) newQuery = false;
				});
			}

			if(criminalClearanceReports != null)
			{
				if(!cbShowExpiredReports.isSelected())
				criminalClearanceReports.removeIf(x-> x.getExpireDate() < Instant.now().getEpochSecond());

				for(int i = 0; i < criminalClearanceReports.size(); i++)
				{
					int sequence = i + 1;
					CriminalClearanceReport criminalClearanceReport = criminalClearanceReports.get(i);
					criminalClearanceReport.setSequence(sequence);
				}

				tvCriminalClearanceReports.getItems().setAll(criminalClearanceReports);
				tvCriminalClearanceReports.requestFocus();
			}
		}
	}

	@Override
	public void onShowingProgress(boolean bShow)
	{
		if(paginationControlBox != null) paginationControlBox.setDisable(bShow);
		disableInquiryButtonProperty.setValue(bShow);
		piCriminalClearanceReportsPlaceHolder.setVisible(bShow);
		lblCriminalClearanceReportsPlaceHolder.setVisible(!bShow);
	}

	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		if(btnInquiry.isDisabled() || !btnInquiry.isVisible()) return;

		reportNumber = null;
		personId = null;

		if(rdoReportNumber.isSelected())
		{
			if(!txtReportNumber.isDisabled()) reportNumber = Long.parseLong(txtReportNumber.getText());
			serviceType= ServiceType.BY_REPORT_NUMBER;
		}
		else
		{
			personId = Long.parseLong(txtPersonId.getText());
			serviceType= ServiceType.BY_ID_NUMBER;
		}

//		pagination.setPageCount(1);
		tvCriminalClearanceReports.getItems().clear();
//		recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
//		pageIndex = 0;
		resultsTotalCount = null;
		criminalClearanceReports = null;
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" + AppUtils.localizeNumbers("0") + ")");

		newQuery = true;
		continueWorkflow();
	}

	@FXML
	private void onClearFieldsButtonClicked(ActionEvent actionEvent)
	{
		txtReportNumber.clear();
		txtPersonId.clear();

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
				CriminalClearanceReport selectedItem = tvCriminalClearanceReports.getSelectionModel().getSelectedItem();
				controller.setCriminalClearanceReport(selectedItem);
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00008.getCode();
			String[] errorDetails = {"Failed to load (" + ShowReportDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
		}
	}

//	private Node createPage(int pageIndex)
//	{
//		if(tableInitialized)
//		{
//			if(!newQuery)
//			{
//				tvCriminalClearanceReports.getItems().clear();
////				recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
//				this.pageIndex = pageIndex;
//				resultsTotalCount = null;
//				criminalClearanceReports = null;
//				tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
//					                                                        AppUtils.localizeNumbers("0") + ")");
//				continueWorkflow();
//			}
//		}
//		else tableInitialized = true;
//
//		return paneTable;
//	}

	private static BooleanBinding createTextFieldNotCompleteBooleanBinding(TextField textField)
	{
		return textField.disabledProperty().not().and(
						Bindings.createBooleanBinding(() -> textField.getText().isBlank(), textField.textProperty()));
	}
	private static BooleanBinding createTextFieldNotCompleteBooleanBinding(TextField textField , int minCharCount)
	{
		return textField.disabledProperty().not().and(
				Bindings.createBooleanBinding(() -> textField.getText().isBlank() || textField.getText().length() < minCharCount, textField.textProperty()));
	}

}