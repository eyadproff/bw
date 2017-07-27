package sa.gov.nic.bio.bw.client.core.interfaces;

import sa.gov.nic.bio.bw.client.core.StateBundle;

/**
 * Created by Fouad on 17-Jul-17.
 */
public interface LanguageSwitchingController
{
	void onSaveState(StateBundle stateBundle);
	void onLoadState(StateBundle stateBundle);
}