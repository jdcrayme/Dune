package game.dune.ii;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameScenario.GameObjectDescription;
import game.dune.ii.GameSound.MusicTracks;
import game.dune.ii.Player.Brain;
import game.dune.ii.Player.House;
import game.helpers.IniFile;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ActivityGame extends LoaderActivity
{
	// ===========================================================
	// Constants
	// ===========================================================

	public static final boolean DISABLE_FOG 		= false;		//If this is true, then do not draw fog of war
	public static final boolean DEBUG_MOVEMENT 		= false;		//If this is true, then do not draw fog of war
	public static final String OVERRIDE_SCENARIO 	= null;		//If this is not null then we skip the menus and do this
	
	private static final String BITMAP_PATH 		= "gfx/";				//The path of the bitmaps
	private static final String MAP_BITMAP_NAME 	= "spritesheet.png"; 	//The name of the sprite sheet

	private static final int TEXTURE_ATLAS_WIDTH = 1024;		//The size of the texture atlas that holds our sprite sheet
	private static final int TEXTURE_ATLAS_HEIGHT = 1024;
	
	//Game states
	protected static final int GAME_PLAY = 0;		//Executing the main loop
	protected static final int GAME_PAUSED = 1;		//Paused for whatever reason
	protected static final int GAME_FINISHED = 2;	//Done and cleaning up
	
	// ===========================================================
	// Fields
	// ===========================================================

	private int gameState;							//The current game state (see above)

	private BitmapTextureAtlas gameTextureAtlas;	//The game texture atlas

	private LayerTerrain terrain;					//Rendering layers
	private LayerUnits units;

	private GameSound sound;						//The sound interface

	private static Thread updateThread;				//The game loop thread
	
	private GameScenario scenario;					//The active scenario 
	private int scenarioNum;						//The scenario number
	private int house;								//The player house

	private UserInterface userInterface;			//The user interface instance
	private TextView creditsText;					//The credits text
	private TextView powerText;						//The power text
	
	private Hashtable<House, Player> players;		//The players
	private Player humanPlayer;						//The human player
		
	private List<GameObject> objects = new ArrayList<GameObject>();		//These are the currently active objects
	private List<GameObject> newObjects = new ArrayList<GameObject>();	//These are the objects to be added at the end of the next cycle
	private List<GameObject> oldObjects = new ArrayList<GameObject>();	//These are the objects to be removed at the end of the next cycle

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ActivityGame() {
		scenario = new GameScenario();
		players = new Hashtable<House, Player>();
		
		Globals.gameActivity = this;
	}
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return Main game screen layout ID 
	 */
	@Override
	protected int getLayoutID() { return R.layout.game_layout; }

	/**
	 * @return OpenGL render surface view ID 
	 */
	@Override
	protected int getRenderSurfaceViewID() { return R.id.main_surface; }

	/**
	 * @return The game camera
	 */
	public ZoomCamera getCamera() {	return camera; }
	
	/**
	 * @return The current game state 
	 */
	public int getGameState() {	return gameState; }

	/**
	 * @return The game scene 
	 */
	public Scene getScene() { return scene; }

	/**
	 * @return The terrain layer 
	 */
	public LayerTerrain getTerrainLayer() { return terrain; }

	/**
	 * @return The unit layer 
	 */
	public LayerUnits getUnitLayer() { return units; }

	/**
	 * @return The user interface 
	 */
	public UserInterface getUserInterface() { return userInterface; }

	/**
	 * @return The user interface 
	 */
	public Player getHumanPlayer() { return humanPlayer; }

	/**
	 * @return The user interface 
	 */
	public Player[] getPlayers() { return this.players.values().toArray(new Player[players.size()]); }

	/**
	 * @return The scenario technology level
	 */
	public int getTechnologyLevel() { return 10; }

	/**
	 * @return The game texture atlas
	 */
	public BitmapTextureAtlas getTextureAtlas() { return this.gameTextureAtlas; }

	/**
	 * @return The game units list
	 */
	public List<GameObject> getUnits() { return objects; }

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Called when the activity is first created. This is where you should do all of your normal static 
	 * set up: create views, bind data to lists, etc. This method also provides you with a Bundle 
	 * containing the activity's previously frozen state, if there was one. 
	 * 
	 * Always followed by onStart().
	 * 
	 * @param saved activity instance
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        //
        // Get data from the intent
        //
        Intent intent = getIntent();
        
        house = intent.getIntExtra(Globals.HOUSE_TAG,0);
        scenarioNum = intent.getIntExtra(Globals.SCENARIO_NUM_TAG,0);
        
        //
        // Now move along
        //
		super.onCreate(savedInstanceState);
    }

	/**
	 * This function creates the loading dialog and starts a background thread which loads the scenario and texture files into memory
	 */
	@Override
	protected void loadResources() {
		
		creditsText = (TextView)Globals.gameActivity.findViewById(R.id.credits_text);
		powerText = (TextView)Globals.gameActivity.findViewById(R.id.power_text);
		
		//
		//load the sprite sheets
		//
		gameTextureAtlas = new BitmapTextureAtlas(getTextureManager(), TEXTURE_ATLAS_WIDTH, TEXTURE_ATLAS_HEIGHT, TextureOptions.DEFAULT);
		gameTextureAtlas.addTextureAtlasSource(AssetBitmapTextureAtlasSource.create(this.getAssets(), BITMAP_PATH+MAP_BITMAP_NAME), 0, 0);
		gameTextureAtlas.load();
		
		//
		//load the sounds
		//
		sound = new GameSound(this);		
		sound.LoadSounds();

		//
		//Load the scenario
		//
		IniFile file = new IniFile();
		file.load(this, Globals.SCENARIO_PATH+Globals.scenarioFiles[house][scenarioNum]);
		scenario.load(file, this);
				
		//
		//Generate the terrain
		//
		terrain = new LayerTerrain();
		terrain.createResources(this,gameTextureAtlas);
		terrain.generateMap(this, scenario.seed, 64, 64);
		scene.attachChild(terrain.getTerrainEntity());

		//
		//Generate the units
		//
		units = new LayerUnits();
		units.createResources(this,gameTextureAtlas);
		scene.attachChild(units.getEntity());
		
		//
		//Generate the fog of war
		//
		if(!DISABLE_FOG )
		scene.attachChild(terrain.getFogEntity());

		//
		//Create the scenario players, units and structures
		//
		createPlayers();
		createGameObjects();

		//
		// Generate the user interface
		//
		userInterface = new UserInterface(scene);
		userInterface.setNormalMode();
	}

	/**
	 * This function creates and places units in accordance with the scenario data
	 */
	private void createGameObjects() {
		
		GameObjectTypes.buildUnitTable();
		
		for(int i=0;i<scenario.units.size();i++){
			
			GameObjectDescription unit = scenario.units.get(i);
			
			if(players.containsKey(unit.house))
				spawn(players.get(unit.house),unit.type, unit.health, unit.col, unit.row, unit.angle, unit.mode);
		}
	}

	/**
	 * Place a GameObject on the map 
	 *
	 * @param player	- The owning player
	 * @param type		- The unit type
	 * @param units		- The unit health
	 * @param i			- The placement column
	 * @param j			- The placement row
	 * @param angle 
	 * @param mode  	- The unit AI mode
	 * @return	- The new GameObject 
	 */
	public GameObject spawn(Player player, String type, short health, int col, int row, short facingDirection, String mode) {
		if(GameObjectTypes.gameObjectTypes == null)
			GameObjectTypes.buildUnitTable();

		if(GameObjectTypes.gameObjectTypes.containsKey(type))
		{
			Log.i(this.getClass().getSimpleName(), "Creating object of type '" + type+"'");
			int x = col*Globals.TERRAIN_TILE_SIZE; 
			int	y = row*Globals.TERRAIN_TILE_SIZE;
			return spawn(player, GameObjectTypes.gameObjectTypes.get(type), health, x, y, facingDirection, mode);
		}
		Log.w("SPAWN FAILED", "Attempted to spawn object of type " + type + ". Deffinition data not found.");
		
		return null;
	}
	
	/**
	 * Place a GameObject on the map 
	 *
	 * @param player	- The owning player
	 * @param type		- The game object factory
	 * @param health	- The unit health
	 * @param i			- The placement column
	 * @param j			- The placement row
	 * @param angle 	- The facing direction
	 * @param mode  	- The unit AI mode
	 * @return	- The new GameObject 
	 */
	public GameObject spawn(Player player, GameObjectFactory type, short health, int x, int y, short facingDirection, String mode) {

		GameObject unit = type.create(player);
		
		if(unit != null){
			IComponentMover mover = unit.getComponant(IComponentMover.class);
			if (mover != null) 
				mover.addToMap(x,y);
			
			IComponentSprite sprite = unit.getComponant(IComponentSprite.class);
			if (sprite != null){
				sprite.setFacingDirection(facingDirection);
				sprite.setPosition(x,y);
			}
			
			newObjects.add(unit);
		}else{
			Log.w(this.getClass().getSimpleName(), "Object creation failed");
		}
		
		return unit;
	}
	
	/**
	 * This function creates players accordance with the scenario data
	 */
	private void createPlayers() {
		for(int i=0;i<scenario.houses.size();i++){
			Player newPlayer = new Player(scenario.houses.get(i).house, scenario.houses.get(i).brain, scenario.houses.get(i).credits, scenario.houses.get(i).quota,scenario.houses.get(i).maxUnits); 
			players.put(scenario.houses.get(i).house, newPlayer);

			if(newPlayer.brain==Brain.HUMAN&&humanPlayer!=null){ Log.w("SCENARIO ERROR:", "Only one human player may be specified in scenario"); }
			
			if(newPlayer.brain==Brain.HUMAN)
				humanPlayer = newPlayer;
		}

		//Check to make sure we have a human player
		if(humanPlayer == null)	{ Log.e("SCENARIO ERROR:", "No human player specified in scenario"); finish();}
	}

	/**
	 * This function executes after the loading is finished and the dialog is closed
	 */
	@Override
	protected void loadingFinished() {
		//
		//Start the update thread
		//
	    updateThread = new Thread(new Runnable() {
            @Override
	        public void run() {
	            while (getGameState() != ActivityGame.GAME_FINISHED) {
	            		if(getGameState() == ActivityGame.GAME_PLAY) {
	            			
	            			//
	            			// Update the players
	            			//
	            			Iterator<Player> playerIterator = players.values().iterator();
	            			while(playerIterator.hasNext()){
	            				Player player = (Player)playerIterator.next();;
	            				player.Update();
	            				
	            				//
	            				// If this is the human player then update the user interface
	            				//
	            				if(player.brain == Brain.HUMAN)
									updatePlayerInterface(player);
	            			}

	            			//
	            			// Update the objects
	            			//
	            			Iterator<GameObject> objectIterator = objects.iterator();
	            			while(objectIterator.hasNext()){
	            				GameObject obj = (GameObject)objectIterator.next();
	            			    obj.Update();
	            			    
	            			    if(obj.destroyed)
	            			    	oldObjects.add(obj);
	            			}

	            			//
	            			// Move all the new objects into the list and clear the newObject list
	            			//
	            			objects.addAll(newObjects);
	            			newObjects.clear();

	            			objects.removeAll(oldObjects);
	            			oldObjects.clear();
	            		}
	            			
                		//
                		// Sleep
                		//
                    	try {
                    		Thread.sleep(100); 
                    	} catch (InterruptedException e) { 
                    		e.printStackTrace(); 
                    	}
	            }}

			});
	    
	    updateThread.start();
	    
	    //
	    // Play the music
	    //
	    sound.playMusic(MusicTracks.Under_Construction);
	}

	// ===========================================================
	// Non-inherited Methods
	// ===========================================================

    /**
	 * Update the user interface
	 */
	private void updatePlayerInterface(final Player player) {
		runOnUiThread(new Runnable() {            
			@Override
		    public void run() {
				//
				// Update the credits
				//
				int credits = player.getCredits();
				
				if(credits<100)
					creditsText.setTextColor(Color.RED);
				else
					creditsText.setTextColor(Color.WHITE);
				
				creditsText.setText("Credits: " + credits);

				//
				// Update the power
				//
				int power = player.getPowerGenerated() - player.getPowerRequired();

				if(power<1)
					powerText.setTextColor(Color.RED);
				else
					powerText.setTextColor(Color.WHITE);

				powerText.setText("Power: " + power);
			}
		});
	}

	/**
	 * Gets a player from the active list
	 * @param index - the index of the player
	 * @return - the player
	 */
	public Player getPlayer(int index) {
		return players.get(index);
	}
}
