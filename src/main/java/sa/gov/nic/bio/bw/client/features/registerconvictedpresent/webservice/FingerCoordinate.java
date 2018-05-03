package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import java.awt.Point;
import java.util.Objects;

public class FingerCoordinate
{
	private Point topLeft;
	private Point topRight;
	private Point bottomLeft;
	private Point bottomRight;
	
	public FingerCoordinate(){}
	
	public FingerCoordinate(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight)
	{
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
	}
	
	public Point getTopLeft(){return topLeft;}
	public void setTopLeft(Point topLeft){this.topLeft = topLeft;}
	
	public Point getTopRight(){return topRight;}
	public void setTopRight(Point topRight){this.topRight = topRight;}
	
	public Point getBottomLeft(){return bottomLeft;}
	public void setBottomLeft(Point bottomLeft){this.bottomLeft = bottomLeft;}
	
	public Point getBottomRight(){return bottomRight;}
	public void setBottomRight(Point bottomRight){this.bottomRight = bottomRight;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerCoordinate that = (FingerCoordinate) o;
		return Objects.equals(topLeft, that.topLeft) && Objects.equals(topRight, that.topRight) &&
			   Objects.equals(bottomLeft, that.bottomLeft) && Objects.equals(bottomRight, that.bottomRight);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(topLeft, topRight, bottomLeft, bottomRight);
	}
	
	@Override
	public String toString()
	{
		return "FingerCoordinate{" + "topLeft=" + topLeft + ", topRight=" + topRight + ", bottomLeft=" + bottomLeft +
			   ", bottomRight=" + bottomRight + '}';
	}
}