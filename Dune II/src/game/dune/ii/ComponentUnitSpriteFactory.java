package game.dune.ii;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;
import game.dune.ii.LayerTerrain.TerrainCell;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ComponentUnitSpriteFactory implements IGameObjectComponantFactory{

	// ===========================================================
	// Sub-classes/Interfaces
	// ===========================================================
	public interface ISpriteAnimationFunction {
		public int getAnimationFrame(int facingDirection, int currentFrame, GameObject gameObject);
	}

	// ===========================================================
	// Fields
	// ===========================================================
	private ITiledTextureRegion spriteRegion;			//The region of the sprite on the atlas
	private ISpriteAnimationFunction animationFunction;	//The sprite animation function
	private float xOffset, yOffset; 					//The sprite x/y offset from the unit x/y

	// ===========================================================
	// Getters/Setters
	// ===========================================================

	// ===========================================================
	// The Component
	// ===========================================================
	/**
	 * Draws a unit
	 */
	public class ComponantUnitSprite implements IGameObjectComponant, IComponentSprite{

		private GameObject gameObject;				//The game object
		private TiledSprite sprite;					//The sprite
		private int frame;							//The sprite frame
		private int facingDirection;				//The direction the sprite is facing
		private int x, y ;							//The position of the sprite on the map

		public int getCol() {return x/Globals.TERRAIN_TILE_SIZE;}
		public int getRow() {return y/Globals.TERRAIN_TILE_SIZE;}

		public int getX() {return x;}
		public int getY() {return y;}

		@Override
		public TerrainCell getCell(){return Globals.gameActivity.getTerrainLayer().getCell(x/Globals.TERRAIN_TILE_SIZE, y/Globals.TERRAIN_TILE_SIZE); }

		public TiledSprite getSprite() { return sprite;	}

		public void setFacingDirection(int facingDirection) { this.facingDirection = facingDirection; }
		
		public void setPosition(int x, int y) {	this.x = x;	this.y = y;	}
		
		public int getNumFrames() {	return sprite.getTileCount(); }

		public int getFacingDirection() { return facingDirection; }


		/**
		 * Initialize the component
		 * 
		 * @param obj	- The containing game object
		 */
		@Override
		public boolean initialize(GameObject obj) {
			gameObject = obj;
		
			//
			//Offset the sprite based on its size
			//
			sprite = new TiledSprite(100,100, spriteRegion, Globals.gameActivity.getVertexBufferObjectManager());
			sprite.setShaderProgram(gameObject.owner.getShader());
			Globals.gameActivity.getUnitLayer().getUnitLayer().attachChild(sprite);

			xOffset = (Globals.TERRAIN_TILE_SIZE - spriteRegion.getWidth())/2;
			yOffset = (Globals.TERRAIN_TILE_SIZE - spriteRegion.getHeight())/2;
			
			
			return true;
		}
		
		/**
		 * Update the component 
		 * 
		 * @param messageQue	- The message list
		 */
		@Override
		public void update(){

			//
			// If the unit has a defined animation function, then retrieve the correct frame
			//
			if(animationFunction!=null)
				frame = animationFunction.getAnimationFrame(facingDirection, frame, gameObject);
		
			//
			// Set the sprite
			//
			sprite.setCurrentTileIndex(frame);
			sprite.setPosition(x+xOffset, y+yOffset);
		}

		/**
		 * Called when the containing object is destroyed
		 */
		@Override
		public void destroy() {
			Globals.gameActivity.runOnUpdateThread(new Runnable() {
				
				@Override
			    public void run() {
					Globals.gameActivity.getUnitLayer().getUnitLayer().detachChild(sprite);
				}
			});
		}
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Create a unit sprite factory
	 * 
	 * @param spriteRegion			- The texture atlas region to pull sprite data from
	 * @param genericAnimationFunc	- The animation function
	 * @param xOffset				- The horizontal offset from the origin
	 * @param yOffset				- The vertical offset from the origin
	 */
	protected ComponentUnitSpriteFactory(ITiledTextureRegion spriteRegion, ISpriteAnimationFunction animationFunction, float xOffset, float yOffset){
		this.spriteRegion = spriteRegion;
		this.animationFunction = animationFunction;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	/**
	 * Spawn a component
	 * 
	 * @return The new component 	
	 */
	@Override
	public IGameObjectComponant create() {
		return new ComponantUnitSprite(); 
	}
}
