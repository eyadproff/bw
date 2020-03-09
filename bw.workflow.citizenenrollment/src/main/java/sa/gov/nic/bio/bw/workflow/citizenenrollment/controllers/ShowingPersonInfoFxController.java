package sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks.RetrieveBioExclusionsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@FxmlFile("showingPersonInfo.fxml")
public class ShowingPersonInfoFxController extends WizardStepFxControllerBase {
    @Input
    private PersonInfo personInfo;
    @Output
    private NormalizedPersonInfo normalizedPersonInfo;
    @Output
    List<BioExclusion> bioExclusion;


    @FXML
    private ScrollPane infoPane;
    //@FXML private ImageView ivPersonPhoto;
    @FXML
    private Label lblFirstName;
    @FXML
    private Label lblFatherName;
    @FXML
    private Label lblGrandfatherName;
    @FXML
    private Label lblFamilyName;
    @FXML
    private Label lblGender;
    @FXML
    private Label lblNationality;
    @FXML
    private Label lblOccupation;
    @FXML
    private Label lblBirthPlace;
    @FXML
    private Label lblBirthDate;
    @FXML
    private Label lblPersonId;
    @FXML
    private Label lblPersonType;
    //	@FXML private Label lblDocumentId;
//	@FXML private Label lblDocumentType;
//	@FXML private Label lblDocumentIssuanceDate;
//	@FXML private Label lblDocumentExpiryDate;
    @FXML
    private Label lblNaturalizedSaudi;
    @FXML
    private Button btnStartOver;
    @FXML
    private Button btnConfirmPersonInfo;

    @Override
    protected void onAttachedToScene() {
        normalizedPersonInfo = new NormalizedPersonInfo(personInfo);

        GuiUtils.showNode(lblNaturalizedSaudi, normalizedPersonInfo.getNationality() != null &&
                normalizedPersonInfo.getNationality().getCode() > 0 &&
                !"SAU".equalsIgnoreCase(normalizedPersonInfo.getNationality().getMofaNationalityCode()) &&
                String.valueOf(normalizedPersonInfo.getPersonId()).startsWith("1"));

//		String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
//		Gender gender = normalizedPersonInfo.getGender();
//		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);

        String notAvailable = resources.getString("label.notAvailable");
        Consumer<Label> consumer = label ->
        {
            label.setText(notAvailable);
            label.setTextFill(Color.RED);
        };

        GuiUtils.setLabelText(lblFirstName, normalizedPersonInfo.getFirstNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblFatherName, normalizedPersonInfo.getFatherNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblGrandfatherName, normalizedPersonInfo.getGrandfatherNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblFamilyName, normalizedPersonInfo.getFamilyNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblGender, normalizedPersonInfo.getGender()).orElse(consumer);
        GuiUtils.setLabelText(lblNationality, normalizedPersonInfo.getNationality()).orElse(consumer);
        GuiUtils.setLabelText(lblOccupation, normalizedPersonInfo.getOccupation()).orElse(consumer);
        GuiUtils.setLabelText(lblBirthPlace, normalizedPersonInfo.getBirthPlace()).orElse(consumer);
        GuiUtils.setLabelText(lblBirthDate, normalizedPersonInfo.getBirthDate()).orElse(consumer);
        GuiUtils.setLabelText(lblPersonId, normalizedPersonInfo.getPersonId()).orElse(consumer);
        GuiUtils.setLabelText(lblPersonType, normalizedPersonInfo.getPersonType()).orElse(consumer);
//		GuiUtils.setLabelText(lblDocumentId, normalizedPersonInfo.getDocumentId()).orElse(consumer);
//		GuiUtils.setLabelText(lblDocumentType, normalizedPersonInfo.getDocumentType()).orElse(consumer);
//		GuiUtils.setLabelText(lblDocumentIssuanceDate, normalizedPersonInfo.getDocumentIssuanceDate()).orElse(consumer);
//		GuiUtils.setLabelText(lblDocumentExpiryDate, normalizedPersonInfo.getDocumentExpiryDate()).orElse(consumer);

        infoPane.autosize();
        btnConfirmPersonInfo.requestFocus();
    }

    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) goNext();
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {

        setData(RetrieveBioExclusionsWorkflowTask.class,
                "samisId", normalizedPersonInfo.getPersonId().intValue());

        executeUiTask(RetrieveBioExclusionsWorkflowTask.class, new SuccessHandler() {
                    @Override
                    protected void onSuccess() {
						 bioExclusion = getData( "bioExclusionList");
						goNext();
                    }
                }, throwable ->
                {


                    if (throwable instanceof Signal) {
                        Signal signal = (Signal) throwable;
                        Map<String, Object> payload = signal.getPayload();
                        if (payload != null) {
                            TaskResponse<?> taskResponse = (TaskResponse<?>)
                                    payload.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);

                            if (taskResponse != null) {
                                this.reportNegativeTaskResponse(taskResponse.getErrorCode(),
                                        taskResponse.getException(),
                                        taskResponse.getErrorDetails());
                                return;
                            }
                        }

						String errorCode = CitizenEnrollmentErrorCodes.C011_00010.getCode();
						String[] errorDetails = {"failed to execute the task RetrieveBioExclusionsWorkflowTask! signal = " +
								signal};
						Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                    } else {
						String errorCode = CitizenEnrollmentErrorCodes.C011_00011.getCode();
						String[] errorDetails = {"failed to execute the task RetrieveBioExclusionsWorkflowTask!"};
						Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
                    }
                }
        );

       // continueWorkflow();
    }


}