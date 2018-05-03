package sa.gov.nic.bio.bw.client.core.utils;

import javafx.geometry.NodeOrientation;

import java.util.Locale;

public enum GuiLanguage
{
	ARABIC(AppConstants.Locales.SAUDI_AR_LOCALE, NodeOrientation.RIGHT_TO_LEFT, "عربي", "sa/gov/nic/bio/bw/client/core/images/sa.png"),
	ENGLISH(AppConstants.Locales.SAUDI_EN_LOCALE, NodeOrientation.LEFT_TO_RIGHT, "English", "sa/gov/nic/bio/bw/client/core/images/us.png");
	
	private final Locale locale;
	private final NodeOrientation nodeOrientation;
	private final String text;
	private final String flagPath;
	
	GuiLanguage(Locale locale, NodeOrientation nodeOrientation, String text, String flagPath)
	{
		this.locale = locale;
		this.nodeOrientation = nodeOrientation;
		this.text = text;
		this.flagPath = flagPath;
	}
	
	public final Locale getLocale(){return locale;}
	public final NodeOrientation getNodeOrientation(){return nodeOrientation;}
	public final String getText(){return text;}
	public String getFlagPath(){return flagPath;}
	
	@Override
	public String toString()
	{
		return text;
	}
	
	public static GuiLanguage byText(String text)
	{
		for(GuiLanguage language : values())
		{
			if(language.getText().equals(text)) return language;
		}
		
		return null;
	}
}
