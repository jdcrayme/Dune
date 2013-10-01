package game.dune.ii;

import game.dune.ii.ComponentGroundMoverFactory.ComponentGroundMover;
import game.dune.ii.ComponentRefineryFactory.ComponentRefinery;
import game.dune.ii.ComponentUnitSpriteFactory.ComponantUnitSprite;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;

import java.util.List;

import android.util.Log;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentHarvesterFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================
	public enum HarvesterState {NotHarvesting, Harvesting, Full}
	
	// Messages
	public static final String HARVEST = "HARVEST";

	// ===========================================================
	// Fields
	// ===========================================================
	private int spiceCapacaty;	//The maximum amount of spice that we can carry  

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to take damage, be destroyed and spawn infantry
	 */
	class ComponentHarvester implements IGameObjectComponant{

		private GameObject gameObject;	//The game object
		private int currentSpiceLoad;		
		private int current_tile;

		private HarvesterState state = HarvesterState.NotHarvesting;	//What is the harvester doing?
		private IComponentSprite sprite;

		/**
		 * @return The spice capacity of this harvester
		 */
		public int getSpiceCapacity() { return spiceCapacaty;	}

		/**
		 * @return The amount of spice currently carried
		 */
		public int getCurrentSpiceLoad() { return currentSpiceLoad;	}

		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;

			sprite = this.gameObject.getComponant(ComponantUnitSprite.class);
			if(sprite == null){
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
		public void update() {
			
			switch(state){
				case Harvesting:
					//
					// If we are over spice and harvesting...
					//
					TerrainCell currentCell = sprite.getCell();
					if(currentCell.type == LayerTerrain.TERRAIN_SPICE||currentCell.type == LayerTerrain.TERRAIN_SUPER_SPICE){
						current_tile++;	//Increment the current tile counter 
						currentSpiceLoad+=2; //Increment the current spice counter
						
						//
						// If the tile counter is high enough, turn the spice tile to sand or the super 
						// spice tile to spice then move on
						//
						if(current_tile>Globals.SPICE_PER_TILE){
							current_tile = 0; //Reset the tile counter

							if(currentCell.type == LayerTerrain.TERRAIN_SUPER_SPICE){
								currentCell.setType(LayerTerrain.TERRAIN_SPICE);	
							} else {
								currentCell.setType(LayerTerrain.TERRAIN_SAND);
								state=HarvesterState.NotHarvesting;
							}
						}

						//
						// If the harvester is full, send it home
						//
						if(currentSpiceLoad>spiceCapacaty) {
							currentSpiceLoad=spiceCapacaty;
							state=HarvesterState.Full;

/*							ComponentRefinery refinery = findNearestRefinery();
							
							if(refinery == null){
								state = HarvesterState.NotHarvesting;
								Log.d("Harvester", "No refinery. Parking");
							} else {
								moveTarget=refinery.getCell();
								
								mover.moveTo(moveTarget);
								state = HarvesterState.ReturningForUnloading;
								refinery.startFlashing();
								Log.d("Harvester", "Full, Returning");
							}*/
						}
					}
					break;
				case Full:
				case NotHarvesting:
					break;
			}
		}

/*		public void moveToNextSpice() {
			//
			// We have used this one up, find the nearest new spice tile
			//
			state = HarvesterState.MovingToSpice;
			moveTarget = findNearestSpice();
	
			if(moveTarget!=null){
				//gameObject.sendMessage(ComponentHarvesterFactory.HARVEST, moveTarget);
			} else {
				//
				// If we cannot, then change the state to NotHarvesting
				//
				state = HarvesterState.NotHarvesting;
				Log.d("Harvester", "Can't harvest");
			}
		}*/

		private TerrainCell checkCell(LayerTerrain terrain, int i, int j)
		{
			if(i>0&&i<terrain.getWidthCells()-1&&j>0&&j<terrain.getHeightCells()-1) {
				TerrainCell thisCell = terrain.getCell(i, j);
				if((thisCell.type==LayerTerrain.TERRAIN_SPICE||thisCell.type==LayerTerrain.TERRAIN_SUPER_SPICE)&&thisCell.containedObject==null) {
					return thisCell;
				}
			}
			return null;
		}
		
		public TerrainCell findNearestSpice()
		{
			LayerTerrain terrain = Globals.gameActivity.getTerrainLayer();

			int startCol = sprite.getCol();
			int startRow = sprite.getRow();
			
			//check 10 squares around the origin point
			for(int i=0; i<10; i++)
			{
				//Check the top and bottom
				for(int j=0; j<i; j++)
				{
					TerrainCell thisCell;

					//check top left
					thisCell = checkCell(terrain, startCol+j, startRow-i); 
					if(thisCell!=null)
						return thisCell;

					//check top right
					thisCell = checkCell(terrain, startCol-j, startRow-i); 
					if(thisCell!=null)
						return thisCell;

					//check bottom left
					thisCell = checkCell(terrain, startCol+j, startRow+i); 
					if(thisCell!=null)
						return thisCell;

					//check bottom right
					thisCell = checkCell(terrain, startCol-j, startRow+i); 
					if(thisCell!=null)
						return thisCell;

					//check left top
					thisCell = checkCell(terrain, startCol-i, startRow-j); 
					if(thisCell!=null)
						return thisCell;

					//check left bottom
					thisCell = checkCell(terrain, startCol-i, startRow+j); 
					if(thisCell!=null)
						return thisCell;

					//check right top
					thisCell = checkCell(terrain, startCol+i, startRow-j); 
					if(thisCell!=null)
						return thisCell;

					//check right bottom
					thisCell = checkCell(terrain, startCol+i, startRow+j); 
					if(thisCell!=null)
						return thisCell;
				}
			}
			return null;	
		}

		public ComponentRefinery findNearestRefinery(){

			List<ComponentRefinery> objects = ComponentRefineryFactory.getRefineries();
			float bestDist = Float.MAX_VALUE;
			ComponentRefinery bestRefinery = null;

			ComponentGroundMover mover = this.gameObject.getComponant(ComponentGroundMover.class);
			if(mover == null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Sprite not found.");
				return null;
			}

			for(int i = 0; i < objects.size(); i++){
				ComponentRefinery obj = objects.get(i);

				if(obj.getObject().owner!=this.gameObject.owner)
					continue;
				
				obj.getCell();
				
				float dst = mover.getDistanceTo(obj.getCell());
				
				if(dst<bestDist){
					bestDist = dst;
					bestRefinery = obj;
				}
					
			}	
			
			return bestRefinery;	
		}
		
		/**
		 * Parse the messages 
		 * 
		 * @param messageQue	- The message list
		 */
/*		private void parseMessages(ArrayList<Message> messageQue) {
			Iterator<Message> it = messageQue.iterator();
			while(it.hasNext()){
				Message message = (Message)it.next();

				if(message.message==HARVEST&&(TerrainCell)message.data!=null){
					state = HarvesterState.MovingToSpice;
					moveTarget = (TerrainCell)message.data;
					mover.moveTo(moveTarget);
					Log.d("Harvester", "Moving to Spice!");
				} else if(message.message==ComponentMoverFactory.MOVER_BLOCKED_BY&&(GameObject)message.data!=null){
					ComponentRefinery refinery = (ComponentRefinery) ((GameObject)message.data).getComponant(ComponentRefineryFactory.componantID);
					
					if(refinery!=null){
						refinery.Unload(this.gameObject);
						Log.d("Harvester", "Arrived!, Unloading");
					}
				}else{
					state = HarvesterState.NotHarvesting;
				}
			}
		}*/

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
		}

		public void loadSpice(int i) {
			this.currentSpiceLoad+=i;			
		}

		public HarvesterState getState() {
			return state;
		}

		public void harvest() {
			state = HarvesterState.Harvesting;
		}
	}

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Create a hull factory that will create hulls
	 * 
	 * @param maxHitPoints			- The hit-points of this object at full health 
	 * @param infantrySpawnChance	- The percent chance of spawning infantry on death 
	 */
	public ComponentHarvesterFactory(int spiceCapacaty) {
		this.spiceCapacaty = spiceCapacaty;
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
		return new ComponentHarvester();
	}
}