package game.dune.ii;

import game.dune.ii.ComponentProductionFactory.ComponentProduction;
import game.dune.ii.ComponentProductionFactory.FactoryState;
import game.dune.ii.ComponentSelectableFactory.ISelectableFunctions;
import game.dune.ii.GameObjectFactory.GameObject;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * (c) 2012 Joshua Craymer
 *
 * This will probably need to be renamed in the future to something like "FactoryFunctions" or whatever
 *  
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class StructureBuilderFunctions implements ISelectableFunctions, OnClickListener{

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Sub Classes
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	public static StructureBuilderFunctions instance = new StructureBuilderFunctions();
	
	private static LinearLayout contentView;

	private static ImageView 	buildImage;
	private static ProgressBar 	buildBar;
	private static TextView 	buildText;

	private static Button 	buildBtn;
	private static Button 	canxBtn;
	private static Button 	placeBtn;

	//private GameObject 		selectedObject;
	private static ComponentProduction selectedProductionComponant;
			
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Called when the object is selected.
	 * 
	 * @param The selected object instance
	 */
	@Override
	public void onSelected(GameObject obj) { //Do nothing 
	}

	/**
	 * Called when the object is unselected.
	 * 
	 * @param The unselected object instance
	 */
	@Override
	public void onDeselected(GameObject obj) { //Do nothing
	}

	/**
	 * Retrieves the view of the selection panel content displayed when this object is selected
	 * 
	 * @param The selected object instance
	 * @param The game activity  
	 */
	@Override
	public View getSelectionPanelContentView(GameObject obj) {
		if(contentView == null)
		{
			contentView = (LinearLayout)LayoutInflater.from(Globals.gameActivity).inflate(R.layout.structure_selected_panel, null);
			buildImage = (ImageView)contentView.findViewById(R.id.building_image);
			buildText = (TextView)contentView.findViewById(R.id.building_name);
			buildBar = (ProgressBar)contentView.findViewById(R.id.building_progress_bar);
			buildBar.setMax(100);
			buildBar.setProgress(100);

			buildBtn = (Button)contentView.findViewById(R.id.build_btn);
			placeBtn = (Button)contentView.findViewById(R.id.place_btn);
			canxBtn = (Button)contentView.findViewById(R.id.canx_btn);
			
			buildBtn.setOnClickListener(this);
			placeBtn.setOnClickListener(this);
			canxBtn.setOnClickListener(this);
		}
		
		selectedProductionComponant = obj.getComponant(ComponentProduction.class);

		return contentView;
	}
	
	/**
	 * Refresh the UI to reflect any changes to the represented data 
	 * 
	 * @param The game activity
	 */
	@Override
	public void refreshSelectionPanelContentView() {

		if(selectedProductionComponant == null)
			return;
		
		final FactoryState state = selectedProductionComponant.getFactoryState();

		Globals.gameActivity.runOnUiThread(new Runnable() {            

			@Override
	        public void run() {
				if(state == FactoryState.Finished)
				{
					buildImage.setVisibility(View.VISIBLE);
					buildText.setVisibility(View.VISIBLE);
					buildBar.setVisibility(View.VISIBLE);

					buildBtn.setVisibility(View.GONE);

					//
					//If the object has a StructureSpriteFactory that means it it a structure and must be placed
					//
					ComponentStructureSpriteFactory spriteData = selectedProductionComponant.getCurrentProduction().getComponantFactory(ComponentStructureSpriteFactory.class);
					if(spriteData!=null){
						placeBtn.setVisibility(View.VISIBLE);
						canxBtn.setVisibility(View.VISIBLE);
					} else {
						placeBtn.setVisibility(View.GONE);
						canxBtn.setVisibility(View.GONE);
					}

					ComponentSelectableFactory selData = selectedProductionComponant.getCurrentProduction().getComponantFactory(ComponentSelectableFactory.class);
					buildImage.setImageResource(selData.getIcon());
					buildText.setText(selData.getName());
					buildBar.setProgress(buildBar.getMax());
					
				}else if(state == FactoryState.Placing)
				{
					buildImage.setVisibility(View.VISIBLE);
					buildText.setVisibility(View.VISIBLE);
					buildBar.setVisibility(View.VISIBLE);

					buildBtn.setVisibility(View.GONE);
					placeBtn.setVisibility(View.GONE);
					canxBtn.setVisibility(View.VISIBLE);

					ComponentSelectableFactory selData = selectedProductionComponant.getCurrentProduction().getComponantFactory(ComponentSelectableFactory.class);
					
					buildImage.setImageResource(selData.getIcon());
					buildText.setText(selData.getName());
					buildBar.setProgress(buildBar.getMax());
				} 
				else if(state == FactoryState.Building)
				{
					buildImage.setVisibility(View.VISIBLE);
					buildText.setVisibility(View.VISIBLE);
					buildBar.setVisibility(View.VISIBLE);

					buildBtn.setVisibility(View.GONE);
					placeBtn.setVisibility(View.GONE);
					canxBtn.setVisibility(View.VISIBLE);

					ComponentSelectableFactory selData = selectedProductionComponant.getCurrentProduction().getComponantFactory(ComponentSelectableFactory.class);

					buildImage.setImageResource(selData.getIcon());
					buildText.setText(selData.getName());
					buildBar.setMax(selectedProductionComponant.getProductionCost());
					buildBar.setProgress(selectedProductionComponant.getSpentCredits());
				} 
				else if(state == FactoryState.Idle)
				{
					buildImage.setVisibility(View.GONE);
					buildText.setVisibility(View.GONE);
					buildBar.setVisibility(View.GONE);

					buildBtn.setVisibility(View.VISIBLE);
					placeBtn.setVisibility(View.GONE);
					canxBtn.setVisibility(View.GONE);
				}
	        }});
	}
	
	/**
	 * Someone clicked on something in the selection panel while this factory was selected
	 * 
	 * @param The something which was clicked
	 */
	@Override
	public void onClick(View view) {

		UserInterface userInterface = Globals.gameActivity.getUserInterface();
		final FactoryState factoryState = selectedProductionComponant.getFactoryState();
		
		if(view == buildBtn){
			SelectProductionDialog.showDialog(Globals.gameActivity,selectedProductionComponant);
		}else if(view == placeBtn){
			userInterface.setPlaceStructureMode(selectedProductionComponant);
			selectedProductionComponant.startPlacing();
		}else if(view == canxBtn){
			//If we hit cancel while placing then stop placing, but don't cancel the production
			if(factoryState == FactoryState.Placing){
				selectedProductionComponant.stopPlacing();
				userInterface.setNormalMode();
				placeBtn.setVisibility(View.VISIBLE);
			}else{
				selectedProductionComponant.cancelProduction();
				refreshSelectionPanelContentView();
			}
		}
	}

	@Override
	public void onSpawn(GameObject obj) {
	}
}