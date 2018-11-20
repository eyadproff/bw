package sa.gov.nic.bio.bw.core.interfaces;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;

public interface LocalizedText
{
	String getArabicText();
	String getEnglishText();
	
	default String getLocalizedText()
	{
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		return arabic ? getArabicText() : getEnglishText();
	}
}