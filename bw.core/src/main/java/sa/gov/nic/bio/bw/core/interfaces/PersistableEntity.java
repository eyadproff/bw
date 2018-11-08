package sa.gov.nic.bio.bw.core.interfaces;

import sa.gov.nic.bio.bw.core.beans.StateBundle;

/**
 * An entity whose state can be saved in a <code>StateBundle</code> to be loaded later on.
 *
 * @author Fouad Almalki
 */
public interface PersistableEntity
{
	/**
	 * On saving the state of the entity to state bundle.
	 *
	 * @param stateBundle the state bundle to save the entity's state into
	 */
	void onSaveState(StateBundle stateBundle);
	
	/**
	 * On loading the state from state bundle to the entity.
	 *
	 * @param stateBundle the state bundle to be loaded to the entity
	 */
	void onLoadState(StateBundle stateBundle);
}