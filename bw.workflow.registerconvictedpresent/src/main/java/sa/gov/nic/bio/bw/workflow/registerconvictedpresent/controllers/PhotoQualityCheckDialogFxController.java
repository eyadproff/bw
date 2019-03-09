package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import sa.gov.nic.bio.biokit.face.beans.CaptureFaceResponse;
import sa.gov.nic.bio.biokit.face.beans.GetIcaoImageResponse;
import sa.gov.nic.bio.bw.commons.resources.fxml.CommonFXML;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.workflow.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.PhotoQualityCheckWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@FxmlFile("photoQualityCheckDialog.fxml")
public class PhotoQualityCheckDialogFxController extends BodyFxControllerBase
{
	@FXML private Pane buttonPane;
	@FXML private AutoScalingStackPane subScenePane;
	@FXML private TitledPane tpUploadedImage;
	@FXML private TitledPane tpCroppedImage;
	@FXML private ProgressIndicator piIcao;
	@FXML private ProgressIndicator piCroppedImage;
	@FXML private ImageView ivSuccessIcao;
	@FXML private ImageView ivWarningIcao;
	@FXML private ImageView ivErrorIcao;
	@FXML private ImageView ivUploadedImage;
	@FXML private ImageView ivCroppedImagePlaceholder;
	@FXML private ImageView ivCroppedImage;
	@FXML private Label lblIcaoMessage;
	@FXML private Button btnClose;
	@FXML private Dialog<ButtonType> dialog;
	
	private BodyFxControllerBase hostController;
	private Image inputPhotoImage;
	private Image outputPhotoImage;
	
	private Group face3D;
	private Timeline faceAnimationTimeline;
	private PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
	
	private Translate pivotBase = new Translate(0.0, 0.0, -1.0);
	
	public void setHostController(BodyFxControllerBase hostController){this.hostController = hostController;}
	public void setInputPhotoImage(Image inputPhotoImage){this.inputPhotoImage = inputPhotoImage;}
	public Image getOutputPhotoImage(){return outputPhotoImage;}
	
	@Override
	protected void initialize()
	{
		if(Platform.isSupported(ConditionalFeature.SCENE3D))
		{
			Context.getExecutorService().submit(() ->
			{
			    Group face3DGroup;
			    try
			    {
				    URL url = CommonFXML.FACE_3D_MODEL.getAsUrl();
				    if(url != null) face3DGroup = FXMLLoader.load(url, resources);
				    else
				    {
					    String errorCode = CommonsErrorCodes.C008_00017.getCode();
					    String[] errorDetails = {"failed to load the face 3D model fxml!"};
					    Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
					
					    return;
				    }
			    }
			    catch(Exception e)
			    {
			        String errorCode = CommonsErrorCodes.C008_00001.getCode();
			        String[] errorDetails = {"failed to load the face 3D model!"};
			        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
			
			        return;
			    }
			
			    Platform.runLater(() ->
			    {
			        face3D = (Group) face3DGroup.lookup("#face3D");
			        face3D.getTransforms().setAll(pivotBase);
			
			        AutoScalingStackPane shapePane = new AutoScalingStackPane(face3DGroup);
			        SubScene subScene = new SubScene(shapePane, 100.0, 80.0, true,
			                                         SceneAntialiasing.BALANCED);
			        subScene.setFill(Color.TRANSPARENT);
			        subScene.setCamera(perspectiveCamera);
			
			        subScenePane.getChildren().setAll(subScene);
			        GuiUtils.showNode(subScenePane, true);
			    });
			});
		}
		
		dialog.setOnShown(event ->
		{
			String photoBase64;
			try
			{
				photoBase64 = AppUtils.imageToBase64(inputPhotoImage);
			}
			catch(IOException e)
			{
				dialog.close();
				String errorCode = RegisterConvictedPresentErrorCodes.C007_00009.getCode();
				String[] errorDetails = {"failed to convert inputPhotoImage to base64"};
				Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				return;
			}
			
			GuiUtils.attachFacePhotoBase64(ivUploadedImage, photoBase64);
			
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
			
			setData(PhotoQualityCheckWorkflowTask.class, "photoBase64", photoBase64);
			executeUiTask(PhotoQualityCheckWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					GuiUtils.showNode(piIcao, false);
					GuiUtils.showNode(piCroppedImage, false);
					
					GetIcaoImageResponse result = getData("result");
					
					String croppedImageBase64 = result.getCroppedImage();
					Image croppedImage;
					
					if(croppedImageBase64 != null)
					{
						croppedImage = GuiUtils.attachFacePhotoBase64(ivCroppedImage, croppedImageBase64);
						GuiUtils.showNode(ivCroppedImage, true);
					}
					else
					{
						croppedImage = null;
						GuiUtils.showNode(ivCroppedImagePlaceholder, true);
					}
					
					if(result.getReturnCode() == GetIcaoImageResponse.SuccessCodes.SUCCESS)
					{
						String icaoCode = result.getIcaoErrorMessage();
						boolean icaoSuccess = GetIcaoImageResponse.IcaoCodes.SUCCESS.equals(icaoCode);
						
						if(icaoSuccess)
						{
							GuiUtils.showNode(ivSuccessIcao, true);
							lblIcaoMessage.setText(resources.getString("label.icao.success"));
							outputPhotoImage = croppedImage;
							stage.close();
						}
						else
						{
							switch(icaoCode)
							{
								case CaptureFaceResponse.IcaoCodes.FACE_NOT_DETECTED:
								{
									GuiUtils.showNode(ivErrorIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.noFace"));
									break;
								}
								case CaptureFaceResponse.IcaoCodes.MULTIPLE_FACES_DETECTED:
								{
									GuiUtils.showNode(ivErrorIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.multipleFaces"));
									break;
								}
								case CaptureFaceResponse.IcaoCodes.PITCH_ERROR:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.pitch"));
									animateHeadRotation(Rotate.X_AXIS, 10.0);
									break;
								}
								case CaptureFaceResponse.IcaoCodes.YAW_ERROR:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.yaw"));
									animateHeadRotation(Rotate.Y_AXIS, 25.0);
									break;
								}
								case CaptureFaceResponse.IcaoCodes.ROLL_ERROR:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.roll"));
									animateHeadRotation(Rotate.Z_AXIS, 10.0);
									break;
								}
								case CaptureFaceResponse.IcaoCodes.SHADOW_ERROR:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.shadow"));
									break;
								}
								case CaptureFaceResponse.IcaoCodes.RIGHT_EYE_CLOSED:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.rightEyeClosed"));
									break;
								}
								case CaptureFaceResponse.IcaoCodes.LEFT_EYE_CLOSED:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.leftEyeClosed"));
									break;
								}
								case CaptureFaceResponse.IcaoCodes.EYE_GAZE_ERROR:
								{
									GuiUtils.showNode(ivWarningIcao, true);
									lblIcaoMessage.setText(resources.getString("label.icao.eyeGaze"));
									break;
								}
								default:
								{
									GuiUtils.showNode(ivErrorIcao, true);
									lblIcaoMessage.setText(String.format(
															resources.getString("label.icao.unknown"), icaoCode));
									break;
								}
							}
						}
					}
					else if(result.getReturnCode() == GetIcaoImageResponse.FailureCodes.FACE_DATA_CANNOT_BE_EMPTY)
					{
						GuiUtils.showNode(ivErrorIcao, true);
						lblIcaoMessage.setText(resources.getString("label.faceDateNotSet"));
					}
					else
					{
						GuiUtils.showNode(ivErrorIcao, true);
						lblIcaoMessage.setText(String.format(
												resources.getString("label.errorOccurs"), result.getReturnCode()));
					}
				}
			}, throwable ->
			{
				stage.close();
				
				if(throwable instanceof Signal)
				{
					Signal signal = (Signal) throwable;
					Map<String, Object> payload = signal.getPayload();
					if(payload != null)
					{
						TaskResponse<?> taskResponse = (TaskResponse<?>)
															payload.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
						
						if(taskResponse != null)
						{
							hostController.reportNegativeTaskResponse(taskResponse.getErrorCode(),
							                                          taskResponse.getException(),
							                                          taskResponse.getErrorDetails());
							return;
						}
					}
					
					String errorCode = RegisterConvictedPresentErrorCodes.C007_00010.getCode();
					String[] errorDetails = {"failed to execute the task PhotoQualityCheckWorkflowTask! signal = " +
											signal};
					Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
				}
				else
				{
					String errorCode = RegisterConvictedPresentErrorCodes.C007_00011.getCode();
					String[] errorDetails = {"failed to execute the task PhotoQualityCheckWorkflowTask!"};
					Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
				}
			});
		});
	}
	
	@FXML
	private void onCloseButtonClicked(ActionEvent actionEvent)
	{
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.close();
	}
	
	public void showDialogAndWait()
	{
		dialog.showAndWait();
	}
	
	private void animateHeadRotation(Point3D axis, double degree)
	{
		if(Platform.isSupported(ConditionalFeature.SCENE3D))
		{
			Rotate rotate = new Rotate(0.0, 0.0, 0.0, 0.0, axis);
			
			face3D.getTransforms().setAll(pivotBase, rotate);
			faceAnimationTimeline = new Timeline(
					new KeyFrame(
							Duration.seconds(0.0),
							new KeyValue(rotate.angleProperty(), 0.0)
					),
					new KeyFrame(
							Duration.seconds(0.5),
							new KeyValue(rotate.angleProperty(), degree)
					),
					new KeyFrame(
							Duration.seconds(1.0),
							new KeyValue(rotate.angleProperty(), 0.0)
					),
					new KeyFrame(
							Duration.seconds(1.5),
							new KeyValue(rotate.angleProperty(), -degree)
					),
					new KeyFrame(
							Duration.seconds(2.0),
							new KeyValue(rotate.angleProperty(), 0.0)
					)
			);
			faceAnimationTimeline.setCycleCount(5);
			faceAnimationTimeline.play();
		}
	}
}