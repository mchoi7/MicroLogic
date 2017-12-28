package Control;

public class GameThread extends Thread
{
	private int targetFPS = 60;
	private boolean running;
	private int nCatch = 1;
	
	public GameThread(Runnable target, int targetFPS)
	{
		super(target);
		this.targetFPS = Math.max(targetFPS, 1);
	}
	
	@Override
	public synchronized void start()
	{
		running = true;
		super.start();
	}
	
	@Override
	public void run()
	{
		long delayTime = 1000000000L/targetFPS;
		long startTime;
		long sleepDelay;
		long targetTime = System.nanoTime();
		
		while (running)
		{
			startTime = System.nanoTime();
			targetTime += delayTime;
			super.run();
			sleepDelay = (targetTime-System.nanoTime())/1000000-1;
			
			if (sleepDelay > 0)
			{
				try { sleep(sleepDelay); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			else
			{
				targetTime = Math.min(targetTime, startTime+nCatch*delayTime);
			}
		}
	}
	
	public void setnCatch(int nCatch)
	{
		this.nCatch = nCatch;
	}
	
	public void finish()
	{
		running = false;
	}
}
