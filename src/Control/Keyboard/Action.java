package Control.Keyboard;

/*  No need for getters and setters, class acts as immutable struct */
public final class Action
{
	public final int code;
	public final State[] states;
	
	public Action(int code, State... states)
	{
		this.code = code;
		this.states = states;
	}
}
