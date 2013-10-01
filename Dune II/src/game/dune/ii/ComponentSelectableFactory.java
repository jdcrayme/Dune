package game.dune.ii;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.view.View;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentSelectableFactory implements IGameObjectComponantFactory{

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static List<ComponentSelectable> selectables = new ArrayList<ComponentSelectable>();

	private int name, icon, description, cost, tech_lvl, build_time;
	private ISelectableFunctions selectionFunctions;

	// ===========================================================
	// Interface
	// ===========================================================

	public interface ISelectableFunctions {
		public void onSpawn(GameObject obj);
		public void onSelected(GameObject obj);
		public void onDeselected(GameObject obj);
		public View getSelectionPanelContentView(GameObject obj);
		public void refreshSelectionPanelContentView();
	}

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to be selected
	 */
	public class ComponentSelectable implements IGameObjectComponant{

		private GameObject gameObject;				//The game object
		private IComponentSprite sprite;
	
		/**
		 * @return The gameObject
		 */
		public GameObject getGameObject() {	return gameObject; }

		/**
		 * @return The name text resource of the item 
		 */
		public int getName() { return name; }

		/**
		 * @return The icon resource of the item 
		 */
		public int getIcon() { return icon; }

		/**
		 * @return The description text resource of the item 
		 */
		public int getDescription() { return description; }

		/**
		 * @return The base cost of the item 
		 */
		public int getCost() { return cost;	}

		/**
		 * @return The required technology level to build the item 
		 */
		public int getTechLevel() {	return tech_lvl; }

		/**
		 * @return The object selection view 
		 */
		public View getSelectionView() { 
			return selectionFunctions.getSelectionPanelContentView(gameObject); 
		}

		/**
		 * Refresh the object selection view 
		 */
		public void refreshSelectionView() { 
			selectionFunctions.refreshSelectionPanelContentView();	
		}

		/**
		 * Tell the object it is selected
		 */
		public void select() { 
			selectionFunctions.onSelected(gameObject);
		}

		/**
		 * Tell the object it is no longer selected 
		 */
		public void deselect() {
			selectionFunctions.onDeselected(gameObject);
		}

		/**
		 * Checks if a gameObject is under the coordinates
		 * 
		 * @param x		- The x coordinate
		 * @param y		- The y coordinate
		 * @return True if gameObject is under coordinates
		 */
		public boolean isUnder(float x, float y) {
			float dx = x-sprite.getX();
			float dy = y-sprite.getY();

		    float distSqr = dx*dx+dy*dy; 
			if(distSqr<Globals.TERRAIN_TILE_SIZE*Globals.TERRAIN_TILE_SIZE+Globals.TERRAIN_TILE_SIZE*Globals.TERRAIN_TILE_SIZE)
				return true;

			return false;
		}

		/**
		 * @return The object selection functions 
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;
			selectables.add(this);
			
			sprite = gameObject.getComponant(IComponentSprite.class);
			if(sprite == null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Sprite not found.");
				return false;
			}
			
			return true;
		}

		/**
		 * @return The object selection functions 
		 */
		@Override
		public void update(){
		}

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
			selectables.remove(this);
			
			if(Globals.gameActivity.getUserInterface().getSelection()==gameObject)
				Globals.gameActivity.getUserInterface().select(null);
		}
	}

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Create a selectable factory that will create selectables
	 * 
	 * @param name					- The name resource
	 * @param icon					- The icon resource
	 * @param description			- The description resource
	 * @param cost					- The base build cost
	 * @param build_time			- The build time
	 * @param tech_lvl				- The required technology level
	 * @param selectionFunctions	- The gameObject selection functions
	 */
	protected ComponentSelectableFactory(int name, int icon, int description,int cost, int build_time, int tech_lvl, ISelectableFunctions selectionFunctions){
		this.name = name;
		this.icon = icon;
		this.description = description;
		this.cost = cost;
		this.build_time = build_time;
		this.tech_lvl = tech_lvl;
		this.selectionFunctions = selectionFunctions;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The name text resource of the item 
	 */
	public int getName() { return name; }

	/**
	 * @return The icon resource of the item 
	 */
	public int getIcon() { return icon; }

	/**
	 * @return The description text resource of the item 
	 */
	public int getDescription() { return description; }

	/**
	 * @return The base cost of the item 
	 */
	public int getCost() { return cost;	}

	/**
	 * @return The build time of the item 
	 */
	public int getBuildTime() {	return build_time; }
	
	/**
	 * @return The required technology level to build the item 
	 */
	public int getTechLevel() {	return tech_lvl; }
	
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
		return new ComponentSelectable(); 
	}

	public static GameObject isUnder(float x, float y){
		
		Iterator<ComponentSelectable> it = selectables.iterator();
		while(it.hasNext()){
			ComponentSelectable obj = (ComponentSelectable)it.next();
			if(obj.isUnder(x,y))
				return obj.getGameObject();
		}
		return null;
	}
}
