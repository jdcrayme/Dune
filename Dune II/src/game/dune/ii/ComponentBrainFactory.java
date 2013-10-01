package game.dune.ii;

import game.dune.ii.ComponentHarvesterFactory.ComponentHarvester;
import game.dune.ii.ComponentGroundMoverFactory.ComponentGroundMover;
import game.dune.ii.ComponentGroundMoverFactory.MoveState;
import game.dune.ii.ComponentRefineryFactory.ComponentRefinery;
import game.dune.ii.ComponentWeaponFactory.ComponentWeapon;
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
public class ComponentBrainFactory  implements IGameObjectComponantFactory {

	// ===========================================================
	// Constants
	// ===========================================================
	private static boolean DEBUG = true;

	public enum OrderType{Stop, Move, Harvest, Attack, Unload}
	public enum ActionType{Stopped, Moving, Harvesting, Firing}
	
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// The Component
	// ===========================================================
	
	/**
	 * Allows an object to move
	 */
	public class ComponentBrain implements IGameObjectComponant {

		private GameObject gameObject;						//The game object

		private OrderType currentOrder = OrderType.Stop;
		private ActionType currentAction = ActionType.Stopped;
		
		private TerrainCell targetCell;						//The cell that this unit is currently interested in
		private GameObject targetObject;					//The object that this unit is currently interested in

		//
		// The components of this unit 
		//
		private ComponentGroundMover mover;
		private ComponentHarvester harvester;
		private ComponentWeapon weapon;

		/**
		 * @return The gameObject
		 */
		public GameObject getGameObject() {	return gameObject; }
		
		/**
		 * @return The order this unit is currently following
		 */
		public OrderType getCurrentOrder() { return currentOrder;	}
	
		/**
		 * @return The action this cell is currently executing
		 */
		public ActionType getCurrentAction() { return currentAction;	}

		/**
		 * @return The terrain cell this unit is currently interested in
		 */
		public TerrainCell getTargetCell() { return targetCell;	}

		/**
		 * @return The game object this unit is currently interested in
		 */
		public GameObject getTargetObject() { return targetObject;	}
		
		/**
		 * Initialize the component.
		 * This is called by GameObject on creation
		 * 
		 * @param gameObject	- The GameObject
		 */
		@Override
		public boolean initialize(GameObject gameObject) {
			this.gameObject = gameObject;

			//
			// Get the appropriate components, so that we can execute control over the unit
			//
			this.mover = gameObject.getComponant(ComponentGroundMover.class);
			this.harvester = gameObject.getComponant(ComponentHarvester.class);
			this.weapon = gameObject.getComponant(ComponentWeapon.class);

			return true;
		}

		/**
		 * Update the component 
		 * 
		 * @param messageQue	- The message list
		 */
		@Override
		public void update(){
			
			switch(currentAction){
			
				case Harvesting:
					switch(harvester.getState()){
						//
						// If we are currently harvesting, then keep it up
						//
						case Harvesting:
							break;
						//
						// If we are not harvesting, then move to next spice
						//
						case NotHarvesting:
							GetNextAction();
							break;
							
						//
						// If we are full then head back
						//
						case Full:
							ComponentRefinery tgt = harvester.findNearestRefinery();
							if(tgt == null){
								currentOrder = OrderType.Stop;
								currentAction = ActionType.Stopped;
								debug("Cannot locate refinery. Giving up.");
								break;
							}
							
							debug("Returning to refinery.");
							mover.moveTo(tgt.getCell());
							tgt.startFlashing();
							currentAction = ActionType.Moving;
							break;
					}

				case Firing:
					if(weapon!=null){
						weapon.fireAt(targetCell);
					}
					break;
				case Moving:
					//
					// If we are moving towards a target to attack it and are in range, then stop
					//
					if(currentOrder==OrderType.Attack&&mover.getDistanceSquaredTo(targetCell)<(weapon.getRange()*weapon.getRange())){
						debug("In range "+mover.getDistanceSquaredTo(targetCell)+" "+(weapon.getRange()*weapon.getRange()));
						mover.stop();
						currentAction=ActionType.Firing;
						break;
					}
					
					//
					// If we are blocked by a refinery or a repair facility then unload/repair
					//
					if(mover.getState()==MoveState.Stopped)
						if(mover.getBlockedBy()!=null){
							ComponentRefinery refinery = (ComponentRefinery) mover.getBlockedBy().getComponant(ComponentRefinery.class);
							if(refinery != null)
								refinery.Unload(gameObject);
						}else{
							GetNextAction();
						}
					break;
				default:
					break;
			}
		}

		private void GetNextAction() {
			switch(currentOrder){
			case Harvest:
				//
				// If we are over a spice cell then start sucking up spice
				//
				if(mover.getLastCell().type == LayerTerrain.TERRAIN_SPICE||mover.getLastCell().type == LayerTerrain.TERRAIN_SUPER_SPICE){
					currentAction = ActionType.Harvesting;
					harvester.harvest();
				} else {
					targetCell = harvester.findNearestSpice();
					
					if(targetCell!=null){
						mover.moveTo(targetCell);
						currentAction = ActionType.Moving;
					}else{
						currentOrder = OrderType.Stop;
						currentAction = ActionType.Stopped;
					}
				}
				break;
			default:
					currentAction = ActionType.Stopped;
				break;
			}
		}

		/**
		 * Send an order to this unit 
		 */
		public void sendOrder(OrderType order) {
			
			switch(order){

				case Harvest:
					if(mover!=null){
						if(targetCell==null){
							// If we do not have a target destination then set the nearest spice cell as the target
							targetCell = harvester.findNearestSpice();
						}

						if(targetCell!=null){
							// If we do have a target cell then move there 
							mover.moveTo(targetCell);
							currentOrder = OrderType.Harvest;
							currentAction = ActionType.Moving;
						}else{
							// If we still don't have a target then park
							currentOrder = OrderType.Stop;
							currentAction = ActionType.Stopped;
						}
					}
					break;

				case Attack:
					if(weapon!=null&&targetCell!=null&&targetCell.containedObject!=null){
						targetObject = targetCell.containedObject;
						if(mover!=null)
						{
							if(mover.getDistanceSquaredTo(targetCell)>(weapon.getRange()*weapon.getRange())&&currentAction!=ActionType.Moving){
									debug("Moving to target");
									mover.moveTo(targetCell);
									currentAction=ActionType.Moving;
							}						
							currentOrder = OrderType.Attack;
						}
					}
					break;
					
				case Unload:	
				case Move:
					if(mover!=null&&targetCell!=null){
						//If we have a target and a mover then go there
						mover.moveTo(targetCell);
						currentOrder = OrderType.Move;
						currentAction = ActionType.Moving;
					}
					break;
					
				default:
					currentOrder = order;
					break;
			}
		}
		public void sendOrder(OrderType order, GameObject target) {
			targetObject = target;
			targetCell = null;
			sendOrder(order);
		}
		public void sendOrder(OrderType order, TerrainCell target) {
			targetObject = null;
			targetCell = target;
			sendOrder(order);
		}

		/**
		 * Executes when this unit is destroyed 
		 */
		@Override
		public void destroy() {
		}

		public ArrayList<OrderType> getAvalibleCommands(TerrainCell targetCell) {
			
			ArrayList<OrderType> list = new ArrayList<OrderType>();
			//If we have a mover, and the target cell is either empty or not specified then we can move
			if(mover!=null&&(targetCell==null||targetCell.containedObject==null))
				list.add(OrderType.Move);

			//If we have a harvester..., and the target cell is either empty or not specified then we can move
			if(harvester!=null){
				if(targetCell.type == LayerTerrain.TERRAIN_SPICE||targetCell.type == LayerTerrain.TERRAIN_SUPER_SPICE)
					list.add(OrderType.Harvest);
				if(targetCell.containedObject!=null&&targetCell.containedObject.owner == gameObject.owner&&targetCell.containedObject.getComponant(ComponentRefinery.class)!=null)
					list.add(OrderType.Unload);
			}
			
			if(weapon!=null){
				if(targetCell.containedObject!=null)//&&targetCell.containedObject.owner != gameObject.owner)
					list.add(OrderType.Attack);
				
			}

			
			return list;
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
	 * Create a mover factory that will create movers
	 * 
	 * @param moveType 		- Tracked, Fly etc.
	 * @param speed			- Speed
	 * @param turnSpeed		- Turn Speed
	 */
	public ComponentBrainFactory() {
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
		return new ComponentBrain();
	}
}
