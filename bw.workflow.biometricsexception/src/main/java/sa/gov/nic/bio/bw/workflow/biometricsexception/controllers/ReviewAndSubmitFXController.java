package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitFXController extends WizardStepFxControllerBase {
    @FXML
    private Label PersonID, PersonName;

    @FXML
    private SVGPath svgRightLittle;
    @FXML
    private SVGPath svgRightRing;
    @FXML
    private SVGPath svgRightMiddle;
    @FXML
    private SVGPath svgRightIndex;
    @FXML
    private SVGPath svgRightThumb;

    @FXML
    private SVGPath svgLeftLittle;
    @FXML
    private SVGPath svgLeftRing;
    @FXML
    private SVGPath svgLeftMiddle;
    @FXML
    private SVGPath svgLeftIndex;
    @FXML
    private SVGPath svgLeftThumb;

    @FXML
    private VBox VRightThumb;
    @FXML
    private VBox VRightIndexFinger;
    @FXML
    private VBox VRightMiddleFinger;
    @FXML
    private VBox VRightRingFinger;
    @FXML
    private VBox VRightLittleFinger;

    @FXML
    private VBox VLeftThumb;
    @FXML
    private VBox VLeftIndexFinger;
    @FXML
    private VBox VLeftMiddleFinger;
    @FXML
    private VBox VLeftRingFinger;
    @FXML
    private VBox VLeftLittleFinger;

    @FXML
    private Label RThumbCouse, RThumbStatus;
    @FXML
    private Label RIndexCouse, RIndexStstus;
    @FXML
    private Label RMiddleCouse, RMiddleStatus;
    @FXML
    private Label RRingCouse, RRingStatus;
    @FXML
    private Label RLittleCouse, RLittleStatus;

    @FXML
    private Label LThumbCouse, LThumbStatus;
    @FXML
    private Label LIndexCouse, LIndexStstus;
    @FXML
    private Label LMiddleCouse, LMiddleStatus;
    @FXML
    private Label LRingCouse, LRingStatus;
    @FXML
    private Label LLittleCouse, LLittleStatus;

    @Override
    protected void onAttachedToScene() {

        VRightThumb.setManaged(true);
        VRightThumb.setVisible(true);

        VLeftThumb.setManaged(true);
        VLeftThumb.setVisible(true);

        svgRightThumb.setManaged(true);
        svgRightThumb.setVisible(true);

        svgLeftThumb.setManaged(true);
        svgLeftThumb.setVisible(true);

        RThumbCouse.setText("حرق");
        RThumbStatus.setText("مؤقت");

        LThumbCouse.setText("حرق");
        LThumbStatus.setText("دائم");


    }
}
