package game.dune.ii;

import game.dune.ii.LayerTerrain.TerrainCell;

public interface IComponentSprite {

	public int getCol();
	public int getRow();

	public int getX();
	public int getY();
	public TerrainCell getCell();

	public void setPosition(int x, int y);
	public void setFacingDirection(int facingDirection);
}
