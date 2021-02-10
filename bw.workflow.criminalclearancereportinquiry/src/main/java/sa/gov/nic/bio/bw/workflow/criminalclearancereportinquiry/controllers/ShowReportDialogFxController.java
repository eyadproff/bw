package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.utils.CriminalClearanceReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks.BuildCriminalClearanceReportTask;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showReportDialog.fxml")
public class ShowReportDialogFxController extends ContentFxControllerBase {
    @FXML private CriminalClearanceReportFxController criminalClearanceReportPaneController;
    @FXML private ScrollPane paneReport;
    @FXML private Pane criminalClearanceReportPane;
    @FXML private Pane paneLoadingInProgress;
    @FXML private Pane paneLoadingError;
    @FXML private Pane buttonPane;

    @FXML private Label lblWatermarkExpiredReport;
    @FXML private ProgressIndicator piPrinting;
    @FXML private Button btnPrintReport;
    @FXML private Button btnSaveReportAsPDF;
    @FXML private Dialog<ButtonType> dialog;

    private FileChooser fileChooser = new FileChooser();
    private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();

    private CriminalClearanceReport criminalClearanceReport;
    private Map<Integer, String> fingerprintBase64Images;

    @Override
    protected void initialize() {

        fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
        FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
                resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
        fileChooser.getExtensionFilters().addAll(extFilterPDF);

        dialog.setOnShown(event ->
        {

            segmentFingerprints();

            // workaround to center buttons and remove extra spaces
            ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
            buttonBar.getButtons().setAll(buttonPane);
            HBox hBox = (HBox) buttonBar.lookup(".container");
            hBox.setPadding(new Insets(0.0, 10.0, 10.0, 10.0));
            hBox.getChildren().remove(0);

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.sizeToScene();
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        });
    }


    private void segmentFingerprints() {

        List<Finger> subjFingers = criminalClearanceReport.getFingers();

        // return null from MW if it's empty and task ConvertWsq doesn't accept null value
        // We create empty list if it's null
        List<Integer> subjMissingFingers = new ArrayList<>();
        List<Integer> missingFingers = criminalClearanceReport.getMissingFingers();
        if (missingFingers != null) { subjMissingFingers.addAll(missingFingers); }

        setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                "fingerprints", subjFingers);
        setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                "missingFingerprints", subjMissingFingers);

        boolean success = executeUiTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                new SuccessHandler() {
                    @Override
                    protected void onSuccess() {
                        fingerprintBase64Images = getData("fingerprintBase64Images");
                        populateData();
                    }
                }, throwable ->
                {
                    GuiUtils.showNode(paneLoadingInProgress, false);
                    GuiUtils.showNode(paneLoadingError, true);

                    String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00001.getCode();
                    String[] errorDetails = {"failed to load the fingerprints for report number ( " +
                                             criminalClearanceReport.getReportNumber() + ")"};
                    Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                });

        if (!success) {
            GuiUtils.showNode(paneLoadingInProgress, false);
            GuiUtils.showNode(paneLoadingError, true);
        }

    }

    private void populateData() {
        if (criminalClearanceReport.getExpireDate() >= Instant.now().getEpochSecond()) {
            btnPrintReport.setDisable(false);
            btnSaveReportAsPDF.setDisable(false);
        }
        else {
            GuiUtils.showNode(lblWatermarkExpiredReport, true);
        }

        criminalClearanceReportPaneController.populateCriminalClearanceReportData(criminalClearanceReport, fingerprintBase64Images);

        GuiUtils.showNode(paneLoadingInProgress, false);
        GuiUtils.showNode(paneReport, true);
    }

    public void setCriminalClearanceReport(CriminalClearanceReport criminalClearanceReport) {
        this.criminalClearanceReport = criminalClearanceReport;
    }

    public void show() {
        dialog.show();
    }

    @FXML
    private void onPrintReportButtonClicked(ActionEvent actionEvent) {
        GuiUtils.showNode(btnPrintReport, false);
        GuiUtils.showNode(btnSaveReportAsPDF, false);
        GuiUtils.showNode(piPrinting, true);

        if (jasperPrint.get() == null) {
            BuildCriminalClearanceReportTask buildCriminalClearanceReportTask = new BuildCriminalClearanceReportTask(criminalClearanceReport,
                    fingerprintBase64Images);
            buildCriminalClearanceReportTask.setOnSucceeded(event ->
            {
                JasperPrint value = buildCriminalClearanceReportTask.getValue();
                jasperPrint.set(value);
                printConvictedReport(value);
            });
            buildCriminalClearanceReportTask.setOnFailed(event ->
            {
                GuiUtils.showNode(piPrinting, false);
                GuiUtils.showNode(btnPrintReport, true);
                GuiUtils.showNode(btnSaveReportAsPDF, true);

                Throwable exception = buildCriminalClearanceReportTask.getException();

                String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00002.getCode();
                String[] errorDetails = {"failed while building the convicted report!"};
                Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
            });
            Context.getExecutorService().submit(buildCriminalClearanceReportTask);
        }
        else { printConvictedReport(jasperPrint.get()); }
    }

    @FXML
    private void onSaveReportAsPdfButtonClicked(ActionEvent actionEvent) {
        File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

        if (selectedFile != null) {
            GuiUtils.showNode(btnPrintReport, false);
            GuiUtils.showNode(btnSaveReportAsPDF, false);
            GuiUtils.showNode(piPrinting, true);

            if (jasperPrint.get() == null) {
                BuildCriminalClearanceReportTask buildCriminalClearanceReportTask = new BuildCriminalClearanceReportTask(criminalClearanceReport,
                        fingerprintBase64Images);
                buildCriminalClearanceReportTask.setOnSucceeded(event ->
                {
                    JasperPrint value = buildCriminalClearanceReportTask.getValue();
                    jasperPrint.set(value);
                    try {
                        saveConvictedReportAsPDF(value, selectedFile);
                    } catch (Exception e) {
                        GuiUtils.showNode(piPrinting, false);
                        GuiUtils.showNode(btnPrintReport, true);
                        GuiUtils.showNode(btnSaveReportAsPDF, true);

                        String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00003.getCode();
                        String[] errorDetails = {"failed while saving the convicted report as PDF!"};
                        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
                    }
                });
                buildCriminalClearanceReportTask.setOnFailed(event ->
                {
                    GuiUtils.showNode(piPrinting, false);
                    GuiUtils.showNode(btnPrintReport, true);
                    GuiUtils.showNode(btnSaveReportAsPDF, true);

                    Throwable exception = buildCriminalClearanceReportTask.getException();

                    String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00004.getCode();
                    String[] errorDetails = {"failed while building the convicted report!"};
                    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
                });
                Context.getExecutorService().submit(buildCriminalClearanceReportTask);
            }
            else {
                try {
                    saveConvictedReportAsPDF(jasperPrint.get(), selectedFile);
                } catch (Exception e) {
                    GuiUtils.showNode(piPrinting, false);
                    GuiUtils.showNode(btnPrintReport, true);
                    GuiUtils.showNode(btnSaveReportAsPDF, true);

                    String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00005.getCode();
                    String[] errorDetails = {"failed while saving the convicted report as PDF!"};
                    Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
                }
            }
        }
    }

    private void printConvictedReport(JasperPrint jasperPrint) {
        PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
        printReportTask.setOnSucceeded(event ->
        {
            GuiUtils.showNode(piPrinting, false);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);
        });
        printReportTask.setOnFailed(event ->
        {
            GuiUtils.showNode(piPrinting, false);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);

            Throwable exception = printReportTask.getException();

            String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00006.getCode();
            String[] errorDetails = {"failed while printing the convicted report!"};
            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
        });
        Context.getExecutorService().submit(printReportTask);
    }

    private void saveConvictedReportAsPDF(JasperPrint jasperPrint, File selectedFile) throws FileNotFoundException {
        SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
                new FileOutputStream(selectedFile));
        printReportTaskAsPdfTask.setOnSucceeded(event ->
        {
            GuiUtils.showNode(piPrinting, false);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);

            String title = resources.getString("showConvictedReport.savingAsPDF.success.title");
            String contentText = resources.getString("showCriminalClearanceReport.savingAsPDF.success.message");
            String buttonText = resources.getString("showConvictedReport.savingAsPDF.success.button");
            boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

            try {
                Desktop.getDesktop().open(selectedFile);
            } catch (Exception e) {
                LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
            }

            DialogUtils.showInformationDialog(stage, Context.getCoreFxController(), title, null, contentText,
                    buttonText, rtl);
        });
        printReportTaskAsPdfTask.setOnFailed(event ->
        {
            GuiUtils.showNode(piPrinting, false);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);

            Throwable exception = printReportTaskAsPdfTask.getException();

            String errorCode = CriminalClearanceReportInquiryErrorCodes.C022_00007.getCode();
            String[] errorDetails = {"failed while saving the convicted report as PDF!"};
            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
        });
        Context.getExecutorService().submit(printReportTaskAsPdfTask);
    }
}