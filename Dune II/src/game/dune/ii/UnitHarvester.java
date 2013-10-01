package game.dune.ii;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import game.dune.ii.ComponentHarvesterFactory.ComponentHarvester;
import game.dune.ii.ComponentHarvesterFactory.HarvesterState;
import game.dune.ii.ComponentSelectableFactory.ISelectableFunctions;
import game.dune.ii.ComponentUnitSpriteFactory.ISpriteAnimationFunction;
import game.dune.ii.GameObjectFactory.GameObject;

public class UnitHarvester {
	public static ISpriteAnimationFunction animationFunc = new ISpriteAnimationFunction(){

		@Override
		public int getAnimationFrame(int facingDirection, int currentFrame, GameObject gameObject) {
			ComponentHarvester harvester = gameObject.getComponant(ComponentHarvester.class);
			if(harvester.getState() != HarvesterState.Harvesting){
				if(facingDirection>7)
					facingDirection = facingDirection%8;
			
				return facingDirection;
			} else {
				if(facingDirection>7)
					facingDirection = facingDirection%8;
				
				currentFrame = (currentFrame+1)%3;				
				
				return facingDirection + (currentFrame%3+1) * 8;
			}
			
		}};

	public static ISelectableFunctions selectionFunctions = new ISelectableFunctions(){
		private LinearLayout contentView;
		private TextView modeText;
		private TextView spiceText;
		private ProgressBar spiceBar;
		
		private GameObject currentObject;
		private ComponentHarvester harvester;

		
		@Override
		public void onSpawn(GameObject obj) {
		}

		@Override
		public void onSelected(GameObject obj) {
			currentObject = obj;
			
			if(currentObject!=null){
				harvester = currentObject.getComponant(ComponentHarvester.class);
			}
		}

		@Override
		public void onDeselected(GameObject obj) {
			currentObject = null;
			harvester = null;
		}

		@Override
		public View getSelectionPanelContentView(GameObject obj) {
			currentObject = obj;
			if(currentObject!=null){
				harvester = currentObject.getComponant(ComponentHarvester.class);
			}

			if(contentView == null)
			{
				modeText = new TextView(Globals.gameActivity);
				modeText.setTextAppearance(Globals.gameActivity, android.R.style.TextAppearance_Large);
				modeText.setText(R.string.generic);

				spiceText = new TextView(Globals.gameActivity);
				spiceText.setTextAppearance(Globals.gameActivity, android.R.style.TextAppearance_DeviceDefault_Small);
				spiceText.setText(R.string.spice);

				spiceBar = new ProgressBar(Globals.gameActivity, null, android.R.attr.progressBarStyleHorizontal);
				spiceBar.setProgress(50);
				spiceBar.setHorizontalScrollBarEnabled(true);
				
				contentView = new LinearLayout(Globals.gameActivity);
				contentView.setOrientation(LinearLayout.VERTICAL);
				contentView.addView(modeText);
				contentView.addView(spiceText);
				contentView.addView(spiceBar);
			}

			return contentView;
		}

		@Override
		public void refreshSelectionPanelContentView() {
			if(harvester != null && modeText != null)
			{
				switch(harvester.getState())
				{
				case NotHarvesting:
					modeText.setText(R.string.stopped);
					break;
				case Harvesting:
					modeText.setText(R.string.harvesting);
					break;
				case Full:
					modeText.setText(R.string.returning);
					break;
				}
				
				spiceBar.setMax(harvester.getSpiceCapacity());
				spiceBar.setProgress(harvester.getCurrentSpiceLoad());
			}

		}};
}
