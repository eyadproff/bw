package sa.gov.nic.bio.bw.features.cancelcriminal;

import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.features.cancelcriminal.controllers.CancelCriminalPaneFxController;
import sa.gov.nic.bio.bw.features.cancelcriminal.workflow.CancelCriminalWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.lookups.SamisIdTypesLookup;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(id = "menu.cancel.cancelCriminal", title = "menu.title", order = 1)
@WithLookups(SamisIdTypesLookup.class)
public class CancelCriminalWorkflow extends SinglePageWorkflowBase
{
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(CancelCriminalPaneFxController.class);
		
		passData(CancelCriminalPaneFxController.class, CancelCriminalWorkflowTask.class,
		         "criminalId", "inquiryId", "personId", "samisIdType", "cancelCriminalMethod");
		
		executeTask(CancelCriminalWorkflowTask.class);
		passData(CancelCriminalWorkflowTask.class, CancelCriminalPaneFxController.class, "success");
	}
}