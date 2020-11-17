package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;

@FxmlFile("registeringCriminalClearanceReport.fxml")
public class RegisteringCriminalClearanceReportPaneFxController extends WizardStepFxControllerBase {
//    public enum Request {
//        SUBMIT_Criminal_Clearance_Report_REGISTRATION,
//        CHECK_Criminal_Clearance_Report_REGISTRATION,
//    }

//    @Input protected Long personId;
    //	@Input protected String rightIrisBase64;
    //	@Input protected String leftIrisBase64;
    //	@Input private Status irisRegistrationStatus;
//    @Output private Request request;

    @FXML private ProgressIndicator piProgress;
    @FXML private ImageView ivSuccess;
    @FXML private ImageView ivFailure;
    @FXML private Label lblStatus;
    @FXML private Button btnRetry;
    @FXML private Button btnStartOver;

    private boolean disableRetryButtonForever = false;

    @Override
    protected void onAttachedToScene() {
//        request = Request.SUBMIT_Criminal_Clearance_Report_REGISTRATION;
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        //		if(request == Request.SUBMIT_Criminal_Clearance_Report_REGISTRATION)
        //		{
        if (successfulResponse) {
            lblStatus.setText(resources.getString("label.waitingCriminalClearanceReport"));
//            request = Request.CHECK_Criminal_Clearance_Report_REGISTRATION;
            continueWorkflow();
        }
        else {
            lblStatus.setText(resources.getString("label.failedToSendCriminalClearanceReport"));
            piProgress.setVisible(false);
            ivFailure.setVisible(true);
            btnStartOver.setVisible(true);
            btnRetry.setVisible(!disableRetryButtonForever);
        }
        //		}
        //		else if(request == Request.CHECK_Criminal_Clearance_Report_REGISTRATION)
        //		{
        //			if(successfulResponse)
        //			{
        //				if(irisRegistrationStatus == Status.PENDING)
        //				{
        //					Context.getExecutorService().submit(() ->
        //					{
        //						try
        //						{
        //							int seconds = Integer.parseInt(Context.getConfigManager().getProperty(
        //																	"registerIris.inquiry.checkEverySeconds"));
        //							Thread.sleep(seconds * 1000);
        //						}
        //						catch(InterruptedException e)
        //						{
        //							e.printStackTrace();
        //						}
        //
        //						Platform.runLater(this::continueWorkflow);
        //					});
        //
        //				}
        //				else if(irisRegistrationStatus == Status.SUCCESS)
        //				{
        //					lblStatus.setText(resources.getString("label.successIrisRegistration"));
        //					piProgress.setVisible(false);
        //					ivSuccess.setVisible(true);
        //					btnStartOver.setVisible(true);
        //				}
        //			}
        //			else
        //			{
        //				lblStatus.setText(resources.getString("label.failedToRegisterIris"));
        //				piProgress.setVisible(false);
        //				ivFailure.setVisible(true);
        //				btnStartOver.setVisible(true);
        //				btnRetry.setVisible(!disableRetryButtonForever);
        //			}
        //		}
    }

    //	@Override
    //	public void reportNegativeTaskResponse(String errorCode, Throwable exception, String[] errorDetails)
    //	{
    //		if("B003-0066".equals(errorCode)) disableRetryButtonForever = true;
    //
    //		super.reportNegativeTaskResponse(errorCode, exception, errorDetails);
    //	}
    //
    @FXML
    private void onRetryButtonClicked(ActionEvent actionEvent) {

        btnRetry.setVisible(false);
        btnStartOver.setVisible(false);
        ivFailure.setVisible(false);
        piProgress.setVisible(true);

        //		if(request == Request.SUBMIT_Criminal_Clearance_Report_REGISTRATION)
        //		{
        lblStatus.setText(resources.getString("label.submitCriminalClearanceReport"));
        //		}
        //		else if(request == Request.CHECK_Criminal_Clearance_Report_REGISTRATION)
        //		{
        //			lblStatus.setText(resources.getString("label.waitingCriminalClearanceReport"));
        //		}

        continueWorkflow();
    }
}