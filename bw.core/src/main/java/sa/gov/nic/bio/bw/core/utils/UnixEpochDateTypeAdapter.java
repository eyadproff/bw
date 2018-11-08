package sa.gov.nic.bio.bw.core.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

public class UnixEpochDateTypeAdapter extends TypeAdapter<Date>
{
	@Override
	public Date read(final JsonReader in) throws IOException
	{
		return new Date(in.nextLong());
	}
	
	@Override
	public void write(final JsonWriter out, final Date value) throws IOException
	{
		out.value(value.getTime());
	}
}