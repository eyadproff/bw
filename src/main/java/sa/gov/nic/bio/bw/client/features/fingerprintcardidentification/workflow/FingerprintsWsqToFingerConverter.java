package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import sa.gov.nic.bio.bw.client.core.workflow.Converter;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FingerprintsWsqToFingerConverter implements Converter<Map<Integer, String>, List<Finger>>
{
	@Override
	public List<Finger> convert(Map<Integer, String> data)
	{
		List<Finger> fingerprints = new ArrayList<>();
		for(Entry<Integer, String> entry : data.entrySet())
		{
			fingerprints.add(new Finger(entry.getKey(), entry.getValue(), null));
		}
		
		return fingerprints;
	}
}