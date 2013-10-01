package game.dune.ii;

import android.view.View;
import android.widget.TextView;
import game.dune.ii.ComponentUnitSpriteFactory.ComponantUnitSprite;
import game.dune.ii.ComponentGroundMoverFactory.ComponentGroundMover;
import game.dune.ii.ComponentGroundMoverFactory.MoveState;
import game.dune.ii.ComponentSelectableFactory.ISelectableFunctions;
import game.dune.ii.ComponentUnitSpriteFactory.ISpriteAnimationFunction;
import game.dune.ii.GameObjectFactory.GameObject;

public class UnitGeneric {
	public static ISpriteAnimationFunction genericAnimationFunc = new ISpriteAnimationFunction(){

		@Override
		public int getAnimationFrame(int facingDirection, int currentFrame, GameObject gameObject) {
			if(facingDirection>7)
				facingDirection = facingDirection%8;
			
			return facingDirection;
		}};

	public static ISpriteAnimationFunction noAnimationFunc = new ISpriteAnimationFunction(){

		@Override
		public int getAnimationFrame(int facingDirection, int currentFrame, GameObject gameObject) {
			return 0;
		}};

	
	public static ISpriteAnimationFunction generic4FrameAnimationFunc = new ISpriteAnimationFunction(){

			@Override
			public int getAnimationFrame(int facingDirection, int currentFrame, GameObject gameObject) {
				ComponentGroundMover mover = (ComponentGroundMover) gameObject.getComponant(ComponentGroundMover.class);
				
				if(mover.getState()== MoveState.Moving){
					
					int aniFrame = (currentFrame/7)%3;
				
					if(facingDirection>7)
						facingDirection = facingDirection%8;
				
					return (aniFrame+1)*8 + facingDirection;
				}else{
				
					if(facingDirection>7)
						facingDirection = facingDirection%8;
				
					return facingDirection;
				}
			}};
		
	public static ISpriteAnimationFunction explosionAnimationFunc = new ISpriteAnimationFunction(){

		@Override
		public int getAnimationFrame(int facingDirection, int currentFrame, GameObject gameObject) {
			ComponantUnitSprite sprite = (ComponantUnitSprite) gameObject.getComponant(ComponantUnitSprite.class);
			
			currentFrame++;
			if(currentFrame>=sprite.getNumFrames()-1){
				gameObject.destroy();
				return sprite.getNumFrames()-1;
			}
			
			return currentFrame;
		}};

			
	public static ISelectableFunctions genericSelectionFunctions = new ISelectableFunctions(){

		@Override
		public void onSpawn(GameObject obj) {
		}

		@Override
		public void onSelected(GameObject obj) {
		}

		@Override
		public void onDeselected(GameObject obj) {
		}

		@Override
		public View getSelectionPanelContentView(GameObject obj) {
			return new TextView(Globals.gameActivity);
		}

		@Override
		public void refreshSelectionPanelContentView() {
			
		}};
}
