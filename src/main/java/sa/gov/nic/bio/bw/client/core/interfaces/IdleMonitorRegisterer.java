package sa.gov.nic.bio.bw.client.core.interfaces;

import javafx.stage.Stage;

/**
 * Registerer of new stages to be included in the idle monitoring process.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public interface IdleMonitorRegisterer
{
	/**
	 * Register a stage to monitored.
	 *
	 * @param stage the stage to be registered
	 */
	void registerStageForIdleMonitoring(Stage stage);
	
	/**
	 * Unregister a stage from being monitored.
	 *
	 * @param stage the stage to be unregistered
	 */
	void unregisterStageForIdleMonitoring(Stage stage);
}