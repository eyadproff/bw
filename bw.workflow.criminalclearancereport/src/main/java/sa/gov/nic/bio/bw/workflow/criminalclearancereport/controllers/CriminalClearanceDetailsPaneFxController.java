package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import net.sf.jasperreports.engine.xml.JRPenFactory;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;

@FxmlFile("criminalclearancedetails.fxml")
public class CriminalClearanceDetailsPaneFxController extends WizardStepFxControllerBase {

    @FXML private TextField txtWhoRequestedTheReport;
    @FXML private TextField txtPurposeOfTheReport;
    @FXML private Button btnNext;

    @Override
    protected void onAttachedToScene() {

        btnNext.disableProperty().bind((txtWhoRequestedTheReport.textProperty().isEmpty().or(txtWhoRequestedTheReport.disabledProperty())).or
                                       (txtPurposeOfTheReport.textProperty().isEmpty().or(txtPurposeOfTheReport.disabledProperty())));

        GuiUtils.applyValidatorToTextField(txtWhoRequestedTheReport, 100);
        GuiUtils.applyValidatorToTextField(txtPurposeOfTheReport, 100);



    }


}
