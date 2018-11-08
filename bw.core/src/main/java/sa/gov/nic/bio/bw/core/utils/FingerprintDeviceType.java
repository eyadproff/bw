package sa.gov.nic.bio.bw.core.utils;

import sa.gov.nic.bio.bw.core.biokit.FingerPosition;

public enum FingerprintDeviceType
{
	SINGLE(FingerPosition.RIGHT_THUMB.getPosition()),
	SLAP(FingerPosition.RIGHT_SLAP.getPosition());
	
	private final int position;
	
	FingerprintDeviceType(int position)
	{
		this.position = position;
	}
	
	public int getPosition(){return position;}
}