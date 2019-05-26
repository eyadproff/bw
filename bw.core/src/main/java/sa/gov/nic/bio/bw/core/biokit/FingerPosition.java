package sa.gov.nic.bio.bw.core.biokit;

public enum FingerPosition
{
	RIGHT_THUMB(1),
	RIGHT_INDEX(2),
	RIGHT_MIDDLE(3),
	RIGHT_RING(4),
	RIGHT_LITTLE(5),
	LEFT_THUMB(6),
	LEFT_INDEX(7),
	LEFT_MIDDLE(8),
	LEFT_RING(9),
	LEFT_LITTLE(10),
	RIGHT_THUMB_SLAP(11),
	LEFT_THUMB_SLAP(12),
	RIGHT_SLAP(13),
	LEFT_SLAP(14),
	TWO_THUMBS(15),
	RIGHT_WRITERS_PALM(22),
	LEFT_WRITERS_PALM(24),
	RIGHT_LOWER_PALM(25),
	LEFT_LOWER_PALM(27);
	
	private final int position;
	
	FingerPosition(int position)
	{
		this.position = position;
	}
	
	public final int getPosition(){return position;}
}