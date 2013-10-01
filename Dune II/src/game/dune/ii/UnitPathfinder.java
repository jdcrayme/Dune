package game.dune.ii;

import game.dune.ii.GameObjectFactory.GameObject;
import game.dune.ii.LayerTerrain.TerrainCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An implementation of the A* path finding algorithm.
 *
 * @author Gene McCulley
 */
public class UnitPathfinder {
	private static final int MAX_PATH_ITERATIONS = 10000;
	
	private static List<TerrainCell> openList = new ArrayList<TerrainCell>();
	private static List<TerrainCell> closedList = new ArrayList<TerrainCell>();

    public static Stack<TerrainCell> findPath(GameObject mover, TerrainCell start, TerrainCell end) {
    	Stack<TerrainCell> path = new Stack<TerrainCell>();
	    	
    	openList.clear();
    	closedList.clear();
    	
    	// 
    	// Add the starting square (or node) to the open list.
    	//
    	start.setPath(mover, null, end);
    	openList.add(start);
    	
    	int counter = 0;

    	while(!openList.isEmpty()&&counter<MAX_PATH_ITERATIONS)
    	{
    		//Look for the lowest F cost square on the open list. We refer to this as the currentCell.
    		TerrainCell currentCell = null;
        	int bestCost = Integer.MAX_VALUE;
        	
        	for(int i = 0; i < openList.size(); i++)
        	{
        		TerrainCell cell = openList.get(i); 
        		int cellCost = cell.getTotalCostEstimate(); 
        		if(cellCost < bestCost )
        		{
        			currentCell = cell;
        			bestCost	= cellCost;
        		}
        	}
        	
        	//
        	// If the cell we are looking at is part of the same structure as our destination then count this cell as 
        	//
        	if(currentCell.containedObject != null && currentCell.containedObject == end.containedObject)
        		end = currentCell;
        	
        	//If the selected cell is the destination, then retrace the path
        	if(currentCell == end)
        	{
        		retracePath(path, end);
        		return path;
        	}
        	
        	//...otherwise
        	
        	//Switch it to the closed list.
        	openList.remove(currentCell);
        	closedList.add(currentCell);
    		
        	//For each of the 8 squares adjacent to this current square …
        	evaluate(mover, currentCell, currentCell.getTopLeftNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getTopNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getTopRightNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getLeftNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getRightNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getBottomLeftNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getBottomNeighbor(), end);
        	evaluate(mover, currentCell, currentCell.getBottomRightNeighbor(), end);
        	
        	//Increment the counter
        	counter++;
    	}

    	//If there are no more nodes to check and we have not found a path, then one does not exist
    	return null;
    }

	private static void evaluate(GameObject mover, TerrainCell parent, TerrainCell cell, TerrainCell end) {
		if(cell == null)
			return;
		
		//if it is on the closed list, ignore it
		if(closedList.contains(cell))
			return;
		
		if(openList.contains(cell))
		{
			//If it is on the open list already, check to see if this path to that square 
			//is better, using G cost as the measure. A lower G cost means that this is a 
			//better path. If so, change the parent of the square to the current square, 
			//and recalculate the G and F scores of the square. If you are keeping your 
			//open list sorted by F score, you may need to resort the list to account for 
			//the change.
			if(cell.pathNodeCost + parent.pathGScore < cell.pathGScore)
			{
				cell.pathParent = parent;
				cell.pathGScore = cell.pathNodeCost + parent.pathGScore;
			}
		} else {
			//If it isn’t on the open list, add it to the open list. Make the current 
			//square the parent of this square. Record the F, G, and H costs of the square.
			openList.add(cell);
			cell.setPath(mover, parent, end);
		}
	}
	
	private static void retracePath(Stack<TerrainCell> arrayList, TerrainCell cell) {
		TerrainCell parent = cell.pathParent; 

		arrayList.push(cell);

		if(parent != null)
			retracePath(arrayList,parent);
	}
}