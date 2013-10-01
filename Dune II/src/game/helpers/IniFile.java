package game.helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.StringTokenizer;

import android.app.Activity;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 * 
 * This class parses an INI file into a hashtable of key values separated into groups
 * ;
 * ;comment
 * ;
 * [Group1]
 * key1 = value1
 * key2 = value2
 * 
 * [Group2]
 * key3 = value3
 * ...
 * 
 */
public class IniFile {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final String START_GROUP_STRING = "["; 		//If a line starts with this...
	private static final String END_GROUP_STRING = "]"; 		//...and ends with this, then whatever is between is a new group
	private static final String DEFAULT_GROUP_NAME = "[BASIC]";	//Key/value pairs will be put in this group until the file declares a new one
	private static final String COMMENT_STRING = ";"; 			//If a line starts with this then ignore it
	private static final String LINE_DELIMITERS = "=\t";		//One of these must be between each key/value pair

	// ===========================================================
	// Fields
	// ===========================================================
	Hashtable<String, Hashtable<String,String>> sections = new Hashtable<String, Hashtable<String,String>>();

	// ===========================================================
	// Non-inherited Methods
	// ===========================================================

	/**
	 * Loads a file into a hashtable
	 * 
	 * @param activity 		- The loading activity
	 * @param filename		- The file to load
	 */
	public boolean load(Activity activity, String filename)
	{
		try {
			InputStream in = activity.getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			//
			//Create a generic group for any key/value pairs that occur before a group is declared in the file
			//
			String currentSection = DEFAULT_GROUP_NAME;
			String line;
						
			while((line = br.readLine()) != null) {
				//
				//If a line is blank or starts with the comment symbol, then don't parse it
				//
				if(!line.startsWith(COMMENT_STRING)&&!(line.isEmpty()))
				{
					//
					//Break the line up into tokens
					//
					StringTokenizer data = new StringTokenizer(line, LINE_DELIMITERS);
					
					//
					//Trim out any white space on either end
					//
					String token = data.nextToken().trim(); 

					//
					//If the token is a group name, then make it the current group
					//
					if(token.startsWith(START_GROUP_STRING)&&token.endsWith(END_GROUP_STRING)) {
						
						//
						//Remove the start/end group designator strings
						//I.E. turn "[group]" into "group"
						//
						currentSection = token.substring(START_GROUP_STRING.length(), token.length()-END_GROUP_STRING.length());

						//
						//If the group does not exist then create it
						//
						if(!sections.containsKey(currentSection))
							sections.put(currentSection, new Hashtable<String,String>());
						
					} else {
						//
						//If the token does note designate a group then treat it as a key/value pair
						//
						String value = data.nextToken();
						
						if(value!=null)
							sections.get(currentSection).put(token, value.trim());
						else
							sections.get(currentSection).put(token, "");
					}					
				}
			} 
		} catch (Exception e) {
 			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * Gets a hashtable value based on its group and key
	 * 
	 * @param group 	- The value group
	 * @param key		- The value key
	 */
	public String getString(String group, String key, String defaultVal)	{
		if(sections.containsKey(group)&&sections.get(group).containsKey(key))
			return sections.get(group).get(key);
		else		
			return defaultVal;
	}

	/**
	 * Gets a hashtable value based on its group and key
	 * and attempts to convert it to an integer
	 * 
	 * @param group 	- The value group
	 * @param key		- The value key
	 * @param defaultVal - The default value if the key does not exist 
	 */
	public int getInt(String group, String key, int defaultVal)	{
		if(sections.containsKey(group)&&sections.get(group).containsKey(key))
			return getInts(group, key , new Integer[]{defaultVal}, ", \t")[0];
		else
			return defaultVal;

	}

	/**
	 * Gets a hash table value based on its group and key
	 * and attempts to convert it to an array of ints
	 * 
	 * @param group 	- The value group
	 * @param key		- The value key
	 * @param defaultVal- The default value
	 */
	public String[] getStrings(String group, String key, String[] defaultVal)	{
		return getStrings(group, key , defaultVal, ", \t");
	}
	
	/**
	 * Gets a hashtable value based on its group and key
	 * and attempts to convert it to an array of ints
	 * 
	 * @param group 	- The value group
	 * @param key		- The value key
	 */
	public Integer[] getInts(String group, String key, Integer[] defaultVal)	{
		return getInts(group, key , defaultVal, ", \t");
	}
	
	/**
	 * Gets a hashtable value based on its group and key
	 * and attempts to convert it to an array of integers
	 * 
	 * @param group 	- The value group
	 * @param key		- The value key
	 * @param delimiters- That is between the integers
	 */
	public Integer[] getInts(String group, String key, Integer[] defaultVals, String delimiters)	{

		if(!sections.containsKey(group)||!sections.get(group).containsKey(key))
			return defaultVals;
		
		StringTokenizer data = new StringTokenizer(sections.get(group).get(key), delimiters);
		
		ArrayList<Integer> ints = new ArrayList<Integer>();
		
		while(data.hasMoreTokens()){
			
		int i =	Integer.parseInt(data.nextToken().trim());
		ints.add(i);

		}
		
		return ints.toArray(new Integer[ints.size()]);
	}

	/**
	 * Gets a hashtable value based on its group and key
	 * and attempts to convert it to an array of tokens
	 * 
	 * @param group 	- The value group
	 * @param key		- The value key
	 * @param delimiters- That is between the integers
	 */
	public String[] getStrings(String group, String key, String[] defaultVal, String delimiters)	{

		if(!sections.containsKey(group)||!sections.get(group).containsKey(key))
			return defaultVal;
		
		StringTokenizer data = new StringTokenizer(sections.get(group).get(key), delimiters);
		
		ArrayList<String> ints = new ArrayList<String>();
		
		while(data.hasMoreTokens()){
			
		ints.add(data.nextToken().trim());

		}
		
		return ints.toArray(new String[ints.size()]);
	}

	public boolean groupExists(String group) {
		return sections.containsKey(group);
	}

	public String[] getGroupValues(String group) {
		if(sections.containsKey(group))
		{
			Collection<String> vals = sections.get(group).values();
			return vals.toArray(new String[vals.size()]);
		}
		else
			return new String[]{};
	}
}