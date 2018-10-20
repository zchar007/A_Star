package com.astar;

import java.awt.Color;

public class AStar {

	
	public static int MAP_SIZE = 800;// 地图大小
	public static int NODE_SIZE = 200;// 地图上每个单位大小

	public static int STEP_MAX = 1000;
	public static boolean STRAIGHT = true;// 是否能直着走
	public static boolean SKEW = true;// 是否能斜着走

	public static final int NODE_GROUND = 0;// 空地
	public static final int NODE_START = 1;// 开始点
	public static final int NODE_END = 2;// 结束点
	public static final int NODE_OBS_0 = 3;// 障碍物
	public static final int NODE_OBS_1 = 4;// 障碍物
	public static final int NODE_OBS_2 = 5;// 障碍物
	public static final int NODE_OBS_3 = 6;// 障碍物
	public static final int NODE_PATH = 7;// 路径
	public static final int NODE_HIS_WAY = 8;// 路径
	public static int NOW_DRAW_TYPE = AStar.NODE_START;

	public static Color COLOR_LINE = Color.WHITE;
	public static Color COLOR_GROUND = Color.RED;
	public static Color COLOR_START = Color.GREEN;
	public static Color COLOR_END = Color.BLUE;
	public static Color COLOR_OBS_0 = Color.ORANGE;
	public static Color COLOR_OBS_1 = Color.LIGHT_GRAY;
	public static Color COLOR_OBS_2 = Color.GRAY;
	public static Color COLOR_OBS_3 = Color.BLACK;
	public static Color COLOR_PATH = Color.YELLOW;
	public static Color COLOR_HIS_WAY = Color.PINK;

	public static Color NOW_DRAW_COLOR = AStar.COLOR_START;

	public static final int INSPIRE_TYPE_CLASSIC = 0;//
	public static final int INSPIRE_TYPE_OLD = 1;//
	public static final int INSPIRE_TYPE_FUDGE = 2;//
	public static int NOW_INSPIRE_TYPE = AStar.INSPIRE_TYPE_OLD;// 路径

	public static long DRAW_PATH_INTERVAL = 10;// 绘制路径方块时的时间间隔，默认20毫秒

	public static final double THROW_LEVEL_SKEW_RATIO = 1.4;// 非要斜着走
	
	public static final double THROW_LEVEL_START = 100;// 起始点，成本影响最后的逻辑，可能影响结果
	public static final double THROW_LEVEL_END = 1.0;// 穿过成本为0，所以特容易到达
	public static final double THROW_LEVEL_0 = 0.1;// 特别好通过
	public static final double THROW_LEVEL_GROUND = 1;// 正常通过
	public static final double THROW_LEVEL_1 = 5;// 特难通过
	public static final double THROW_LEVEL_2 = 10;// 基本无法通过
	public static final double THROW_LEVEL_3 = Double.MAX_VALUE;// 确定无法通过

	/**
	 * 静态值 值勿动，牵扯到运算逻辑
	 */
	public static final int ORDER_SELF = 0;
	public static final int ORDER_UP = 1;
	public static final int ORDER_DOWN = 2;
	public static final int ORDER_RIGHT = 3;
	public static final int ORDER_LEFT = 4;
	public static final int ORDER_RIGHT_UP = 31;
	public static final int ORDER_LEFT_UP = 41;
	public static final int ORDER_RIGHT_DOWN = 32;
	public static final int ORDER_LEFT_DOWN = 42;

	// 异常类型
	public static final int EXCEPTION_WARN = 0;//
	public static final int EXCEPTION_INFO = 1;//
	public static final int EXCEPTION_ERROR = 2;//
	public static final int EXCEPTION_QUESTION = 3;//


	public static Color getColor(int now_draw_type) {
		switch (now_draw_type) {
		case AStar.NODE_START: {
			return AStar.COLOR_START;
		}
		case AStar.NODE_END: {
			return AStar.COLOR_END;
		}
		case AStar.NODE_OBS_0: {
			return AStar.COLOR_OBS_0;
		}
		case AStar.NODE_OBS_1: {
			return AStar.COLOR_OBS_1;
		}
		case AStar.NODE_OBS_2: {
			return AStar.COLOR_OBS_2;
		}
		case AStar.NODE_OBS_3: {
			return AStar.COLOR_OBS_3;
		}
		case AStar.NODE_GROUND: {
			return AStar.COLOR_GROUND;
		}
		case AStar.NODE_PATH: {
			return AStar.COLOR_PATH;
		}
		case AStar.NODE_HIS_WAY: {
			return AStar.COLOR_HIS_WAY;
		}
		default: {
			return AStar.COLOR_GROUND;

		}
		}

	}
}
