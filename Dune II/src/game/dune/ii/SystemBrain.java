package game.dune.ii;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.GameObjectFactory.IGameObjectComponant;
import game.dune.ii.LayerTerrain.TerrainCell;

public class SystemBrain {
	
	// ===========================================================
	// Constants
	// ===========================================================
	public static final int CAN_ATTACK 	= 1;	// Binary 000000001
	public static final int CAN_GAURD 	= 2;	// Binary 000000010
	public static final int CAN_SPAWN 	= 4;	// Binary 000000100
	public static final int CAN_MOVE 	= 8;	// Binary 000001000
	public static final int CAN_PATROL 	= 16;	// Binary 000010000
	public static final int CAN_GROUP 	= 32;	// Binary 000100000
	public static final int CAN_HARVEST = 64;	// Binary 001000000
	public static final int CAN_STOP 	= 128;	// Binary 010000000
	public static final int CAN_RALLY 	= 256;	// Binary 100000000

	// ===========================================================
	// Interfaces
	// ===========================================================
	
	public interface IBrain extends IGameObjectComponant {
		public boolean sendMessage(int action, TerrainCell cell);
		public boolean sendMessage(int action, GameObject target);
	}
}
