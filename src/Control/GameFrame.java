package Control;

import Support.Constants;
import Support.Drawable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class GameFrame extends JFrame
{
	private Rectangle view = new Rectangle(0, 0, 15*Constants.unit, 15*Constants.unit);
	private Stroke stroke = new BasicStroke(1);
	
	public GameFrame(String title) throws HeadlessException
	{
		super(title);
		setSize(1000, 1000);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setVisible(true);
		createBufferStrategy(2);
	}
	
	public int getX(int mx)
	{
		return (int) (view.getWidth()*mx/getWidth()-view.getX());
	}
	
	public int getY(int my)
	{
		return (int) (view.getHeight()*my/getHeight()-view.getY());
	}
	
	public void pan(int dx, int dy)
	{
		view.translate(dx, dy);
	}
	
	public void zoom(boolean in)
	{
		int amount = in ? Constants.unitH : -Constants.unitH;
		
		if (view.getWidth() >= 9*Constants.unit || !in)
		{
			view.setSize((int) view.getWidth()-2*amount, (int) view.getHeight()-2*amount);
			view.translate(-amount, -amount);
		}
	}
	
	public void render(Drawable drawable)
	{
		BufferStrategy bs = getBufferStrategy();
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(stroke);
		AffineTransform at = new AffineTransform();
		at.scale(getWidth()/view.getWidth(), getHeight()/view.getHeight());
		at.translate(view.getMinX(), view.getMinY());
		g2d.transform(at);
		drawable.draw(g2d);
		bs.show();
		g2d.dispose();
	}
}