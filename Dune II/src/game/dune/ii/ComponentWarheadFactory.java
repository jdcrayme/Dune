package game.dune.ii;

import game.dune.ii.ComponentHullFactory.ComponentHull;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;
import android.util.Log;

public class ComponentWarheadFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private int damage;  

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to take damage, be destroyed and spawn infantry
	 */
	class ComponentWarhead implements IGameObjectComponant{

		private GameObject gameObject;	//The game object
		private IComponentSprite sprite;

		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;

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
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
			TerrainCell cell = sprite.getCell(); 
			if(cell !=null&&cell.containedObject!=null){
				ComponentHull hull = cell.containedObject.getComponant(ComponentHull.class);
				if(hull!=null)
					hull.damage(damage);
			}
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
	public ComponentWarheadFactory(int damage) {
		this.damage = damage;
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
		return new ComponentWarhead();
	}
}
