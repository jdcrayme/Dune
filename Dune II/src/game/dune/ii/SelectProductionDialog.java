package game.dune.ii;

import game.dune.ii.ComponentProductionFactory.ComponentProduction;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class SelectProductionDialog {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private static Dialog buildStructureSelectDlg;
	private static GameObjectFactory selectedProduction;
	private static ComponentProduction selectedFactory; 
	
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void showDialog(final ActivityGame activity, final ComponentProduction factory)	{
		selectedFactory = factory;
		
		//If this is the first time through, then we have to build everything
//		if(treubuildStructureSelectDlg==null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setView(activity.getLayoutInflater().inflate(R.layout.structure_build_selector_layout, null))
				.setPositiveButton(R.string.build, new OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(selectedProduction!=null)
							selectedFactory.startProduction(selectedProduction);
					}})
				.setNegativeButton(R.string.cancel, null)
				.setTitle(R.string.select_production);

			//Show the dialog
			buildStructureSelectDlg = builder.show();
			
			//Get handles to all the dynamic content in the dialog
			final ListView  structureList  = (ListView)  buildStructureSelectDlg.findViewById(R.id.structure_build_selector_list);
			final ImageView selectionImage = (ImageView) buildStructureSelectDlg.findViewById(R.id.structure_build_selector_selection_image);
			final TextView  selectionName  = (TextView)  buildStructureSelectDlg.findViewById(R.id.structure_build_selector_selection_name);
			final TextView  selectionCost  = (TextView)  buildStructureSelectDlg.findViewById(R.id.structure_build_selector_selection_cost);
			final TextView  selectionDesc  = (TextView)  buildStructureSelectDlg.findViewById(R.id.structure_build_selector_selection_description);
			
			GameObjectFactory[] productionList = factory.getAvalibleProduction();
			List<GameObjectFactory> buildableList = new ArrayList<GameObjectFactory>();

			for(int i =0;i<productionList.length;i++)
			{
				GameObjectFactory prod = productionList[i];
				ComponentSelectableFactory seldata = prod.getComponantFactory(ComponentSelectableFactory.class);
						
				if(seldata!=null&seldata.getTechLevel()<= activity.getTechnologyLevel())
						buildableList.add(prod);
			}
					
			//Build and attach the adapter and listeners
			ProductionListAdapter adapter = new ProductionListAdapter(activity, R.layout.selection_list_row_layout, buildableList.toArray(new GameObjectFactory[buildableList.size()]));
			structureList.setAdapter(adapter);
			structureList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list, View view, int position, long id) {

						//if the user clicks on something then make it the primary selection
						GameObjectFactory selection = (GameObjectFactory)list.getItemAtPosition(position);
						setSelection(selection);
					}

					private void setSelection(GameObjectFactory selection) {
						selectedProduction = selection;

						ComponentSelectableFactory seldata = selection.getComponantFactory(ComponentSelectableFactory.class);
						
						if(seldata!=null)
						{
							//Update the GUI
							selectionImage.setImageResource(seldata.getIcon());
							selectionName.setText(seldata.getName());
							selectionCost.setText(""+seldata.getCost());
							selectionDesc.setText(seldata.getDescription());
						}
					}});
			
			structureList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
			structureList.clearFocus();
			structureList.post(new Runnable() {

                @Override
                public void run() { structureList.setSelection(0); }});

		//} else {
	  //If everything is already built, then just show it.
	  //buildStructureSelectDlg.show();
		}
}
