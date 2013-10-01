package game.dune.ii;

import game.dune.ii.ComponentBrainFactory.ComponentBrain;
import game.dune.ii.ComponentBrainFactory.OrderType;
import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.LayerTerrain.TerrainCell;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This is the rectangle that shows where a new structure is going to go
 */
class UserInterfaceActionMenu
{
	/**
	 * 
	 */
	private final UserInterface userInterface;
	private CircleLayout actionMenu;

	public UserInterfaceActionMenu(UserInterface userInterface) {
		this.userInterface = userInterface;
		actionMenu = (CircleLayout)Globals.gameActivity.findViewById(R.id.action_menu);
	}
	
	public void hide() {
		actionMenu.setVisibility(View.GONE);
	}
	
	Button createAcitionButon(CharSequence btnText, int btnIcon, OnClickListener onClick)
	{
		Button txt = new Button(actionMenu.getContext());
		txt.setOnClickListener(onClick);
		txt.setText(btnText);
		txt.setCompoundDrawablesWithIntrinsicBounds(0, btnIcon, 0, 0);
		txt.setBackgroundColor(0x00000000);
		return txt;
	}
	
	public void show(final GameObject selectedEntity, GameObject targetEntity, final TerrainCell targetCell) {
		Globals.gameActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				boolean atLeastOneCommand = false;
				actionMenu.removeAllViews();
				
				ArrayList<OrderType> commands;
				
				final ComponentBrain brain = selectedEntity.getComponant(ComponentBrain.class);
				
				if(brain==null)
					return;
				
				commands = brain.getAvalibleCommands(targetCell);
				
				if(commands.contains(OrderType.Move))
				{
					atLeastOneCommand = true;
					actionMenu.addView(createAcitionButon("Move", R.drawable.icon_move, new OnClickListener(){
						@Override
						public void onClick(View v) {
							brain.sendOrder(OrderType.Move, targetCell);
							UserInterfaceActionMenu.this.userInterface.setNormalMode();
						}}));
				}

				if(commands.contains(OrderType.Harvest))
				{
					atLeastOneCommand = true;
					actionMenu.addView(createAcitionButon("Harvest", R.drawable.icon_harvest, new OnClickListener(){
						@Override
						public void onClick(View v) {
							brain.sendOrder(OrderType.Harvest, targetCell);
							UserInterfaceActionMenu.this.userInterface.setNormalMode();
						}}));
				}

				if(commands.contains(OrderType.Unload))
				{
					atLeastOneCommand = true;
					actionMenu.addView(createAcitionButon("Unload", R.drawable.icon_harvest, new OnClickListener(){
						@Override
						public void onClick(View v) {
							brain.sendOrder(OrderType.Unload, targetCell);
							UserInterfaceActionMenu.this.userInterface.setNormalMode();
						}}));
				}
				
				if(commands.contains(OrderType.Attack))
				{
					atLeastOneCommand = true;
					actionMenu.addView(createAcitionButon("Attack", R.drawable.icon_attack, new OnClickListener(){
						@Override
						public void onClick(View v) {
							brain.sendOrder(OrderType.Attack, targetCell);
							UserInterfaceActionMenu.this.userInterface.setNormalMode();
						}}));
				}
				
				/*
				if(commands.contains(ComponentWeaponFactory.ATTACK))
				{
					atLeastOneCommand = true;
					actionMenu.addView(createAcitionButon("Attack", R.drawable.icon_attack, new OnClickListener(){
						@Override
						public void onClick(View v) {
							brain.sendOrder(OrderType.Attack, targetCell);
//							selectedEntity.sendMessage(ComponentWeaponFactory.ATTACK, targetCell);
							UserInterfaceActionMenu.this.userInterface.setNormalMode();
						}}));
				}
				/*
				if(commands.contains("DESTRUCT"))
				{
					actionMenu.addView(createAcitionButon("Destruct", R.drawable.icon_attack, new OnClickListener(){
						@Override
						public void onClick(View v) {
							selectedEntity.sendMessage("DAMAGE", 9999999);
							UserInterfaceActionMenu.this.userInterface.setNormalMode();
						}}));
				}				
				
				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_MOVE)==GameObject.CAN_MOVE)
				{
					actionMenu.addView(createAcitionButon("Move", R.drawable.icon_move, new OnClickListener(){
						@Override
						public void onClick(View v) {
							((Unit)selectedEntity).moveTo(targetCell);
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_ATTACK)==GameObject.CAN_ATTACK)
				{
					actionMenu.addView(createAcitionButon("Attack", R.drawable.icon_attack, new OnClickListener(){
						@Override
						public void onClick(View v) {
							((Unit)selectedEntity).attack(null);
							Toast.makeText(Globals.gameActivity, "Attack", Toast.LENGTH_SHORT).show();
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_GROUP)==GameObject.CAN_GROUP)
				{
					actionMenu.addView(createAcitionButon("Add to Group", R.drawable.icon_group_add, new OnClickListener(){
						@Override
						public void onClick(View v) {
							Toast.makeText(Globals.gameActivity, "Add to Group", Toast.LENGTH_SHORT).show();
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_GROUP)==GameObject.CAN_GROUP)
				{
					actionMenu.addView(createAcitionButon("Remove from Group", R.drawable.icon_group_remove, new OnClickListener(){
						@Override
						public void onClick(View v) {
							Toast.makeText(Globals.gameActivity, "Remove from Group", Toast.LENGTH_SHORT).show();
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_HARVEST)==GameObject.CAN_HARVEST&&(targetCell.type==LayerTerrain.TERRAIN_SPICE||targetCell.type==LayerTerrain.TERRAIN_SUPER_SPICE))
				{
					actionMenu.addView(createAcitionButon("Harvest", R.drawable.icon_harvest, new OnClickListener(){
						@Override
						public void onClick(View v) {
							((Unit)selectedEntity).harvest(targetCell);
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_PATROL)==GameObject.CAN_PATROL)
				{
					actionMenu.addView(createAcitionButon("Patrol", R.drawable.icon_patrol, new OnClickListener(){
						@Override
						public void onClick(View v) {
							Toast.makeText(Globals.gameActivity, "Patrol", Toast.LENGTH_SHORT).show();
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_RALLY)==GameObject.CAN_RALLY)
				{
					actionMenu.addView(createAcitionButon("Rally", R.drawable.icon_rally, new OnClickListener(){
						@Override
						public void onClick(View v) {
							Toast.makeText(Globals.gameActivity, "Rally", Toast.LENGTH_SHORT).show();
							setNormalMode();
						}}));
				}

				if((selectedEntity.GetObjectType().getCapabilities()&GameObject.CAN_STOP)==GameObject.CAN_STOP)
				{
					actionMenu.addView(createAcitionButon("Stop", R.drawable.icon_stop, new OnClickListener(){
						@Override
						public void onClick(View v) {
							Toast.makeText(Globals.gameActivity, "Stop", Toast.LENGTH_SHORT).show();
							setNormalMode();
						}}));
				}*/
				if(atLeastOneCommand)
					actionMenu.setVisibility(View.VISIBLE);
			}        
		});
	}
}