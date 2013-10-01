package game.dune.ii;

import android.util.Log;
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
public class ComponentStructureSpriteFactory implements IGameObjectComponantFactory{

	// ===========================================================
	// Sub-classes/Interfaces
	// ===========================================================
	public interface IStructureSpriteAnimationFunction {
		public int getAnimationFrame(int currentFrame, GameObject gameObject);
	}
	
	// ===========================================================
	// Fields
	// ===========================================================
	IStructureSpriteAnimationFunction animationFunction;	//The sprite animation function
	private char[][][] structureTiles;

	// ===========================================================
	// Getters/Setters
	// ===========================================================

	/**
	 * @return The structure width in tiles
	 */
	public int getWidth() {	return structureTiles[0].length; }

	/**
	 * @return The structure height in tiles
	 */
	public int getHeight() {
		return structureTiles[0][0].length;
	}

	
	// ===========================================================
	// The Component
	// ===========================================================
	/**
	 * Draws a structure sprite
	 */
	public class ComponantStructureSprite implements IGameObjectComponant, IComponentSprite{

		private LayerTerrain terrainLayer;
		private GameObject gameObject;				//The game object
		private int frame;							//The sprite frame
		private int col,row;

		public int getCol() {return col;}
		public int getRow() {return row;}

		public int getX() {return col*Globals.TERRAIN_TILE_SIZE;}
		public int getY() {return row*Globals.TERRAIN_TILE_SIZE;}

		public int getNumFrames() {	return structureTiles.length; }

		@Override
		public TerrainCell getCell(){ return Globals.gameActivity.getTerrainLayer().getCell(col, row); }

		/**
		 * Initialize the component
		 * 
		 * @param obj	- The containing game object
		 */
		@Override
		public boolean initialize(GameObject obj) {

			this.gameObject = obj;
			terrainLayer = Globals.gameActivity.getTerrainLayer();
			if(terrainLayer == null){
				Log.w(this.getClass().getSimpleName(), "Initialization failed: Terrain Layer not found.");
				return false;
			}
			
			return true;
		}

		/**
		 * Remove the sprite from the map
		 */
		public void removeFromMap() {
			for(int i = 0; i < structureTiles[frame].length; i++)
				for(int j = 0; j < structureTiles[frame][0].length; j++)
				{
					TerrainCell cell = terrainLayer.getCell(i + col, j + row);
					cell.type = LayerTerrain.TERRAIN_RUBBLE;
					cell.graphic = structureTiles[structureTiles.length-1][i][j];
					cell.containedObject = null;
				}
		}

		/**
		 * Add the sprite to the map 
		 * @param data	- The cell that the sprite is on
		 */
		public void setPosition(int x, int y) {
			col = x / Globals.TERRAIN_TILE_SIZE; 
			row = y / Globals.TERRAIN_TILE_SIZE;
			
			for(int i = 0; i < structureTiles[frame].length; i++)
				for(int j = 0; j < structureTiles[frame][0].length; j++)
				{
					TerrainCell cell = terrainLayer.getCell(i + col, j + row);
					cell.type = LayerTerrain.TERRAIN_STRUCTURE;
					cell.graphic = structureTiles[frame][i][j];
					cell.containedObject = gameObject;
				}
			
			Log.d("Structure", "Placed ("+col+","+row+")");
		}

		/**
		 * Update the component 
		 * 
		 * @param messageQue	- The message list
		 */
		@Override
		public void update(){

			//
			// Parse the messages
			//
			//parseMessages(messageQue);
			
			//
			// If the row or column are still 0 it likely means that they have not been set yet
			//
			if(row == 0 || col == 0)
				return;
			
			//
			// If the unit has a defined animation function, then retrieve the correct frame
			//
			if(animationFunction!=null)
				frame = animationFunction.getAnimationFrame(frame, gameObject);

			for(int i = 0; i < structureTiles[frame].length; i++)
				for(int j = 0; j < structureTiles[frame][0].length; j++)
				{
					TerrainCell cell = terrainLayer.getCell(i + col, j + row);
					cell.type = LayerTerrain.TERRAIN_STRUCTURE;
					cell.graphic = structureTiles[frame][i][j];
					cell.containedObject = gameObject;
				}		
		}
		
		/**
		 * @return cell	- An open cell adjacent to this sprite if one exists
		 */
		public TerrainCell getNearestOpenCell() {

			//
			// Check the top and bottom
			//
			for(int i = col-1; i < col+structureTiles[0].length+1; i++)
			{
				if(terrainLayer.getCell(i,row-1).containedObject==null)
					return terrainLayer.getCell(i,row-1);
						
				if(terrainLayer.getCell(i,row+structureTiles[0][0].length).containedObject==null) 
					return terrainLayer.getCell(i,row+structureTiles[0][0].length);
			}				
			
			//
			// Check the sides
			//
			for(int j = row-1; j < row + structureTiles[0][0].length+1; j++)
			{
				if(terrainLayer.getCell(col-1,j).containedObject==null)
					return terrainLayer.getCell(col-1,j);
					
				if(terrainLayer.getCell(col+structureTiles[0].length,j).containedObject==null) 
					return terrainLayer.getCell(col+structureTiles[0].length,j);
			}
			
			return null;
		}

		/**
		 * Called when the containing object is destroyed
		 */
		@Override
		public void destroy() {
			
			//TODO Place correct rubble tiles 
			for(int i = 0; i < structureTiles[0].length; i++)
				for(int j = 0; j < structureTiles[0][0].length; j++)
				{
					TerrainCell cell = terrainLayer.getCell(i + col, j + col);
					cell.graphic = 125;;
					cell.containedObject = null;
				}
		}
		@Override
		public void setFacingDirection(int facingDirection) {
			// Do nothing
		}
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Create a structure sprite factory
	 * 
	 * @param structureTiles		- The structure tiles
	 * @param genericAnimationFunc	- The animation function
	 */
	protected ComponentStructureSpriteFactory(char[][][] structureTiles, IStructureSpriteAnimationFunction genericAnimationFunc){
		this.structureTiles = structureTiles;
		this.animationFunction = genericAnimationFunc;
	}

	/**
	 * Spawn a component
	 * 
	 * @return The new component 	
	 */
	@Override
	public IGameObjectComponant create() {
		return new ComponantStructureSprite();
	}
}
