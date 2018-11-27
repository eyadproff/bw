package sa.gov.nic.bio.bw.core.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;

public class ZonedDateTimeTypeAdapter extends TypeAdapter<ZonedDateTime>
{
	@Override
	public ZonedDateTime read(final JsonReader in) throws IOException
	{
		return AppUtils.secondsToGregorianDateTime(in.nextLong());
	}
	
	@Override
	public void write(final JsonWriter out, final ZonedDateTime value) throws IOException
	{
		out.value(AppUtils.gregorianDateTimeToSeconds(value));
	}
}