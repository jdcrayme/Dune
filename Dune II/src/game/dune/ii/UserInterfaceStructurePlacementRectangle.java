package game.dune.ii;

import game.dune.ii.ComponentProductionFactory.ComponentProduction;
import game.dune.ii.GameObjectFactory.GameObject;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import android.util.Log;

/**
 * This is the rectangle that shows where a new structure is going to go
 */
class UserInterfaceStructurePlacementRectangle
{
	// ===========================================================
	// Fields
	// ===========================================================

	private final UserInterface userInterface;	//The user interface

	private ComponentProduction placingFactory;	//The factory that built the structure we are placing

	private int placementCol;					//The currently selected map column
	private int placementRow;					//The currently selected map row
	private int placementWidth;					//The structure's width in cells
	private int placementHeight;				//The structure's height in cells
	
	private Rectangle placementRect;			//The red/green rectangle on the screen
	private boolean canPlace;					//True if the location can support the structure

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * This is the rectangle that shows where a new structure is going to go
	 * 
	 * @param userInterface			- The user interface
	 * @param scene					- The scene
	 */
	public UserInterfaceStructurePlacementRectangle(UserInterface userInterface, Scene scene)
	{
		this.userInterface = userInterface;
		
		//
		// Build the placement rectangle
		//
		placementRect = new Rectangle(0, 0, Globals.TERRAIN_TILE_SIZE, Globals.TERRAIN_TILE_SIZE, Globals.gameActivity.getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction()==TouchEvent.ACTION_DOWN)
					UserInterfaceStructurePlacementRectangle.this.userInterface.placeStructure();
				
				return false;
			}
		};
		placementRect.setColor(Color.RED);
		placementRect.setAlpha(0.2f);
		scene.attachChild(placementRect);
		scene.registerTouchArea(placementRect);
	}
	
	/**
	 * Hide the placement rectangle
	 */
	public void hide()
	{
		ZoomCamera camera = Globals.gameActivity.getCamera();
		LayerTerrain terrainLayer = Globals.gameActivity.getTerrainLayer();

		Log.d(this.getClass().getSimpleName(), "Hide placement rectangle.");
		

		//
		// Hide the graphic
		//
		placementRect.setVisible(false);

		//
		// Reset the camera bounding
		//
		camera.setBounds(terrainLayer.getMinX(), terrainLayer.getMinY(), terrainLayer.getMaxX(), terrainLayer.getMaxY());
		camera.setBoundsEnabled(true);
		
		this.userInterface.placementRectangle.update();
	}

	/**
	 * Show the placement rectangle
	 * 
	 * @param factory		- The factory that built the structure we are placing
	 */
	public void show(ComponentProduction factory) {
		
		Log.d(this.getClass().getSimpleName(), "Show placement rectangle.");

		placingFactory = factory;

		ZoomCamera camera = Globals.gameActivity.getCamera();
		LayerTerrain terrainLayer = Globals.gameActivity.getTerrainLayer();

		float cameraWidth = camera.getWidth();
		float cameraHeight = camera.getHeight();

		//
		// Show the graphic
		//
		placementRect.setVisible(true);
		
		//
		// Set the graphic to the appropriate size
		//
		placementWidth=placementHeight=1;
		ComponentStructureSpriteFactory graphic = factory.getCurrentProduction().getComponantFactory(ComponentStructureSpriteFactory.class);
		if(graphic!=null) {
			placementWidth = graphic.getWidth();
			placementHeight =  graphic.getHeight();
		}
		placementRect.setSize(Globals.TERRAIN_TILE_SIZE*placementWidth, Globals.TERRAIN_TILE_SIZE*placementHeight);

		//
		// Modify the camera bounding
		//
		camera.setBounds(
				terrainLayer.getMinX()-cameraWidth/2+Globals.TERRAIN_TILE_SIZE, 
				terrainLayer.getMinY()-cameraHeight/2+Globals.TERRAIN_TILE_SIZE, 
				terrainLayer.getMaxX()+cameraWidth/2-Globals.TERRAIN_TILE_SIZE*placementWidth, 
				terrainLayer.getMaxY()+cameraHeight/2-Globals.TERRAIN_TILE_SIZE*placementHeight);
	}

	/**
	 * Update the placement rectangle
	 */
	public void update() {
		ZoomCamera camera = Globals.gameActivity.getCamera();
		LayerTerrain terrainLayer = Globals.gameActivity.getTerrainLayer();

		//
		// Move the rectangle into place
		//
		placementCol = (int) (camera.getCenterX()/Globals.TERRAIN_TILE_SIZE);
		placementRow = (int) (camera.getCenterY()/Globals.TERRAIN_TILE_SIZE);

		placementCol = Math.max(1, placementCol);
		placementRow = Math.max(1, placementRow);

		placementCol = Math.min(placementCol, terrainLayer.getWidthCells() - placementWidth);
		placementRow = Math.min(placementRow, terrainLayer.getHeightCells() - placementHeight-1);

		float px, py;
		
		px = placementCol*Globals.TERRAIN_TILE_SIZE;
		py = placementRow*Globals.TERRAIN_TILE_SIZE;
		
		placementRect.setPosition(px, py);
		
		//
		// Check if it is touching a friendly structure
		//
		canPlace = false;
		
		LayerTerrain terrain = Globals.gameActivity.getTerrainLayer();
		Player humanPlayer = Globals.gameActivity.getHumanPlayer();
		
		for(int i = -1; i < placementWidth+1; i++)
		{
			//
			// Check along the top
			//
			GameObject struct = terrain.getCell(placementCol+i, placementRow-1).containedObject;
			if(struct!=null&&struct.getOwner()==humanPlayer)
				canPlace=true;
			
			//
			// Check along the bottom
			//
			struct = terrain.getCell(placementCol+i, placementRow+placementHeight).containedObject;
			if(struct!=null&&struct.getOwner()==humanPlayer)
				canPlace=true;
		}
			
		for(int j = -1; j < placementHeight+1; j++)
		{
			//
			// Check along the left
			//
			GameObject struct = terrain.getCell(placementCol-1, placementRow+j).containedObject;
			if(struct!=null&&struct.getOwner()==humanPlayer)
				canPlace=true;
			
			//
			// Check along the right
			struct = terrain.getCell(placementCol+placementWidth, placementRow+j).containedObject;
			if(struct!=null&&struct.getOwner()==humanPlayer)
				canPlace=true;
		}

		//
		// Check if the ground underneath is clear
		//
		for(int i = 0; i < placementWidth; i++)
			for(int j = 0; j < placementHeight; j++)
			{
				LayerTerrain.TerrainCell tile = terrainLayer.getCell(i + placementCol,j + placementRow);
				if(tile.type!=LayerTerrain.TERRAIN_ROCK||tile.containedObject!=null)
					canPlace = false;
			}

		//
		// If the location is good then make the shadow green else red
		//
		if(canPlace)
			placementRect.setColor(Color.GREEN);
		else
			placementRect.setColor(Color.RED);
		
		placementRect.setAlpha(0.2f);
	}

	/**
	 * This is called when the user click on the rectangle
	 * 
	 * @return true if structure is placed.
	 */
	public boolean place() {
		if(canPlace==true){
			placingFactory.place(placementCol, placementRow);
			return true;
		} else {
			return false;
		}
	}
}