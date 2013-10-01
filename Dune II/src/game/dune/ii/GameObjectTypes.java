package game.dune.ii;

import game.dune.ii.ComponentGroundMoverFactory.MoveType;
import game.dune.ii.GameObjectFactory.IGameObjectComponantFactory;

import java.util.Hashtable;

import org.andengine.opengl.texture.region.TiledTextureRegion;

public class GameObjectTypes {
	
	public static Hashtable<String, GameObjectFactory> gameObjectTypes;

	
	public static void buildUnitTable(){
		gameObjectTypes = new Hashtable<String, GameObjectFactory>();

		
		////////////////////////////////////////////////////
		// Explosion
		////////////////////////////////////////////////////
		gameObjectTypes.put("Worm", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 1000, 0, 24, 120, 1, 5), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("Smoke", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 945, 66, 45, 20, 3, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplTrike", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 945, 86, 45, 23, 2, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplDot", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 945, 109, 45, 16, 3, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplGass", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 400, 120, 24, 5, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplTank1", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 424, 120, 24, 5, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplTank2", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 448, 120, 24, 5, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplStruct1", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 472, 120, 24, 5, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplStruct2", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 496, 120, 24, 5, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		gameObjectTypes.put("ExplRocket", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 948, 126, 80, 16, 5, 1), UnitGeneric.explosionAnimationFunc, 0, 0),
						}));

		
		////////////////////////////////////////////////////
		// Weapons
		////////////////////////////////////////////////////
		gameObjectTypes.put("SBullet", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 1021, 1017, 3, 3, 1, 1), UnitGeneric.noAnimationFunc, 0, 0),
						new ComponentBulletMoverFactory(10),
						new ComponentWarheadFactory(1)
						}));
		
		gameObjectTypes.put("MBullet", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 1020, 1020, 4, 4, 1, 1), UnitGeneric.noAnimationFunc, 0, 0),
						new ComponentBulletMoverFactory(10),
						new ComponentWarheadFactory(1)
						}));
		
		gameObjectTypes.put("LBullet", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 1015, 1019, 5, 5, 1, 1), UnitGeneric.noAnimationFunc, 0, 0),
						new ComponentBulletMoverFactory(10),
						new ComponentWarheadFactory(1)
						}));

		////////////////////////////////////////////////////
		// Trike
		////////////////////////////////////////////////////
		gameObjectTypes.put("Trike", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.unit_trike_name, R.drawable.trike, R.string.unit_trike_description, 150, 40, 0, UnitGeneric.genericSelectionFunctions),
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 832, 62, 112, 13, 8, 1), UnitGeneric.genericAnimationFunc, 0, 0),
						new ComponentHullFactory(100, 25),
						new ComponentSensorFactory(2),
						new ComponentGroundMoverFactory(MoveType.Wheeled, 45, 2),
						new ComponentWeaponFactory(gameObjectTypes.get("SBullet"),4,20),
						new ComponentBrainFactory()
						}));
		
		////////////////////////////////////////////////////
		// Quad
		////////////////////////////////////////////////////
		gameObjectTypes.put("Quad", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.unit_quad_name, R.drawable.quad, R.string.unit_quad_description, 200, 48, 0, UnitGeneric.genericSelectionFunctions),
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 320, 128, 16, 8, 1), UnitGeneric.genericAnimationFunc, 0, 0),
						new ComponentHullFactory(130, 25),
						new ComponentSensorFactory(2),
						new ComponentGroundMoverFactory(MoveType.Wheeled, 45, 2),
						new ComponentBrainFactory()
						}));
		
		////////////////////////////////////////////////////
		// Harvester
		////////////////////////////////////////////////////
		gameObjectTypes.put("Harvester", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.unit_harvester_name, R.drawable.harvester, R.string.unit_harvester_description, 300, 64, 0, UnitHarvester.selectionFunctions),
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 640, 201, 320, 104, 8, 4), UnitHarvester.animationFunc, 0, 0),
						new ComponentHarvesterFactory(800),
						new ComponentHullFactory(150, 50),
						new ComponentSensorFactory(2),
						new ComponentGroundMoverFactory(MoveType.Tracked, 20,1),
						new ComponentBrainFactory()
						}));

		////////////////////////////////////////////////////
		// Soldier
		////////////////////////////////////////////////////
		gameObjectTypes.put("Soldier", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.unit_infantry_name, R.drawable.infantry, R.string.unit_infantry_description, 60, 32, 0, UnitGeneric.genericSelectionFunctions),
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 0, 128, 64, 8, 4), UnitGeneric.generic4FrameAnimationFunc, 0, 0),
						new ComponentHullFactory(20,0),
						new ComponentSensorFactory(1),
						new ComponentGroundMoverFactory(MoveType.Foot, 8,3),
						new ComponentBrainFactory()
						}));

		////////////////////////////////////////////////////
		// Infantry
		////////////////////////////////////////////////////
		gameObjectTypes.put("Infantry", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.unit_infantry_name, R.drawable.infantry, R.string.unit_infantry_description, 60, 32, 0, UnitGeneric.genericSelectionFunctions),
						new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 64, 128, 64, 8, 4), UnitGeneric.generic4FrameAnimationFunc, 0, 0),
						new ComponentHullFactory(20,0),
						new ComponentSensorFactory(1),
						//new ComponentWeaponFactory(),
						new ComponentGroundMoverFactory(MoveType.Foot, 8,3),
						new ComponentBrainFactory()
						}));

		////////////////////////////////////////////////////
		// Windtrap
		////////////////////////////////////////////////////
		gameObjectTypes.put("Windtrap", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.structure_windtrap_name, R.drawable.windtrap, R.string.structure_windtrap_description, 300, 48, 1, StructureWindtrapFunctions.instance),
						new ComponentHullFactory(200,25),
						new ComponentPowerFactory(-100),
						new ComponentSensorFactory(2),
						new ComponentStructureSpriteFactory(new char[][][] {								//Tiles
									{
										{238,244},
										{239,245}
									},
									{
										{310,312},
										{311,313}
									},
									{
										{310,312},
										{311,314}
									},
									{
										{387,246},
										{388,247}
									}
								}, StructureGeneric.genericStructureAnimationFunctions),
								new ComponentBrainFactory()
						}));

		////////////////////////////////////////////////////
		// Barracks
		////////////////////////////////////////////////////
		gameObjectTypes.put("Barracks", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.structure_barracks_name, R.drawable.barracks, R.string.structure_barracks_description, 300, 72, 2, StructureBuilderFunctions.instance),
						new ComponentPowerFactory(-100),
						new ComponentProductionFactory(new String[]{"Soldier","Harvester"}),
						new ComponentHullFactory(300, 50),
						new ComponentPowerFactory(10),
						new ComponentSensorFactory(2),
						new ComponentStructureSpriteFactory(new char[][][] {								//Tiles
											{
												{238,244},
												{239,245}
											},
											{
												{305,307},
												{306,308}
											},
											{
												{305,307},
												{306,309}
											},
											{
												{387,246},
												{388,247}
											}
								}, StructureGeneric.genericStructureAnimationFunctions),
								new ComponentBrainFactory()
						}));

		////////////////////////////////////////////////////
		// Refinery
		////////////////////////////////////////////////////
		gameObjectTypes.put("Refinery", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.structure_refinery_name, R.drawable.refinery, R.string.structure_refinery_description, 400, 80, 1, StructureRefineryFactory.instance),
						new ComponentPowerFactory(30),
						new ComponentRefineryFactory(),
						new ComponentHullFactory(450, 50),
						new ComponentPowerFactory(10),
						new ComponentSensorFactory(4),
						new ComponentStructureSpriteFactory(new char[][][] {
									{
										{251,258},
										{252,259},
										{253,260}
									},
									{
										{338,343},
										{339,344},
										{340,345}
									},
									{
										{338,343},
										{341,344},
										{340,345}
									},
									{
										{338,343},
										{339,344},
										{340,345}
									},
									{
										{338,343},
										{341,344},
										{340,345}
									},
									{
										{338,343},
										{339,344},
										{342,346}
									},
									{
										{338,343},
										{341,344},
										{342,346}
									},
									{
										{338,343},
										{339,344},
										{347,349}
									},
									{
										{338,343},
										{341,344},
										{347,349}
									},
									{
										{213,233},
										{214,234},
										{215,232}
									}
								}, StructureRefineryFactory.instance),
								new ComponentBrainFactory()
						}));
		
		////////////////////////////////////////////////////
		// Construction Yard
		////////////////////////////////////////////////////
		gameObjectTypes.put("Const Yard", new GameObjectFactory(
				new IGameObjectComponantFactory[]{
						new ComponentSelectableFactory(R.string.structure_constyard_name, R.drawable.constyard, R.string.structure_constyard_description, 400, 80, 99, StructureBuilderFunctions.instance),
						new ComponentProductionFactory(new String[]{"Windtrap", "Refinery", "Barracks"}),
						new ComponentHullFactory(400, 25),
						new ComponentSensorFactory(3),
						new ComponentStructureSpriteFactory(new char[][][] {
									{
										{143,301},
										{297,302}
									},
									{
										{298,303},
										{299,304}
									},
									{
										{300,303},
										{299,304}
									},
									{
										{387,246},
										{388,247}
									}
								}, StructureGeneric.genericStructureAnimationFunctions),
								new ComponentBrainFactory()
						}));
	}
}
