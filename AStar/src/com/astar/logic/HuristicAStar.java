package com.astar.logic;

import java.awt.Point;
import java.util.Vector;

import com.astar.AStarException;
import com.astar.model.Map;
import com.astar.model.Node;

public class HuristicAStar extends OneTailAStar implements AStarFindPath {
	double minCost;
	private int stepSpeed;

	public HuristicAStar() {
		super();
		stepSpeed = 20;
	}

	/**
	 * calculates the waighted manhattan distance from a to b
	 */
	public double cbDist(Point a, Point b, double low) {
		return low * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - 1);
	}

	public Vector<Node> findPath(Map map) throws AStarException {
//		minCost = Double.MAX_VALUE;
//		for (int i = 0; i < Map.getNodes().size(); i++) {
//				minCost = Math.min(Map.getNodes().getCost(), minCost);
//			}
//		}
//		// minCost=0.9;
//		System.out.println("Cheepest Tile = " + minCost);
		return super.findPath(map);
	}

	public int step() throws AStarException {
		int tests = 0;
		boolean found = false;
		boolean growth = true;
		Node finish = Node.getEndNode();
		Point end = finish.getPoint();
		@SuppressWarnings("unchecked")
		Vector<Node> temp = (Vector<Node>) edge.clone();
		// find the most promesing edge cell
		double min = Double.MAX_VALUE;
		double score;
		// int best = -1;
		Node best = (Node) temp.elementAt(temp.size() - 1);
	
		Node now;
		for (int i = 0; i < temp.size(); i++) {
			now = (Node) temp.elementAt(i);
			if (!done.contains(now)) {
				// score =now.getDistFromStart();

				score = now.getDistFromStart();
				score += cbDist(now.getPoint(), end, minCost);
				if (score < min) {
					min = score;
					best = now;
				}
			}
		}
		
		now = best;
		// System.out.println(now.getPosition()+" Selected for expansion");
		edge.removeElement(now);
		done.addElement(now);
		Node next[] = map.getAdjacent(now);
		for (int i = 0; i < next.length; i++) {
			if (next[i] != null) {
				if (next[i] == finish) {
					found = true;
				}
				if (!next[i].isObs_3()) {
					next[i].addToPathFromStart(now);
					tests++;
					if (!edge.contains(next[i]) && !done.contains(next[i])) {
						next[i].toHisWay();
						edge.addElement(next[i]);
						growth = true;
					}
				}
			}
			if (found) {
				return FOUND;
			}
		}
		map.repaint();
		if (edge.size() == 0) {
			return NO_PATH;
		}

		// now process best.
		return NOT_FOUND;
	}

}