package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Cause;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.FaceExceptionFXController.TypeFaceService;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;

import java.util.ArrayList;
import java.util.List;

@FxmlFile("reviewAndSubmitFaceException2.fxml")
public class ReviewAndSubmitFaceExceptionFXController extends WizardStepFxControllerBase {
    @Input(alwaysRequired = true)
    private NormalizedPersonInfo normalizedPersonInfo;
    @Input
    private BioExclusion EditFaceException;
    @Input(alwaysRequired = true)
    private TypeFaceService typeFaceService;
    @Input(alwaysRequired = true)
    private List<Cause> causesFC;

    @Output
    private List<BioExclusion> EditedBioExclusionsList;
    @FXML
    private Label LblfaceExcReason;
    @FXML
    private Label LblfaceExcStatus;
    @FXML
    private Label PersonID, PersonName;
    @FXML
    private Label LblCause;
    @FXML
    private Label LblStatus;

    @Override
    protected void onAttachedToScene() {

        EditedBioExclusionsList = new ArrayList<BioExclusion>();

        PersonName.setText(normalizedPersonInfo.getFirstName() + " " + normalizedPersonInfo.getFatherName() + " " + normalizedPersonInfo.getFamilyName());
        PersonID.setText(normalizedPersonInfo.getPersonId().toString());

        if (EditFaceException != null) {
            LblCause.setVisible(true);
            for (Cause causeFC : causesFC) {

                if (causeFC.getCauseId() == EditFaceException.getCasueId()) {
                    if (EditFaceException.getCasueId() == 1) {
                        LblfaceExcReason.setText(EditFaceException.getDescription());
                    } else {
                        if (Context.getGuiLanguage() == GuiLanguage.ARABIC)
                            LblfaceExcReason.setText(causeFC.getArabicText());
                        else
                            LblfaceExcReason.setText(causeFC.getEnglishText());
                    }
                    break;
                }
            }

//            if (EditFaceException.getDuration() == 0)
//                LblfaceExcStatus.setText(resources.getString("Permanent"));
             if (EditFaceException.getMonth() == 3)
                LblfaceExcStatus.setText(resources.getString("3months"));
            else if (EditFaceException.getMonth() == 6)
                LblfaceExcStatus.setText(resources.getString("6months"));
            else
                LblfaceExcStatus.setText(resources.getString("oneYear"));

        } else {
            LblCause.setVisible(false);
            LblStatus.setVisible(false);
            LblfaceExcReason.setText(resources.getString("NoFaceException"));
        }

    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (typeFaceService.equals(TypeFaceService.ADD_OR_EDIT)) {

            EditFaceException.setSamisId(normalizedPersonInfo.getPersonId());
            EditFaceException.setBioType(3);
//            Long ExDate = new Long(1564475459);
//            EditFaceException.setExpireDate(ExDate);
            UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
            EditFaceException.setOperatorId(userInfo.getOperatorId());
            EditedBioExclusionsList.add(EditFaceException);
        }
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) goNext();
    }
}
