package com.astar.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import com.astar.AStar;
import com.astar.AStarException;

public class Node extends JComponent implements Serializable {

	/**
	 * 
	 */
	private int my_draw_type;
	private Color my_draw_color;
	private static final long serialVersionUID = 1L;
	private static final int LINE_WIDTH = 1;
	transient Image buffer;
	private int number;// 编号，用于确定位置

	// 这些彼此只会单独存在
	private boolean isObs_0;// 是不是更好走的路
	private boolean isGround;// 是不是地面
	private boolean isObs_1;// 是不是一级障碍物
	private boolean isObs_2;// 是不是二级障碍物
	private boolean isObs_3;// 是不是三级障碍物
	private boolean isEnd; // 是不是终点
	private boolean isStart;// 是不是起点

	// 这些与上边可以同时存在，自身单独存在
	private boolean isPath;// 是不是路径
	private boolean isHisWay;// 是不是历史路径

	public static Node startNode;
	public static Node endNode;

	public static boolean canDraw = true;

	private double distFromStart = -1;

	private static boolean showPath = true;

	private static boolean rightMouseIsClick = false;

	private DecimalFormat df = new java.text.DecimalFormat("#.00");

	public Node(int number) {
		this.number = number;
		this.setSize(AStar.NODE_SIZE - Node.LINE_WIDTH, AStar.NODE_SIZE - Node.LINE_WIDTH);

		Border blackline, etched, raisedbevel, loweredbevel, empty; // 用Border类定义边框
		blackline = BorderFactory.createLineBorder(Color.black); // 创建黑色的线状边框（当然，此处可以改为其他颜色）
		etched = BorderFactory.createEtchedBorder(); // 创建蚀刻边框
		raisedbevel = BorderFactory.createRaisedBevelBorder(); // 创建凸出边框
		loweredbevel = BorderFactory.createLoweredBevelBorder(); // 创建凹陷边框
		empty = BorderFactory.createEmptyBorder(); // 创建空的边框

		this.setBorder(etched);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (canDraw) {
					if (e.getButton() == e.BUTTON1) {
						rightMouseIsClick = false;
						toByType(AStar.NOW_DRAW_TYPE);
					}
					if (e.getButton() == e.BUTTON3) {
						rightMouseIsClick = true;
					}
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (canDraw) {

					if (e.getButton() == e.BUTTON1) {
						rightMouseIsClick = false;
						toByType(AStar.NOW_DRAW_TYPE);
					}
					if (e.getButton() == e.BUTTON3) {
						rightMouseIsClick = true;
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (canDraw && rightMouseIsClick) {
					toByType(AStar.NOW_DRAW_TYPE);
				}
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(AStar.NODE_SIZE - Node.LINE_WIDTH, AStar.NODE_SIZE - Node.LINE_WIDTH);
	}

	/**
	 * 画本方块
	 * 
	 * @param now_draw_type
	 */
	public void drawColor(int now_draw_type) {

		if (AStar.NODE_PATH != now_draw_type && AStar.NODE_HIS_WAY != now_draw_type) {
			my_draw_type = now_draw_type;
			my_draw_color = AStar.getColor(now_draw_type);
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 只允许有一个开始点，一个结束点,本点是起始点或当前要画起始点
		// 要画开始点
		if (my_draw_type == AStar.NODE_START) {
			// 当前点现在不是开始节点，则删除其他开始节点
			if (null != Node.startNode && !Node.startNode.equals(this)) {
				Vector<Node> nodes = Map.getNodes();
				for (int i = 0; i < nodes.size(); i++) {
					Node node = nodes.get(i);
					if (node != this && node.isStart()) {
						node.toGround();
					}
				}
			}
			if (null != Node.endNode && Node.endNode.equals(this)) {
				Node.endNode = null;
			}
			Node.startNode = this;
			my_draw_type = AStar.NODE_START;
			my_draw_color = AStar.getColor(my_draw_type);
		} else if (my_draw_type == AStar.NODE_END) {
			if (null != Node.endNode && !Node.endNode.equals(this)) {
				Vector<Node> nodes = Map.getNodes();
				for (int i = 0; i < nodes.size(); i++) {
					Node node = nodes.get(i);
					if (node != this && node.isEnd()) {
						node.toGround();
					}
				}
			}
			if (null != Node.startNode && Node.startNode.equals(this)) {
				Node.startNode = null;
			}
			Node.endNode = this;
			my_draw_type = AStar.NODE_END;
			my_draw_color = AStar.getColor(my_draw_type);
		} else {
			if (null != Node.startNode && Node.startNode.equals(this)) {
				this.toStart();
			} else if (null != Node.endNode && Node.endNode.equals(this)) {
				this.toEnd();
			}
		}

		if (buffer == null) {
			buffer = createImage(getBounds().width, getBounds().height);
		}
		Graphics bg = buffer.getGraphics();
		super.paint(bg);
		Graphics2D bg2d = (Graphics2D) bg;

		bg2d.setColor(my_draw_color);
		bg2d.fillRect(0, 0, AStar.NODE_SIZE - Node.LINE_WIDTH, AStar.NODE_SIZE - Node.LINE_WIDTH);
		if (Node.showPath && this.isHisWay()) {
			bg2d.setColor(Color.BLACK);
			Font font2 = new Font("SansSerif", Font.ITALIC, AStar.NODE_SIZE / 4);
			bg2d.setFont(font2);
			bg2d.drawString(df.format(this.getDistFromStart()) + "", AStar.NODE_SIZE / 5, AStar.NODE_SIZE / 2);

			bg2d.setColor(AStar.COLOR_HIS_WAY);
			bg2d.setStroke(new BasicStroke(AStar.NODE_SIZE / 5));
			bg2d.drawRect(0, 0, AStar.NODE_SIZE - Node.LINE_WIDTH, AStar.NODE_SIZE - Node.LINE_WIDTH);
		} else if (Node.showPath && this.isPath()) {
			bg2d.setColor(Color.BLACK);
			Font font2 = new Font("SansSerif", Font.ITALIC, AStar.NODE_SIZE / 4);
			bg2d.setFont(font2);
			bg2d.drawString(df.format(this.getDistFromStart()) + "", AStar.NODE_SIZE / 5, AStar.NODE_SIZE / 2);

			bg2d.setColor(AStar.COLOR_PATH);
			bg2d.setStroke(new BasicStroke(AStar.NODE_SIZE / 5));
			bg2d.drawRect(0, 0, AStar.NODE_SIZE - Node.LINE_WIDTH, AStar.NODE_SIZE - Node.LINE_WIDTH);
		}

		// 路径画上框

		g.drawImage(buffer, 0, 0, null);

	}

	/**
	 * 获取从传入点穿过此点的花销
	 * 
	 * @param node
	 * @return
	 * @throws AStarException
	 */
	public double getThrowCost(Node fatherNode) throws AStarException {

		int order = this.getPosition(fatherNode);
		double cost_t = getCost();

		if (order == AStar.ORDER_DOWN || order == AStar.ORDER_UP || order == AStar.ORDER_RIGHT
				|| order == AStar.ORDER_LEFT) {
			return cost_t;
		} else if (order == AStar.ORDER_RIGHT_DOWN || order == AStar.ORDER_RIGHT_UP || order == AStar.ORDER_LEFT_DOWN
				|| order == AStar.ORDER_LEFT_UP) {
			if (AStar.SKEW) {
				return AStar.THROW_LEVEL_SKEW_RATIO * cost_t;
			}
		} else {
			throw new AStarException(this.getClass().getName() + ".getThrowCost().getPosition()：方法有问题，请检查！");
		}

		// 可直行

		return 1.0;
	}

	private double getCost() throws AStarException {
		if (isObs_0) {
			return AStar.THROW_LEVEL_0;
		} else if (isGround) {
			return AStar.THROW_LEVEL_GROUND;

		} else if (isObs_1) {
			return AStar.THROW_LEVEL_1;

		} else if (isObs_2) {
			return AStar.THROW_LEVEL_2;

		} else if (isObs_3) {
			return AStar.THROW_LEVEL_3;
		} else if (isEnd) {
			return AStar.THROW_LEVEL_END;
		} else if (isStart) {
			return AStar.THROW_LEVEL_START;
		} else {
			throw new AStarException(this.getClass().getName() + ".getCost():方法有问题，请检查！");
		}
	}

	public boolean isObs_0() {
		return isObs_0;
	}

	public boolean isGround() {
		return isGround;
	}

	public boolean isObs_1() {
		return isObs_1;
	}

	public boolean isObs_2() {
		return isObs_2;
	}

	public boolean isObs_3() {
		return isObs_3;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public boolean isStart() {
		return isStart;
	}

	public boolean isPath() {
		return isPath;
	}

	public boolean isHisWay() {
		return isHisWay;
	}

	private void toByType(int now_draw_type) {
		switch (now_draw_type) {
		case AStar.NODE_START: {
			this.toStart();
		}
			break;
		case AStar.NODE_END: {
			this.toEnd();
		}
			break;
		case AStar.NODE_OBS_0: {
			this.toObs_0();
		}
			break;
		case AStar.NODE_OBS_1: {
			this.toObs_1();
		}
			break;
		case AStar.NODE_OBS_2: {
			this.toObs_2();
		}
			break;
		case AStar.NODE_OBS_3: {
			this.toObs_3();
		}
			break;
		case AStar.NODE_GROUND: {
			this.toGround();
		}
			break;
		case AStar.NODE_PATH: {
			this.toPath();
		}
			break;
		case AStar.NODE_HIS_WAY: {
			this.toHisWay();
		}
			break;
		default:
			break;
		}
	}

	public void toObs_0() {
		this.isObs_0 = true;
		this.isObs_1 = false;
		this.isObs_2 = false;
		this.isObs_3 = false;
		this.isGround = false;
		this.isEnd = false;
		this.isStart = false;
		drawColor(AStar.NODE_OBS_0);
	}

	public void toObs_1() {
		this.isObs_0 = false;
		this.isObs_1 = true;
		this.isObs_2 = false;
		this.isObs_3 = false;
		this.isGround = false;
		this.isEnd = false;
		this.isStart = false;
		drawColor(AStar.NODE_OBS_1);
	}

	public void toObs_2() {
		this.isObs_0 = false;
		this.isObs_1 = false;
		this.isObs_2 = true;
		this.isObs_3 = false;
		this.isGround = false;
		this.isEnd = false;
		this.isStart = false;
		drawColor(AStar.NODE_OBS_2);
	}

	public void toObs_3() {
		this.isObs_0 = false;
		this.isObs_1 = false;
		this.isObs_2 = false;
		this.isObs_3 = true;
		this.isGround = false;
		this.isEnd = false;
		this.isStart = false;
		drawColor(AStar.NODE_OBS_3);
	}

	public void toGround() {
		this.isObs_0 = false;
		this.isObs_1 = false;
		this.isObs_2 = false;
		this.isObs_3 = false;
		this.isGround = true;
		this.isEnd = false;
		this.isStart = false;
		drawColor(AStar.NODE_GROUND);

	}

	public void toEnd() {
		this.isObs_0 = false;
		this.isObs_1 = false;
		this.isObs_2 = false;
		this.isObs_3 = false;
		this.isGround = false;
		this.isEnd = true;
		this.isStart = false;
		drawColor(AStar.NODE_END);

	}

	public void toStart() {
		this.isObs_0 = false;
		this.isObs_1 = false;
		this.isObs_2 = false;
		this.isObs_3 = false;
		this.isGround = false;
		this.isEnd = false;
		this.isStart = true;
		drawColor(AStar.NODE_START);
	}

	public void toPath() {
		this.isPath = true;
		this.isHisWay = false;
		drawColor(AStar.NODE_PATH);
	}

	public void toHisWay() {
		this.isPath = false;
		this.isHisWay = true;
		drawColor(AStar.NODE_HIS_WAY);
	}

	/**
	 * number是从0开始的，以下+1是为了让其转化成从1开始的形式，好理解 <br/>
	 * 判断此点是在传入点哪个位置(只判断方向，并不计较是不是相邻)
	 * 
	 * @return
	 */
	public int getPosition(Node fatherNode) {
		if (null == fatherNode) {
			return -1;
		}

		Point point1 = this.getPoint();
		Point point2 = fatherNode.getPoint();

		int y1 = (int) point1.getX();// 行
		int y2 = (int) point2.getX();

		int x1 = (int) point1.getY();// 列
		int x2 = (int) point2.getY();

		int h_t, v_t, p_t;
		// 上下
		if (y1 > y2) {// 下
			v_t = AStar.ORDER_DOWN;
		} else if (y1 < y2) {// 上
			v_t = AStar.ORDER_UP;
		} else {
			v_t = AStar.ORDER_SELF;
		}
		// 左右
		if (x1 > x2) {// 右
			h_t = AStar.ORDER_RIGHT;
		} else if (x1 < x2) {// 左
			h_t = AStar.ORDER_LEFT;
		} else {
			h_t = AStar.ORDER_SELF;
		}
		p_t = h_t * 10 + v_t;
		return p_t % 10 == 0 ? h_t : p_t;
	}

	public Point getPoint() {
		int number = this.getNumber();
		int every = AStar.MAP_SIZE / AStar.NODE_SIZE;

		int row = ((number + 1) / every) + ((number + 1) % every > 0 ? 1 : 0);
		int line = number % every + 1;

		return new Point(row, line);

	}

	public int getNumber() {
		return number;
	}

	public static void main(String[] args) {
		AStar.MAP_SIZE = 600;
		AStar.NODE_SIZE = 100;
		Node n1 = new Node(6);
		Node n2 = new Node(2);
	}

	
	public static Node getStartNode() {
		return startNode;
	}

	public static void setStartNode(Node startNode) {
		Node.startNode = startNode;
	}

	public static Node getEndNode() {
		return endNode;
	}

	public static void setEndNode(Node endNode) {
		Node.endNode = endNode;
	}

	public double getDistFromStart() {
		if (Node.startNode.equals(this)) {
			return 0;
		}
		if (this.isObs_3()) {
			return -1;
		}
		return distFromStart;
	}

	/**
	 * 添加远点到此节点需要走过的路程
	 * 
	 * @param distFromStart
	 * @throws AStarException
	 */
	public void addToPathFromStart(Node fatherNode) throws AStarException {
		double distSoFar = fatherNode.getDistFromStart();
		double cost = getThrowCost(fatherNode);
		if (distFromStart == -1) {
			distFromStart = distSoFar + cost;
			return;
		}
		//只用最短的
		if (distSoFar + cost < distFromStart) {
			distFromStart = distSoFar + cost;
		}
	}

	public static void setShowPath(boolean showPath) {
		Node.showPath = showPath;
	}

	public void clearPath() {
		this.isPath = false;
		this.isHisWay = false;
		this.distFromStart = -1;
		rightMouseIsClick = false;
		if (this.isStart) {
			this.my_draw_type = AStar.NODE_START;
			this.my_draw_color = AStar.COLOR_START;
		} else if (this.isEnd) {
			this.my_draw_type = AStar.NODE_END;
			this.my_draw_color = AStar.COLOR_END;
		} else if (this.isGround) {
			this.my_draw_type = AStar.NODE_GROUND;
			this.my_draw_color = AStar.COLOR_GROUND;
		} else if (this.isObs_0) {
			this.my_draw_type = AStar.NODE_OBS_0;
			this.my_draw_color = AStar.COLOR_OBS_0;
		} else if (this.isObs_1) {
			this.my_draw_type = AStar.NODE_OBS_1;
			this.my_draw_color = AStar.COLOR_OBS_1;
		} else if (this.isObs_2) {
			this.my_draw_type = AStar.NODE_OBS_2;
			this.my_draw_color = AStar.COLOR_OBS_2;
		} else if (this.isObs_3) {
			this.my_draw_type = AStar.NODE_OBS_3;
			this.my_draw_color = AStar.COLOR_OBS_3;
		}

		this.repaint();
	}

	@Override
	public String toString() {
		return "Node [point=" + this.getPoint() + ",number=" + number + ", isObs_0=" + isObs_0 + ", isGround=" + isGround + ", isObs_1=" + isObs_1
				+ ", isObs_2=" + isObs_2 + ", isObs_3=" + isObs_3 + ", isEnd=" + isEnd + ", isStart=" + isStart
				+ ", isPath=" + isPath + ", isHisWay=" + isHisWay + ", distFromStart=" + distFromStart + ", df=" + df
				+ "]";
	}
	
}
