package game.helpers;

public abstract class InerpolatorFunction {
	public void start(){};
	
	public abstract boolean update(float i);
	
	public void end(){};
}