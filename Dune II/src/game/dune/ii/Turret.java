package game.dune.ii;

import org.andengine.entity.sprite.TiledSprite;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
class Turret{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	protected TiledSprite sprite;			//The unit sprite
	protected int i = 0; //animation index;
	protected int direction;
	protected int startTileIndex;
	protected int animationFrames;
	protected int offsetX,offsetY;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public interface ITurretFactory{
		abstract public Turret CreateTurret();
	}
	
	public Turret(ActivityGame activity, LayerUnits units, int offsetX, int offsetY, int startTileIndex, int animationFrames) {
		//
		//Build the sprite...
		//
		//this.sprite = new TiledSprite(offsetX,offsetY, units.get16x16region(), activity.getVertexBufferObjectManager());
		this.sprite.setCurrentTileIndex(startTileIndex);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.startTileIndex = startTileIndex;
		this.animationFrames = animationFrames;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return The turret sprite 
	 */
	public TiledSprite getSprite() { return sprite;	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Non-inherited Methods
	// ===========================================================

	/**
	 * Called when the owning vehicle stops moving
	 * (Used for harvester)
	 */
	public void stop() {
		this.sprite.setVisible(false);
	}

	/**
	 * Sets the turret direction
	 * 	 -1 0 1
	 *	 -2 * 2
	 *	 -3 4 3
	 * @param direction - 
	 */
	public void setDirection(int direction)
	{
		this.direction = direction;
		
		switch(this.direction)
		{
		case 0:
			sprite.setCurrentTileIndex(this.startTileIndex+i);
			sprite.setFlippedHorizontal(false);
			sprite.setPosition(offsetX-1, offsetY-3);
			break;
		case 1:
			sprite.setCurrentTileIndex(this.startTileIndex+i+animationFrames );
			sprite.setFlippedHorizontal(false);
			sprite.setPosition(offsetX-8, offsetY-3);
			break;
		case 2:
			sprite.setCurrentTileIndex(this.startTileIndex+i+2*animationFrames);
			sprite.setFlippedHorizontal(false);
			sprite.setPosition(offsetX-15, offsetY-7);
			break;
		case 3:
			sprite.setCurrentTileIndex(this.startTileIndex+i+3*animationFrames);
			sprite.setFlippedHorizontal(false);
			sprite.setPosition(offsetX-10, offsetY-16);
			break;
		case 4:
		case -4:
			sprite.setCurrentTileIndex(this.startTileIndex+i+4*animationFrames);
			sprite.setFlippedHorizontal(false);
			sprite.setPosition(offsetX-1, -offsetY+6);
			break;
		case -3:
			sprite.setCurrentTileIndex(this.startTileIndex+i+3*animationFrames);
			sprite.setFlippedHorizontal(true);
			sprite.setPosition(offsetX+10, offsetY-16);
			break;
		case -2:
			sprite.setCurrentTileIndex(this.startTileIndex+i+2*animationFrames);
			sprite.setFlippedHorizontal(true);
			sprite.setPosition(offsetX+13, offsetY-7);
			break;
		case -1:
			sprite.setCurrentTileIndex(this.startTileIndex+i+1*animationFrames);
			sprite.setFlippedHorizontal(true);
			sprite.setPosition(offsetX+8, offsetY-3);
			break;
		}
	}
	
	/**
	 * Fire zee weapon!!
	 */
	public void fire() {
		this.sprite.setVisible(true);
		i++;
		i=i%3;
	}
}