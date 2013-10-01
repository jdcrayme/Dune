package game.dune.ii;

import game.dune.ii.ComponentSelectableFactory.ISelectableFunctions;
import game.dune.ii.GameObjectFactory.GameObject;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StructureWindtrapFunctions implements ISelectableFunctions{

	public static StructureWindtrapFunctions instance = new StructureWindtrapFunctions();
	
	private static LinearLayout contentView;
	private static TextView powerProduced;
	private static TextView powerProducedNumber;
	private static TextView powerRequired;
	private static TextView powerRequiredNumber;
	private Player owner;

	@Override
	public void onSelected(GameObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeselected(GameObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View getSelectionPanelContentView(GameObject obj) {
		if(contentView == null)
		{
			contentView = new LinearLayout(Globals.gameActivity);
			powerProduced = new TextView(Globals.gameActivity);
			powerProducedNumber = new TextView(Globals.gameActivity);
			powerRequired = new TextView(Globals.gameActivity);
			powerRequiredNumber = new TextView(Globals.gameActivity);
			
			powerProducedNumber.setTextAppearance(Globals.gameActivity, android.R.style.TextAppearance_Large);
			powerRequiredNumber.setTextAppearance(Globals.gameActivity, android.R.style.TextAppearance_Large);
			
			contentView.setOrientation(LinearLayout.VERTICAL);
			contentView.addView(powerProduced);
			contentView.addView(powerProducedNumber);
			contentView.addView(powerRequired);
			contentView.addView(powerRequiredNumber);

			powerProduced.setText(R.string.windtrap_power_produced);
			powerRequired.setText(R.string.windtrap_power_required);

		}
		owner = obj.getOwner();
		
		return contentView;
	}

	@Override
	public void refreshSelectionPanelContentView() {
		
		int powerReq = owner.getPowerRequired();
		int powerGen = owner.getPowerGenerated();
		
		powerProducedNumber.setText("   " + powerGen + " Gw");
		powerRequiredNumber.setText("   " + powerReq + " Gw");

		powerProducedNumber.setTextColor(Color.GREEN);

		if(powerGen-powerReq<20)
			powerProducedNumber.setTextColor(Color.YELLOW);

		if(powerGen-powerReq<0)
			powerProducedNumber.setTextColor(Color.RED);
	}

	@Override
	public void onSpawn(GameObject obj) {
	}
}
