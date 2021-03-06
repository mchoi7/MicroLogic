import Control.Controller;
import Control.GameFrame;
import Control.GameThread;
import Control.Keyboard.Action;
import Control.Keyboard.State;
import Part.Capacitor;
import Part.Circuit;
import Part.Wire;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static Support.Constants.unitH;

public class Driver
{
	private GameFrame gFrame;
	private GameThread gThread;
	private Controller controller;
	private Circuit circuit;
	private Class selection = Wire.class;
	private String path = "";
	
	public static void main(String[] args)
	{
		Driver driver = new Driver();
		driver.init();
		driver.gThread.start();
	}
	
	private void init()
	{
		gFrame = new GameFrame("Micrologic");
		gThread = new GameThread(this::loop, 60);
		controller = new Controller();
		circuit = new Circuit();
		
		controller.connect(gFrame);
		gThread.setnCatch(4);
		gThread.setDaemon(true);
		
		addBindings();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> circuit.save()));
	}
	
	private void addBindings()
	{
		controller.bind(() -> gFrame.pan(0, unitH), KeyEvent.VK_W, State.PRESS, State.HOLD);
		controller.bind(() -> gFrame.pan(unitH, 0), KeyEvent.VK_A, State.PRESS, State.HOLD);
		controller.bind(() -> gFrame.pan(0, -unitH), KeyEvent.VK_S, State.PRESS, State.HOLD);
		controller.bind(() -> gFrame.pan(-unitH, 0), KeyEvent.VK_D, State.PRESS, State.HOLD);
		
		Runnable zoomIn = () -> gFrame.zoom(true);
		Runnable zoomOut = () -> gFrame.zoom(false);
		controller.bind(zoomIn, Controller.mScroll, State.PRESS);
		controller.bind(zoomOut, Controller.mScroll, State.RELEASE);
		controller.bind(zoomIn, KeyEvent.VK_E, State.PRESS, State.HOLD);
		controller.bind(zoomOut, KeyEvent.VK_Q, State.PRESS, State.HOLD);
		
		controller.bind(() -> selection = Wire.class,
				new Action(KeyEvent.VK_1, State.PRESS),
				new Action(KeyEvent.VK_SHIFT, State.PRESS, State.HOLD));
		controller.bind(() -> selection = Capacitor.class,
				new Action(KeyEvent.VK_2, State.PRESS),
				new Action(KeyEvent.VK_SHIFT, State.PRESS, State.HOLD));
		
		/*  TODO: Interpolate mouse commands between intervals */
		controller.bind(() -> circuit.put(gFrame.getX(controller.getMx()), gFrame.getY(controller.getMy()), selection),
				new Action(Controller.lClick, State.PRESS, State.HOLD),
				new Action(KeyEvent.VK_SHIFT, State.FREE));
		controller.bind(() -> circuit.remove(gFrame.getX(controller.getMx()), gFrame.getY(controller.getMy())),
				new Action(Controller.rClick, State.PRESS, State.HOLD),
				new Action(KeyEvent.VK_SHIFT, State.FREE));
		controller.bind(() -> circuit.interact(gFrame.getX(controller.getMx()), gFrame.getY(controller.getMy())),
				new Action(Controller.lClick, State.PRESS, State.HOLD),
				new Action(KeyEvent.VK_SHIFT, State.HOLD));
		
		/*Runnable record = new Runnable() {
			// runnable that takes parameter HERE
			boolean start;
			String record;
			@Override
			public void run()
			{
				start = !start;
				if (start)
				{
					record = "";
					controller.listen(KeyEvent.VK_ENTER);
				}
				else
					record = controller.getLine();
			}
		};*/
		
		controller.bind(() -> circuit.save(),
				new Action(KeyEvent.VK_CONTROL, State.PRESS, State.HOLD),
				new Action(KeyEvent.VK_S, State.PRESS));
		controller.bind(() -> circuit.open("test.file"),
				new Action(KeyEvent.VK_CONTROL, State.PRESS, State.HOLD),
				new Action(KeyEvent.VK_O, State.PRESS));
		controller.bind(() -> System.exit(0), KeyEvent.VK_ESCAPE, State.RELEASE);
	}
	
	private void loop()
	{
		controller.update();
		circuit.update();
		gFrame.render(circuit);
	}
}
