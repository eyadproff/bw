package sa.gov.nic.bio.bw.client.core.utils;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class NumberStringFilteredConverter extends NumberStringConverter
{
	private static final NumberFormat ENGLISH_NUMBER_FORMAT = NumberFormat.getInstance(Locale.ENGLISH);
	
	public UnaryOperator<TextFormatter.Change> getFilter()
	{
		return change ->
		{
			String newText = change.getControlNewText();
			if(newText.isEmpty()) return change;
			
			ParsePosition parsePosition = new ParsePosition(0 );
			Object object = ENGLISH_NUMBER_FORMAT.parse(newText, parsePosition);
			
			if(object == null || parsePosition.getIndex() < newText.length()) return null;
			else return change;
		};
	}
}