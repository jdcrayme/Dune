package game.dune.ii;

import game.dune.ii.ComponentGroundMoverFactory.ComponentGroundMover;
import game.dune.ii.ComponentProductionFactory.ComponentProduction;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.LayerTerrain.TerrainCell;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.util.color.Color;

import android.view.GestureDetector;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 * 
 * This class handles the major UI states of the game play screen 
 */
public class UserInterface  implements IOnSceneTouchListener, IPinchZoomDetectorListener {
	
	// ===========================================================
	// Sub classes
	// ===========================================================
	public enum InterfaceState {Play, Place, Command}

	// ===========================================================
	// Constants
	// ===========================================================
	protected static final float FLING_DRAG = 0.80f;				//The speed at which a fling gesture slows down
	protected static final float FLING_FACTOR = 2;					//This scales the fling velocity to make it feel more natural
	protected static final float FLING_MIN_SPEED_SQUARED = 0.1f;	//The point at which the fling stops
	protected static final float MIN_ZOOM = 1.0f;					//The minimum camera zoom
	protected static final float MAX_ZOOM = 2.0f;					//The maximum camera zoom
	
	// ===========================================================
	// Fields
	// ===========================================================
	private InterfaceState interfaceState;			//The current interface state

	private float zoom = 1;							//The camera zoom
	private float cameraVelocityX;					//X-velocity used for fling
	private float cameraVelocityY;					//Y-velocity used for fling
	private Thread interfaceUpdateThread;			//Used for panning and flinging the camera 

	//The gesture detectors
	private PinchZoomDetector pinchZoomDetector; 	
	private GestureDetector basicGestureDetector;

	private UserInterfaceSelectionPanel selectionPanel;			 //The panel that slides in from the side when something is selected
	UserInterfaceStructurePlacementRectangle placementRectangle; //The rectangle that shows where a new structure will be placed

	private UserInterfaceActionMenu actionMenu;					//The action menu which pops up when giving a command

	private Rectangle nextNodeRect, finalNodeRect, currentNodeRect;		//This is for debuging movement

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Create a user interface
	 *  
	 * @param The game activity
	 * @param The game scene
	 */
	public UserInterface(Scene scene) {

		//
		// Create the movement debug rectangles
		//
		if(ActivityGame.DEBUG_MOVEMENT){
			nextNodeRect = new Rectangle(0, 0, Globals.TERRAIN_TILE_SIZE, Globals.TERRAIN_TILE_SIZE, Globals.gameActivity.getVertexBufferObjectManager());
			nextNodeRect.setColor(Color.BLUE);
			nextNodeRect.setAlpha(0.2f);
			scene.attachChild(nextNodeRect);
	
			finalNodeRect = new Rectangle(0, 0, Globals.TERRAIN_TILE_SIZE, Globals.TERRAIN_TILE_SIZE, Globals.gameActivity.getVertexBufferObjectManager());
			finalNodeRect.setColor(Color.GREEN);
			finalNodeRect.setAlpha(0.2f);
			scene.attachChild(finalNodeRect);
	
			currentNodeRect = new Rectangle(0, 0, Globals.TERRAIN_TILE_SIZE, Globals.TERRAIN_TILE_SIZE, Globals.gameActivity.getVertexBufferObjectManager());
			currentNodeRect.setColor(Color.YELLOW);
			currentNodeRect.setAlpha(0.2f);
			scene.attachChild(currentNodeRect);
		}

		//
		// Create the selection panel
		//
		selectionPanel = new UserInterfaceSelectionPanel();
		
		//
		// Create the placement rectangle for new structure
		//
		placementRectangle = new UserInterfaceStructurePlacementRectangle(this, scene);
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		//
		// Create the action menu
		//
		actionMenu = new UserInterfaceActionMenu(this);
		
		//
		// Setup the gesture detectors
		//
		pinchZoomDetector = new PinchZoomDetector(this);
		Globals.gameActivity.runOnUiThread(new Runnable() { public void run() { basicGestureDetector = new GestureDetector(new UserInterfaceGestureDetector(UserInterface.this)); }});
		
		//
		// Setup the interface update thread
		//
	    interfaceUpdateThread = new Thread(new Runnable() {
            @Override
	        public void run() {
            	
            	long lastTicks = System.currentTimeMillis()-1; //the minus 1 prevents a divide by zero on the first cycle
            	long frameCounter = 0;
            	
	            while (Globals.gameActivity.getGameState() != ActivityGame.GAME_FINISHED) {
	        		
	            	//
	            	// Calculate the frame delta in seconds
	            	//
	            	long curTicks = System.currentTimeMillis();
       				float timeDelta = 1000f/(curTicks - lastTicks);
       				lastTicks = curTicks;

       				//
       				// Update the camera behavior with speed appropriately scaled by the above delta
       				//
           			ZoomCamera camera = Globals.gameActivity.getCamera();
           			final float zoomFactor = camera.getZoomFactor();
           			camera.offsetCenter(-cameraVelocityX / zoomFactor * timeDelta * FLING_FACTOR, -cameraVelocityY / zoomFactor * timeDelta * FLING_FACTOR);
	            	
           			//
           			// If the camera is being "flung" then slow it down by applying the FLING_DRAG coefficient
           			//
	            	cameraVelocityX*=FLING_DRAG;
	            	cameraVelocityY*=FLING_DRAG;
	            			
	            	//
	            	// If the camera is moving slower than our minimum fling speed stop it completely
	            	//
	            	if(Math.abs(cameraVelocityX*cameraVelocityY)<FLING_MIN_SPEED_SQUARED)
	            		cameraVelocityX=cameraVelocityY=0;

	            	//
	            	// If we are currently placing a structure, then update the placement rectangle appropriately
	            	//
	            	if(interfaceState==InterfaceState.Place)
	            		placementRectangle.update();
	            			
	            	//
	            	// Update the selection panel every 10th frame
	            	//
	            	if(selectionPanel.isVisible()&&frameCounter%10==0)
	            		selectionPanel.update();

	            	//
	            	// Draw the path debugging squares if the selected unit can move
	            	//
           			if(selectionPanel.getSelectedObject() != null)
           			{
           				//
           				// If we are debugging the movement then draw that
           				//
           				if(ActivityGame.DEBUG_MOVEMENT){
               				//
               				// Check if the selected object has a mover component
               				//
               				ComponentGroundMover mover = selectionPanel.getSelectedObject().getComponant(ComponentGroundMover.class);

	           				if(mover != null){
	           			
	           					//
	           					// Draw the target destination
	           					//
	           					TerrainCell targetCell = mover.getTargetCell();
		            			if(targetCell!=null){
			            			finalNodeRect.setVisible(true);
			            			finalNodeRect.setPosition(targetCell.getCol()*Globals.TERRAIN_TILE_SIZE, targetCell.getRow()*Globals.TERRAIN_TILE_SIZE);
		            			}

		            			//
		            			// Draw the next cell in the path
		            			//
		            			TerrainCell nextCell = mover.getNextCell();
		            			if(nextCell!=null){
		            				nextNodeRect.setVisible(true);
		            				nextNodeRect.setPosition(nextCell.getCol()*Globals.TERRAIN_TILE_SIZE, nextCell.getRow()*Globals.TERRAIN_TILE_SIZE);
		            			}
		            			
		            			//
		            			// Draw the next cell in the path
		            			//
		            			TerrainCell currentCell = mover.getLastCell();
		            			if(currentCell!=null){
		            				currentNodeRect.setVisible(true);
		            				currentNodeRect.setPosition(currentCell.getCol()*Globals.TERRAIN_TILE_SIZE, currentCell.getRow()*Globals.TERRAIN_TILE_SIZE);
		            			}
		           							
	           				} else {
	           					finalNodeRect.setVisible(false);
	           					nextNodeRect.setVisible(false);
	           					currentNodeRect.setVisible(false);
	           				}
           				}
           			}
           			//
           			// Increment the frame counter
           			//
           			frameCounter++;

           			//
           			// Let some other processes use the CPU for a while
           			//
                    try {
                    	Thread.sleep(50); 
                    	} 
                    catch (InterruptedException e) { 
                    	e.printStackTrace(); 
                    	}
	            }}});
	    
	    //
	    // Start the interface thread
	    //
	    interfaceUpdateThread.start();
	    setNormalMode();
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The current user interface state
	 */  
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}

	/**
	 * @return the currently selected object 
	 */
	public GameObject getSelection() {
		return selectionPanel.getSelectedObject();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Selects an object for the user
	 * 
	 * @param newSelection - The object to select
	 */
	public void select(final GameObject newSelection) {
		//
		// Just pass the object to the selection panel...
		//
		selectionPanel.setSelection(newSelection);
	}

	/**
	 * Places a structure on the map 
	 */
	protected void placeStructure() {
		//
		// If we are in placement mode... 
		//
		if(interfaceState==InterfaceState.Place)
		{
			//
			// ...and we successfully place the new structure...
			//
			if(placementRectangle.place())
			{
				//
				// ...switch back to the normal mode.
				//
				setNormalMode();
			} else {
				//
				// Otherwise let the user know this is not possible
				//
				//TODO: Do some kind of buzzing thing
			}
		}
	}

	/**
	 * Turns on the placement rectangle, allowing the user to select the location for a new structure 
	 * 
	 * @param factory	- The factory that produced the new structure
	 */
	public void setPlaceStructureMode(ComponentProduction factory)
	{
		//
		// Set placement mode and turn on the rectangle
		//
		interfaceState = InterfaceState.Place;
		placementRectangle.show(factory);
	}
	
	/**
	 * Activates the action menu so the player can give the selected unit orders
	 * 
	 * @param targetEntity	- The target entity to pass 
	 * @param targetCell 	- The target cell to pass
	 */
	public void setCommandMode(GameObject targetEntity, TerrainCell targetCell)
	{
		//
		// If we don't have an object selected then do nothing. Otherwise show the action menu
		//
		if(selectionPanel.getSelectedObject() == null)
			return;
		
		interfaceState = InterfaceState.Command;
		actionMenu.show(selectionPanel.getSelectedObject(), targetEntity, targetCell);
	}

	/**
	 * Resumes normal game play
	 */
	public void setNormalMode()
	{
		//
		// Set the interface state, hide the placement rectangle and the action menu
		//
		interfaceState = InterfaceState.Play;
		placementRectangle.hide();
		actionMenu.hide();
	}

	/**
	 * Gets any GameObjects at the screen coordinates
	 *  
	 * @param x		- The screen x.
	 * @param y		- The screen y.
	 * @return The unit at x,y or null
	 */
	private GameObject getUnitNear(float x, float y) {
		float[] pt = toSceneFromScreen(x,y);
		return ComponentSelectableFactory.isUnder(pt[0], pt[1]);
	}

	/**
	 * Process touch events
	 * 
	 * @param scene			- The scene
	 * @param touchEvent 	- The touch event
	 * @return always returns true
	 */
	@Override
	public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
		
		//
		// Check if the touch event is a zoom command.
		//
		pinchZoomDetector.onTouchEvent(touchEvent);
		if(!pinchZoomDetector.isZooming()) {
			
			//
			// If not, then pass it to our custom gesture detector
			//
			basicGestureDetector.onTouchEvent(touchEvent.getMotionEvent());
		}
		return true;
	}

	/**
	 * This converts from screen coordinates to scene coordinates
	 * 
	 * @param x 	- The screen x.
	 * @param y		- The screen y.
	 * @return The scene coordinates
	 */
	private float[] toSceneFromScreen(float x, float y)
	{
		final ZoomCamera camera = Globals.gameActivity.getCamera();

		float xScale = camera.getWidthRaw()/camera.getSurfaceWidth();
		float yScale = camera.getHeightRaw()/camera.getSurfaceHeight();
		
		return camera.getSceneCoordinatesFromCameraSceneCoordinates(x*xScale, y*yScale);
	}
	
	/**
	 * This converts from scene coordinates to cell coordinates
	 * 
	 * @param x 	- The scene x.
	 * @param y		- The scene y.
	 * @return The cell coordinates
	 */
	private int[] toCellFromScene(float x, float y)
	{
		final int cellX = (int) (x/(Globals.TERRAIN_TILE_SIZE));
		final int cellY = (int) (y/(Globals.TERRAIN_TILE_SIZE));

		return new int [] {cellX,cellY};
	}
	
	/**
	 * This converts from screen coordinates to cell coordinates
	 * 
	 * @param x 	- The screen x.
	 * @param y		- The screen y.
	 * @return The cell coordinates
	 */
	private int[] toCellFromScreen(float x, float y)
	{
		float[] f = toSceneFromScreen(x,y);
		
		return toCellFromScene(f[0],f[1]);
	}
	
	/**
	 * This method scrolls the screen by a certain amount over a period of
	 * time.
	 * 
	 * @param dx	- The amount to scroll in the X direction.
	 * @param dy	- The amount to scroll in the Y direction.
	 * @param duration	The duration of the scroll.
	 */
/*	public void scrollBy (float dx, float dy, long duration) {
		final float totalAnimDx = dx;
		final float totalAnimDy = dy;
		
		//
		// Get the current time, start time, and end time for the interpolation.
		//
		final long startTime = System.currentTimeMillis();
		final long endTime = startTime + duration;

		//
		// Use a "DecelerateInterpolator" for this animation.  This interpolator
		// starts off fast, and decreases in speed as it gets closer to the
		// end of the interpolation.
		//
		final DecelerateInterpolator interpolator = new DecelerateInterpolator(1.0f);
		
		new Thread(new Runnable() {
            @Override
	        public void run() {
            	while(checkDeceleration(interpolator, totalAnimDx, totalAnimDy, startTime, endTime))
            	{}
            }}).start();
	}*/

	/**
	 * This is the method that is called by the thread that updates the map
	 * based on a DecelerateInterpolator.  It determines how far along in the
	 * interpolation process we are, and if the deceleration has completed.
	 * If the interpolator has not finished interpolating, it spawns a new
	 * thread to continue updating.
	 * @param interpolator 	- The interpolator
	 * @param totalAnimDx  	- Total movement in the x-axis 
	 * @param totalAnimDy 	- Total movement in the y-axis
	 * @param startTime 	- The movement start time
	 * @param endTime 		- The movement end time
	 * @return returns true if still moving.
	 */
	/*public boolean checkDeceleration(DecelerateInterpolator interpolator, float totalAnimDx, float totalAnimDy, long startTime, long endTime) {
		//
		// How far (percentage wise) are we through the time?
		//
		long curTime = System.currentTimeMillis();
		float percentTime = (float) (curTime - startTime) / (float) (endTime - startTime);

		//
		// Calculate the percentage distance we are through the interpolation
		// and use that to determine the distance (in pixels) we've traveled.
		//
		float percentDistance = interpolator.getInterpolation(percentTime);
		float curDx = percentDistance * totalAnimDx;
		float curDy = percentDistance * totalAnimDy;

		//
		// Use the original starting position, and the current distance
		// traveled to get the current position to be displayed.
		//
		ZoomCamera camera = Globals.gameActivity.getCamera();
		float zoomFactor = camera.getZoomFactor();
		camera.offsetCenter(curDx / zoomFactor, curDy / zoomFactor);

		//
		// If we're not done yet, repost a thread to call us again.
		//
		if (percentTime < 1.0f)
			return true;
		else
			return false;
	}*/

	/**
	 * Start pinch zoom
	 * 
	 * @param pinchZoomDetector		- The detector
	 * @param sceneTouchEvent		- The event
	 */
	@Override
	public void onPinchZoomStarted(PinchZoomDetector pinchZoomDetector, TouchEvent sceneTouchEvent) {
		ZoomCamera camera = Globals.gameActivity.getCamera();
		zoom = camera.getZoomFactor();
	}

	/**
	 * Update pinch zoom
	 * 
	 * @param pinchZoomDetector		- The detector
	 * @param touchEvent			- The event
	 * @param zoomFactor			- The zoom
	 */
	@Override
	public void onPinchZoom(PinchZoomDetector pinchZoomDetector, TouchEvent touchEvent, float zoomFactor) {
		ZoomCamera camera = Globals.gameActivity.getCamera();

		//
		// Limit the zoom to our minimum and maximum constraints and apply it to the camera 
		//
		if(camera.getZoomFactor() * zoomFactor > MAX_ZOOM){
			camera.setZoomFactor(2);
		} else if(camera.getZoomFactor() * zoomFactor < MIN_ZOOM){
			camera.setZoomFactor(1);
		} else {
			camera.setZoomFactor(zoom * zoomFactor);
		}
	}

	/**
	 * Finish pinch zoom
	 * 
	 * @param pinchZoomDetector		- The detector
	 * @param touchEvent			- The event
	 * @param zoomFactor			- The zoom
	 */
	@Override
	public void onPinchZoomFinished(PinchZoomDetector pinchZoomDetector, TouchEvent touchEvent, float zoomFactor) {
		ZoomCamera camera = Globals.gameActivity.getCamera();
		
		//
		// Limit the zoom to our minimum and maximum constraints and apply it to the camera 
		//
		if(camera.getZoomFactor() * zoomFactor > MAX_ZOOM){
			camera.setZoomFactor(2);
		} else if(camera.getZoomFactor() * zoomFactor < MIN_ZOOM){
			camera.setZoomFactor(1);
		} else {
			camera.setZoomFactor(zoom * zoomFactor);
		}
	}

	/**
	* This method is called for the "fling" type of scrolling." The velocity, x & y, are in pixels per second.
	* 
	* @param velocityX	- The velocity of this fling measured in pixels per second along the x axis.
	* @param velocityY	- The velocity of this fling measured in pixels per second along the y axis.
	* @return true (i.e. we've handled this event).
	*/
	public boolean onFling(float velocityX, float velocityY) {
		
		//
		// Scale by the screen dimensions.
		//
		velocityX /= Globals.gameActivity.getRenderSurfaceViewWidth();
		velocityY /= Globals.gameActivity.getRenderSurfaceViewHeight();

		//
		// And add the velocity
		//
		addVelocity(velocityX,velocityY);
		
		return true;	
	}

	/**
	 * This method scrolls the screen at a specific speed.
	 * 
	 * @param velocityX		- The velocity in the X direction.
	 * @param velocityY		- The velocity in the Y direction.
	 */
	private void addVelocity(final float velocityX, final float velocityY) {
		cameraVelocityX += velocityX;
		cameraVelocityY += velocityY;
	}

	/**
	 * This stops a fling if the user puts a finger on the screen
	 * 
	 * @return true, always
	 */
	public boolean onDown() {
		cameraVelocityX = cameraVelocityY = 0;
		return true;
	}
	
	/**
	 * If we double tap while something is selected, we send the coordinates to that thing. 
	 * If there is an enemy there, it will attack and pursue, otherwise it will just move
	 *
	 * @param x		- The x coordinate of the action
	 * @param y		- The y coordinate of the action
	 * @return always returns false
	 */
	public boolean onDoubleTap(float x, float y) {

		final int [] cell = toCellFromScreen(x,y);
		
		//Globals.gameActivity.runOnUiThread(new Runnable() {
		//	public void run() {
		//		Toast.makeText(Globals.gameActivity, "Double Tap. "+cell[0]+" "+cell[1], Toast.LENGTH_SHORT).show();
		//	}});
		
		if(interfaceState==InterfaceState.Play&&selectionPanel.getSelectedObject()!=null){	
			GameObject targetEntity = getUnitNear(x,y);
			setCommandMode(targetEntity, Globals.gameActivity.getTerrainLayer().getCell(cell[0], cell[1]));
		}
		
		return false;
	}

	/**
	 * If the user single taps, do nothing right now
	 * 
	 * @param x		- The x coordinate of the action
	 * @param y		- The y coordinate of the action
	 * @return always returns false
	 */
	public boolean onSingleTap(float x, float y) {

		if(interfaceState==InterfaceState.Command)
		{
			setNormalMode();
			return false;
		} else if(interfaceState == InterfaceState.Play)
		{
			//
			// Check for a unit under the cursor.
			//
			select(getUnitNear(x,y));
		}
		return false;
	}

	/**
	 * If the user drags a finger, then scroll appropriately
	 * 
	 * @param distanceX		- The distance to move in the x-axis
	 * @param distanceY		- The distance to move in the y-axis
	 * @return always true;
	 */
	public boolean onScroll(float distanceX, float distanceY) {
		ZoomCamera camera = Globals.gameActivity.getCamera();
		cameraVelocityX = cameraVelocityY = 0;

		float xScale = camera.getWidth()/camera.getSurfaceWidth();
		float yScale = camera.getHeight()/camera.getSurfaceHeight();
		
		camera.offsetCenter(distanceX * xScale, distanceY * yScale);
		return true;
	}
}