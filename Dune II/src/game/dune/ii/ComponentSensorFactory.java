package game.dune.ii;

import android.util.Log;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.Player.Brain;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentSensorFactory implements IGameObjectComponantFactory {


	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	private int sightRange;				//The sight range

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to see
	 */
	class ComponentSensor implements IGameObjectComponant{
		
		private GameObject gameObject;
		private IComponentSprite sprite;

		/**
		 * The components sight range
		 */
		public int getSightRange() { return sightRange;	}

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
			//
			// Reveal the terrain around the unit
			//
			if(gameObject.owner.brain==Brain.HUMAN&&sprite.getCell()!=null)
				sprite.getCell().reveal(sightRange);
		}

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
		}
	}

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Create a sensor factory that will create sensors
	 * 
	 * @param sightRange			- The sight range of this object 
	 */
	public ComponentSensorFactory(int sightRange) {	this.sightRange = sightRange; }

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
		return new ComponentSensor();
	}


}