package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.BuildCriminalClearanceReportTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.utils.RegisterCriminalClearanceErrorCodes;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showReport.fxml")
public class ShowReportPaneFxController extends WizardStepFxControllerBase {
    @Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
    @Input(alwaysRequired = true) private CriminalClearanceReport criminalClearanceReport;
    @Input(alwaysRequired = true) private PersonInfo personInfo;
    @Input(alwaysRequired = true) private HashMap<String, Object> criminalClearanceResponse;

    @FXML private TextField txtReportNumber;
    @FXML private ProgressIndicator piProgress;
    @FXML private Button btnStartOver;
    @FXML private Button btnPrintReport;
    @FXML private Button btnSaveReportAsPDF;

    private FileChooser fileChooser = new FileChooser();
    private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();

    @Override
    protected void onAttachedToScene() {
        fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
        FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
                resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
        fileChooser.getExtensionFilters().addAll(extFilterPDF);

        txtReportNumber.setText(String.valueOf(((Number) criminalClearanceResponse.get("reportNumber")).longValue()));

        criminalClearanceReport.setReportNumber(((Number) criminalClearanceResponse.get("reportNumber")).longValue());

    }

    @FXML
    private void onPrintReportButtonClicked(ActionEvent actionEvent) {
        hideNotification();
        GuiUtils.showNode(btnStartOver, false);
        GuiUtils.showNode(btnPrintReport, false);
        GuiUtils.showNode(btnSaveReportAsPDF, false);
        GuiUtils.showNode(piProgress, true);

		if (jasperPrint.get() == null) {
			LocalDate expireDate = (LocalDate) criminalClearanceResponse.get("expireDate");
			var buildCriminalClearanceReportTask = new BuildCriminalClearanceReportTask(criminalClearanceReport,
					fingerprintBase64Images, personInfo, expireDate);
			buildCriminalClearanceReportTask.setOnSucceeded(event ->
			{
				JasperPrint value = buildCriminalClearanceReportTask.getValue();
				jasperPrint.set(value);
				printCriminalClearanceReport(value);
			});
			buildCriminalClearanceReportTask.setOnFailed(event ->
			{
				GuiUtils.showNode(piProgress, false);
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(btnPrintReport, true);
				GuiUtils.showNode(btnSaveReportAsPDF, true);

				Throwable exception = buildCriminalClearanceReportTask.getException();

				String errorCode = RegisterCriminalClearanceErrorCodes.C020_00001.getCode();
				String[] errorDetails = {"failed while building the criminal clearance report!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			});
			Context.getExecutorService().submit(buildCriminalClearanceReportTask);
		}
		else { printCriminalClearanceReport(jasperPrint.get()); }
    }

    @FXML
    private void onSaveReportAsPdfButtonClicked(ActionEvent actionEvent) {
        hideNotification();
        File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

        if (selectedFile != null) {
            GuiUtils.showNode(btnStartOver, false);
            GuiUtils.showNode(btnPrintReport, false);
            GuiUtils.showNode(btnSaveReportAsPDF, false);
            GuiUtils.showNode(piProgress, true);

            if (jasperPrint.get() == null) {
                LocalDate expireDate = (LocalDate) criminalClearanceResponse.get("expireDate");
                var buildCriminalClearanceReportTask = new BuildCriminalClearanceReportTask(criminalClearanceReport,
                        fingerprintBase64Images, personInfo, expireDate);
                buildCriminalClearanceReportTask.setOnSucceeded(event ->
                {
                    JasperPrint value = buildCriminalClearanceReportTask.getValue();
                    jasperPrint.set(value);
                    try {
                        saveCriminalClearanceReportAsPDF(value, selectedFile);
                    } catch (Exception e) {
                        GuiUtils.showNode(piProgress, false);
                        GuiUtils.showNode(btnStartOver, true);
                        GuiUtils.showNode(btnPrintReport, true);
                        GuiUtils.showNode(btnSaveReportAsPDF, true);

                        String errorCode = RegisterCriminalClearanceErrorCodes.C020_00002.getCode();
                        String[] errorDetails = {"failed while saving the criminal clearance report as PDF!"};
                        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
                    }
                });
                buildCriminalClearanceReportTask.setOnFailed(event ->
                {
                    GuiUtils.showNode(piProgress, false);
                    GuiUtils.showNode(btnStartOver, true);
                    GuiUtils.showNode(btnPrintReport, true);
                    GuiUtils.showNode(btnSaveReportAsPDF, true);

                    Throwable exception = buildCriminalClearanceReportTask.getException();

                    String errorCode = RegisterCriminalClearanceErrorCodes.C020_00003.getCode();
                    String[] errorDetails = {"failed while building the criminal clearance report!"};
                    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
                });
                Context.getExecutorService().submit(buildCriminalClearanceReportTask);
            }
            else {
                try {
                    saveCriminalClearanceReportAsPDF(jasperPrint.get(), selectedFile);
                } catch (Exception e) {
                    GuiUtils.showNode(piProgress, false);
                    GuiUtils.showNode(btnStartOver, true);
                    GuiUtils.showNode(btnPrintReport, true);
                    GuiUtils.showNode(btnSaveReportAsPDF, true);

                    String errorCode = RegisterCriminalClearanceErrorCodes.C020_00004.getCode();
                    String[] errorDetails = {"failed while saving the criminal clearance report as PDF!"};
                    Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
                }
            }
        }
    }

    private void printCriminalClearanceReport(JasperPrint jasperPrint) {
        PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
        printReportTask.setOnSucceeded(event ->
        {
            GuiUtils.showNode(piProgress, false);
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);
        });
        printReportTask.setOnFailed(event ->
        {
            GuiUtils.showNode(piProgress, false);
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);

            Throwable exception = printReportTask.getException();

            String errorCode = RegisterCriminalClearanceErrorCodes.C020_00005.getCode();
            String[] errorDetails = {"failed while printing the criminal clearance report!"};
            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
        });
        Context.getExecutorService().submit(printReportTask);
    }

    private void saveCriminalClearanceReportAsPDF(JasperPrint jasperPrint, File selectedFile) throws FileNotFoundException {
        SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
                new FileOutputStream(selectedFile));
        printReportTaskAsPdfTask.setOnSucceeded(event ->
        {
            GuiUtils.showNode(piProgress, false);
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);

            showSuccessNotification(resources.getString("printCriminalClearanceReport.savingAsPDF.success.message"));

            try {
                Desktop.getDesktop().open(selectedFile);
            } catch (Exception e) {
                LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
            }
        });
        printReportTaskAsPdfTask.setOnFailed(event ->
        {
            GuiUtils.showNode(piProgress, false);
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintReport, true);
            GuiUtils.showNode(btnSaveReportAsPDF, true);

            Throwable exception = printReportTaskAsPdfTask.getException();

            String errorCode = RegisterCriminalClearanceErrorCodes.C020_00006.getCode();
            String[] errorDetails = {"failed while saving the criminal clearance report as PDF!"};
            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
        });
        Context.getExecutorService().submit(printReportTaskAsPdfTask);
    }
}