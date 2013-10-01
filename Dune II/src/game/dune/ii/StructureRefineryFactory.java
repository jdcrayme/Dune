package game.dune.ii;

import game.dune.ii.ComponentRefineryFactory.ComponentRefinery;
import game.dune.ii.ComponentSelectableFactory.ISelectableFunctions;
import game.dune.ii.ComponentStructureSpriteFactory.IStructureSpriteAnimationFunction;
import game.dune.ii.GameObjectFactory.GameObject;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StructureRefineryFactory implements ISelectableFunctions, IStructureSpriteAnimationFunction {

	public static StructureRefineryFactory instance = new StructureRefineryFactory();
	
	private static LinearLayout contentView;
	private static TextView spiceStorred;
	private static TextView spiceStorredNumber;
	private static TextView spiceCapacity;
	private static TextView spiceCapacityNumber;
	private Player owner;

	@Override
	public void onSelected(GameObject obj) {

	}

	@Override
	public void onDeselected(GameObject obj) {

	}

	@Override
	public View getSelectionPanelContentView(GameObject obj) {
		if(contentView == null)
		{
			contentView = new LinearLayout(Globals.gameActivity);
			spiceStorred = new TextView(Globals.gameActivity);
			spiceStorredNumber = new TextView(Globals.gameActivity);
			spiceCapacity = new TextView(Globals.gameActivity);
			spiceCapacityNumber = new TextView(Globals.gameActivity);
			
			spiceStorredNumber.setTextAppearance(Globals.gameActivity, android.R.style.TextAppearance_Large);
			spiceCapacityNumber.setTextAppearance(Globals.gameActivity, android.R.style.TextAppearance_Large);
			
			contentView.setOrientation(LinearLayout.VERTICAL);
			contentView.addView(spiceStorred);
			contentView.addView(spiceStorredNumber);
			contentView.addView(spiceCapacity);
			contentView.addView(spiceCapacityNumber);

			spiceStorred.setText(R.string.refinery_spice_storred);
			spiceCapacity.setText(R.string.refinery_spice_capacity);

		}
		owner = obj.getOwner();
		
		return contentView;
	}

	@Override
	public void refreshSelectionPanelContentView() {
		
		int spice = owner.getCredits();
		int capacity = 5000;
		
		spiceStorredNumber.setText("   " + spice);
		spiceCapacityNumber.setText("   " + capacity);

		spiceStorredNumber.setTextColor(Color.GREEN);

		if(capacity-spice<20)
			spiceStorredNumber.setTextColor(Color.YELLOW);

		if(capacity-spice<0)
			spiceStorredNumber.setTextColor(Color.RED);
	}

	@Override
	public void onSpawn(GameObject obj) {
	
		//owner = obj.owner;
		//int maxHeight = Globals.gameActivity.getTerrainLayer().getHeightCells();
		//int maxWidth = Globals.gameActivity.getTerrainLayer().getWidthCells();

		/*int row = 0,col = 0;
		
		int topDist,leftDist,rightDist,bottomDist;
		topDist = obj.getCellRow();
		bottomDist = maxHeight - obj.getCellRow();
		leftDist = obj.getCellCol();
		rightDist = maxWidth - obj.getCellCol();
		
		if(topDist<=bottomDist&&topDist<=leftDist&&topDist<=rightDist){
			row = 1;
			col = obj.getCellCol();
		}

		if(bottomDist<=topDist&&bottomDist<=leftDist&&bottomDist<=rightDist){
			row = maxHeight-2;
			col = obj.getCellCol();
		}

		if(leftDist<=bottomDist&&leftDist<=topDist&&leftDist<=rightDist){
			row = obj.getCellRow();
			col = 1;
		}

		if(rightDist<=topDist&&rightDist<=leftDist&&rightDist<=bottomDist){
			row = obj.getCellRow();
			col = maxWidth-2;
		}

		GameObject unit = Globals.gameActivity.spawn(owner, "Harvester", (short)255, col, row, (short)0, "Harvest");
		unit.sendMessage("MOVE",Globals.gameActivity.getTerrainLayer().getCell(obj.getCellCol(), obj.getCellRow()).getTopNeighbor());
		*/
	}

	@Override
	public int getAnimationFrame(int currentFrame, GameObject gameObject) {
		
		ComponentRefinery refinery = gameObject.getComponant(ComponentRefinery.class);
		
		currentFrame++;
		
		switch(refinery.getState()){
		case Receiving:
			if(currentFrame>8)
				currentFrame = 2;
			break;
		default: 
			if(currentFrame>2)
				currentFrame = 1;
			break;
		}

		return currentFrame;
	}
}
