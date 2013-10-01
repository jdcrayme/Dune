package game.dune.ii;

import android.util.Log;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentBulletMoverFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================
	private static boolean DEBUG = false;

	// ===========================================================
	// Fields
	// ===========================================================
	private int speed;

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to move
	 */
	public class ComponentBulletMover implements IGameObjectComponant, IComponentMover {

		private GameObject gameObject;						//The game object
		private TerrainCell targetCell;						//Where do we want to ultimately end up

		private int startX,startY;
		private int targetX, targetY;									//the speed deltas
		private int bulletX, bulletY;

		private IComponentSprite sprite;


		private double steps;
		private int counter;

		/**
		 * @return The gameObject
		 */
		public GameObject getGameObject() {	return gameObject; }
		
		/**
		 * Constructor
		 * 
		 * @param speed	- The speed
		 */
		public ComponentBulletMover() {
		}

		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;

			this.sprite = gameObject.getComponant(IComponentSprite.class);
			if(this.sprite==null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Sprite not found.");
				return false;
			}
			return true;
		}

		/**
		 * Update the component 
		 * 
		 * @param messageQue	- The message list
		 */
		@Override
		public void update(){
			updateDirection();
			updateMovement();
		}

		@Override
		public void addToMap(int x, int y) {
			this.bulletX=this.startX=x;
			this.bulletY=this.startY=y;
			sprite.setPosition(x, y);
			debug("Added bullet x: " + x + " y: " + y + " col: " + x/Globals.TERRAIN_TILE_SIZE +" row: " + y/Globals.TERRAIN_TILE_SIZE);
		}

		
		/**
		 * Parse the messages 
		 * 
		 * @param messageQue	- The message list
		 */
		public void moveTo(TerrainCell destination) {
				if(destination!=null){
					counter = 0;
					targetCell = destination;
					
					targetX = targetCell.getCol() * Globals.TERRAIN_TILE_SIZE;
					targetY = targetCell.getRow() * Globals.TERRAIN_TILE_SIZE;
					
					double range = Math.sqrt((targetX-bulletX)*(targetX-bulletX)+(targetY-bulletY)*(targetY-bulletY));
					steps = (range/speed);

					debug("firing on x: " + targetX + " y: " + targetY + " col: " + targetX/Globals.TERRAIN_TILE_SIZE +" row: " + targetY/Globals.TERRAIN_TILE_SIZE+ " Range: " + range);
				}
		}
		
		/**
		 * Points the unit in the direction of the next path cell one increment at a time
		 */ 
		private void updateDirection(){
			if(targetCell==null)
				return;
			//													3 2 1
			// Get the relative direction to the target cell	4 * 0
			//													5 6 7
			sprite.setFacingDirection(getDirectionToRelativeTarget(targetCell.getCol() * Globals.TERRAIN_TILE_SIZE-bulletX,targetCell.getRow()* Globals.TERRAIN_TILE_SIZE-bulletY));
		}

		/**
		 * Moves the unit in the direction that it is pointing at a speed based on the movement type and terrain
		 */ 
		private void updateMovement(){
			
			double trip = Math.min(counter/steps,1.0f);
			
			bulletX = (int) (startX*(1-trip)+targetX*trip);
			bulletY = (int) (startY*(1-trip)+targetY*trip);
			
			this.sprite.setPosition(bulletX, bulletY);
			
			debug("moving bullet x: " + bulletX + " y: " + bulletY + " trip: " + trip);
			
			if(counter>steps){
				debug("Detonating at Target");
				gameObject.destroy();
			}
			
			counter++;
		}
		
		/**
		 * Get the direction on our target relative to where we are pointing
		 * 
		 * @param The relative column of the target
		 * @param The relative row of the target
		 * @Return The facing direction which points at the units target
		 */ 
		private int getDirectionToRelativeTarget(float x, float y) {

			int direction = 0;
			
			//  3 2 1
			//  4 * 0
			//	5 6 7

			if(y<0) {
				direction = 2;
				
				if(x<0)
					direction = 3;
				
				if(x>0)
					direction = 1;
			}

			if(y>0) {
				direction = 6;
				
				if(x<0)
					direction = 5;
				
				if(x>0)
					direction = 7;
			}
			
			if(0==y) {
				if(x<0)
					direction = 4;
				
				if(x>0)
					direction = 0;
			}

			return direction;
		}

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
		}
		
		private void debug(String msg) {
			if(!DEBUG)
				return;
			
			Log.d(this.getClass().getSimpleName(), msg);
		}
	}
	
	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Create a mover factory that will create movers
	 * 
	 * @param moveType 		- Tracked, Fly etc.
	 * @param speed			- Speed
	 * @param turnSpeed		- Turn Speed
	 */
	public ComponentBulletMoverFactory(int speed) {
		this.speed = speed;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Spawn a component
	 * 
	 * @return The new component 	
	 */
	@Override
	public IGameObjectComponant create() {
		return new ComponentBulletMover();
	}
}
