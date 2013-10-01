package game.dune.ii;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

public class LayerUnits {
	private Entity unitLayer;  
	private Entity airLayer;  
	private Entity groundLayer;

	public IEntity getEntity() { return unitLayer;	}
	
	public void createResources(ActivityGame activity, BitmapTextureAtlas atlas)
	{
		unitLayer = new Entity();
		airLayer = new Entity();
		groundLayer = new Entity();
		unitLayer.attachChild(groundLayer);
		unitLayer.attachChild(airLayer);

	}
	
	public Entity getUnitLayer(){return unitLayer;}
	
	/*public void add(GameObject unit) {
		ComponantUnitSprite sprite = unit.getComponant(ComponantUnitSprite.class);

		//
		// If the sprite is of type ComponantUnitSprite, then add it to the unit layer,
		// Otherwise, treat it like a structure
		//
		if(sprite!=null)
			unitLayer.attachChild(sprite.getSprite());	
		
		
		//Globals.gameActivity.getScene().attachChild(unit.getSelectionRectangle());
		//Globals.gameActivity.getScene().registerTouchArea(unit.getSelectionRectangle());
	}

	public void remove(GameObject unit) {

		ComponantUnitSprite spriteComponant = unit.getComponant(ComponantUnitSprite.class);
		
		if(spriteComponant==null)
			return;
		
		unitLayer.detachChild(spriteComponant.getSprite());
	}*/
}
