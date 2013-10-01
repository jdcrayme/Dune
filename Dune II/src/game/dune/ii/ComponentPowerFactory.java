package game.dune.ii;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentPowerFactory implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final String componantID = "POWER";
	
	// ===========================================================
	// Fields
	// ===========================================================

	//The list of power components for updating each cycle
	private static ArrayList<ComponentPower> powerComponants = new ArrayList<ComponentPower>();

	private int powerConsumption;		//The power consumed by this object per cycle

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to generate and consume power
	 */
	class ComponentPower implements IGameObjectComponant{

		private GameObject gameObject;

		/**
		 * @return The power consumption
		 */
		public int getPowerConsumption() {
			return powerConsumption;
		}

		/**
		 * Initialize the factory
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;
			powerComponants.add(this);
			
			return true;
		}

		/**
		 * Update the component
		 * @param messageQue	- The message que
		 */
		@Override
		public void update() {
		}

		/**
		 * Update power use
		 */
		public void updatePower(){
			if(powerConsumption<0)
				gameObject.owner.generatePower(-powerConsumption);
			else
				gameObject.owner.consumePower(powerConsumption);
		}

		/**
		 * Creates an explosion and spawns infantry
		 */
		@Override
		public void destroy() {
			powerComponants.remove(this);
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
	public ComponentPowerFactory(int powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The factory this component is part of 
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
		return new ComponentPower();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	/**
	 * Recalculates power consumption
	 */
	public static void update() {
		Iterator<ComponentPower> it = powerComponants.iterator();
		while(it.hasNext())	{
			ComponentPower obj = (ComponentPower)it.next();
		    obj.updatePower();
		}
	}
}