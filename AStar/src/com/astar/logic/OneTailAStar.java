package com.astar.logic;

import java.util.Vector;

import com.astar.AStar;
import com.astar.AStarException;
import com.astar.model.Map;
import com.astar.model.Node;

public class OneTailAStar implements AStarFindPath, Runnable {
	public final int NO_PATH = -1, NOT_FOUND = 0, FOUND = 1;

	protected Vector<Node> edge;
	protected Vector<Node> done;
	protected Map map;
	Thread loop;

	@Override
	public Vector<Node> findPath(Map map) throws AStarException {
		this.map = map;
		Vector<Node> vector = map.getNodes();
		edge = new Vector<>();
		done = new Vector<>();
		Node startNode = Node.getStartNode();
		Node endNode = Node.getEndNode();
		if (null == startNode || null == endNode) {
			throw new AStarException("必须包含起点和终点");
		}

		loop = new Thread(this);
		loop.start();

		return null;
	}

	@Override
	public void run() {
		// open表中加入开始节点
		edge.addElement(Node.getStartNode());
		int pass = 0;
		boolean found = false;
		double start, diff;
		int state = NOT_FOUND;
		// 计时并计算
		while (state == NOT_FOUND && pass < AStar.STEP_MAX) {
			pass++;
			start = System.currentTimeMillis();
			try {
				state = step();
			} catch (AStarException e1) {
				e1.printStackTrace();
				break;
			}
			diff = System.currentTimeMillis() - start;
			try {
				loop.sleep(Math.max((long) (AStar.DRAW_PATH_INTERVAL - diff), 0));
			} catch (InterruptedException e) {
			}
		// System.out.println(diff);
		}
		if (state == FOUND) {// 如果找到了下一个点，则继续再找下一个点
			setPath(map);
		} else {
			System.out.println("No Path Found");
		}
	}

	public int step() throws AStarException {
		int tests = 0;
		boolean found = false;
		boolean growth = false;
		// 获取结束点
		Node finish = Node.getEndNode();
		// 克隆open表
		@SuppressWarnings("unchecked")
		Vector<Node> temp = (Vector<Node>) edge.clone();
		// 循环克隆的open表
		for (int i = 0; i < temp.size(); i++) {
			Node now = (Node) temp.elementAt(i);
			// 获取当前节点的周边节点
			Node next[] = map.getAdjacent(now);
			// 循环当前open表中节点的周边节点
			for (int j = 0; j < next.length; j++) {
				if (next[j] != null) {
					// 此节点是结束节点则寻路完成
					if (next[j] == finish) {
						next[j].addToPathFromStart(now.getDistFromStart(), now);
						found = true;
					}
					if(next[j].isObs_3()){
						continue;
					}
					//
					next[j].addToPathFromStart(now.getDistFromStart(), now);
					tests++;
					if (!next[j].isObs_3() && !edge.contains(next[j])) {
						next[j].toHisWay();
						edge.addElement(next[j]);
						growth = true;
					}
				}
			}
			if (found) {
				return FOUND;
			}
			done.addElement(now);

			// edge.removeElement(now);
		}
		map.repaint();
		if (!growth) {
			return NO_PATH;
		}
		return NOT_FOUND;
	}

	public void setPath(Map map) {
		System.out.println("Path Found");
		boolean finished = false;
		Node next;
		Node now = Node.getEndNode();
		Node stop = Node.getStartNode();
		
		now.toPath();//虽然不成功，但是可以重新绘制距离

		while (!finished) {
			next = map.getLowestAdjacent(now);
			next.toPath();
			now = next;
			now.toPath();
			now.repaint();
			if (now == stop) {
				finished = true;
			}
			try {
				loop.sleep(AStar.DRAW_PATH_INTERVAL);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Done");

	}
}
