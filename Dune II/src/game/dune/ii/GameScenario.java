package game.dune.ii;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import game.dune.ii.Player.Brain;
import game.dune.ii.Player.House;
import game.helpers.IniFile;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class GameScenario {


	// ===========================================================
	// Sub classes
	// ===========================================================

	/**
	 * Describes a house from a scenario block
	 */	
	public class PlayerDescription{
		/**
		 * PlayerDescription constructor
		 * 
		 * @param house 	- The owner of this object
		 * @param quota		- The spice quota for human players
		 * @param credits	- Starting credits
		 * @param brain		- The type of brain (CPU or human)
		 * @param maxUnits	- The player's unit cap
		 */
		public PlayerDescription(House house, Integer quota, Integer credits, Brain brain, Integer maxUnits) {
			this.house = house;
			this.quota = quota;
			this.credits = credits;
			this.brain = brain;
			this.maxUnits = maxUnits; 
		}
		public House house;
		public int quota;
		public int credits;
		public Brain brain;
		public int maxUnits; 
	}

	/**
	 * Describes an object from a scenario block
	 */	
	public class GameObjectDescription{
		House house;
		String type;
		short health;
		int row,col;
		String mode;
		short angle;
		
		/**
		 * GameObjectDescription constructor
		 * 
		 * @param house 	- The owner of this object
		 * @param type 		- The type of object
		 * @param health	- The health of the object
		 * @param col		- The placement column
		 * @param row		- The placement row
		 * @param angle     - The placement angle
		 * @param mode		- What it should be doing 
		 */
		public GameObjectDescription(House house, String type, short health, int col, int row, short angle, String mode) {
			this.house = house;
			this.type = type;
			this.health = health;
			this.row = row;
			this.col = col;
			this.angle = angle;
			this.mode = mode; 
		}
	}
	
	// ===========================================================
	// Constants/Fields
	// ===========================================================

	private static final String BASIC_TAG 		= "BASIC";
	private static final String LOSE_PIC_TAG 	= "LosePicture";
	private static final String WIN_PIC_TAG 	= "WinPicture";
	private static final String BRIEF_PIC_TAG 	= "BriefPicture";
	private static final String CURSOR_POS_TAG 	= "CursorPos";
	private static final String TAC_POS_TAG 	= "TacticalPos";
	private static final String LOSE_FLAG_TAG 	= "LoseFlags";
	private static final String WIN_FLAG_TAG 	= "WinFlags";

	public int winPic;
	public int losePic;
	public int breifPic;
	public int cursorPos;
	public int tacPos;
	public int winFlags;
	public int loseFlags;
	
	private static final String MAP_TAG 		= "MAP";
	private static final String FIELD_TAG		= "Field";
	private static final String BLOOM_TAG		= "Bloom";
	private static final String SEED_TAG		= "Seed";
	
	public Integer[] fields;
	public Integer[] blooms;
	public int seed;

	private static final String ATREIDES_TAG	= "Atreides";
	private static final String ORDOS_TAG		= "Ordos";
	private static final String HARKONEN_TAG	= "Harkonnen";
	private static final String QUOTA_TAG		= "Quota";
	private static final String CREDITS_TAG		= "Credits";
	private static final String BRAIN_TAG		= "Brain";
	private static final String MAX_UNIT_TAG	= "MaxUnit";

	private static final String STRUCTURES_TAG	= "STRUCTURES";
	private static final String UNITS_TAG		= "UNITS";

	public ArrayList<PlayerDescription> houses = new ArrayList<PlayerDescription>();
	public ArrayList<GameObjectDescription> units = new ArrayList<GameObjectDescription>();

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Loads a scenario file.
	 * 
	 * @param file 		- The file to load
	 * @param context 	- The application context
	 */
	public void load(IniFile file, Context context) {
		
		//The pictures
		winPic = Utility.getResIDFromName(file.getString(BASIC_TAG, WIN_PIC_TAG, "HARVESTER"), context);
		losePic = Utility.getResIDFromName(file.getString(BASIC_TAG, LOSE_PIC_TAG, "HARVESTER"), context);
		breifPic = Utility.getResIDFromName(file.getString(BASIC_TAG, BRIEF_PIC_TAG, "HARVESTER"), context);
		
		//This position specifies the position of the selected unit or structure when the map is loaded.
		cursorPos = file.getInt(BASIC_TAG, CURSOR_POS_TAG, 0);
		
		//This position specifies the top left corner of the screen when the map is loaded. 
		//If the screen is 15x10 tiles big, this gives the center of the screen at
		//TacticalPos + 64*5 + 7 = TacticalPos + 327
		tacPos = file.getInt(BASIC_TAG, TAC_POS_TAG, 0);
		
		//The Keys “WinFlags” and “LoseFlags” determines under which conditions the
		//game ends and who has won. They are specified using normal numbers but
		//their meaning is better explained with their bit pattern (they both use the same bit pattern):
		//
		// Bit 4: Unused;	Dune II scenario files sometimes have set it, but it isn't checked
		// Bit 3: Check if time < timeout (see Key “TimeOut” why this does not work in Dune II)
		// Bit 2: Check if player credits < quota 
		// Bit 1: Check if player has at least one building
		// Bit 0: Check if AI player has no buildings
		//
		//The bit pattern of the Key “WinFlags” determine which of these conditions
		//should be checked every 5 seconds. The first check is done 2 minutes after the
		//game starts. If any of the specified conditions is met the game will end.
		//“LoseFlags” now determines who has won. If any of the bits in “LoseFlags” is
		//set and the corresponding condition holds true the player has won (and the
		//computer has lost). If all of the specified conditions are not met the computer
		//has won.
		//There is one exception to this rule (only for the “LoseFlags”): If Bit 1 and Bit 0
		//are both set than their conditions have to be both met for the player to win,
		//otherwise the computer has won.
		loseFlags = file.getInt(BASIC_TAG, LOSE_FLAG_TAG,0);
		winFlags = file.getInt(BASIC_TAG, WIN_FLAG_TAG,0);
	
		//////////////////////////////////////////////////////////////////////////////////////

		//This key specifies positions on the map where additional spice fields should be
		//created. Its value is a comma separated list of positions. The field is about 7x7 fields big.
		fields = file.getInts(MAP_TAG, FIELD_TAG, new Integer[]{});
		
		//This key specifies positions on the map where spice blooms should be placed.
		//Its value is a comma separated list of positions. The spice bloom will explode if
		//a unit drives over it or it is shoot. When the spice bloom explodes it will create
		//a field of about 7x7 spice fields.
		blooms = file.getInts(MAP_TAG, BLOOM_TAG, new Integer[]{});
		
		//Dune II creates its maps based on a random map generator. This random map
		//generator uses a pseudo random number generator. The pseudo RNG is seeded
		//with the value specified by the key “Seed”, which is a single number.
		seed = file.getInt(MAP_TAG, SEED_TAG, 0);

		//////////////////////////////////////////////////////////////////////////////////////
		
		//Key “Quota”
		//This key specifies the amount of credits this player needs to harvest and refine
		//to win. It is only used if Bit 2 in “WinFlags” or “LoseFlags” is set. Setting
		//“Quota” for AI players is meaningless.

		//Key “Credits”
		//The starting credits are specified by this key. 
		
		//Key “Brain” (Version < 2)
		//This key can only have the value “Human” or “CPU”. Only one player can be a
		//human player. All others have to be AI Players and therefore of type “CPU”.
		
		//Key “MaxUnits”
		//This key specifies up to which unit count the player is allowed to build units. It
		//is only relevant for building units; not for ordering them at the Starport.
		//Nevertheless ordering units at the Starport will increase the unit count and may
		//later disallow the player to build units.

		//Load the houses
		if(file.groupExists(ATREIDES_TAG)){
			houses.add(new PlayerDescription(House.ATR, file.getInt(ATREIDES_TAG, QUOTA_TAG, 1000),file.getInt(ATREIDES_TAG, CREDITS_TAG, 0), (file.getString(ATREIDES_TAG, BRAIN_TAG, "Human").compareToIgnoreCase("Human")==0?Brain.HUMAN:Brain.CPU),file.getInt(ATREIDES_TAG, MAX_UNIT_TAG,25)));
		}

		if(file.groupExists(ORDOS_TAG)){
			houses.add(new PlayerDescription(House.ORD, file.getInt(ORDOS_TAG, QUOTA_TAG, 1000),file.getInt(ORDOS_TAG, CREDITS_TAG, 0), (file.getString(ORDOS_TAG, BRAIN_TAG, "Human").compareToIgnoreCase("Human")==0?Brain.HUMAN:Brain.CPU),file.getInt(ORDOS_TAG, MAX_UNIT_TAG,25)));
		}
		
		if(file.groupExists(HARKONEN_TAG)){
			houses.add(new PlayerDescription(House.HAR, file.getInt(HARKONEN_TAG, QUOTA_TAG, 1000),file.getInt(HARKONEN_TAG, CREDITS_TAG, 0), (file.getString(HARKONEN_TAG, BRAIN_TAG, "Human").compareToIgnoreCase("Human")==0?Brain.HUMAN:Brain.CPU),file.getInt(HARKONEN_TAG, MAX_UNIT_TAG,25)));
		}
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		//Load the units
		String [] units = file.getGroupValues(UNITS_TAG); 
		for(int i = 0; i < units.length; i++)
			parseGameObject(units[i]);

		//////////////////////////////////////////////////////////////////////////////////////
		
		String [] structures = file.getGroupValues(STRUCTURES_TAG); 
		for(int i = 0; i < structures.length; i++)
			parseGameObject(structures[i]);
	}

	/**
	 * Parses a unit description string
	 * @param description - The description string
	 */	
	private void parseGameObject(String description) {
		StringTokenizer data = new StringTokenizer(description, ",");

		//Example:
		//ID038=Harkonnen,Soldier,256,9,64,Hunt
		//ID000=Ordos,Const Yard,256,2333

		String str = data.nextToken().trim();
		
		House house = House.ORD;

		if(str.compareToIgnoreCase(ATREIDES_TAG)==0)
			house = House.ATR;

		if(str.compareToIgnoreCase(HARKONEN_TAG)==0)
			house = House.HAR;
		
		String unitType = data.nextToken().trim(); 
		
		short health = (short) Integer.parseInt(data.nextToken());
		
//		if(numTolkens<5)
//		{
			int position = Integer.parseInt(data.nextToken());
			int w = 64;
			
			int i = position%w;
			int j = position/w;

			short angle = 0;
			String AIMode = "";

			if(data.hasMoreTokens())
				angle = (short) Integer.parseInt(data.nextToken());
			
			if(data.hasMoreTokens())
				AIMode = data.nextToken().trim();
			
			units.add(new GameObjectDescription(house, unitType, health, i, j, angle, AIMode));
//		} else {
			
//			int col = Integer.parseInt(data.nextToken());
//			int row = Integer.parseInt(data.nextToken());

			//units.add(new GameObjectDescription(house, unitType, health, col, row, AIMode));
			
//		}
	}
}
