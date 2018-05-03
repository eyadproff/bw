package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.UIManager;

public class PrintReportTask extends Task<Void>
{
	static
	{
		try
		{
			// The LAF for the print dialog
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	private JasperPrint jasperPrint;
	
	public PrintReportTask(JasperPrint jasperPrint)
	{
		this.jasperPrint = jasperPrint;
	}
	
	@Override
	protected Void call() throws Exception
	{
		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		printRequestAttributeSet.add(MediaSizeName.ISO_A4);
		
		SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
		configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
		configuration.setDisplayPrintDialog(true);
		
		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		
		return null;
	}
}