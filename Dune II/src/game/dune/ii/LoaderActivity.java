package game.dune.ii;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class LoaderActivity extends SimpleLayoutGameActivity implements IOnSceneTouchListener{
	// ===========================================================
	// Constants
	// ===========================================================

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

	    private LoaderActivity activity;

		public GestureListener(LoaderActivity activity) {
			this.activity = activity;
		}
	    
	    // event when double tap occurs
	    @Override
	    public boolean onDoubleTap(MotionEvent e) {
	        float x = e.getX();
	        float y = e.getY();

	        Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
	        
	        return activity.onDoubleTap(x,y);
	    }
	}
	// ===========================================================
	// Fields
	// ===========================================================
	protected Scene scene;
	protected ZoomCamera camera;

	private int renderSurfaceWidth, renderSurfaceHeight;
	private GestureDetector gestureDetector;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public LoaderActivity() {
		scene = new Scene();
	}
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return OpenGL render surface view height 
	 */
	public int getRenderSurfaceViewHeight() { return renderSurfaceHeight; }

	/**
	 * @return OpenGL render surface view width 
	 */
	public int getRenderSurfaceViewWidth() { return renderSurfaceWidth; }

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Called when the activity is first created. This is where you should do all of your normal static 
	 * set up: create views, bind data to lists, etc. This method also provides you with a Bundle 
	 * containing the activity's previously frozen state, if there was one. 
	 * 
	 * Always followed by onStart().
	 * 
	 * @param saved activity instance
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        //
        // Get the message from the intent
        //
        Intent intent = getIntent();
        renderSurfaceWidth = intent.getIntExtra(Globals.SCREEN_WIDTH_TAG, 800);
        renderSurfaceHeight = intent.getIntExtra(Globals.SCREEN_HEIGHT_TAG, 600);
    
        //
        // If someone passed 0s then make it ugly so it doesn't break
        //
        if(renderSurfaceWidth==0||renderSurfaceHeight==0)
        {
        	renderSurfaceWidth=800;
        	renderSurfaceHeight=600;
        }

        // create the new gesture detector
		gestureDetector = new GestureDetector(this, new GestureListener(this));

        //
        // Now move along
        //
		super.onCreate(savedInstanceState);
		
		
    }

	/**
	* Called when the activity is becoming visible to the user.
	* 
	* Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.	 
	*/
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    
	/**
	 * Called when the activity will start interacting with the user. At this point your activity is at 
	 * the top of the activity stack, with user input going to it. 
	 * 
	 * Always followed by onPause().
	 */
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    
	/**
	 * Called when the system is about to start resuming a previous activity. This is typically used to 
	 * commit unsaved changes to persistent data, stop animations and other things that may be consuming 
	 * CPU, etc. Implementations of this method must be very quick because the next activity will not be 
	 * resumed until this method returns. 
	 * 
	 * Followed by either onResume() if the activity returns back to the front, or onStop() if it becomes 
	 * invisible to the user.
	 */
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

	/**
	 * Called when the activity is no longer visible to the user, because another activity has been resumed 
	 * and is covering this one. This may happen either because a new activity is being started, an existing 
	 * one is being brought in front of this one, or this one is being destroyed.
	 * 
	 * Followed by either onRestart() if this activity is coming back to interact with the user, or onDestroy() 
	 * if this activity is going away.
	 */
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    
	/**
	 * The final call you receive before your activity is destroyed. This can happen either because the activity 
	 * is finishing (someone called finish() on it, or because the system is temporarily destroying this instance 
	 * of the activity to save space. 
	 * 
	 * You can distinguish between these two scenarios with the isFinishing() method.
	 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

	/**
	 * This function is called prior to creating an instance of the engine. Every activity that the 
	 * game uses will have its own instance of the engine that will run within the activity lifecycle..
	 * 
	 * @return An EngineOptions structure containing the desired engine parameters
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		Log.d("DEBUG", "Creating Engine");
		
		//Create the camera
		camera = new ZoomCamera(0, 0, renderSurfaceWidth, renderSurfaceHeight);
		
		//org.andengine.opengl.view.RenderSurfaceView
		
		//EngineOptions(fullScreen, screenOrientation, resolutionPolicy(width, height), camera)
		//fullScreen determines whether the game will be play full screen or not
		//screenOrientation, here we can choose between “LANDSCAPE” and “PORTRAIT”
		//resolutionPolicy is the ratio of you Engine(same values as in Camera)
		//camera is the game camera object
		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(renderSurfaceWidth, renderSurfaceHeight), camera);
		options.getAudioOptions().setNeedsMusic(true);
		options.getAudioOptions().setNeedsSound(true);
		return options;  
	}

	/**
	 * This function loads all the game resources into memory.
	 */
	@Override
	protected void onCreateResources() {
		Log.d("DEBUG", "Creating Resources");
		this.runOnUiThread(new Runnable(){
            ProgressDialog progressDialog;

			@Override
			public void run() {
                //
                // This is executed on UI thread before doInBackground(). It is
                // the perfect place to show the progress dialog.
                //
                progressDialog = ProgressDialog.show(LoaderActivity.this, "", "Loading...");

            	//try {
            		loadResources();
            		
                    progressDialog.dismiss();
            		
            		loadingFinished();
                //}
                //catch (Exception e) {
                	
                    //
                    // The task failed
                    //
                	
                	//if (e.getMessage()==null)
                	//	Log.e("Campaign load error: ", "Unknown Error");
                	//else
                	//	Log.e("Campaign load error: ", e.getMessage());
                	
                	//finish();
                //}

			}
			
		});
	}
	
	/**
	 * This function is called after onCreateEngineOptions and onCreateResources are executed. Here we 
	 * create the main game scene and use all the resources that onCreateResources loaded into memory.
	 *
	 * @return The Main game scene 
	 */
	@Override
	protected Scene onCreateScene() {
		Log.d("DEBUG", "Creating Scene");

		scene = new Scene();
		scene.setOnSceneTouchListener(this);
		return scene;
	}
	
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent)
	{
	    return gestureDetector.onTouchEvent(pSceneTouchEvent.getMotionEvent());
	}
	
	// ===========================================================
	// Non-inherited Methods
	// ===========================================================

	protected abstract void loadResources();
	protected abstract void loadingFinished();

	// 
	// User interface methods
	// 

	public boolean onDoubleTap(float x, float y) { return false; }
}
