package Part;

import Support.Constants;
import Support.Drawable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static Support.Constants.*;

public class Wire implements Serializable, Drawable
{
	private static final long serialVersionUID = 1L;
	private static final Wire empty = new Wire(0, 0);
	Wire[] neighbors = {empty, empty, empty, empty};
	private Set<Wire> open = new HashSet<>(4);
	
	private List<Wire> sources = new ArrayList<>(4);
	private List<Wire> paths = new ArrayList<>(4);
	private AffineTransform at = new AffineTransform();
	
	boolean logic;
	private boolean write;
	
	Wire(int x, int y)
	{
		at.translate(x, y);
	}
	
	void interact()
	{
		write = true;
	}
	
	void setup()
	{
		logic = write;
		write = false;
	}
	
	void acquire()
	{
		if (logic)
			open.forEach(this::power);
		else
			open.forEach(wire -> wire.paths.add(this));
	}
	
	void flush()
	{
		open.removeAll(sources);
		open.addAll(paths);
		sources.clear();
		paths.clear();
	}
	
	private void power(Wire wire)
	{
		wire.write = true;
		wire.sources.add(this);
	}
	
	final void connect(int dir, Wire wire)
	{
		if (wire != null)
		{
			neighbors[dir] = wire;
			open.add(wire);
			
			wire.neighbors[(dir+2)%4] = this;
			wire.open.add(this);
		}
	}
	
	final void disconnect()
	{
		for (int dir = 0; dir < neighbors.length; dir++)
		{
			neighbors[dir].open.remove(this);
			neighbors[dir].neighbors[(dir+2)%4] = empty;
		}
		open.clear();
		neighbors = null;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		g2d.transform(at);
		g2d.setColor(logic ? Constants.red : Constants.dred);
		g2d.drawRoundRect(unitE, unitE, unit3Q, unit3Q, unit3E, unit3E);
	}
}
