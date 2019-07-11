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
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.FaceExceptionFXController.TypeFaceService;

import java.util.ArrayList;
import java.util.List;

@FxmlFile("reviewAndSubmitFaceException.fxml")
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
    private Label PersonID, PersonName;
    @FXML
    private Label LblCause;

    @Override
    protected void onAttachedToScene() {
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

        } else {
            LblCause.setVisible(false);
            LblfaceExcReason.setText(resources.getString("NoFaceException"));
        }
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (typeFaceService.equals(TypeFaceService.ADD_OR_EDIT)) {
            EditedBioExclusionsList = new ArrayList<BioExclusion>();
            EditFaceException.setSamisId(normalizedPersonInfo.getPersonId());
            EditFaceException.setBioType(3);
            Long ExDate = new Long(1564475459);
            EditFaceException.setExpireDate(ExDate);
            UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
            EditFaceException.setOperatorId(userInfo.getOperatorId());
            EditedBioExclusionsList.add(EditFaceException);
        }
        super.onNextButtonClicked(actionEvent);
    }
}
