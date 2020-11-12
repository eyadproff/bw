package sa.gov.nic.bio.bw.workflow.biometricsverification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.*;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.biometricsverification.tasks.BuildVerificationReportTask;
import sa.gov.nic.bio.bw.workflow.biometricsverification.utils.BiometricsVerificationtErrorCodes;
import sa.gov.nic.bio.bw.workflow.commons.beans.*;
import sa.gov.nic.bio.bw.workflow.biometricsverification.controllers.VerificationMethodSelectionFxController.VerificationMethod;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showResult.fxml")
public class ShowResultFxController extends WizardStepFxControllerBase {
    @Input(alwaysRequired = true) private VerificationMethod verificationMethod;
    @Input(alwaysRequired = true) private Long personId;
    @Input private String facePhotoBase64;
    @Input(alwaysRequired = true) private PersonInfo personInfo;

    @FXML private ImageViewPane paneImageView;
    @FXML private ImageView ivPersonPhoto;
    @FXML private Pane matchedPane;
    @FXML private Pane infoPane;
    @FXML private Label lblPersonId;
    @FXML private Label lblFirstName;
    @FXML private Label lblFatherName;
    @FXML private Label lblGrandfatherName;
    @FXML private Label lblFamilyName;
    @FXML private Label lblGender;
    @FXML private Label lblNationality;
    @FXML private Label lblOutOfKingdom;
    @FXML private Button btnStartOver;
    @FXML private Button btnPrintRecord;
    @FXML private Button btnSaveRecordAsPDF;

    private NormalizedPersonInfo normalizedPersonInfo;
    private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
    private FileChooser fileChooser = new FileChooser();

    @Override
    protected void onAttachedToScene() {

        fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
        FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
                resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
        fileChooser.getExtensionFilters().addAll(extFilterPDF);

        long samisId = personInfo.getSamisId();
        Name name = personInfo.getName();
        String firstName = name.getFirstName();
        String fatherName = name.getFatherName();
        String grandfatherName = name.getGrandfatherName();
        String familyName = name.getFamilyName();

        Gender gender = Gender.values()[personInfo.getGender() - 1];
        GuiUtils.attachFacePhotoBase64(ivPersonPhoto, personInfo.getFace(), true, gender);
        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
                resources.getString("label.personPhoto"),
                resources.getString("label.contextMenu.showImage"), false);

        GuiUtils.showNode(matchedPane, true);
        GuiUtils.showNode(infoPane, true);

        if (firstName != null && (firstName.trim().isEmpty() || firstName.trim().equals("-"))) { firstName = null; }
        if (fatherName != null && (fatherName.trim().isEmpty() || fatherName.trim().equals("-"))) { fatherName = null; }
        if (grandfatherName != null && (grandfatherName.trim().isEmpty() || grandfatherName.trim().equals("-"))) {
            grandfatherName = null;
        }
        if (familyName != null && (familyName.trim().isEmpty() || familyName.trim().equals("-"))) { familyName = null; }

        if (samisId > 0) {
            String sSamisId = AppUtils.localizeNumbers(String.valueOf(samisId));
            lblPersonId.setText(sSamisId);
        }
        else {
            lblPersonId.setText(resources.getString("label.notAvailable"));
            lblPersonId.setTextFill(Color.RED);
        }

        if (firstName != null) { lblFirstName.setText(firstName); }
        else {
            lblFirstName.setText(resources.getString("label.notAvailable"));
            lblFirstName.setTextFill(Color.RED);
        }

        if (fatherName != null) { lblFatherName.setText(fatherName); }
        else {
            lblFatherName.setText(resources.getString("label.notAvailable"));
            lblFatherName.setTextFill(Color.RED);
        }

        if (grandfatherName != null) { lblGrandfatherName.setText(grandfatherName); }
        else {
            lblGrandfatherName.setText(resources.getString("label.notAvailable"));
            lblGrandfatherName.setTextFill(Color.RED);
        }

        if (familyName != null) { lblFamilyName.setText(familyName); }
        else {
            lblFamilyName.setText(resources.getString("label.notAvailable"));
            lblFamilyName.setTextFill(Color.RED);
        }


        lblGender.setText(gender == Gender.MALE ? resources.getString("label.male") :
                          resources.getString("label.female"));

        @SuppressWarnings("unchecked")
        List<Country> countries = (List<Country>)
                Context.getUserSession().getAttribute(CountriesLookup.KEY);

        Country countryBean = null;

        for (Country country : countries) {
            if (country.getCode() == personInfo.getNationality()) {
                countryBean = country;
                break;
            }
        }

        if (countryBean != null) {
            boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
            lblNationality.setText(arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN());
        }
        else {
            lblNationality.setText(resources.getString("label.notAvailable"));
            lblNationality.setTextFill(Color.RED);
        }

        Boolean outOfKingdom = personInfo.isOut();
        if (outOfKingdom != null) {
            lblOutOfKingdom.setText(outOfKingdom ? resources.getString("label.yes") :
                                    resources.getString("label.no"));
        }
        else {
            lblOutOfKingdom.setText(resources.getString("label.notAvailable"));
            lblOutOfKingdom.setTextFill(Color.RED);
        }
    }

    @FXML
    private void onPrintRecordButtonClicked(ActionEvent actionEvent) {
        normalizedPersonInfo = new NormalizedPersonInfo(personInfo);

        hideNotification();
        GuiUtils.showNode(btnStartOver, false);
        GuiUtils.showNode(btnPrintRecord, false);
        GuiUtils.showNode(btnSaveRecordAsPDF, false);

        if (jasperPrint.get() == null) {
            String verificationMethodToString="";
            if (verificationMethod.equals(VerificationMethod.FACE_PHOTO))
                verificationMethodToString=resources.getString("%label.byFacePhoto");
            if (verificationMethod.equals(VerificationMethod.FINGERPRINT))
                verificationMethodToString=resources.getString("%label.byFingerprint");

                BuildVerificationReportTask buildVerificationReportTask =
                        new BuildVerificationReportTask(normalizedPersonInfo, verificationMethodToString, facePhotoBase64);


            buildVerificationReportTask.setOnSucceeded(event ->
            {
                JasperPrint value = buildVerificationReportTask.getValue();
                jasperPrint.set(value);
                printReport(value);
            });
            buildVerificationReportTask.setOnFailed(event ->
            {
                GuiUtils.showNode(btnStartOver, true);
                GuiUtils.showNode(btnPrintRecord, true);
                GuiUtils.showNode(btnSaveRecordAsPDF, true);

                Throwable exception = buildVerificationReportTask.getException();

                String errorCode = BiometricsVerificationtErrorCodes.C019_00001.getCode();
                String[] errorDetails = {"failed while building the verification report!"};
                Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
            });
            Context.getExecutorService().submit(buildVerificationReportTask);
        }
        else { printReport(jasperPrint.get()); }
    }

    @FXML
    private void onSaveRecordAsPdfButtonClicked(ActionEvent actionEvent) {
        normalizedPersonInfo = new NormalizedPersonInfo(personInfo);

        hideNotification();
        File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

        if (selectedFile != null) {
            GuiUtils.showNode(btnStartOver, false);
            GuiUtils.showNode(btnPrintRecord, false);
            GuiUtils.showNode(btnSaveRecordAsPDF, false);

            if (jasperPrint.get() == null) {
                String verificationMethodToString="";
                if (verificationMethod.equals(VerificationMethod.FACE_PHOTO))
                    verificationMethodToString=resources.getString("label.FacePhoto");
                if (verificationMethod.equals(VerificationMethod.FINGERPRINT))
                    verificationMethodToString=resources.getString("label.Fingerprint");

                BuildVerificationReportTask buildVerificationReportTask =
                        new BuildVerificationReportTask(normalizedPersonInfo, verificationMethodToString, facePhotoBase64);
                buildVerificationReportTask.setOnSucceeded(event ->
                {
                    JasperPrint value = buildVerificationReportTask.getValue();
                    jasperPrint.set(value);
                    try {
                        saveReportAsPDF(value, selectedFile);
                    } catch (Exception e) {
                        GuiUtils.showNode(btnStartOver, true);
                        GuiUtils.showNode(btnPrintRecord, true);
                        GuiUtils.showNode(btnSaveRecordAsPDF, true);

                        String errorCode = BiometricsVerificationtErrorCodes.C019_00002.getCode();
                        String[] errorDetails = {"failed while saving the verification report as PDF!"};
                        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
                    }
                });
                buildVerificationReportTask.setOnFailed(event ->
                {
                    GuiUtils.showNode(btnStartOver, true);
                    GuiUtils.showNode(btnPrintRecord, true);
                    GuiUtils.showNode(btnSaveRecordAsPDF, true);

                    Throwable exception = buildVerificationReportTask.getException();

                    String errorCode = BiometricsVerificationtErrorCodes.C019_00003.getCode();
                    String[] errorDetails = {"failed while building the verification report!"};
                    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
                });
                Context.getExecutorService().submit(buildVerificationReportTask);
            }
            else {
                try {
                    saveReportAsPDF(jasperPrint.get(), selectedFile);
                } catch (Exception e) {
                    GuiUtils.showNode(btnStartOver, true);
                    GuiUtils.showNode(btnPrintRecord, true);
                    GuiUtils.showNode(btnSaveRecordAsPDF, true);

                    String errorCode = BiometricsVerificationtErrorCodes.C019_00004.getCode();
                    String[] errorDetails = {"failed while saving the verification report as PDF!"};
                    Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
                }
            }
        }
    }


    private void printReport(JasperPrint jasperPrint) {
        PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
        printReportTask.setOnSucceeded(event ->
        {
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintRecord, true);
            GuiUtils.showNode(btnSaveRecordAsPDF, true);
        });
        printReportTask.setOnFailed(event ->
        {
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintRecord, true);
            GuiUtils.showNode(btnSaveRecordAsPDF, true);

            Throwable exception = printReportTask.getException();

            String errorCode = BiometricsVerificationtErrorCodes.C019_00005.getCode();
            String[] errorDetails = {"failed while printing the verification report!"};
            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
        });
        Context.getExecutorService().submit(printReportTask);
    }

    private void saveReportAsPDF(JasperPrint jasperPrint, File selectedFile)
            throws FileNotFoundException {
        SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
                new FileOutputStream(selectedFile));
        printReportTaskAsPdfTask.setOnSucceeded(event ->
        {
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintRecord, true);
            GuiUtils.showNode(btnSaveRecordAsPDF, true);

            showSuccessNotification(resources.getString("verificationReport.savingAsPDF.success.message"));
            try {
                Desktop.getDesktop().open(selectedFile);
            } catch (Exception e) {
                LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
            }
        });
        printReportTaskAsPdfTask.setOnFailed(event ->
        {
            GuiUtils.showNode(btnStartOver, true);
            GuiUtils.showNode(btnPrintRecord, true);
            GuiUtils.showNode(btnSaveRecordAsPDF, true);

            Throwable exception = printReportTaskAsPdfTask.getException();

            String errorCode = BiometricsVerificationtErrorCodes.C019_00006.getCode();
            String[] errorDetails = {"failed while saving the verification report as PDF!"};
            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
        });
        Context.getExecutorService().submit(printReportTaskAsPdfTask);
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) { goNext(); }
    }
}