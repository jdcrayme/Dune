package game.dune.ii;

import game.dune.ii.ComponentBulletMoverFactory.ComponentBulletMover;
import game.dune.ii.ComponentUnitSpriteFactory.ComponantUnitSprite;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;
import android.util.Log;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentWeaponFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================
	private static boolean DEBUG = true;

	// ===========================================================
	// Fields
	// ===========================================================
	private int reloadTime = 30;
	private int range;

	private GameObjectFactory projectileFactory;
	
	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to fire on another object
	 */
	class ComponentWeapon implements IGameObjectComponant{

		private GameObject gameObject;	//The game object
		private ComponantUnitSprite sprite;
		private int ticks;
		
		public int getRange(){return range;}
		
		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;

			this.sprite = gameObject.getComponant(ComponantUnitSprite.class);
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
		public void update() {
			ticks++;
		}

		/**
		 * Parse the messages 
		 * 
		 * @param messageQue	- The message list
		 */
		public void fireAt(TerrainCell target) {
			if(ticks<reloadTime){
				debug("Unable to fire, reloading");
				return;
			}
			
			ticks = 0;
			
			debug("FIRE!");
			GameObject bullet = Globals.gameActivity.spawn(gameObject.getOwner(), projectileFactory, (short)255, sprite.getX(), sprite.getY(), (short)0, "");
			
			ComponentBulletMover mover = (ComponentBulletMover)bullet.getComponant(ComponentBulletMover.class);
			if(mover!=null){
				mover.moveTo(target);
			}
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
	 * Create a hull factory that will create hulls
	 * @param reloadTime 
	 * @param range 
	 * @param projectileFactory 
	 */
	public ComponentWeaponFactory(GameObjectFactory projectileFactory, int range, int reloadTime) {
		this.projectileFactory = projectileFactory;
		this.range = range*Globals.TERRAIN_TILE_SIZE;
		this.reloadTime = reloadTime;
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
		return new ComponentWeapon();
	}
}