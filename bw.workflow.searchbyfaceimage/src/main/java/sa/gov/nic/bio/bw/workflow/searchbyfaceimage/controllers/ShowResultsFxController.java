package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans.Candidate;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.tasks.BuildIdentifyByFaceReportTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.ui.ToggleTitledPane;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.utils.SearchByFaceImageErrorCodes;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showResults.fxml")
public class ShowResultsFxController extends WizardStepFxControllerBase {
	@Input(alwaysRequired = true) private String facePhotoBase64;
	@Input(alwaysRequired = true) private List<Candidate> candidates;
	@Input private Boolean showReportWithAlahwalLogo;

	@FXML private SplitPane splitPane;
	@FXML private HBox imagePane;
	@FXML private ImageView ivCenterImage;
	@FXML private VBox detailsPane;
	@FXML private Label lblBioId;
	@FXML private Label lblScore;
	@FXML private Label lblPersonId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblFamilyName;
	@FXML private Button btnCompareWithUploadedImage;
	@FXML private ScrollPane spCandidates;
	@FXML private ToggleTitledPane tpFinalImage;
	@FXML private HBox hbCandidatesContainer;
	@FXML private HBox hbCandidatesImages;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;

	private Image facePhoto;
	private Candidate chosenCandidate;

	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private FileChooser fileChooser = new FileChooser();

	@Override
	protected void onAttachedToScene() {
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
				resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);

		if(showReportWithAlahwalLogo != null && showReportWithAlahwalLogo){
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);
		}

		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane(getTabIndex()).widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane(getTabIndex()).heightProperty());
		ivCenterImage.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivCenterImage.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(0.0));

		ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) ->
		{
			if (!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
					imagePane.autosize();
					Context.getCoreFxController().getBodyPane(getTabIndex()).autosize();
				});
			}
		};
		Context.getCoreFxController().getStage().maximizedProperty().addListener(changeListener);
		imagePane.sceneProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == null)
				Context.getCoreFxController().getStage().maximizedProperty()
						.removeListener(changeListener);
		});

		Collections.sort(candidates);

		spCandidates.maxHeightProperty().bind(new SimpleDoubleProperty(Double.MAX_VALUE));
		btnCompareWithUploadedImage.setDisable(true);

		GuiUtils.showNode(spCandidates, true);
		GuiUtils.showNode(btnCompareWithUploadedImage, true);
		GuiUtils.showNode(detailsPane, true);

		// make the list scrollable horizontally
		spCandidates.setOnScroll(event ->
		{
			if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
				spCandidates.setHvalue(spCandidates.getHvalue() - event.getDeltaY() * 3 /
																  ((Pane) this.spCandidates.getContent()).getWidth());
			}
		});

		spCandidates.prefHeightProperty().bind(imagePane.heightProperty().divide(5));
		splitPane.getStyleClass().remove("hidden-divider"); // show the divider

		facePhoto = AppUtils.imageFromBase64(facePhotoBase64);
		ImageView imageView = new ImageView();
		imageView.setImage(facePhoto);
		imageView.setPreserveRatio(true);

		final double[] hScrollbarHeight = {13.0};
		imageView.fitHeightProperty().bind(spCandidates.heightProperty()
				.subtract(hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
		GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, tpFinalImage.getText(),
				resources.getString("label.contextMenu.showImage"), false);

		imagePane.autosize();

		// workaround to resolve the issue of not resizing sometimes
		new Thread(() ->
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
			Platform.runLater(() -> imagePane.autosize());
		}).start();

		ivCenterImage.setImage(facePhoto);
		GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCenterImage, tpFinalImage.getText(),
				resources.getString("label.contextMenu.showImage"), false);

		tpFinalImage.setContent(imageView);
		ToggleGroup toggleGroup = new ToggleGroup();
		tpFinalImage.setToggleGroup(toggleGroup);
		toggleGroup.selectToggle(tpFinalImage);
		tpFinalImage.setOnMouseClicked(event ->
		{
			chosenCandidate = null;
			toggleGroup.selectToggle(tpFinalImage);
			ivCenterImage.setImage(facePhoto);
			btnCompareWithUploadedImage.setDisable(true);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCenterImage, tpFinalImage.getText(),
					resources.getString("label.contextMenu.showImage"), false);

			lblBioId.setText(resources.getString("label.notAvailable"));
			lblScore.setText(resources.getString("label.notAvailable"));
			lblPersonId.setText(resources.getString("label.notAvailable"));
			lblFirstName.setText(resources.getString("label.notAvailable"));
			lblFatherName.setText(resources.getString("label.notAvailable"));
			lblFamilyName.setText(resources.getString("label.notAvailable"));
		});

		hbCandidatesImages.getChildren().clear();

		for (Candidate candidate : candidates) {
			ImageView candidateImageView = new ImageView();
			byte[] photoByteArray = Base64.getDecoder().decode(candidate.getImage());
			Image candidateImage = new Image(new ByteArrayInputStream(photoByteArray));
			candidateImageView.setImage(candidateImage);
			candidateImageView.setPreserveRatio(true);
			candidateImageView.fitHeightProperty().bind(spCandidates.heightProperty().subtract(
					hScrollbarHeight[0] * 3 + 2)); // 2 = top border + bottom border
			String scoreTitle = AppUtils.localizeNumbers(String.valueOf(candidate.getScore()));

			GuiUtils.attachImageDialog(Context.getCoreFxController(), candidateImageView, scoreTitle,
					resources.getString("label.contextMenu.showImage"), false);

			ToggleTitledPane toggleTitledPane = new ToggleTitledPane(scoreTitle, candidateImageView);
			toggleTitledPane.setToggleGroup(toggleGroup);
			toggleTitledPane.setCollapsible(false);
			toggleTitledPane.setFocusTraversable(false);
			toggleTitledPane.setOnMouseClicked(event ->
			{
				toggleGroup.selectToggle(toggleTitledPane);
				ivCenterImage.setImage(candidateImage);
				btnCompareWithUploadedImage.setDisable(false);
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivCenterImage, scoreTitle,
						resources.getString("label.contextMenu.showImage"), false);

				// default values
				lblBioId.setText(resources.getString("label.notAvailable"));
				lblScore.setText(resources.getString("label.notAvailable"));
				lblPersonId.setText(resources.getString("label.notAvailable"));
				lblFirstName.setText(resources.getString("label.notAvailable"));
				lblFatherName.setText(resources.getString("label.notAvailable"));
				lblFamilyName.setText(resources.getString("label.notAvailable"));

				long bioId = candidate.getBioId();
				int score = candidate.getScore();
				long samisId = candidate.getSamisId();
				String firstName = candidate.getFirstName();
				String fatherName = candidate.getFatherName();
				String familyName = candidate.getFamilyName();

				chosenCandidate = candidate;

				if (firstName != null && (firstName.trim().isEmpty() || firstName.trim().equals("-")))
					firstName = null;
				if (fatherName != null && (fatherName.trim().isEmpty() || fatherName.trim().equals("-")))
					fatherName = null;
				if (familyName != null && (familyName.trim().isEmpty() || familyName.trim().equals("-")))
					familyName = null;

				String sBioId = AppUtils.localizeNumbers(String.valueOf(bioId));
				String sScore = AppUtils.localizeNumbers(String.valueOf(score));

				lblBioId.setText(sBioId);
				lblScore.setText(sScore);

				if (samisId > 0) {
					String sSamisId = AppUtils.localizeNumbers(String.valueOf(samisId));
					lblPersonId.setText(sSamisId);
				}

				if (firstName != null)
					lblFirstName.setText(firstName);
				if (fatherName != null)
					lblFatherName.setText(fatherName);
				if (familyName != null)
					lblFamilyName.setText(familyName);
			});
			hbCandidatesImages.getChildren().add(toggleTitledPane);
		}

		spCandidates.setHvalue(0.0); // scroll to the beginning
	}

	@FXML
	private void onCompareWithUploadedImageButtonClicked(ActionEvent actionEvent) {
		Image selectedImage = ivCenterImage.getImage();

		if (facePhoto.getHeight() >= selectedImage.getHeight()) {
			double ratio = selectedImage.getHeight() / selectedImage.getWidth();
			if (ratio < 1.0)
				ratio = 1.0 / ratio;
			double heightDiff = facePhoto.getHeight() - selectedImage.getHeight();
			double extraWidth = heightDiff * ratio;
			selectedImage = GuiUtils.scaleImage(selectedImage, selectedImage.getWidth() + extraWidth,
					selectedImage.getHeight() + heightDiff);
		}
		else {
			double ratio = facePhoto.getHeight() / facePhoto.getWidth();
			if (ratio < 1.0)
				ratio = 1.0 / ratio;
			double heightDiff = selectedImage.getHeight() - facePhoto.getHeight();
			double extraWidth = heightDiff * ratio;
			facePhoto = GuiUtils.scaleImage(facePhoto, facePhoto.getWidth() + extraWidth,
					facePhoto.getHeight() + heightDiff);
		}

		String title = resources.getString("dialog.compare.title");
		String buttonText = resources.getString("dialog.compare.buttons.close");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;

		Image mergedImage;
		if (rtl)
			mergedImage = GuiUtils.mergeImage(facePhoto, selectedImage);
		else
			mergedImage = GuiUtils.mergeImage(selectedImage, facePhoto);

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
			if (t.getCode() == KeyCode.ESCAPE) {
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

	@FXML
	private void onPrintRecordButtonClicked(ActionEvent actionEvent) {

		if (chosenCandidate != null) {
			hideNotification();
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnPrintRecord, false);
			GuiUtils.showNode(btnSaveRecordAsPDF, false);

			BuildIdentifyByFaceReportTask buildIdentifyByFaceReportTask =
					new BuildIdentifyByFaceReportTask(chosenCandidate, facePhotoBase64);


			buildIdentifyByFaceReportTask.setOnSucceeded(event ->
			{
				JasperPrint value = buildIdentifyByFaceReportTask.getValue();
				jasperPrint.set(value);
				printReport(value);
			});
			buildIdentifyByFaceReportTask.setOnFailed(event ->
			{
				GuiUtils.showNode(btnStartOver, true);
				GuiUtils.showNode(btnPrintRecord, true);
				GuiUtils.showNode(btnSaveRecordAsPDF, true);

				Throwable exception = buildIdentifyByFaceReportTask.getException();

				String errorCode = SearchByFaceImageErrorCodes.C005_00005.getCode();
				String[] errorDetails = {"failed while building the identify by face report!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			});
			Context.getExecutorService().submit(buildIdentifyByFaceReportTask);
		}
		else {
			showWarningNotification(resources.getString("searchByFaceImage.selectCandidate.warning.message"));
		}

	}

	@FXML
	private void onSaveRecordAsPdfButtonClicked(ActionEvent actionEvent) {
		if (chosenCandidate != null) {
			hideNotification();
			File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

			if (selectedFile != null) {
				GuiUtils.showNode(btnStartOver, false);
				GuiUtils.showNode(btnPrintRecord, false);
				GuiUtils.showNode(btnSaveRecordAsPDF, false);

				BuildIdentifyByFaceReportTask buildIdentifyByFaceReportTask =
						new BuildIdentifyByFaceReportTask(chosenCandidate, facePhotoBase64);

				buildIdentifyByFaceReportTask.setOnSucceeded(event ->
				{
					JasperPrint value = buildIdentifyByFaceReportTask.getValue();
					jasperPrint.set(value);
					try {
						saveReportAsPDF(value, selectedFile);
					} catch (Exception e) {
						GuiUtils.showNode(btnStartOver, true);
						GuiUtils.showNode(btnPrintRecord, true);
						GuiUtils.showNode(btnSaveRecordAsPDF, true);

						String errorCode = SearchByFaceImageErrorCodes.C005_00006.getCode();
						String[] errorDetails = {"failed while saving the identify by face report as PDF!"};
						Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
					}
				});
				buildIdentifyByFaceReportTask.setOnFailed(event ->
				{
					GuiUtils.showNode(btnStartOver, true);
					GuiUtils.showNode(btnPrintRecord, true);
					GuiUtils.showNode(btnSaveRecordAsPDF, true);

					Throwable exception = buildIdentifyByFaceReportTask.getException();

					String errorCode = SearchByFaceImageErrorCodes.C005_00007.getCode();
					String[] errorDetails = {"failed while building the identify by face report!"};
					Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
				});
				Context.getExecutorService().submit(buildIdentifyByFaceReportTask);
			}
		}
		else {
			showWarningNotification(resources.getString("searchByFaceImage.selectCandidate.warning.message"));
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

			String errorCode = SearchByFaceImageErrorCodes.C005_00009.getCode();
			String[] errorDetails = {"failed while printing the identify by face report!"};
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

			showSuccessNotification(resources.getString("searchByFaceImage.savingAsPDF.success.message"));
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

			String errorCode = SearchByFaceImageErrorCodes.C005_00010.getCode();
			String[] errorDetails = {"failed while saving the identify by face report as PDF!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}

}