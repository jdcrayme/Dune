package game.dune.ii;

import java.util.Random;

public class Globals {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final String CAMPAIGN_FILE_TAG 	= "Campaign";
	public static final String BATTLE_NUMBER_TAG	= "Battle";
	public static final String SCENARIO_FILE_TAG 	= "Scenario";
	public static final String HOUSE_TAG 			= "House";
	public static final String SCENARIO_NUM_TAG 	= "ScenarioNum";
	public static final String SCREEN_WIDTH_TAG 	= "ScreenWidth";
	public static final String SCREEN_HEIGHT_TAG 	= "ScreenHeight";
	
	public static final String SCENARIO_PATH 		= "scenario/";
	public static final String SOUND_PATH 			= "sfx/";
	public static final String MUSIC_PATH 			= "music/";
	
	public static final int HOUSE_ATR 		= 0;
	public static final int HOUSE_ORD 		= 1;
	public static final int HOUSE_HAR 		= 2;
	
	public static final int TERRAIN_TOP 		= 0;
	public static final int TERRAIN_BOTTOM 		= 800;
	public static final int TERRAIN_LEFT 		= 0;
	public static final int TERRAIN_RIGHT 		= 512;
	public static final int TERRAIN_TILE_SIZE 	= 32;

	public static final int SPICE_PER_TILE 		= 100;
	public static final int TURN_DELAY = 4;
	public static final float MOVE_SCALE = 0.25f;

	public static ActivityGame gameActivity;
	public static Random random = new Random();
	
	public static final String[][] scenarioFiles = new String[][] {
		{
				"SCENA001.INI",
				"SCENA002.INI",
				"SCENA003.INI",
				"SCENA004.INI",
				"SCENA005.INI",
				"SCENA006.INI",
				"SCENA007.INI",
				"SCENA008.INI",
				"SCENA009.INI",
				"SCENA010.INI",
				"SCENA011.INI",
				"SCENA012.INI",
				"SCENA013.INI",
				"SCENA014.INI",
				"SCENA015.INI",
				"SCENA016.INI",
				"SCENA017.INI",
				"SCENA018.INI",
				"SCENA019.INI",
				"SCENA020.INI",
				"SCENA021.INI",
				"SCENA022.INI",
			},
			{
				"SCENO001.INI",
				"SCENO002.INI",
				"SCENO003.INI",
				"SCENO004.INI",
				"SCENO005.INI",
				"SCENO006.INI",
				"SCENO007.INI",
				"SCENO008.INI",
				"SCENO009.INI",
				"SCENO010.INI",
				"SCENO011.INI",
				"SCENO012.INI",
				"SCENO013.INI",
				"SCENO014.INI",
				"SCENO015.INI",
				"SCENO016.INI",
				"SCENO017.INI",
				"SCENO018.INI",
				"SCENO019.INI",
				"SCENO020.INI",
				"SCENO021.INI",
				"SCENO022.INI",
			},
			{
				"SCENH001.INI",
				"SCENH002.INI",
				"SCENH003.INI",
				"SCENH004.INI",
				"SCENH005.INI",
				"SCENH006.INI",
				"SCENH007.INI",
				"SCENH008.INI",
				"SCENH009.INI",
				"SCENH010.INI",
				"SCENH011.INI",
				"SCENH012.INI",
				"SCENH013.INI",
				"SCENH014.INI",
				"SCENH015.INI",
				"SCENH016.INI",
				"SCENH017.INI",
				"SCENH018.INI",
				"SCENH019.INI",
				"SCENH020.INI",
				"SCENH021.INI",
				"SCENH022.INI",
			}};
}
