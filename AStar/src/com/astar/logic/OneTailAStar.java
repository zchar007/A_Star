package com.astar.logic;

import java.util.Vector;

import com.astar.AStar;
import com.astar.AStarException;
import com.astar.model.Map;
import com.astar.model.Node;

public class OneTailAStar implements AStarFindPath, Runnable {
	public final int NO_PATH = -1, NOT_FOUND = 0, FOUND = 1;

	public int index = 1;// TO FINE 修复找到最后一个点后不能再继续查找的bug

	protected Vector<Node> edge;
	protected Vector<Node> edge_temp;

	protected Vector<Node> done;
	protected Map map;
	Thread loop;
	private boolean check = false;

	@Override
	public Vector<Node> findPath(Map map) throws AStarException {
		this.index = 1;
		this.check = false;
		this.map = map;
		edge = new Vector<>();
		done = new Vector<>();
		edge_temp = new Vector<>();
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
		edge_temp.addElement(Node.getStartNode());

		int pass = 0;
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
				Thread.sleep(Math.max((long) (AStar.DRAW_PATH_INTERVAL - diff), 0));
			} catch (InterruptedException e) {
			}
		}
		if (state == FOUND) {// 如果找到了下一个点，则继续再找下一个点
			try {
				setPath(map);
			} catch (AStarException e) {
				System.out.println("Set Path Fail");
				e.printStackTrace();
			}
		} else {
			System.out.println("No Path Found");
		}
	}

	public int step() throws AStarException {
		boolean found = false;
		boolean growth = false;
		// 获取结束点
		Node finish = Node.getEndNode();
		// 克隆open表
		@SuppressWarnings("unchecked")
		Vector<Node> temp = (Vector<Node>) edge_temp.clone();
		edge_temp = new Vector<>();
		// 循环克隆的open表
		for (int i = 0; i < temp.size(); i++) {
			Node now = (Node) temp.elementAt(i);
			// 获取当前节点的周边节点
			Node next[] = map.getAdjacent(now);
			// System.out.println(now.getNumber());

			// 循环当前open表中节点的周边节点
			for (int j = 0; j < next.length; j++) {
				if (next[j] != null) {
					// 此节点是结束节点则寻路完成
					if (next[j].equals(finish)) {
						if (!edge.contains(next[j])) {
							edge.addElement(next[j]);
						}
						found = true;
					}
					if (next[j].isObs_3()) {
						continue;
					}
					next[j].addToPathFromStart(now);
					next[j].repaint();
					if (!edge.contains(next[j])) {
						next[j].toHisWay();
						edge.addElement(next[j]);
						edge_temp.addElement(next[j]);
						growth = true;
					}
				}
			}
			
			try {
				Thread.sleep(Math.max((long) (AStar.DRAW_PATH_INTERVAL), 0));
			} catch (InterruptedException e) {
			}
			/**
			 * TOFINE 找到终点后在进行个循环运算，以便找到终点的最小值Cost
			 */
			if (found) {
				this.check = true;
			}
			done.addElement(now);

			// edge.removeElement(now);
		}
		if (this.check) {
			if (this.index < 2) {
				growth = true;
				this.index++;
				// return NOT_FOUND;
			} else {
				this.check = false;
				return FOUND;
			}
		} 
		map.repaint();
		if (!growth) {
			return NO_PATH;
		}
		return NOT_FOUND;
	}

	public void setPath(Map map) throws AStarException {
		System.out.println("Path Found");
		boolean finished = false;
		Node next;
		Node now = Node.getEndNode();
		Node stop = Node.getStartNode();

		now.toPath();// 虽然有可能不成功，但是可以重新绘制距离

		while (!finished) {
			next = map.getLowestAdjacent(now);
			next.toPath();
			next.addToPathFromStart(now);
			now = next;
			now.toPath();
			now.repaint();
			if (now.equals(stop)) {
				finished = true;
			}

			// try {
			// Thread.sleep(AStar.DRAW_PATH_INTERVAL);
			// } catch (InterruptedException e) {
			// }
		}

//		Vector<Node> vector = Map.getNodes();
//		for (int i = 0; i < vector.size(); i++) {
//			System.out.println(vector.get(i));
//		}
		System.out.println("Done");

	}
}
