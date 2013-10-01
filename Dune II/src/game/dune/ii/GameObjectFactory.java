package game.dune.ii;

import java.util.ArrayList;
import java.util.Iterator;

import org.andengine.entity.primitive.Rectangle;

import android.util.Log;
import game.dune.ii.GameSound.Sounds;

public class GameObjectFactory {

	public interface IGameObjectComponant {
		public boolean initialize(GameObject obj);
		void update();
		void destroy();
	} 
	
	public interface IGameObjectComponantFactory {
		public IGameObjectComponant create();
	}
	
	public class Message{
		String message; 
		Object data;

		public Message(String message, Object data) {
			this.message = message;
			this.data = data;
		}
	}

	public class GameObject{

		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		protected Player owner;				//The player who owns this object
		//private char health;				//The object health
		//private float x,y;				//The object position
		//public int facingDirection;			//The direction the object is facing
		protected Rectangle selectionRect;	//The selection rectangle
		private ArrayList<IGameObjectComponant> componants;
		boolean destroyed;

		// ===========================================================
		// Constructors
		// ===========================================================

		public GameObject(Player owner)
		{
			this.owner = owner;
			componants = new ArrayList<IGameObjectComponant>();
		}
		
		/**
		 * @return The object owner 
		 */
		public Player getOwner() {	return owner; }

		/**
		 * @param The object owner 
		 */
		public void setOwner(Player owner) {	this.owner = owner; }

		/**
		 * @return The selection rectangle
		 */
		public Rectangle getSelectionRectangle() { return selectionRect; }

		/**
		 * Select this unit
		 */
		public void select() {
			//selectedObject = this;
			GameSound.sound.playSound(Sounds.Acknowledged);
		}

		/**
		 * Deselect this unit
		 */
		public void deselect() {
		}
		
		public void addComponant(IGameObjectComponant componant) {
			componants.add(componant);		
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getComponant(Class<T> type){
			for(int i = 0; i < componants.size(); i++) {
				if(type.isInstance(componants.get(i)))
					return (T)componants.get(i);
			}
			return null;
		}
		
/*		public IGameObjectComponant getComponant(String type) {
			for(int i = 0; i < componants.size(); i++) {
				if(componants.get(i).getComponantID()==type)
					return componants.get(i);
			}
			
			return null;
		}*/

		public boolean initializeComponants() {
			for(int i = 0; i < componants.size(); i++) {
				if(!componants.get(i).initialize(this)){
					Log.w(this.getClass().getSimpleName(), "Componant '" + componants.get(i).getClass().getName() +"' failed to initialize.");
					return false;
				}
			}
			return true;
		}

		public void Update() {
			Iterator<IGameObjectComponant> it = componants.iterator();
			while(it.hasNext())	{
				IGameObjectComponant componant = (IGameObjectComponant)it.next();
				componant.update();
			}
		}
		
		public void destroy(){
			Iterator<IGameObjectComponant> it = componants.iterator();
			while(it.hasNext())	{
				IGameObjectComponant componant = (IGameObjectComponant)it.next();
				componant.destroy();
			}
			this.destroyed = true;
		}
	}
	
	protected IGameObjectComponantFactory[] componantFactories ;

	
	GameObjectFactory(IGameObjectComponantFactory [] componantFactories){
		this.componantFactories = componantFactories;
	}
	
	public GameObject create(Player owner){
		GameObject newObject = new GameObject(owner);
		//newObject.setPosition(col * Globals.TERRAIN_TILE_SIZE, row * Globals.TERRAIN_TILE_SIZE);
		
		for(int i = 0; i < componantFactories.length; i++)	{
			IGameObjectComponant componant = componantFactories[i].create();
			newObject.addComponant(componant);
		}
		
		if(!newObject.initializeComponants()){
			Log.w(this.getClass().getSimpleName(), "Object failed to initialize.");
			return null;
		}
		
		return newObject;
	}

	@SuppressWarnings("unchecked")
	public <T> T getComponantFactory(Class<T> type) {
		for(int i = 0; i < componantFactories.length; i++)
		{
			if(type.isInstance(componantFactories[i]))
				return (T)componantFactories[i];
		}
		return null;
	}
}
