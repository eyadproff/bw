package sa.gov.nic.bio.bw.client.features.commons.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.OutputStream;

public class SaveReportAsPdfTask extends Task<Void>
{
	private JasperPrint jasperPrint;
	private OutputStream pdfOutputStream;
	
	public SaveReportAsPdfTask(JasperPrint jasperPrint, OutputStream pdfOutputStream)
	{
		this.jasperPrint = jasperPrint;
		this.pdfOutputStream = pdfOutputStream;
	}
	
	@Override
	protected Void call() throws Exception
	{
		JasperExportManager.exportReportToPdfStream(jasperPrint, pdfOutputStream);
		return null;
	}
}