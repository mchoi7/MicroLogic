package Control.Keyboard;

/*  Keys enter PRESS on the first frame, then HOLD, until released. Then enters
		RELEASE for one frame and finally returns to FREE after.*/
public enum State
{
	PRESS,
	HOLD,
	RELEASE,
	FREE
}
