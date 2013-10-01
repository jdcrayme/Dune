package game.dune.ii;

import android.content.Context;

// ===========================================================
// Methods for/from SuperClass/Interfaces
// ===========================================================

public class Utility {
	/**
	 * This method retrieves an image resource ID from a unit structure or house name
	 * @param name 		- The name of the image to retrieve
	 * @param context 	- The context containing the resource
	 */
	public static int getResIDFromName(String name, Context context)
	{
		if(name == null || name == "")
			return R.drawable.barracks;
		
		//Units
		if(name == context.getString(R.string.unit_carryall_name))
			return R.drawable.carryall;

		if(name == context.getString(R.string.unit_devastator_name))
			return R.drawable.devastator;

		if(name == context.getString(R.string.unit_deviator_name))
			return R.drawable.deviator;

		if(name == context.getString(R.string.unit_dhand_name))
			return R.drawable.dhand;

		if(name == context.getString(R.string.unit_fremen_name))
			return R.drawable.fremen;

		if(name == context.getString(R.string.unit_harvester_name))
			return R.drawable.harvester;

		if(name == context.getString(R.string.unit_infantry_name))
			return R.drawable.infantry;

		if(name == context.getString(R.string.unit_launcher_name))
			return R.drawable.launcher;

		if(name == context.getString(R.string.unit_mcv_name))
			return R.drawable.mcv;

		if(name == context.getString(R.string.unit_ornithopter_name))
			return R.drawable.ornithopter;

		if(name == context.getString(R.string.unit_quad_name))
			return R.drawable.quad;

		if(name == context.getString(R.string.unit_raider_name))
			return R.drawable.raider;

		if(name == context.getString(R.string.unit_saboteur_name))
			return R.drawable.saboteur;

		if(name == context.getString(R.string.unit_sardaukar_name))
			return R.drawable.sardaukar;

		if(name == context.getString(R.string.unit_sonic_name))
			return R.drawable.sonic;

		if(name == context.getString(R.string.unit_stank_name))
			return R.drawable.stank;

		if(name == context.getString(R.string.unit_tank_name))
			return R.drawable.tank;

		if(name == context.getString(R.string.unit_trike_name))
			return R.drawable.trike;

		if(name == context.getString(R.string.unit_troopers_name))
			return R.drawable.troopers;

		if(name == context.getString(R.string.unit_worm_name))
			return R.drawable.worm;

		//Structures
		if(name == context.getString(R.string.structure_barracks_name))
			return R.drawable.barracks;

		if(name == context.getString(R.string.structure_constyard_name))
			return R.drawable.constyard;

		if(name == context.getString(R.string.structure_heavyfactory_name))
			return R.drawable.heavyfactory;
		
		if(name == context.getString(R.string.structure_ix_name))
			return R.drawable.ix;

		if(name == context.getString(R.string.structure_lightfactory_name))
			return R.drawable.lightfactory;

		if(name == context.getString(R.string.structure_outpost_name))
			return R.drawable.outpost;

		if(name == context.getString(R.string.structure_palace_name))
			return R.drawable.palace;
		
		if(name == context.getString(R.string.structure_refinery_name))
			return R.drawable.refinery;
		
		if(name == context.getString(R.string.structure_repairfactory_name))
			return R.drawable.repairfactory;
		
		if(name == context.getString(R.string.structure_rturret_name))
			return R.drawable.rturret;
		
		if(name == context.getString(R.string.structure_silo_name))
			return R.drawable.silo;
		
		if(name == context.getString(R.string.structure_slab_name))
			return R.drawable.slab;
		
		if(name == context.getString(R.string.structure_starport_name))
			return R.drawable.starport;
		
		if(name == context.getString(R.string.structure_techfactory_name))
			return R.drawable.techfactory;
		
		if(name == context.getString(R.string.structure_turret_name))
			return R.drawable.turret;

		if(name == context.getString(R.string.structure_wall_name))
			return R.drawable.wall;
		
		if(name == context.getString(R.string.structure_windtrap_name))
			return R.drawable.windtrap;

		if(name == context.getString(R.string.structure_wor_name))
			return R.drawable.wor;

		//Houses
		if(name == context.getString(R.string.atreides))
			return R.drawable.crest_atr;

		if(name == context.getString(R.string.harkonnen))
			return R.drawable.crest_har;

		if(name == context.getString(R.string.ordos))
			return R.drawable.crest_ord;

		return R.drawable.carryall;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T safeCast(Object obj, Class<T> type){
		if(type.isInstance(obj))
			return (T)obj;
		
		return null;
	}
}
