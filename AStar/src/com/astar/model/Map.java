package com.astar.model;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JPanel;

import com.astar.AStar;
import com.astar.AStarException;

public class Map extends JPanel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Vector<Node> nodes = new Vector<>();

	/**
	 * Create the panel.
	 */
	public Map() {
		this.setPreferredSize(new Dimension(AStar.MAP_SIZE, AStar.MAP_SIZE));
		setLayout(null);
		nodes = new Vector<>();
		this.removeAll();
		int every = AStar.MAP_SIZE / AStar.NODE_SIZE;

		int index = 0;
		for (int i = 0; i < every; i++) {
			for (int j = 0; j < every; j++) {
				Node node = new Node(index++);
				node.setLocation(i * AStar.NODE_SIZE, j * AStar.NODE_SIZE);
				node.toGround();
				nodes.addElement(node);
				this.add(node);
			}
		}

	}

	@Override
	public void repaint() {
		super.repaint();

	}

	public void clearPath() {
		Node.canDraw = true;
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).clearPath();
		}
		repaint();
	}

	public void clear() {
		Node.canDraw = true;
		nodes = new Vector<>();
		this.removeAll();
		int every = AStar.MAP_SIZE / AStar.NODE_SIZE;
		Node.startNode = null;
		Node.endNode = null;
		int index = 0;
		for (int i = 0; i < every; i++) {
			for (int j = 0; j < every; j++) {
				Node node = new Node(index++);
				node.setLocation(i * AStar.NODE_SIZE, j * AStar.NODE_SIZE);
				node.toGround();
				nodes.addElement(node);
				this.add(node);
			}
		}
		repaint();
	}

	public static Vector<Node> getNodes() {
		return nodes;
	}

	public Node[] getAdjacent(Node now) {
		Node[] node_array = new Node[8];

		int number = now.getNumber();
		int every = AStar.MAP_SIZE / AStar.NODE_SIZE;

		if (AStar.STRAIGHT) {
			// 上
			if (number - every >= 0) {
				node_array[0] = Map.nodes.get(number - every);
			}
			// 下
			if (number + every < Map.nodes.size()) {
				node_array[1] = Map.nodes.get(number + every);
			}
			// 左 最左边的没有左点
			if (number - 1 >= 0 && number % every != 0) {
				node_array[2] = Map.nodes.get(number - 1);
			}
			// 右 最右边的没有右点
			if ((number + 1) % every != 0 && number + 1 < Map.nodes.size()) {
				node_array[3] = Map.nodes.get(number + 1);

			}
		}
		if (AStar.SKEW) {
			// 左上 最左边的没有左上点
			if (number % every != 0 && number - every - 1 >= 0) {
				node_array[4] = Map.nodes.get(number - every - 1);
			}
			// 左下 最左边的没有左下点
			if (number % every != 0 && number + every - 1 < Map.nodes.size()) {
				node_array[5] = Map.nodes.get(number + every - 1);
			}
			// 右上 最右边的没有右上
			if ((number + 1) % every != 0 && number - every + 1 >= 0) {
				node_array[6] = Map.nodes.get(number - every + 1);
			}
			// 右下 最右边的没有右下
			if ((number + 1) % every != 0 && number + every + 1 < Map.nodes.size()) {
				node_array[7] = Map.nodes.get(number + every + 1);
			}
		}

		return node_array;
	}

	/**
	 * TOFINE 这里对原作者的逻辑进行了优化<br>
	 * <br>
	 * 参照以下地图片段制图即可理解<br>
	 * 
	 * end, fineway, fineway, normalway,.......<br>
	 * normalway, fineway, fineway, normalway.........<br>
	 * normalway, normalway, normalway, normalway.........<br>
	 * normalway, normalway, normalway, start.........<br>
	 * 
	 * @throws AStarException
	 * 
	 */
	public Node getLowestAdjacent(Node now) throws AStarException {
		Node next[] = getAdjacent(now);
		Node small = next[0];
		double dist = Double.MAX_VALUE;

		Node start = null;
		for (int i = 0; i < next.length; i++) {
			if (next[i] != null) {

				double nextDist = next[i].getDistFromStart();
				// 增加过是开始节点，则直接返回，防止在开始节点的前一个点走入死循环
				// 如果一圈内有终点，且终点和此点在一同一行或同一列，那么直接返回杰克，否则在斜线上，有可能直接走并不是最好的

				
				int position = next[i].getPosition(now);
				if (next[i].isStart()) {
					start = next[i];
					if (position < 10) {
						return start;
					}
				}
				if (!next[i].isHisWay() && !next[i].isPath()) {
					continue;
				}
				double c = now.getThrowCost(next[i]);
				double distFromStart = next[i].getDistFromStart();

				if (c >= 0) {
					nextDist += c;
				}
				// }
				if (nextDist < dist && nextDist >= 0) {
					small = next[i];
					if (c > 0) {
						dist = distFromStart + c;
					} else {
						dist = distFromStart;
					}

				}
			}
		}
		// 当前点 now,已寻到的下一步 small
		if (null != start && Node.getStartNode().equals(start) && !small.equals(start)) {
			double startToSmall = now.getThrowCost(start);
			double startToSmallToNow = small.getThrowCost(start) + now.getThrowCost(small);

			if (startToSmallToNow >= startToSmall) {
				return start;
			}

		}
		return small;
	}
}
