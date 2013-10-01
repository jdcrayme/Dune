package game.dune.ii;

import game.dune.ii.ComponentBrainFactory.ComponentBrain;
import game.dune.ii.ComponentHullFactory.ComponentHull;
import game.dune.ii.ComponentSelectableFactory.ComponentSelectable;
import game.dune.ii.GameObjectFactory.GameObject;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This is the panel that pops up on the side when something is selected
 */
class UserInterfaceSelectionPanel {

	// ===========================================================
	// Fields
	// ===========================================================

	private LinearLayout 	selectionPanel;			//The selection panel
	private ImageView 		selection_image;		//The unit/structure image on the selection panel
	private TextView 		selectionText;			//The name of the thing selected
	private ProgressBar 	selectionDamage;		//The damage bar
	private TextView 		selectionOrder;		//The damage bar
	private TextView 		selectionAction;		//The damage bar
	private LinearLayout 	selectionPanelContent;	//The container for selection based custom content on the selection panel

	private GameObject 			selection_object;		//The selected object
	private ComponentSelectable selection_selectable;	//The selectable component of the selected object
		
	// ===========================================================
	// Constructor
	// ===========================================================

	/**
	 * This is the panel that pops up on the side when something is selected
	 */
	protected UserInterfaceSelectionPanel()
	{
		selection_image = (ImageView)Globals.gameActivity.findViewById(R.id.selection_graphic);
		selectionPanel = (LinearLayout)Globals.gameActivity.findViewById(R.id.selection_panel);
		selectionPanelContent = (LinearLayout)Globals.gameActivity.findViewById(R.id.selection_panel_content);
		selectionText = (TextView)Globals.gameActivity.findViewById(R.id.selection_name);
		selectionDamage = (ProgressBar)Globals.gameActivity.findViewById(R.id.selection_damage_bar);
		selectionOrder = (TextView)Globals.gameActivity.findViewById(R.id.ai_order_text);
		selectionAction = (TextView)Globals.gameActivity.findViewById(R.id.ai_action_text);
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	/**
	 * @return Is the selection panel visible 
	 */
	public boolean isVisible() {
		return selectionPanel.getVisibility()==View.VISIBLE;
	}

	/**
	 * @return The selected GameObject
	 */
	public GameObject getSelectedObject() {
		return selection_object;
	}

	/**
	 * @return The selected object's selectable component 
	 */
	public ComponentSelectable getSelectedSelectable() {
		return selection_selectable;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Sets the object shown on the selection panel
	 * Note: this can be called from any thread 
	 * @param newSelection	- The new selected object
	 */
	public void setSelection(final GameObject newSelection) {
		
		//
		// If the entity is already selected then do nothing
		//
		if(selection_object == newSelection)
			return;

		//
		// Create the UI update thread
		//
		Globals.gameActivity.runOnUiThread(new Runnable() {            
	
			@Override
	        public void run() {
				//
				// Deselect whatever was shown before as long as it's not null
				//
				if(selection_selectable!=null)
					selection_selectable.deselect();
			
				//
				// Set the selected entity to the new entity
				//
				selection_object		 = newSelection;
				
				//
				// Check for a selectable component
				//
				if(selection_object!=null)
					selection_selectable = newSelection.getComponant(ComponentSelectable.class);
					
				//
				// If the selected entity or the selectable component is now a null, hide the panel
				//
				if(selection_object == null||selection_selectable == null){

					//
					// null out both
					//
					selection_object = null;
					selection_selectable = null;
					
					//
					// Hide the panel
					//
					selectionPanel.setVisibility(View.GONE);
					return;
				}

				//
				//Tell the new entity that it is selected
				//
				selection_selectable.select();
		
				//
				//Set the appropriate content in the content view
				//
				selection_image.setImageResource(selection_selectable.getIcon());
				selectionText.setText(selection_selectable.getName());
				updateOrders();				
				updateDamage();
				
				//
				// Add the new selection panel content, and make sure it is visible
				//
				selectionPanelContent.removeAllViews();
				selectionPanelContent.addView(selection_selectable.getSelectionView());
				selection_selectable.refreshSelectionView();
				selectionPanel.setVisibility(View.VISIBLE);
			}
		});
	}

	private void updateDamage() {
		//
		// If the selection has a hull, then show the correct damage bar. If not hide it 
		//
		ComponentHull hull = selection_object.getComponant(ComponentHull.class);
		if(hull!=null)
		{
			selectionDamage.setVisibility(View.VISIBLE);
			selectionDamage.setMax(hull.getMaxHitPoints());
			selectionDamage.setProgress(hull.getCurrentHitPoints());
		}else{
			selectionDamage.setVisibility(View.INVISIBLE);
		}
	}
	
	private void updateOrders() {
		//
		// If the selection has a hull, then show the correct damage bar. If not hide it 
		//
		ComponentBrain brain = selection_object.getComponant(ComponentBrain.class);
		if(brain!=null)
		{
			selectionOrder.setVisibility(View.VISIBLE);
			selectionOrder.setText(brain.getCurrentOrder().toString());

			selectionAction.setVisibility(View.VISIBLE);
			selectionAction.setText(brain.getCurrentAction().toString());
		}else{
			selectionOrder.setVisibility(View.INVISIBLE);
			selectionAction.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * Update the selection panel
	 * Note: this can be called from any thread 
	 */
	public void update() {
		//
		// Refresh the UI on the appropriate thread
		//
		Globals.gameActivity.runOnUiThread(new Runnable() {
			public void run() {
				if(selection_selectable!=null)
					updateOrders();
					updateDamage();
					selection_selectable.refreshSelectionView();
				}});
	}
}