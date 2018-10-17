package com.astar.logic;

import java.util.Vector;

import com.astar.AStarException;
import com.astar.model.Map;
import com.astar.model.Node;

public interface AStarFindPath {
	public Vector<Node> findPath(Map map)throws AStarException;
}
