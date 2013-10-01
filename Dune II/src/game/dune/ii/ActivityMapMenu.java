package game.dune.ii;

import game.helpers.InerpolatorFunction;
import game.helpers.IniFile;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.batch.DynamicSpriteBatch;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
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
public class ActivityMapMenu extends LoaderActivity{
	
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int MUSIC_ID = R.raw.score;
	
	private static final String BITMAP_PATH 		= "gfx/";	//The path of the bitmaps
	private static final String MAP_BITMAP_NAME 	= "region_map.png";

	private static final int MAP_BITMAP_WIDTH 		= 1024;
	private static final int MAP_BITMAP_HEIGHT 		= 1024;
	private static final int MAP_SPRITE_WIDTH 		= 512;
	private static final int MAP_SPRITE_HEIGHT 		= 256;
	private static final int MAP_REGION_SIZE 		= 140;
	private static final int MAP_REGION_ROWS 		= 4;
	private static final int MAP_REGION_COLS 		= 7;
	private static final int MAP_ARROW_SIZE 		= 128;
	private static final int NUM_MAP_REGIONS 		= 27;

	private static final float REGION_ALPHA 		= 0.4f;

	protected static final long TEXT_LINE_TIME 		= 3000;

	private static final float REGION_FADE_INCREMENT = 0.003f;

	private Regions [] regions = {
			new Regions(  0,  0,  0),
			new Regions( 90,  0,  1),
			new Regions(162,  1,  2),
			new Regions(246,  1,  3),
			new Regions(357,  1,  4),
			new Regions(417,  1,  5),
			new Regions(  1, 17,  6),
			new Regions(119, 80,  7),
			new Regions(222, 64,  8),
			new Regions(292, 31,  9),
			new Regions(370, 45, 10),
			new Regions(445, 35, 11),
			new Regions(  1, 87, 12),
			new Regions( 34, 98, 13),
			new Regions(145,147, 14),
			new Regions(249, 96, 15),
			new Regions(315, 97, 16),
			new Regions(409,100, 17),
			new Regions(470,101, 18),
			new Regions(  1,175, 19),
			new Regions( 41,193, 20),
			new Regions( 96,181, 21),
			new Regions(227,194, 23),
			new Regions(327,147, 22),
			new Regions(363,208, 25),
			new Regions(289,191, 24),
			new Regions(420,163, 26),
			};

	// ===========================================================
	// Sub Classes
	// ===========================================================
	
	private class Regions {
		float x,y;		//The location
		int sprite;		//The sprite
		float r,g,b,a;	//The color
		
		public Regions(float x, float y, int sprite) {
			this.x = x;
			this.y = y;
			this.sprite = sprite;
			this.r = this.g = this.b = this.a = 0;
		}

		/**
		 * Instantly set the region to a specific color
		 * 
		 * @param r - the red
		 * @param g - the green
		 * @param b - the blue
		 * @param a - the alpha
		 */
		public void setColor(float r, float g, float b, float a) {
			this.r=r;
			this.g=g;
			this.b=b;
			this.a=a;
		}
		
		/**
		 * Fades the region to a specific color over approximately 1 second
		 * 
		 * @param r - the red
		 * @param g - the green
		 * @param b - the blue
		 * @param a - the alpha
		 */
		public void fadeTo(final float r, final float g, final float b, final float a) {

			final float oldR = this.r;
			final float oldG = this.g;
			final float oldB = this.b;
			final float oldA = this.a;
						
			game.helpers.Interpolator.StepInterpolate(0,1,1000,new InerpolatorFunction(){
				@Override
				public boolean update(float i) {
	            	setColor(((1.0f-i)*oldR+i*r),((1.0f-i)*oldG+i*g),((1.0f-i)*oldB+i*b),((1.0f-i)*oldA+i*a));
	    	    	
	            	if(!skipAnimations)
	            		return true;
			
	            	return false;
				}
			});
		}
	}
	
	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mapTextureAtlas;		//The map textures
	private Sprite mapSprite;						//The map sprite
	private Sprite regionSprite;					//The map region lines
	private TiledSprite mapPiecesSprite;			//The individual regions
	private SpriteBatch pieceSpriteBatch;			//The sprite batch for the individual regions
	
	private TextView mentat_txt;					//The text below
	private ZoomCamera camera;						//The camera which does not really do anything

	private String campaignFileName = "REGIONA.INI";				//The campaign file name 
	
	private Thread briefThread;						//The briefing thread 

	private boolean skipAnimations = false;
	
	//private GameCampaign campaign;
	private MenuSound sound;

	private IniFile campaignFile;
	private int currentScenario = 1;
	private String scenarioDesignator = "GROUP";
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ActivityMapMenu() {
	}
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return Main game screen layout ID 
	 */
	@Override
	protected int getLayoutID() { return R.layout.dune_map_layout; }

	/**
	 * @return OpenGL render surface view ID 
	 */
	@Override
	protected int getRenderSurfaceViewID() { return R.id.region_map; }

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
        // Get the message from the intent
        //
        Intent intent = getIntent();
        campaignFileName = intent.getCharSequenceExtra(Globals.CAMPAIGN_FILE_TAG).toString();
        currentScenario = intent.getIntExtra(Globals.BATTLE_NUMBER_TAG, 1);

        //
        //Initialize the sound and start the music
        //
        sound = new MenuSound(this);
        sound.fadeInMusic(MUSIC_ID);
        
        //
        // Now move along
        //
		super.onCreate(savedInstanceState);
    }
	
	/**
	 * This function is called prior to creating an instance of the engine. Every activity that the 
	 * game uses will have its own instance of the engine that will run within the activity lifecycle..
	 * 
	 * @return An EngineOptions structure containing the desired engine parameters
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		Log.d("DEBUG", "Creating Engine");
		
		int width = 512;
		int height = 256;
		
		//
		//Create the camera
		//
		camera = new ZoomCamera(0, 0, width, height);
		
		//EngineOptions(fullScreen, screenOrientation, resolutionPolicy(width, height), camera)
		//fullScreen determines whether the game will be play full screen or not
		//screenOrientation, here we can choose between “LANDSCAPE” and “PORTRAIT”
		//resolutionPolicy is the ratio of you Engine(same values as in Camera)
		//camera is the game camera object
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(width, height), camera);
	}

	
	/**
	 * This function is called after onCreateEngineOptions and onCreateResources are executed. Here we 
	 * create the main game scene and use all the resources that onCreateResources loaded into memory.
	 *
	 * @return The Main game scene 
	 */
	@Override
	protected Scene onCreateScene() {
    	mentat_txt = (TextView) findViewById(R.id.mentat_text);

    	return super.onCreateScene();
	}

	// ===========================================================
	// Non-inherited Methods
	// ===========================================================
	
	/**
	 * This function creates the loading dialog and starts a background thread which loads the scenario and texture files into memory
	 */
	@Override
	protected void loadResources() {
		//
		//If the override scenario is specified, then just skip the menus in the interest of time
		//
        if(ActivityGame.OVERRIDE_SCENARIO!=null)
        	startScenario(ActivityGame.OVERRIDE_SCENARIO);
		
		//
		//Set the asset path
		//
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(BITMAP_PATH);

		//
		//Build the atlas used to create the tile regions
		//
		mapTextureAtlas = new BitmapTextureAtlas(getTextureManager(), MAP_BITMAP_WIDTH, MAP_BITMAP_HEIGHT, TextureOptions.DEFAULT);
		mapTextureAtlas.addTextureAtlasSource(AssetBitmapTextureAtlasSource.create(this.getAssets(), BITMAP_PATH+MAP_BITMAP_NAME), 0, 0);
		mapTextureAtlas.load();

		//
		// Create the tile regions
		//
		mapSprite 		= new Sprite(0, 0,  MAP_SPRITE_WIDTH,  MAP_SPRITE_HEIGHT,  new TextureRegion(mapTextureAtlas, 0, 				MAP_BITMAP_HEIGHT-MAP_SPRITE_HEIGHT, MAP_SPRITE_WIDTH, MAP_SPRITE_HEIGHT), this.getVertexBufferObjectManager());
		regionSprite 	= new Sprite(0, 0,  MAP_SPRITE_WIDTH,  MAP_SPRITE_HEIGHT,  new TextureRegion(mapTextureAtlas, MAP_SPRITE_WIDTH, MAP_BITMAP_HEIGHT-MAP_SPRITE_HEIGHT, MAP_SPRITE_WIDTH, MAP_SPRITE_HEIGHT), this.getVertexBufferObjectManager());
		mapPiecesSprite = new TiledSprite(0, 0, MAP_REGION_SIZE, MAP_REGION_SIZE, TiledTextureRegion.create(mapTextureAtlas, 0, 0, MAP_REGION_SIZE*MAP_REGION_COLS, MAP_REGION_SIZE*MAP_REGION_ROWS, MAP_REGION_COLS, MAP_REGION_ROWS), this.getVertexBufferObjectManager());

		//
		// ...and the piece sprite batch
		//
		pieceSpriteBatch = new DynamicSpriteBatch(mapTextureAtlas, NUM_MAP_REGIONS, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onUpdateSpriteBatch() {
				for(int i=0; i<NUM_MAP_REGIONS;i++)	{
					mapPiecesSprite.setCurrentTileIndex(regions[i].sprite);
					mapPiecesSprite.setPosition(regions[i].x, regions[i].y);
					mapPiecesSprite.setColor(regions[i].r, regions[i].g, regions[i].b, regions[i].a);
					this.draw(mapPiecesSprite);
				}
				return true;
			}
		};

		//
		// add the components to the scene
		//
		scene.attachChild(mapSprite);
		scene.attachChild(pieceSpriteBatch);
		scene.attachChild(regionSprite);

		//
		// Load the campaign info
		//
		campaignFile = new IniFile();
		campaignFile.load(this, Globals.SCENARIO_PATH+campaignFileName);
	}

	/**
	 * This function executes after the loading is finished and the dialog is closed
	 */
	@Override
	protected void loadingFinished() {
		//
		//Everything is loaded, start the brief 
		//
    	startBrief();
	}

	/**
	 * Start briefing.
	 * @param scenario 		- The scenario to brief
	 */
    protected void startBrief() {
    	setText("");
    	
    	//
    	//Make the map look like it should at this point in the war
    	//
    	for(int i = 1;i < currentScenario;i++)
    	{
    		//
    		//Set the ATR regions
    		//
    		setRegionsColor(campaignFile.getInts(scenarioDesignator+i, "ATR", new Integer[]{}),getResources().getColor(R.color.atreides_color));
    		
    		//
    		//Set the ORD regions
    		//
    		setRegionsColor(campaignFile.getInts(scenarioDesignator+i, "ORD", new Integer[]{}),getResources().getColor(R.color.ordos_color));
    		
    		//
    		//Set the HAR regions
    		//
    		setRegionsColor(campaignFile.getInts(scenarioDesignator+i, "HAR", new Integer[]{}),getResources().getColor(R.color.harkonen_color));
    	}
    	
    	//
    	//Now start the actual animations
    	//
    	briefThread = new Thread(new Runnable() {
    	    public void run() {
    	    	
    	    	String scenarioName = "";
    	    	if(campaignFileName.compareToIgnoreCase("REGIONA.INI")==0)
        	    {
    	    		scenarioName = "SCENA";
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "ATR", new Integer[]{}),getResources().getColor(R.color.atreides_color));
   	          		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "ORD", new Integer[]{}),getResources().getColor(R.color.ordos_color));
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "HAR", new Integer[]{}),getResources().getColor(R.color.harkonen_color));
   	           	}

   	           	if(campaignFileName.compareToIgnoreCase("REGIONO.INI")==0)
   	           	{
    	    		scenarioName = "SCENO";
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "ORD", new Integer[]{}),getResources().getColor(R.color.ordos_color));
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "ATR", new Integer[]{}),getResources().getColor(R.color.atreides_color));
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "HAR", new Integer[]{}),getResources().getColor(R.color.harkonen_color));
   	           	}

   	           	if(campaignFileName.compareToIgnoreCase("REGIONH.INI")==0)
   	           	{
    	    		scenarioName = "SCENH";
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "HAR", new Integer[]{}),getResources().getColor(R.color.harkonen_color));
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "ATR", new Integer[]{}),getResources().getColor(R.color.atreides_color));
   	           		fadeRegionsColor(campaignFile.getInts(scenarioDesignator+currentScenario, "ORD", new Integer[]{}),getResources().getColor(R.color.ordos_color));
   	           	}
   	           	
   	           	setText("Choose your battle...");

   	           	//ToDo make this actually work
   	           	Integer[] ints;
   	           	ints = campaignFile.getInts(scenarioDesignator+currentScenario, "REG1", new Integer[]{});
   	           	addInvasionArrow((ints[2]-0)*512/320.0f,(ints[3]-0)*256/200.0f,ints[1]-1, ints[0]-1, scenarioName +"001"+".INI");
   	           	
   	           	ints = campaignFile.getInts(scenarioDesignator+currentScenario, "REG2", new Integer[]{});
   	           	addInvasionArrow((ints[2]-0)*512/320.0f,(ints[3]-0)*256/200.0f,ints[1]-1, ints[0]-1, scenarioName +"002"+".INI");
   	           	
   	           	ints = campaignFile.getInts(scenarioDesignator+currentScenario, "REG3", new Integer[]{});
   	           	addInvasionArrow((ints[2]-0)*512/320.0f,(ints[3]-0)*256/200.0f,ints[1]-1, ints[0]-1, scenarioName +"003"+".INI");
   	           	
    	    }
    	});
    	
    	briefThread.start();
    }

	private void setRegionsColor(Integer[] regionIndexs, int faction_color) {
		float r,g,b;
		
		//
		//parse the faction color
		//
		r = Color.red(faction_color)/255.0f;
		g = Color.green(faction_color)/255.0f;
		b = Color.blue(faction_color)/255.0f;
		    		
		
		for(int j = 0;j<regionIndexs.length;j++)
		{
			Regions region = regions[regionIndexs[j]-1];
			region.setColor(r, g, b, REGION_ALPHA);
		}
	}

	private void fadeRegionsColor(Integer[] regionIndexs, int faction_color) {
		float r,g,b;
		
		//
		//parse the faction color
		//
		r = Color.red(faction_color)/255.0f;
		g = Color.green(faction_color)/255.0f;
		b = Color.blue(faction_color)/255.0f;
		
		//
		//Cycle through the regions fading them to the correct color
		//
		for(int j = 0;j<regionIndexs.length;j++)
		{
			int index = regionIndexs[j];
			String txt = campaignFile.getString(scenarioDesignator+currentScenario, "ENGTXT"+index, null);
			
			if(txt!=null)
				setText(txt);
			
			Regions region = regions[index-1];
			
			final float oldR = region.r;
			final float oldG = region.g;
			final float oldB = region.b;
			final float oldA = region.a;
						
			for(float i = 0;i<1;i+=REGION_FADE_INCREMENT){
				if(skipAnimations)
				{
					region.setColor(r,g,b,REGION_ALPHA);
					break;
				}
				region.setColor(((1.0f-i)*oldR+i*r),((1.0f-i)*oldG+i*g),((1.0f-i)*oldB+i*b),((1.0f-i)*oldA+i*REGION_ALPHA));
				
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

    /**
     * Sets the briefing text on the bottom of the screen
     * 
     * @param txt 		- The new text
     */
    public void setText(final CharSequence txt) {
		runOnUiThread(new Runnable() {            
			public void run() {
				mentat_txt.setText(txt);
	        }});
    }
    
    /**
     * Fade a region to a new color
     * 
     * @param region 	- The region
     * @param color		- The color
     */
    private void setRegion(int region, int color) {
    	regions[region].fadeTo(Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, REGION_ALPHA);
    }

    /**
     * Place an invasion arrow on the map
     * 
     * @param x			- The x coordinate
     * @param y 		- The y coordinate
     * @param arrow		- The arrow sprite
     * @param scenario 	- The scenario to play when the arrow is clicked
     */
	public void addInvasionArrow(float x, float y, int arrow, final int region, final String scenario)
	{
		TiledSprite arrowSprite = new TiledSprite(x, y, MAP_ARROW_SIZE, MAP_ARROW_SIZE, TiledTextureRegion.create(mapTextureAtlas, 0, 640, 1024, 128, 8, 1), this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						//The user clicked on an invasion arrow

						setRegion(region, Color.WHITE);
						
						sound.playGoodClick();
						
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// sleeps some other number of seconds

						//Start the main game activity
						startScenario(scenario);
						break;
				}
				return true;
			}
		};
		arrowSprite.setCurrentTileIndex(arrow);
		arrowSprite.setScale(0.25f);
		scene.attachChild(arrowSprite);
		scene.registerTouchArea(arrowSprite);
	}

    /**
     * Start playing the game
     * 
     * @param scenario 	- The scenario
     */
	private void startScenario(String scenario) {
    	//
    	// Build an intent
    	//
    	Intent intent = new Intent(this, ActivityGame.class);

    	//
    	//get the main window width and height so we know how to setup OpenGL in the next activity
    	//
    	intent.putExtra(Globals.SCREEN_WIDTH_TAG, getRenderSurfaceViewWidth());
    	intent.putExtra(Globals.SCREEN_HEIGHT_TAG, getRenderSurfaceViewHeight());
    	intent.putExtra(Globals.SCENARIO_FILE_TAG, scenario);

    	//
    	// Show the ActivityMapMenu
    	//
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	/**
	 * Called when the activity will start interacting with the user. At this point your activity is at 
	 * the top of the activity stack, with user input going to it. 
	 * 
	 * Always followed by onPause().
	 */
    @Override
    protected void onResume() {
        sound.resume();

    	super.onResume();
        // The activity has become visible (it is now "resumed").
    }

	/**
	 * Called when the system is about to start resuming a previous activity. This is typically used to 
	 * commit unsaved changes to persistent data, stop animations and other things that may be consuming 
	 * CPU, etc. Implementations of this method must be very quick because the next activity will not be 
	 * resumed until this method returns. 
	 * 
	 * Followed by either onResume() if the activity returns back to the front, or onStop() if it becomes 
	 * invisible to the user.
	 */
    @Override
    protected void onPause() {
        sound.pause();

    	super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
	}

	/**
	 * Overrides the activity's behavior when the back button is pressed. This allows the user to transition backwards 
	 * through the menus.
	 */
    @Override
	public void onBackPressed() {

    	sound.playGoodClick();
        super.onBackPressed();
    }

	@Override
	public boolean onDoubleTap(float x, float y) {
		//if the user double clicks, then skip everything.
		skipAnimations=true;
		return true;
	}

}
