package Part;

import Support.Drawable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static Support.Constants.getXY;
import static Support.Constants.unit;

public class Circuit implements Drawable, Serializable
{
	private static final long serialVersionUID = 1L;
	private Map<Long, Wire> wireMap = new HashMap<>();
	
	public void interact(int x, int y)
	{
		x = unit*Math.floorDiv(x, unit);
		y = unit*Math.floorDiv(y, unit);
		
		Wire wire = wireMap.get(getXY(x, y));
		if (wire != null)
			wire.interact();
	}
	
	public Wire put(int x, int y, Class type)
	{
		x = unit*Math.floorDiv(x, unit);
		y = unit*Math.floorDiv(y, unit);
		
		Wire curr;
		if (type.equals(Capacitor.class))
			curr = new Wire(x, y);
		else
			curr = new Wire(x, y);
		Wire prev = wireMap.put(getXY(x, y), curr);
		if (prev != null) prev.disconnect();
		curr.connect(0, wireMap.get(getXY(x, y-unit)));
		curr.connect(1, wireMap.get(getXY(x+unit, y)));
		curr.connect(2, wireMap.get(getXY(x, y+unit)));
		curr.connect(3, wireMap.get(getXY(x-unit, y)));
		return prev;
	}
	
	public Wire remove(int x, int y)
	{
		x = unit*Math.floorDiv(x, unit);
		y = unit*Math.floorDiv(y, unit);
		
		Wire prev = wireMap.remove(getXY(x, y));
		if (prev != null)
			prev.disconnect();
		return prev;
	}
	
	public void update()
	{
		Collection<Wire> wires = wireMap.values();
		wires.forEach(Wire::setup);
		wires.forEach(Wire::acquire);
		wires.forEach(Wire::flush);
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		AffineTransform restore = g2d.getTransform();
		for (Wire wire : wireMap.values())
		{
			wire.draw(g2d);
			g2d.setTransform(restore);
		}
	}
	
	public void save(String path)
	{
		try
		{
			if (new File(path).exists())
				path+="("+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss"))+")";
			System.out.println(path);
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
			System.out.println("Save to: "+path+" successful.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("Save to: "+path+" fail.");
		}
	}
	
	public static Circuit open(String path)
	{
		Circuit circuit = new Circuit();
		try
		{
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			circuit = (Circuit) ois.readObject();
			ois.close();
			System.out.println("Open from: "+path+" successful.");
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
			System.err.println("Open from: "+path+" fail.");
		}
		return circuit;
	}
}