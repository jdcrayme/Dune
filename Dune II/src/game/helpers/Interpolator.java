package game.helpers;

public class Interpolator {
	static final int ms_per_step = 40;

	public static void StepInterpolate(final float start, final float end, final float time, final InerpolatorFunction function)
	{
		
    	Thread thread = new Thread(new Runnable() {
    	    public void run() {
    			int steps = (int) (time/ms_per_step);
    			float s = start;
    			float e = end;
    			float delta = (end - start)/steps;
    			
   	           	try {
   	           		for(int i = 0; i < steps; i++)
   	           		{
   	           			if(!function.update(s + delta * i))
   	           				break;
   	           			Thread.sleep(ms_per_step);
   	           		}
   	           		function.update(e);
   	           	} catch (Exception err) {
   	           		err.printStackTrace();
   	           	}
    	    }
    	});
	
		function.start();

    	thread.run();
    	
		function.end();

	}
}
