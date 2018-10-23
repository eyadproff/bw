package sa.gov.nic.bio.bw.client.features.faceverification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.faceverification.webservice.FaceMatchingResponse;

import java.util.List;
import java.util.Map;

@FxmlFile("showResult.fxml")
public class ShowResultFxController extends WizardStepFxControllerBase
{
	private static final String AVATAR_PLACEHOLDER_FILE = "sa/gov/nic/bio/bw/client/core/images/avatar_placeholder.jpg";
	
	@Input(required = true) private Long personId;
	@Input(required = true) private Image faceImage;
	@Input(required = true) private FaceMatchingResponse faceMatchingResponse;
	
	@FXML private Pane matchedPane;
	@FXML private Pane notMatchedPane;
	@FXML private Pane imagePane;
	@FXML private ImageView ivUploadedImage;
	@FXML private ImageView ivDBImage;
	@FXML private Label lblNotMatched;
	@FXML private Label lblSamisId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOutOfKingdom;
	@FXML private Button btnCompareWithUploadedImage;
	@FXML private Button btnStartOver;
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	protected void onAttachedToScene()
	{
		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane().heightProperty());
		ivUploadedImage.fitWidthProperty().bind(imagePane.widthProperty().divide(2.5));
		ivUploadedImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.2));
		ivDBImage.fitWidthProperty().bind(imagePane.widthProperty().divide(2.5));
		ivDBImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.2));
		imagePane.autosize();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			if(!faceMatchingResponse.isMatched())
			{
				GuiUtils.showNode(notMatchedPane, true);
				lblNotMatched.setText(String.format(resources.getString("label.faceImageIsNotMatched"),
				                                    AppUtils.localizeNumbers(String.valueOf(personId))));
			}
			else // matched
			{
				GuiUtils.showNode(matchedPane, true);
				GuiUtils.showNode(imagePane, true);
				GuiUtils.showNode(btnCompareWithUploadedImage, true);
				
				PersonInfo personInfo = faceMatchingResponse.getPersonInfo();
				long samisId = personInfo.getSamisId();
				Name name = personInfo.getName();
				String firstName = name.getFirstName();
				String fatherName = name.getFatherName();
				String grandfatherName = name.getGrandfatherName();
				String familyName = name.getFamilyName();
				
				if(firstName != null && (firstName.trim().isEmpty() || firstName.trim().equals("-"))) firstName = null;
				if(fatherName != null && (fatherName.trim().isEmpty() || fatherName.trim().equals("-")))
																									fatherName = null;
				if(grandfatherName != null && (grandfatherName.trim().isEmpty() || grandfatherName.trim().equals("-")))
																								grandfatherName = null;
				if(familyName != null && (familyName.trim().isEmpty() || familyName.trim().equals("-")))
																									familyName = null;
				
				if(samisId > 0)
				{
					String sSamisId = AppUtils.localizeNumbers(String.valueOf(samisId));
					lblSamisId.setText(sSamisId);
				}
				else
				{
					lblSamisId.setText(resources.getString("label.notAvailable"));
					lblSamisId.setTextFill(Color.RED);
				}
				
				if(firstName != null) lblFirstName.setText(firstName);
				else
				{
					lblFirstName.setText(resources.getString("label.notAvailable"));
					lblFirstName.setTextFill(Color.RED);
				}
				
				if(fatherName != null) lblFatherName.setText(fatherName);
				else
				{
					lblFatherName.setText(resources.getString("label.notAvailable"));
					lblFatherName.setTextFill(Color.RED);
				}
				
				if(grandfatherName != null) lblGrandfatherName.setText(grandfatherName);
				else
				{
					lblGrandfatherName.setText(resources.getString("label.notAvailable"));
					lblGrandfatherName.setTextFill(Color.RED);
				}
				
				if(familyName != null) lblFamilyName.setText(familyName);
				else
				{
					lblFamilyName.setText(resources.getString("label.notAvailable"));
					lblFamilyName.setTextFill(Color.RED);
				}
				
				GenderType gender = GenderType.values()[personInfo.getGender() - 1];
				lblGender.setText(gender == GenderType.MALE ? resources.getString("label.male") :
						                                      resources.getString("label.female"));
				
				@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
															Context.getUserSession().getAttribute(CountriesLookup.KEY);
				
				CountryBean countryBean = null;
				
				for(CountryBean country : countries)
				{
					if(country.getCode() == personInfo.getNationality())
					{
						countryBean = country;
						break;
					}
				}
				
				if(countryBean != null)
				{
					boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
					lblNationality.setText(arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN());
				}
				else
				{
					lblNationality.setText(resources.getString("label.notAvailable"));
					lblNationality.setTextFill(Color.RED);
				}
				
				Boolean outOfKingdom = personInfo.isOut();
				if(outOfKingdom != null) lblOutOfKingdom.setText(outOfKingdom ? resources.getString("label.yes") :
						                                                        resources.getString("label.no"));
				else
				{
					lblOutOfKingdom.setText(resources.getString("label.notAvailable"));
					lblOutOfKingdom.setTextFill(Color.RED);
				}
				
				String face = personInfo.getFace();
				Image dbImage = AppUtils.imageFromBase64(face);
				
				ivUploadedImage.setImage(faceImage);
				
				if(dbImage != null) ivDBImage.setImage(dbImage);
				else
				{
					ivDBImage.setImage(new Image(Thread.currentThread().getContextClassLoader()
							                            .getResourceAsStream(AVATAR_PLACEHOLDER_FILE)));
				}
				
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivUploadedImage,
				                           resources.getString("label.uploadedImage"),
				                           resources.getString("label.contextMenu.showImage"), false);
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivDBImage,
				                           resources.getString("label.dbImage"),
				                           resources.getString("label.contextMenu.showImage"), false);
			}
		}
	}
	
	@FXML
	private void onCompareWithUploadedImageButtonClicked(ActionEvent actionEvent)
	{
		Image uploadedImage = ivUploadedImage.getImage();
		Image dbImage = ivDBImage.getImage();
		
		if(uploadedImage.getHeight() >= dbImage.getHeight())
		{
			double ratio = dbImage.getHeight() / dbImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = uploadedImage.getHeight() - dbImage.getHeight();
			double extraWidth = heightDiff * ratio;
			dbImage = GuiUtils.scaleImage(dbImage, dbImage.getWidth() + extraWidth,
			                                    dbImage.getHeight() + heightDiff);
		}
		else
		{
			double ratio = uploadedImage.getHeight() / uploadedImage.getWidth();
			if(ratio < 1.0) ratio = 1.0 / ratio;
			double heightDiff = dbImage.getHeight() - uploadedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			uploadedImage = GuiUtils.scaleImage(uploadedImage, uploadedImage.getWidth() + extraWidth,
			                                 uploadedImage.getHeight() + heightDiff);
		}
		
		String title = resources.getString("dialog.compare.title");
		String buttonText = resources.getString("dialog.compare.buttons.close");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		Image mergedImage;
		if(rtl) mergedImage = GuiUtils.mergeImage(uploadedImage, dbImage);
		else mergedImage = GuiUtils.mergeImage(dbImage, uploadedImage);
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeMenuItem = new MenuItem(buttonText);
		contextMenu.getItems().add(closeMenuItem);
		
		Button btnClose = new Button(buttonText);
		btnClose.requestFocus();
		btnClose.setPadding(new Insets(10));
		GuiUtils.makeButtonClickableByPressingEnter(btnClose);
		
		StackPane stackPane = new StackPane();
		BorderPane borderPane = new BorderPane();
		StackPane.setAlignment(borderPane, Pos.CENTER);
		StackPane.setAlignment(btnClose, Pos.BOTTOM_CENTER);
		StackPane.setMargin(btnClose, new Insets(0, 0, 10, 0));
		stackPane.getChildren().addAll(borderPane, btnClose);
		ImageView ivMergedImage = new ImageView();
		ivMergedImage.setPreserveRatio(true);
		StackPane imageLayer = new StackPane();
		imageLayer.getChildren().add(ivMergedImage);
		borderPane.centerProperty().set(imageLayer);
		
		Stage dialogStage = DialogUtils.buildCustomDialog(Context.getCoreFxController().getStage(), title,
		                                                  stackPane, rtl, false);
		dialogStage.initStyle(StageStyle.UNDECORATED);
		dialogStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, t ->
		{
			if(t.getCode() == KeyCode.ESCAPE)
			{
				contextMenu.hide();
				dialogStage.close();
			}
		});
		dialogStage.getScene().getRoot().setOnContextMenuRequested(event ->
                       contextMenu.show(dialogStage.getScene().getRoot(), event.getScreenX(), event.getScreenY()));
		
		closeMenuItem.setOnAction(event -> dialogStage.close());
		
		ivMergedImage.fitHeightProperty().bind(dialogStage.heightProperty());
		ivMergedImage.fitWidthProperty().bind(dialogStage.widthProperty());
		
		ivMergedImage.setImage(mergedImage);
		btnClose.setOnAction(event ->
		{
		    dialogStage.close();
		    contextMenu.hide();
		});
		
		dialogStage.setFullScreenExitHint("");
		dialogStage.setFullScreen(true);
		dialogStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		dialogStage.setOnHidden(event -> Context.getCoreFxController().unregisterStageForIdleMonitoring(dialogStage));
		Context.getCoreFxController().registerStageForIdleMonitoring(dialogStage);
		dialogStage.show();
	}
}