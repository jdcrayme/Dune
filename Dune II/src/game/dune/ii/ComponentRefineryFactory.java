package game.dune.ii;

import game.dune.ii.ComponentBrainFactory.ComponentBrain;
import game.dune.ii.ComponentBrainFactory.OrderType;
import game.dune.ii.ComponentStructureSpriteFactory.ComponantStructureSprite;
import game.dune.ii.ComponentUnitSpriteFactory.ComponantUnitSprite;
import game.dune.ii.ComponentHarvesterFactory.ComponentHarvester;
import game.dune.ii.ComponentGroundMoverFactory.ComponentGroundMover;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;

import java.util.ArrayList;

import android.util.Log;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentRefineryFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================

	public enum RefineryState {Idle, Receiving, Unloading}

	// ===========================================================
	// Fields
	// ===========================================================

	private static ArrayList<ComponentRefinery> refineries = new ArrayList<ComponentRefinery>();

	class ComponentRefinery implements IGameObjectComponant{

		private GameObject gameObject;
		private GameObject harvesterObject;
		private RefineryState state = RefineryState.Idle;
		private ComponantStructureSprite structureSprite;
		private int harvesterSpawnDelay = 3;

		/**
		 * @return The refinery state
		 */
		public RefineryState getState() {
			return state ;
		}

		/**
		 * @return The game object
		 */
		public GameObject getObject() {
			return gameObject;
		}

		public TerrainCell getCell() {
			return structureSprite.getCell();
		}

		/**
		 * @return The game object
		 */
		public void startFlashing() {
			state = RefineryState.Receiving;
		}

		/**
		 * Initialize the factory
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;
			
			structureSprite = gameObject.getComponant(ComponantStructureSprite.class);
			if(this.structureSprite==null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Sprite not found.");
				return false;
			}
			
			refineries.add(this);
			
			return true;
		}

		/**
		 * Update the component
		 * @param messageQue	- The message que
		 */
		@Override
		public void update() {
			if(harvesterSpawnDelay==0){
				harvesterObject = Globals.gameActivity.spawn(gameObject.owner, "Harvester", (short)255, 0, 0, (short)0, "Harvest");
				if(harvesterObject == null){
					Log.w(this.getClass().getSimpleName(), "Initialization failed: Unable to spawn harvetser.");
				}else{
					Unload(harvesterObject);
				}
				harvesterSpawnDelay=-1;
			}

			if(harvesterSpawnDelay>0)
				harvesterSpawnDelay--;
			
			
			if(harvesterObject!=null){
				ComponentHarvester harvester = (ComponentHarvester) harvesterObject.getComponant(ComponentHarvester.class);

				if(harvester !=null){
					state = RefineryState.Unloading;
				}
				//
				// If we are BUILDING and there is still progress needed and the player still has credits...
				//
				if(harvester.getCurrentSpiceLoad()>0){
					harvesterObject.owner.chargeCredits(-10);
					harvester.loadSpice(-10);
				} else {
					ComponantStructureSprite structureSprite = this.gameObject.getComponant(ComponantStructureSprite.class);
					if(structureSprite != null){
						TerrainCell placeCell = structureSprite.getNearestOpenCell();
						
						if(placeCell!=null){
							ComponentGroundMover mover = harvesterObject.getComponant(ComponentGroundMover.class);
							if(mover!=null){
								mover.frozen = false;
								mover.addToMap(placeCell.getCol()*Globals.TERRAIN_TILE_SIZE, placeCell.getRow()*Globals.TERRAIN_TILE_SIZE);
							}
							
							ComponentBrain brain = harvesterObject.getComponant(ComponentBrain.class);
							if(brain!=null){
								brain.sendOrder(OrderType.Harvest);
							}
							harvesterObject = null;
							state = RefineryState.Idle;
						}
					}
				}
			}
				
			
		}

		/**
		 * Does nothing
		 */
		@Override
		public void destroy() {
			refineries.remove(this);
		}

		public void Unload(GameObject obj) {
			harvesterObject = obj;
			ComponentGroundMover mover = harvesterObject.getComponant(ComponentGroundMover.class);
			if(mover!=null){
				mover.removeFromMap();
			}
			ComponantUnitSprite harvesterSprite = harvesterObject.getComponant(ComponantUnitSprite.class);
			harvesterSprite.setPosition(structureSprite.getX() + Globals.TERRAIN_TILE_SIZE*2, structureSprite.getY() + Globals.TERRAIN_TILE_SIZE/2);
			harvesterSprite.setFacingDirection(2);
		}

	}

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * A component which uses or produces power
	 * 
	 * @param powerConsumption		- The power to consume per cycle
	 */
	public ComponentRefineryFactory() {
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The factory this component is part of 
	 */
//	@Override
//	public GameObjectFactory getFactory() { return factory; }

	/**
	 * @return The refinery state
	 */
	public static ArrayList<ComponentRefinery> getRefineries() {
		return refineries;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Create a new component
	 * 
	 * @return A new component 
	 */
	@Override
	public IGameObjectComponant create() {
		return new ComponentRefinery();
	}
}