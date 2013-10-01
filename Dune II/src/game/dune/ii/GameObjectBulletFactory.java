package game.dune.ii;

import org.andengine.opengl.texture.region.TiledTextureRegion;

public class GameObjectBulletFactory extends GameObjectFactory {

	GameObjectBulletFactory(IGameObjectComponantFactory[] componantFactories) {
		super(componantFactories);
		
		this.componantFactories = new IGameObjectComponantFactory[]{
				new ComponentUnitSpriteFactory(TiledTextureRegion.create(Globals.gameActivity.getTextureAtlas(), 512, 0, 128, 64, 8, 4), UnitGeneric.genericAnimationFunc, 0, 0),
				new ComponentBulletMoverFactory(10)
		};
	}

}
