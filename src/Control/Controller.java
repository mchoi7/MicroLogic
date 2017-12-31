package Control;

import Control.Keyboard.Action;
import Control.Keyboard.State;
import org.jetbrains.annotations.Contract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*  Custom bindings implementation required due to additional constraints required:
	namely, keys must be know if they were pressed, released, held, or freed */
public final class Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	public static final int rClick = -1;
	public static final int lClick = -2;
	public static final int mScroll = -3;
	
	private Map<Integer, State> current = new HashMap<>();
	private Map<Integer, State> next = new HashMap<>();
	private Set<Binding> bindings = new HashSet<>();
	private Point mouse = new Point(), mouseLast = new Point();
	private boolean listening;
	private String line = "";
	private int regex;
	
	/*  Demonstration of Usage: varargs
		Here it is used to add additional key restraints in order to run a command.
		Expression: I want to say "Hello World" if I press ctrl+c
		Execution: bind(() -> System.out.println("Hello World"), KeyEvent.VK_C, State.PRESS, KeyEvent.VK_CONTROL); */
	public void bind(Runnable command, Action... conditions)
	{
		bindings.add(new Binding(command, conditions));
	}
	
	public void bind(Runnable command, int code, State... state)
	{
		bind(command, new Action(code, state));
	}
	
	/*  Controller must be flushed of next values at the end of every poll
		in order to translate actionable events to poll-based events */
	public void update()
	{
		bindings.forEach(binding -> binding.attempt(current));
		
		current.putAll(next);
		next.clear();
		mouseLast = new Point(mouse);
	}
	
	public void listen(int regex)
	{
		this.regex = regex;
		line = "";
		listening = true;
	}
	
	public void connect(Container container)
	{
		container.addKeyListener(this);
		container.addMouseListener(this);
		container.addMouseMotionListener(this);
		container.addMouseWheelListener(this);
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		if (listening)
		{
			char c = e.getKeyChar();
			if (c == regex)
				listening = false;
			else
				line += c;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		current.put(e.getExtendedKeyCode(), State.PRESS);
		next.put(e.getExtendedKeyCode(), State.HOLD);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		current.put(e.getExtendedKeyCode(), State.RELEASE);
		next.put(e.getExtendedKeyCode(), State.FREE);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			current.put(lClick, State.PRESS);
			next.put(lClick, State.HOLD);
		}
		else
		{
			current.put(rClick, State.PRESS);
			next.put(rClick, State.HOLD);
		}
		mouse = e.getPoint();
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			current.put(lClick, State.RELEASE);
			next.put(lClick, State.FREE);
		}
		else
		{
			current.put(rClick, State.RELEASE);
			next.put(rClick, State.FREE);
		}
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		current.put(mScroll, e.getPreciseWheelRotation() < 0 ? State.PRESS : State.RELEASE);
		next.put(mScroll, State.FREE);
		mouse = e.getPoint();
	}
	
	@Contract(pure = true)
	public int getMx()
	{
		return (int) mouse.getX();
	}
	
	@Contract(pure = true)
	public int getMy()
	{
		return (int) mouse.getY();
	}
	
	@Contract(pure = true)
	public String getLine()
	{
		return line;
	}
	
	private final class Binding
	{
		/*  Action class is also final, thus conditions are effectively immutable */
		private final Action[] conditions;
		private final Runnable command;
		
		private Binding(Runnable command, Action... conditions)
		{
			this.conditions = conditions;
			this.command = command;
		}
		
		/*  Given a map of the current key strokes, the binding will attempt
			to execute it's command if all conditions are satisfied */
		private boolean attempt(Map<Integer, State> keys)
		{
			for (Action condition : conditions)
			{
				boolean pass = false;
				for (State state : condition.states)
					if (keys.getOrDefault(condition.code, State.FREE) == state)
						pass = true;
				if (!pass)
					return false;
			}
			command.run();
			return true;
		}
	}
}
