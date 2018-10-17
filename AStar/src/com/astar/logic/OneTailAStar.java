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
		edge = new Vector<>();
		done = new Vector<>();
		Node startNode = Node.getStartNode();
		Node endNode = Node.getEndNode();
		if (null == startNode || null == endNode) {
			throw new AStarException("������������յ�");
		}

		loop = new Thread(this);
		loop.start();

		return null;
	}

	@Override
	public void run() {
		// open���м��뿪ʼ�ڵ�
		edge.addElement(Node.getStartNode());
		int pass = 0;
		double start, diff;
		int state = NOT_FOUND;
		// ��ʱ������
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
		if (state == FOUND) {// ����ҵ�����һ���㣬�����������һ����
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
		boolean found = false,found2 = false;
		boolean growth = false;
		// ��ȡ������
		Node finish = Node.getEndNode();
		// ��¡open��
		@SuppressWarnings("unchecked")
		Vector<Node> temp = (Vector<Node>) edge.clone();
		// ѭ����¡��open��
		for (int i = 0; i < temp.size(); i++) {
			Node now = (Node) temp.elementAt(i);
			// ��ȡ��ǰ�ڵ���ܱ߽ڵ�
			Node next[] = map.getAdjacent(now);
			// ѭ����ǰopen���нڵ���ܱ߽ڵ�
			for (int j = 0; j < next.length; j++) {
				if (next[j] != null) {
					// �˽ڵ��ǽ����ڵ���Ѱ·���
					if (next[j] == finish) {
						if (!edge.contains(next[j])) {
							edge.addElement(next[j]);
						}
						found = true;
					}
					if (next[j].isObs_3()) {
						continue;
					}
					next[j].addToPathFromStart(now.getDistFromStart(), now);

					if (!edge.contains(next[j])) {
						next[j].toHisWay();
						edge.addElement(next[j]);
						growth = true;
					}
				}
			}
			/**
			 * TOFINE  �ҵ��յ���ڽ��и�ѭ�����㣬�Ա��ҵ��յ����СֵCost
			 */
			if (found2) {
				return FOUND;
			}
			if(found){
				found2 = true;
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

	public void setPath(Map map) throws AStarException {
		System.out.println("Path Found");
		boolean finished = false;
		Node next;
		Node now = Node.getEndNode();
		Node stop = Node.getStartNode();

		now.toPath();// ��Ȼ���ɹ������ǿ������»��ƾ���

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
				Thread.sleep(AStar.DRAW_PATH_INTERVAL);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Done");

	}
}
