package game.dune.ii;

import java.util.Stack;

import android.util.Log;
import game.dune.ii.ComponentUnitSpriteFactory.ComponantUnitSprite;
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
public class ComponentGroundMoverFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================
	private static boolean DEBUG = false;
	
	public enum MoveState {Stopped, Turning, Moving};
	public enum MoveType{Wheeled, Tracked, Foot}

	// ===========================================================
	// Fields
	// ===========================================================
	private MoveType moveType;
	private int speed;
	private int turnSpeed;

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to move
	 */
	public class ComponentGroundMover implements IGameObjectComponant, IComponentMover {

		private GameObject gameObject;						//The game object
		private MoveState moveState = MoveState.Stopped;	//The unit movement state
		private Stack<TerrainCell> path;					//The path to the next node
		private TerrainCell targetCell;						//Where do we want to ultimately end up
		private TerrainCell nextCell;						//What is the next node on the way
		private TerrainCell currentCell;					//The cell we are leaving
		private int x,y;									//The location of the unit
		
		private ComponantUnitSprite sprite;
		
		private int speed;									//The speed over the current cell
		private long lastCellTime;							//The time we left the last cell
		
		private int counter;								//The turn counter
		public boolean frozen;								//Freeze the thing
		private boolean active;
		private GameObject blockedBy;
		
		/**
		 * @return The gameObject
		 */
		public GameObject getGameObject() {	return gameObject; }
		
		/**
		 * @return The destination cell this unit will move to
		 */
		public MoveState getState() { return moveState;	}
	
		/**
		 * @return The destination cell this unit will move to
		 */
		public TerrainCell getTargetCell() { return targetCell;	}
	
		/**
		 * @return The next cell this unit will move to
		 */
		public TerrainCell getNextCell() {	return nextCell; }

		/**
		 * @return The last cell this unit occupied
		 */
		public TerrainCell getLastCell() {	return currentCell; }
		
		/**
		 * @return The object blocking this units path, or null
		 */
		public GameObject getBlockedBy() {	return blockedBy; }
		

		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;
//			this.gameObject.addCommand(MOVER_MOVE);
			
			this.sprite = gameObject.getComponant(ComponantUnitSprite.class);
			if(this.sprite == null){
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
			
			if(frozen)
				return;
			
			//
			// If the unit has not been placed on the board then don't do anything more
			//
			if(!active)
				return;
			
			switch(moveState){
			case Turning:
				//
				// Point the unit in a new direction if needed
				//
				updateDirection();
				break;
			
			case Moving:
				//
				// Move the unit if required
				//
				updateMovement();
				break;

			default:
				break;
			}
		}

		/**
		 * Points the unit in the direction of the next path cell one increment at a time
		 */ 
		private void updateDirection(){
			//													3 2 1
			// Get the relative direction to the target cell	4 * 0
			//													5 6 7
			int desiredFacingDirection = getDirectionToRelativeTarget(nextCell.getCol()-currentCell.getCol(),nextCell.getRow()-currentCell.getRow());

			//
			//If we are pointed the right way, then simply switch to drive mode and return...
			//
			if(desiredFacingDirection==sprite.getFacingDirection()){
				moveState = MoveState.Moving;
				lastCellTime = System.nanoTime();
				debug("Already turned");
				return;
			}
			
			//
			//...Otherwise, calculate the relative target deltas resulting from a left and right turn. 
			//
			int rightTurnDelta = getFaceDelta(sprite.getFacingDirection() - 1, desiredFacingDirection);
			int leftTurnDelta = getFaceDelta(sprite.getFacingDirection() + 1, desiredFacingDirection);

			//
			//Use a counter to delay the turning so that some vehicles will be slower than other...
			//
			counter+=turnSpeed;
			if(counter < Globals.TURN_DELAY)
				return;
			counter = 0;
			
			int facingDirection = sprite.getFacingDirection();
			//
			//...then turn in the shortest direction.
			//
			if(rightTurnDelta<leftTurnDelta)
				facingDirection--;
			else
				facingDirection++;

			//
			//Wrap it to the range 0 to 7
			//
			sprite.setFacingDirection(clockNormalize(facingDirection));
			
			//
			//If we are now pointed the right way, then drive
			//
			if(desiredFacingDirection==sprite.getFacingDirection()){
				moveState = MoveState.Moving;
				lastCellTime = System.nanoTime();
				debug("Moving: " + sprite.getFacingDirection());
			}
		}

		/**
		 * Moves the unit in the direction that it is pointing at a speed based on the movement type and terrain
		 */ 
		private void updateMovement(){

			//
			// Find how far we are between the start and end of this movement
			//
			float delta = (float)((System.nanoTime()-lastCellTime)*speed)/(float)(50000000000.0f);
			
			//
			// If the answer is over 1 then cap it and move to the next cell
			//
			if(delta > 1.0f){
				delta = Math.min(delta - 1.0f, 1.0f);
				debug("Next node. Delta: " + delta);
				transitionToNextNode();
			}
			
			//
			// Calculate the inverse of the delta and place the object at the correct interpolated position
			//
			if(nextCell!=null){
			float invDelta = 1.0f-delta;
				x =	(int) (currentCell.getCol()*Globals.TERRAIN_TILE_SIZE*invDelta+nextCell.getCol()*Globals.TERRAIN_TILE_SIZE*delta); 
				y =	(int) (currentCell.getRow()*Globals.TERRAIN_TILE_SIZE*invDelta+nextCell.getRow()*Globals.TERRAIN_TILE_SIZE*delta);
			} else {
				x =	currentCell.getCol()*Globals.TERRAIN_TILE_SIZE; 
				y = currentCell.getRow()*Globals.TERRAIN_TILE_SIZE;
			}
			
			sprite.setPosition(x, y);
		}

		/**
		 * Cycle to the next node in the unit path
		 */
		private void transitionToNextNode(){
			//
			// log the time
			//
			lastCellTime = System.nanoTime();

			//
			// We are no longer at our 'current' cell, so remove the object from it
			//
			currentCell.containedObject = null;
			
			//
			// Since we have arrived at our next cell, make it the current cell
			//
			if(nextCell!=null)
				currentCell = nextCell;
			
			//
			// If we are at the end of our path, then park.
			//
			if(currentCell==targetCell||path==null||path.empty()){
				moveState=MoveState.Stopped;
				nextCell = null;
				debug("Arrived at end of path");
				return;
			}
			
			//
			// Get the next node
			//
			nextCell = path.pop();

			//
			// If there is nothing on it then claim it. Otherwise pass a blocked message and clear the path
			//
			if(nextCell.containedObject==null||nextCell.containedObject==gameObject){
				nextCell.containedObject = gameObject;
				blockedBy = null;
			}else{
				moveState=MoveState.Stopped;
				blockedBy = nextCell.containedObject;
				debug("Path blocked!");
				nextCell = null;
				path.clear();
				return;
			}
			
			//
			// Make sure that the next node is not our current node to prevent "spinning vehicle syndrome"
			//
			if(nextCell == currentCell){
				debug("Next node is same node");
				transitionToNextNode();
				return;
			}

			//
			//Get the speed over this type of terrain
			//
			speed = getSpeed(sprite.getCell().type);

			//
			// Turn towards it
			//
			moveState = MoveState.Turning;
		}

		/**
		 * Add the unit at this position
		 * 
		 * @param The destination cell
		 */ 
		public void addToMap(int x, int y) {
			this.x = x; 
			this.y = y;

			TerrainCell cell = Globals.gameActivity.getTerrainLayer().getCell(x/Globals.TERRAIN_TILE_SIZE, y/Globals.TERRAIN_TILE_SIZE);
			
			if(cell.containedObject != null){
				Log.e("ComponantMover", "Attempted to place unit at ("+cell.getCol()+","+cell.getRow()+"), but cell already ocupied by: " + cell.containedObject);
				return;
			}
			
			active = true;
			currentCell = cell;
			nextCell=cell;
			sprite.setPosition(x, y);
			currentCell.containedObject = gameObject;
			Log.d("ComponantMover", "Unit placed ("+cell.getCol()+","+cell.getRow()+")");
		}

		/**
		 * Remove the unit from the map and no longer update the sprite
		 * 
		 * @param The destination cell
		 */ 
		public void removeFromMap(){
			active = false;
			
			if(sprite.getCell().containedObject==gameObject)
				sprite.getCell().containedObject=null;
			
			if(nextCell!=null&&nextCell.containedObject==gameObject)
				nextCell.containedObject = null;

			if(currentCell!=null&&currentCell.containedObject==gameObject)
				currentCell.containedObject = null;
		}
		
		/**
		 * Tell the unit to move toward a cell
		 * 
		 * @param The destination cell
		 */ 
		public void moveTo(TerrainCell targetCell) {
			//
			//Set the new cell as our target
			//
			this.targetCell = targetCell;
			
			//
			//If we are moving (I.E. in-between cells) make sure we finish our current move before changing direction
			//
			if(moveState == MoveState.Moving) {
				//
				//Since we have already started driving to a cell, then plan the path from that cell
				//
				path = UnitPathfinder.findPath(gameObject, nextCell, targetCell);
				
				//
				//If the path finding fails, then stop after the current move 
				//
				if(path==null){
					targetCell=nextCell;
					debug("Unable to plot path between (" + nextCell.getCol() +","+nextCell.getRow()+") and (" + targetCell.getCol()+","+targetCell.getRow()+")");
				}else
					debug("Revised " + path.size()+ " cells between (" + nextCell.getCol() +","+nextCell.getRow()+") and (" + targetCell.getCol()+","+targetCell.getRow()+")");
			}else{
				//
				//If we are not moving, plot the path from the unit's current cell
				//
				currentCell = sprite.getCell();
				path = UnitPathfinder.findPath(gameObject, currentCell, targetCell);
				
				//
				//If the path finding fails, then don't move at all, otherwise go to the next node 
				//
				if(path==null){
					targetCell=currentCell;
					debug("Unable to plot path between (" + currentCell.getCol() +","+currentCell.getRow()+") and (" + targetCell.getCol()+","+targetCell.getRow()+")");
				} else {
					debug("Plotted " + path.size()+ " cells between (" + currentCell.getCol() +","+currentCell.getRow()+") and (" + targetCell.getCol()+","+targetCell.getRow()+")");
					transitionToNextNode();
				}
			}
		}

		/**
		 * Tell the unit to point toward a cell
		 * 
		 * @param The destination cell
		 */ 
		public void pointTo(TerrainCell targetCell) {
			//TODO t
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
		 * Returns the difference between two directions
		 * 
		 * @param Facing direction 1
		 * @param Facing direction 2
		 * @return The delta between the two facing directions
		 */ 
		private int getFaceDelta(int face1, int face2) {
			return clockNormalize(clockNormalize(face1) - clockNormalize(face2));
		}
		
		/**
		 * Normalizes a direction
		 * 
		 * @param A direction variable
		 * @return The direction variable wrapped to the range 0 - 7
		 */ 
		private int clockNormalize(int direction) {
			int retVal = direction % 8;
			
			if(retVal<0)
				retVal +=8;
			
			return retVal;
		}

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
			if(nextCell!=null&&nextCell.containedObject==gameObject)
				nextCell.containedObject = null;

			if(currentCell!=null&&currentCell.containedObject==gameObject)
				currentCell.containedObject = null;

		}

		public int getDistanceTo(TerrainCell cell) {
			return (int) Math.sqrt(getDistanceSquaredTo(cell));
		}
		
		public int getDistanceSquaredTo(TerrainCell cell) {
			int dx = cell.getCol()*Globals.TERRAIN_TILE_SIZE - x;
			int dy = cell.getRow()*Globals.TERRAIN_TILE_SIZE - y;
			
			return (dx*dx+dy*dy);
		}

		private void debug(String msg) {
			if(!DEBUG)
				return;
			
			Log.d(this.getClass().getSimpleName(), msg);
		}

		public void stop() {
			targetCell=nextCell;
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
	public ComponentGroundMoverFactory(MoveType moveType, int speed, int turnSpeed) {
		this.moveType = moveType;
		this.speed = speed;
		this.turnSpeed = turnSpeed;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The factory this component is part of 
	 */
	//@Override
	//public GameObjectFactory getFactory() { return factory; }

	/**
	 * The speed over the current terrain
	 * 
	 * @param type	- The terrain type
	 */
	public int getSpeed(int type) {
		
		switch(moveType){
		
			default:
				return speed;
		}
	}

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
		return new ComponentGroundMover();
	}
}
