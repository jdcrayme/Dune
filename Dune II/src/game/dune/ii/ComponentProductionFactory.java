package game.dune.ii;

import game.dune.ii.ComponentStructureSpriteFactory.ComponantStructureSprite;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;

import java.util.ArrayList;

import android.os.SystemClock;
import android.util.Log;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentProductionFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================
	
	public enum FactoryState {Idle, Building, Paused, Finished, Placing}

	// ===========================================================
	// Fields
	// ===========================================================
	private GameObjectFactory[] avalible_production;	//What can this factory produce
	private String[] productionNames;					//

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to build structures and units
	 */
	class ComponentProduction implements IGameObjectComponant{

		private FactoryState factoryState = FactoryState.Idle;	//What is the factory doing

		private GameObjectFactory current_production;	//What are we building
		private int current_production_cost;			//The cost of the current production
		private int current_production_build_time;		//The build time of the current production in tenthes of a second
		private int spent_credits;						//How much work have we done
		private int refund_credits;						//Do we need to give a refund

		private float cost_per_mill;					//The credits per mill when building current production

		private int placement_col = -1;					//The placement column
		private int placement_row = -1;					//The placement row
		
		private GameObject gameObject;					//The GameObject that contains this component
		private long lastTime;							//The system mills last cycle

		private ComponantStructureSprite structureSprite;

		/**
		 * @return What the factory is building 
		 */
		public GameObjectFactory getCurrentProduction(){ return current_production; }

		/**
		 * @return The cost of the current production
		 */
		public int getProductionCost() { return current_production_cost; }

		/**
		 * @return What we have spent so far
		 */
		public int getSpentCredits() { return spent_credits; }

		/**
		 * @return How many production points have been applied to current production
		 */
		public int getCurrentProgressPercent(){ return (spent_credits*100)/current_production_cost; }

		/**
		 * @return What is the factory doing
		 */
		public FactoryState getFactoryState() { return factoryState; } 

		/**
		 * @return What can the factory build
		 */
		public GameObjectFactory [] getAvalibleProduction() { return avalible_production; }

		/**
		 * Initialize the factory
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;
			lastTime = SystemClock.currentThreadTimeMillis();
			
			this.structureSprite = gameObject.getComponant(ComponantStructureSprite.class);
			if(this.structureSprite == null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Sprite not found.");
				return false;
			}
			

			
			return true;
		}

		/**
		 * Update the component
		 * @param messageQue	- The message que
		 */
		@Override
		public void update() {

			//
			// Calculate update delta 
			//
			long currentTime = SystemClock.elapsedRealtime();
			Player owner = gameObject.owner;
			long tickDelta = currentTime-lastTime;
			lastTime = currentTime;
			
			if(this.current_production!=null) {
			
				//
				// Calculate amount to charge this cycle
				//
				int charge = (int) Math.min(cost_per_mill*tickDelta, this.current_production_cost-spent_credits); 

				//
				// If we are BUILDING and there is still progress needed and the player still has credits...
				//
				if(factoryState==FactoryState.Building&&spent_credits<=this.current_production_cost&&owner.chargeCredits(charge))
					spent_credits+=charge;
			
				//
				// If the progress gets full...
				//
				if(spent_credits>=this.current_production_cost&&this.factoryState!=FactoryState.Placing)
					this.factoryState = FactoryState.Finished;

				if(this.factoryState == FactoryState.Finished)
				{
					//
					// If the current production is not a structure then set the placement row and column
					//
					if(this.current_production.getComponantFactory(ComponentUnitSpriteFactory.class)!=null){
						ComponentUnitSpriteFactory sprite = current_production.getComponantFactory(ComponentUnitSpriteFactory.class);

						//
						// If the sprite is of type ComponantUnitSprite, then add it to the unit layer,
						// Otherwise, treat it like a structure
						//
						if(sprite != null){
							TerrainCell placeCell = structureSprite.getNearestOpenCell();
							
							if(placeCell!=null){
								placement_col = placeCell.getCol();
								placement_row = placeCell.getRow();
							}
						}
					}
				}
			
				//
				// If the row and column have been set, then place the new object
				//
				if(placement_row != -1){
					Globals.gameActivity.spawn(owner, current_production, (short) 256, placement_col*Globals.TERRAIN_TILE_SIZE, placement_row*Globals.TERRAIN_TILE_SIZE, (short)0, "");
					placement_row = placement_col = -1;
				
					clearProduction();
				}
			}
			//
			// If any credits need to be refunded, then do it
			//
			int inc = Math.min(30, refund_credits);
			owner.chargeCredits(-inc);
			refund_credits-=inc;
		}

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
		}

		/**
		 * Start the factory building something
		 * 
		 * @param production	- The thing to build
		 */
		public void startProduction(GameObjectFactory production){
			if(this.factoryState != FactoryState.Idle)
				cancelProduction();

			current_production = production;

			ComponentSelectableFactory selectable = current_production.getComponantFactory(ComponentSelectableFactory.class);
			current_production_cost = selectable.getCost();
			current_production_build_time = selectable.getBuildTime();
			cost_per_mill = (current_production_cost*1.0f)/(current_production_build_time*100);
			factoryState = FactoryState.Building;
		}

		/**
		 * clears the production que without refunding anything 
		 */
		private void clearProduction(){
			this.spent_credits = 0;
			this.current_production = null;
			this.factoryState = FactoryState.Idle;
		}

		/**
		 * Stop building and refund spent credits 
		 */
		public void cancelProduction(){
			this.refund_credits += spent_credits;
			clearProduction();
		}
		
		/**
		 * Pause the current production
		 */
		public void pauseProduction(){
			if(factoryState==FactoryState.Building)
				this.factoryState = FactoryState.Paused;
			else if(factoryState==FactoryState.Paused)
				this.factoryState = FactoryState.Building;
		}
		
		/**
		 * The player is trying to decide where to place this building
		 */
		public void startPlacing() {
			factoryState=FactoryState.Placing;
		}

		/**
		 * Abort placing
		 */
		public void stopPlacing() {
			factoryState = FactoryState.Finished;		
		}

		/**
		 * Place a finished structure
		 */
		public void place(int placementCol, int placementRow) {
			this.placement_col = placementCol;
			this.placement_row = placementRow;
		}
	}

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * A component which builds game objects
	 * 
	 * @param productionNames		- The list of buildable things
	 */
	public ComponentProductionFactory(String[] productionNames) {
		this.productionNames = productionNames;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The factory which this component is a part of 
	 */
//	@Override
//	public GameObjectFactory getFactory() { return factory; }

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
		if(avalible_production == null)	{
			ArrayList<GameObjectFactory> productionFactories = new ArrayList<GameObjectFactory>();
		
			for(int i = 0; i < productionNames.length;i++) {	
				GameObjectFactory fact = GameObjectTypes.gameObjectTypes.get(productionNames[i]);
				if(fact != null)
					productionFactories.add(fact);
			}
			
			avalible_production = productionFactories.toArray(new GameObjectFactory[productionFactories.size()]);
		}
		return new ComponentProduction();
	}
}