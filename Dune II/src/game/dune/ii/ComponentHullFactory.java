package game.dune.ii;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import android.util.Log;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentHullFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private int infantrySpawnChance;	//The chance out of 100 that infantry will spawn when destroyed  
	private int maxHitPoints;			//The overall strength of the object

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to take damage, be destroyed and spawn infantry
	 */
	class ComponentHull implements IGameObjectComponant{

		private GameObject gameObject;	//The game object
		private int currentHitPoints;	//The amount of hit points the object currently has
		private IComponentSprite sprite;

		/**
		 * @return The number of hit-points this object has at full health
		 */
		public int getMaxHitPoints() { return maxHitPoints;	}

		/**
		 * @return The number of hit-points this object has at its current health
		 */
		public int getCurrentHitPoints() { return currentHitPoints;	}

		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;
			this.currentHitPoints = maxHitPoints;
			
//			this.gameObject.addCommand("DESTRUCT");
			
			sprite = this.gameObject.getComponant(IComponentSprite.class);
			if(sprite == null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Could not find sprite componant");
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
		}

		/**
		 * Parse the messages 
		 * 
		 * @param messageQue	- The message list
		 */
		public void damage(int damage) {
			currentHitPoints -= damage;
					
			if(currentHitPoints<0){
				Log.d("","Object destroyed");
				gameObject.destroy();
			}
		}
		
		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
			Globals.gameActivity.spawn(gameObject.getOwner(), "Explosion", (short)255, sprite.getCol(), sprite.getRow(), (short)0, "");
			
			if(Globals.random.nextFloat()<(infantrySpawnChance/100.0f))
				Globals.gameActivity.spawn(gameObject.getOwner(), "Soldier", (short)255, sprite.getCol(), sprite.getRow(), (short)0, "");
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
	public ComponentHullFactory(int maxHitPoints, int infantrySpawnChance) {
		this.maxHitPoints = maxHitPoints;
		this.infantrySpawnChance = infantrySpawnChance;
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
		return new ComponentHull();
	}
}