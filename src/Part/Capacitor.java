package Part;

import java.awt.*;

import static Support.Constants.unitH;
import static Support.Constants.unitQ;

public class Capacitor extends Wire
{
	private boolean ready;
	
	public Capacitor(int x, int y)
	{
		super(x, y);
	}
	
	@Override
	void setup()
	{
		ready = neighbors[3] != null && !neighbors[3].logic;
		super.setup();
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		super.draw(g2d);
		g2d.drawRect(unitQ, unitQ, unitH, unitH);
	}
}