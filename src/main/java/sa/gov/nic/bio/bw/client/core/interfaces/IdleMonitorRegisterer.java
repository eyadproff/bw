package sa.gov.nic.bio.bw.client.core.interfaces;

import javafx.stage.Stage;

public interface IdleMonitorRegisterer
{
	void registerStageForIdleMonitoring(Stage stage);
	void unregisterStageForIdleMonitoring(Stage stage);
}