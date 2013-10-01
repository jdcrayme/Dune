package game.dune.ii;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//
//  This class fill the production lists with data from unit and structure types
//
public class ProductionListAdapter extends ArrayAdapter<GameObjectFactory>{
	// ===========================================================
	// Sub class
	// ===========================================================

	static class DataHolder
    {
        ImageView icon;
        TextView text;
        TextView cost;
    }
	
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
    Context context; 
    int layoutResourceId;    
    GameObjectFactory data[] = null;


	// ===========================================================
	// Constructors
	// ===========================================================
	public ProductionListAdapter(Context context, int layoutResourceId, GameObjectFactory[] data) {
	    super(context, layoutResourceId, data);
	    this.layoutResourceId = layoutResourceId;
	    this.context = context;
	    this.data = data;
	}
	// ===========================================================
	// Getters & Setters
	// ===========================================================
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DataHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new DataHolder();
            holder.icon = (ImageView)row.findViewById(R.id.selection_list_icon);
            holder.text = (TextView)row.findViewById(R.id.selection_list_text);
            holder.cost = (TextView)row.findViewById(R.id.selection_list_cost);
            
            row.setTag(holder);
        }
        else
        {
            holder = (DataHolder)row.getTag();
        }
        
        GameObjectFactory weather = data[position];
        ComponentSelectableFactory selectionData = weather.getComponantFactory(ComponentSelectableFactory.class);
        
        holder.text.setText(selectionData.getName());
        holder.icon.setImageResource(selectionData.getIcon());
        holder.cost.setText(""+selectionData.getCost());
        
        return row;
    }
    
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
}