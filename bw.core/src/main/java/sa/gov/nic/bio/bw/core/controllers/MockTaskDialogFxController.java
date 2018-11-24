package sa.gov.nic.bio.bw.core.controllers;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import sa.gov.nic.bio.bw.core.beans.TaskInput;
import sa.gov.nic.bio.bw.core.beans.TaskOutput;
import sa.gov.nic.bio.bw.core.beans.TaskResult;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;

import java.util.List;

@FxmlFile("mockTaskDialog.fxml")
public class MockTaskDialogFxController extends FxControllerBase
{
	@FXML private Dialog<ButtonType> dialog;
	@FXML private TableView<TaskInput<?>> tvTaskInputs;
	@FXML private TableView<TaskOutput<?>> tvTaskOutputs;
	@FXML private TableView<TaskResult> tvTaskResults;
	@FXML private TitledPane tpTaskInputs;
	@FXML private TitledPane tpTaskOutputs;
	@FXML private TitledPane tpTaskResults;
	@FXML private TitledPane tpDetails;
	@FXML private TableColumn<TaskInput<?>, TaskInput<?>> tcInputSequence;
	@FXML private TableColumn<TaskInput<?>, String> tcInputName;
	@FXML private TableColumn<TaskInput<?>, String> tvInputType;
	@FXML private TableColumn<TaskInput<?>, String> tcInputAlwaysRequired;
	@FXML private TableColumn<TaskInput<?>, String> tcInputRequiredOnlyIf;
	@FXML private TableColumn<TaskOutput<?>, TaskOutput<?>> tcOutputSequence;
	@FXML private TableColumn<TaskOutput<?>, String> tcOutputName;
	@FXML private TableColumn<TaskOutput<?>, String> tvOutputType;
	@FXML private TableColumn<TaskResult, TaskResult> tcResultSequence;
	@FXML private TableColumn<TaskResult, String> tcResultName;
	@FXML private TableColumn<TaskResult, String> tvSuccessTask;
	@FXML private TextArea txtDetails;
	@FXML private ButtonType btRunMockTask;
	@FXML private ButtonType btRunRealTask;
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event -> Platform.runLater(() ->
		{
			// center the buttons
		    Button btnRunMockTask = (Button) dialog.getDialogPane().lookupButton(btRunMockTask);
		    Button btnRunRealTask = (Button) dialog.getDialogPane().lookupButton(btRunRealTask);
		    HBox hBox = (HBox) btnRunMockTask.getParent();
		    double distance = hBox.getWidth() / 2.0 - btnRunMockTask.getWidth() - hBox.getPadding().getLeft() - 5.0;
			btnRunMockTask.translateXProperty().set(-distance);
			btnRunRealTask.translateXProperty().set(-distance);
			
			btnRunMockTask.disableProperty().bind(Bindings.size(tvTaskResults.getSelectionModel().getSelectedItems())
					                                      .isEqualTo(0));
			
			String yes = resources.getString("label.yes");
			String no = resources.getString("label.no");
			
			GuiUtils.initSequenceTableColumn(tcInputSequence);
			tcInputSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
			tcInputName.setCellValueFactory(param ->
			{
			    TaskInput<?> taskInput = param.getValue();
			    String name = taskInput.getName();
			    return new SimpleStringProperty(name);
			});
			tvInputType.setCellValueFactory(param ->
			{
			    TaskInput<?> taskInput = param.getValue();
			    Class<?> type = taskInput.getType();
			    return new SimpleStringProperty(type.getName());
			});
			tcInputAlwaysRequired.setCellValueFactory(param ->
			{
			    TaskInput<?> taskInput = param.getValue();
			    boolean alwaysRequired = taskInput.isAlwaysRequired();
			    return new SimpleStringProperty(alwaysRequired ? yes : no);
			});
			tcInputRequiredOnlyIf.setCellValueFactory(param ->
			{
			    TaskInput<?> taskInput = param.getValue();
			    String requirementConditions = taskInput.getRequirementConditions();
			    return new SimpleStringProperty(requirementConditions);
			});
		}));
	}
	
	public void showDialogAndWait()
	{
		dialog.showAndWait();
	}
	
	public void setTaskName(String taskName)
	{
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setHeaderText(dialogPane.getHeaderText() + " " + taskName);
	}
	
	public void setTaskInputs(List<TaskInput<?>> taskInputs)
	{
		tvTaskInputs.getItems().setAll(taskInputs);
		GuiUtils.autoFitTableViewColumns(tvTaskInputs);
	}
}