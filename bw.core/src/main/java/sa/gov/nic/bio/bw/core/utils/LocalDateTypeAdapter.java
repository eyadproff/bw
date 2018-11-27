package sa.gov.nic.bio.bw.core.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate>
{
	@Override
	public LocalDate read(final JsonReader in) throws IOException
	{
		return AppUtils.secondsToGregorianDate(in.nextLong());
	}
	
	@Override
	public void write(final JsonWriter out, final LocalDate value) throws IOException
	{
		out.value(AppUtils.gregorianDateToSeconds(value));
	}
}