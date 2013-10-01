package game.dune.ii;

import game.dune.ii.GameObjectFactory.GameObject;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.batch.DynamicSpriteBatch;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public class LayerTerrain {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final int MAX_MOVE_PATH_COST = 90000;

	public static final int TERRAIN_STRUCTURE	= 9;	//
	public static final int TERRAIN_WALL 		= 8;	//
	public static final int TERRAIN_RUBBLE 		= 7;	//
	public static final int TERRAIN_CONCREATE 	= 6;	//
	public static final int TERRAIN_MOUNTAIN 	= 5;	//
	public static final int TERRAIN_ROCK 	 	= 4;	//These should be pretty self explanatory
	public static final int TERRAIN_SAND	 	= 3;	//
	public static final int TERRAIN_DUNE	 	= 2;	//
	public static final int TERRAIN_SPICE	 	= 1;	//
	public static final int TERRAIN_SUPER_SPICE = 0;	//

	public static final int MOUNTAIN_GRAPHIC 	= 160;		//
	public static final int ROCK_GRAPHIC	  	= 128;		//
	public static final int SAND_GRAPHIC	  	= 127;		//These are the base graphic tiles for each terrain type
	public static final int DUNE_GRAPHIC	  	= 144;		//
	public static final int SPICE_GRAPHIC	  	= 176;		//
	public static final int SUPER_SPICE_GRAPHIC = 192;	//

	// ===========================================================
	// Sub-Classes
	// ===========================================================
	public class TerrainCell
	{
		private TerrainCell[][] map;
		private int x, y;
		
		public int graphic = 127;
		public int type = 3;

		public boolean visible = false;
		
		public GameObject containedObject = null;

		TerrainCell pathParent;
		public int pathNodeCost;
		public int pathGScore; 	//the movement cost to move from the starting point A to a given 
								//square on the grid, following the path generated to get there.
		
		public int pathHScore; 	//the estimated movement cost to move from that given square on 
								//the grid to the final destination, point B. This is often 
								//referred to as the heuristic, which can be a bit confusing. The 
								//reason why it is called that is because it is a guess. We really 
								//don’t know the actual distance until we find the path, because 
								//all sorts of things can be in the way (walls, water, etc.).
		
		public TerrainCell(TerrainCell[][] map, int x, int y)
		{
			this.map = map;
			this.x = x;
			this.y = y;
		}
		
		public int getCol(){return x;}
		
		public int getRow(){return y;}
		
		public TerrainCell getTopLeftNeighbor()
		{
			if(x<2||y<2)
				return null;
			
			return map[x-1][y-1];
		}
		
		public TerrainCell getTopNeighbor()
		{
			if(y<2)
				return null;
			
			return map[x][y-1];
		}

		public TerrainCell getTopRightNeighbor()
		{
			if(x>=map.length-3||y<2)
				return null;

			return map[x+1][y-1];
		}

		public TerrainCell getLeftNeighbor()
		{
			if(x<2)
				return null;

			return map[x-1][y];
		}
		
		public TerrainCell getRightNeighbor()
		{
			if(x>=map.length-3)
				return null;

			return map[x+1][y];
		}

		public TerrainCell getBottomLeftNeighbor()
		{
			if(x<2||y>=map[0].length-3)
				return null;

			return map[x-1][y+1];
		}
		
		public TerrainCell getBottomNeighbor()
		{
			if(y>=map[0].length-3)
				return null;

			return map[x][y+1];
		}

		public TerrainCell getBottomRightNeighbor()
		{
			if(x>=map[0].length-3||y>=map[0].length-3)
				return null;

			return map[x+1][y+1];
		}
		
		public void clearPathNode(){
			pathParent = null;
			pathNodeCost = 0;
			pathGScore = 0; 
			pathHScore = 0; 
			
		}
		
		public void setPath(GameObject mover, TerrainCell parent, TerrainCell end)
		{
			this.pathParent = parent;
			
			float speed = 2;//mover.getComponant("Mover"); 
			
			if(containedObject!=null||speed == 0)
			{
				this.pathNodeCost = MAX_MOVE_PATH_COST;
			}
			else
			{
				this.pathNodeCost =  (int)(100.0f/speed);
			}
			
			this.pathHScore = getDistanceTo(end);
			
			if(this.pathParent == null) {
				this.pathGScore = 0;
			}
			else {
				this.pathGScore = pathNodeCost + pathParent.pathGScore;
			}
		}

		public int getTotalCostEstimate() { return pathGScore+pathHScore; }
		
		public int getDistanceTo(TerrainCell target)
		{
			int tx = target.getCol() - x;
			int ty = target.getRow() - y;
			
			//Squared euclidean distance
			//return (int) Math.sqrt(tx*tx + ty*ty);
			
			//Manhattan distance
			return Math.abs(tx)+Math.abs(ty);
		}

		public void reveal(int radius) {
			for(int i = -radius; i < radius; i ++)
			{
				if((x+i)>0&&(x+i)<terrainCells.length)
				{
					for( int j = - radius; j < radius; j++)
					{
						if(i*i+j*j<radius*radius&&(y+j)>0&&(y+j)<map[0].length)
							map[x+i][y+j].visible = true;
					}
				}
			}
		}

		public void setType(int terrainType) {
			this.type = terrainType;
			fixTerrainEdges();
		}
	}
	
	// ===========================================================
	// Fields
	// ===========================================================
	private BitmapTextureAtlas textureAtlas;	//The texture atlas for the terrain bitmaps

	private TiledSprite tileSprite;					//The terrain sprite
	private SpriteBatch terrainSpriteBatch;				//The terrain sprite batch
	private SpriteBatch fogSpriteBatch;				//The terrain sprite batch
	
	private TerrainCell [][] terrainCells;			//A 2 dimensional array of all the terrain tiles
	private ShaderProgram terrainShader;

	// ===========================================================
	// Constructors
	// ===========================================================
	public LayerTerrain() {
		
	}
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================
	/**
	 * @return The terrain layer entity  
	 */
	public Entity getTerrainEntity() { return terrainSpriteBatch; }

	/**
	 * @return The terrain tile set
	 */
	public Entity getFogEntity() { return fogSpriteBatch; }
	
	/**
	 * @return The left edge of the map in pixels
	 */
	public float getMinX() { return 0;	}
	
	/**
	 * @return The top edge of the map in pixels
	 */
	public float getMinY() { return 0;	}
	
	/**
	 * @return The right edge of the map in pixels 
	 */
	public float getMaxX() { return Globals.TERRAIN_TILE_SIZE*(terrainCells.length - 1); }
	
	/**
	 * @return The bottom edge of the map in pixels 
	 */
	public float getMaxY() { return Globals.TERRAIN_TILE_SIZE*(terrainCells[0].length - 1); }

	/**
	 * @return The map width in pixels
	 */
	public int getWidth() { return Globals.TERRAIN_TILE_SIZE*(terrainCells.length - 1); }

	/**
	 * @return The map height in pixels 
	 */
	public int getHeight() { return Globals.TERRAIN_TILE_SIZE*(terrainCells[0].length - 1); }

	/**
	 * @return The map width in cells
	 */
	public int getWidthCells() { return terrainCells.length - 1; }

	/**
	 * @return The map height in cells 
	 */
	public int getHeightCells() { return terrainCells[0].length - 1; }

	/**
	 * Gets a cell from the map
	 * 
	 * @param The x-coordinate of the tile
	 * @param The y-coordinate of the tile 
	 * @return The tile at (i,j) 
	 */
	public TerrainCell getCell(int i, int j) {	
		i = Math.min(i, terrainCells.length-1);
		i = Math.max(i, 0);
		j = Math.min(j, terrainCells[0].length-1);
		j = Math.max(j, 0);
		return terrainCells[i][j];	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	/**
	 * This function loads the resources necessary to render a terrain map.
	 * @param textureAtlas 
	 * 
	 * @param The calling activity
	 */
	public void createResources(ActivityGame activityGame, BitmapTextureAtlas textureAtlas) {
		
		this.textureAtlas = textureAtlas;

		//
		//Create tile region
		//
		TiledTextureRegion tileTextureRegion = TiledTextureRegion.create(textureAtlas, Globals.
				TERRAIN_LEFT, Globals.TERRAIN_TOP, 
				Globals.TERRAIN_RIGHT-Globals.TERRAIN_LEFT, Globals.TERRAIN_BOTTOM-Globals.TERRAIN_TOP, 
				(Globals.TERRAIN_RIGHT-Globals.TERRAIN_LEFT)/Globals.TERRAIN_TILE_SIZE, (Globals.TERRAIN_BOTTOM-Globals.TERRAIN_TOP)/Globals.TERRAIN_TILE_SIZE);
		
		//
		//...and use it to create the sprite
		//
		tileSprite = new TiledSprite(0, 0, Globals.TERRAIN_TILE_SIZE, Globals.TERRAIN_TILE_SIZE, tileTextureRegion, activityGame.getVertexBufferObjectManager());
		terrainShader = tileSprite.getShaderProgram();
	}

	/**
	 * Creates a random map
	 * 
	 * @param seed of the randomness
	 * @param the map width
	 * @param the map height
	 */
	public void generateMap(ActivityGame activityGame, int seed, int map_width, int map_height)	{
		
		final int width = map_width;
		final int height = map_height;

		//
		//Allocate the array of cells
		//
		terrainCells = new TerrainCell[width][height];
		
		//
		//Create the sprite batch
		//
		terrainSpriteBatch = new DynamicSpriteBatch(textureAtlas, (width-3)*(height-3), activityGame.getVertexBufferObjectManager()) {
			@Override
			public boolean onUpdateSpriteBatch() {

				for(int i=1; i<width-2;i++)			//Don't draw the edge tiles since they can't be smoothed
					for(int j=1; j<height-2;j++)
					{
						tileSprite.setCurrentTileIndex(terrainCells[i][j].graphic);
						tileSprite.setPosition(Globals.TERRAIN_TILE_SIZE*i, Globals.TERRAIN_TILE_SIZE*j);
				
						if(terrainCells[i][j].containedObject != null)
							this.setShaderProgram(terrainCells[i][j].containedObject.owner.getShader());
						else
							this.setShaderProgram(terrainShader);
						
						this.draw(tileSprite);
					}
				return true;
			}
		};

		//
		//Create the sprite batch
		//
		fogSpriteBatch = new DynamicSpriteBatch(textureAtlas, (width-3)*(height-3), activityGame.getVertexBufferObjectManager()) {
			@Override
			public boolean onUpdateSpriteBatch() {

				for(int i=1; i<width-2;i++)			//Don't draw the edge tiles since they can't be smoothed
					for(int j=1; j<height-2;j++)
					{
						if(!terrainCells[i][j].visible)
						{
							int cell = 108;
							if(!terrainCells[i][j-1].visible)
								cell+=1;
							if(!terrainCells[i+1][j].visible)
								cell+=2;
							if(!terrainCells[i][j+1].visible)
								cell+=4;
							if(!terrainCells[i-1][j].visible)
								cell+=8;
							
						tileSprite.setCurrentTileIndex(cell);
						tileSprite.setPosition(Globals.TERRAIN_TILE_SIZE*i, Globals.TERRAIN_TILE_SIZE*j);
				
						this.draw(tileSprite);
						}
					}
				return true;
			}
		};
		
		//
		// Fill the map with the terrain types,
		//
		fillTerrainTypesFromPerlinNoise(seed);
		
		//
		// then smooth the edges so that it looks right
		//
		fixTerrainEdges();
	}

	/**
	 * Populates the map with terrain types based on a seed
	 * 
	 * @param seed of the randomness
	 */
	private void fillTerrainTypesFromPerlinNoise(int seed)	{
		
		float MTN_THRESH = 0.05f;
		float RCK_THRESH = MTN_THRESH + 0.35f;

		float DUN_THRESH = 0.15f;

		float SPC_THRESH = 0.60f;
		float SSP_THRESH = SPC_THRESH + 0.25f;
		
		final int width = terrainCells.length;
		final int height = terrainCells[0].length;
		
		//Generate the perlin noise
		Perlin terrain  = new Perlin(width,height,seed);
		Perlin spice  = new Perlin(width,height,-seed);

		for (int i=0; i < terrain.getWidth() ; i++)
			   for (int j=0; j < terrain.getHeight() ; j++)
			   {
				   terrainCells[i][j] = new TerrainCell(terrainCells, i, j);
				   
				   //generate the base terrain
				   float f = terrain.GetNoise(i, j);
				   f+=1;
				   f/=2;

				   if(f<RCK_THRESH)
				   {
					   //Rock
	//				   terrainCells[i][j].graphic = ROCK_GRAPHIC;
					   terrainCells[i][j].type = TERRAIN_ROCK;	
				   }
				   
				   if(f<MTN_THRESH)
				   {
					   //Mountain
	//				   terrainCells[i][j].graphic = MOUNTAIN_GRAPHIC;
					   terrainCells[i][j].type = TERRAIN_MOUNTAIN;	      
				   }
				   
				   //If this tile is still sand then think about putting spice or dunes on it
				   if(terrainCells[i][j].type == TERRAIN_SAND)
				   {
					   //Sand
	//				   terrainCells[i][j].graphic = SAND_GRAPHIC;

					   //	generate the spice terrain
					   f = spice.GetNoise(i, j);
					   f+=1;
					   f/=2;

					   if(f<DUN_THRESH)
					   {
						   //Dune
//						   terrainCells[i][j].graphic = DUNE_GRAPHIC;
						   terrainCells[i][j].type = TERRAIN_DUNE;	      
					   }
					   
					   if(f>SPC_THRESH)
					   {
						   //Spice
	//					   terrainCells[i][j].graphic = SPICE_GRAPHIC;
						   terrainCells[i][j].type = TERRAIN_SPICE;
					   }
					   
					   if(f>SSP_THRESH)
					   {
					       //Super Spice
//						   terrainCells[i][j].graphic = SUPER_SPICE_GRAPHIC;
						   terrainCells[i][j].type = TERRAIN_SUPER_SPICE;
					   }
				   }
			   }
	}
	
	/**
	 * Smoothes the terrain edges to that they don't appear disjointed
	 */
	public void fixTerrainEdges()	{
		
		int width = terrainCells.length-1;
		int height = terrainCells[0].length-1;
		
		for (int i=1; i < width-1 ; i++)		 	//Don't smooth the edges since they don't 
			   for (int j=1; j < height-1 ; j++){ 	//have a full set of neighbors to compare to

				   TerrainCell center_tile = terrainCells[i][j];
		
				   if(center_tile.type == TERRAIN_STRUCTURE)
					   continue;
				   
				   	int lowOffset = 0;
		   			if(terrainCells[i  ][j-1].type<=center_tile.type)
		   				lowOffset |= 1;
		   			if(terrainCells[i+1][j  ].type<=center_tile.type)
		   				lowOffset |= 2;
		   			if(terrainCells[i  ][j+1].type<=center_tile.type)
		   				lowOffset |= 4;
		   			if(terrainCells[i-1][j  ].type<=center_tile.type)
		   				lowOffset |= 8;

		   			int highOffset = 0;
		   			if(terrainCells[i  ][j-1].type>=center_tile.type)
		   				highOffset |= 1;
		   			if(terrainCells[i+1][j  ].type>=center_tile.type)
		   				highOffset |= 2;
		   			if(terrainCells[i  ][j+1].type>=center_tile.type)
		   				highOffset |= 4;
		   			if(terrainCells[i-1][j  ].type>=center_tile.type)
		   				highOffset |= 8;
	
				   	switch(center_tile.type)	//We execute a different bit-shift based on the terrain type
				   	{
				   		case TERRAIN_SAND:
				   			center_tile.graphic = SAND_GRAPHIC;	//Sand is always sand
				   			break;
				   		case TERRAIN_DUNE:				//Dunes and spice are shifted opposite from rock and mountains
				   			center_tile.graphic = DUNE_GRAPHIC + lowOffset;
				   			break;
				   		case TERRAIN_SPICE:
				   			center_tile.graphic = SPICE_GRAPHIC + lowOffset;
				   			break;
				   		case TERRAIN_SUPER_SPICE:
				   			center_tile.graphic = SUPER_SPICE_GRAPHIC + lowOffset;
				   			break;
				   		case TERRAIN_MOUNTAIN:
				   			center_tile.graphic = MOUNTAIN_GRAPHIC + highOffset;
				   			break;
				   		case TERRAIN_ROCK:
				   			center_tile.graphic = ROCK_GRAPHIC + highOffset;
				   			break;
				   		default:
				   			break;
				   	}
			   }
	}

	/**
	 * Adds a structure to the map
	 */
	//public void add(Structure structure) {
	//	Globals.gameActivity.getScene().attachChild(structure.getSelectionRectangle());
	//	Globals.gameActivity.getScene().registerTouchArea(structure.getSelectionRectangle());
	//}

}