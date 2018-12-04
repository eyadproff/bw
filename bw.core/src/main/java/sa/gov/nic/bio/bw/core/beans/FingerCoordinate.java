package sa.gov.nic.bio.bw.core.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.awt.Point;

public class FingerCoordinate extends JavaBean
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
}