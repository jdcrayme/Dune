package game.dune.ii;

import game.dune.ii.ComponentStructureSpriteFactory.ComponantStructureSprite;
import game.dune.ii.ComponentStructureSpriteFactory.IStructureSpriteAnimationFunction;
import game.dune.ii.GameObjectFactory.GameObject;

public class StructureGeneric {
	public static IStructureSpriteAnimationFunction genericStructureAnimationFunctions = new IStructureSpriteAnimationFunction(){

		@Override
		public int getAnimationFrame(int currentFrame, GameObject gameObject) {
			ComponantStructureSprite sprite = (ComponantStructureSprite) gameObject.getComponant(IComponentSprite.class);
			
			currentFrame++;
			if(currentFrame>sprite.getNumFrames()-2)
				currentFrame = 1;
			
			return currentFrame;
		}};
}
