package Support;

import org.jetbrains.annotations.Contract;

import java.awt.*;

public final class Constants
{
	public static final int unit = 16;
	public static final int unitH = unit/2;
	public static final int unitQ = unit/4;
	public static final int unit3Q = unit*3/4;
	public static final int unitE = unit/8;
	public static final int unit3E = unit*3/8;
	public static final int unit5E = unit*5/8;
	public static final int unit7E = unit*7/8;
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public static Color red = new Color(255, 0, 0, 255);
	public static Color dred = new Color(128, 0, 0, 255);
	
	@Contract(pure = true)
	public static long getXY(int x, int y)
	{
		return ((long) x<<32)|(0xFFFFFFFFL&y);
	}
	
	@Contract(pure = true)
	public static int getX(long xy)
	{
		return (int) (xy>>32);
	}
	
	@Contract(pure = true)
	public static int getY(long xy)
	{
		return (int) xy;
	}
}
