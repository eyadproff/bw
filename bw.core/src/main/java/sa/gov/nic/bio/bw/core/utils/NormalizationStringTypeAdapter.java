package sa.gov.nic.bio.bw.core.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.Normalizer;

public class NormalizationStringTypeAdapter extends TypeAdapter<String>
{
	@Override
	public String read(final JsonReader in) throws IOException
	{
		String s = in.nextString();
		
		if(s != null) return Normalizer.normalize(s, Normalizer.Form.NFKC).trim();
		else return null;
	}
	
	@Override
	public void write(final JsonWriter out, final String value) throws IOException
	{
		if(value != null) out.value(Normalizer.normalize(value, Normalizer.Form.NFKC).trim());
		else out.value((String) null);
	}
}