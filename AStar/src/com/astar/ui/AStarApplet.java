package com.astar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.astar.AStar;
import com.astar.AStarException;
import com.astar.Alert;
import com.astar.model.Map;
import com.astar.model.Node;
import com.astar.UnitConversionUtil;
import com.astar.logic.AStarFindPath;
import com.astar.logic.Fudge;
import com.astar.logic.HuristicAStar;
import com.astar.logic.OneTailAStar;

public class AStarApplet extends JApplet implements ActionListener, ItemListener, FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JSplitPane splitPane;
	private Map map;
	private JScrollPane scrollPane_show;
	private static int HEAD_HEIGHT = 20;
	private HashMap<JTextField, Integer> hmJTF = new HashMap<>();
	private JTextField jtf_map_size;
	private JTextField jtf_NODE_SIZE;
	private JTextField jtf_max_repeat;
	private JTextField jtf_draw_path_interval;
	private static final int JTF_MAP_SIZE_P = 1;
	private static final int JTF_NODE_SIZE_P = 2;
	private static final int JTF_MAX_STEP_P = 3;
	private static final int JTF_DRAW_PATH_INTERVAL_P = 4;
	private AStarFindPath findObj;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");// windows风格
					AStarApplet applet = new AStarApplet();
					JFrame f = new JFrame();
					f.setSize(800, 600);
					// f.setResizable(false);
					f.setLayout(new BorderLayout());
					// f.add(new Button("Save"));
					f.add(applet, "Center");
					f.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							System.exit(0);
						}
					});
					applet.init();

					f.setVisible(true);
					applet.start();
					applet.splitPane.setDividerLocation(0.3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AStarApplet() {
		// this.setTitle("自动寻路模拟器");
		setBounds(100, 100, 800, 600);
		// 菜单创建
		createJMenu();

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		splitPane = new JSplitPane();
		splitPane.setOpaque(false);
		splitPane.setDividerSize(3);

		contentPane.add(splitPane, BorderLayout.CENTER);

		// 配置面板创建
		createConfigureJPanel();
		// 展示面板创建
		createShowJPanel();
	}

	/**
	 * 创建菜单
	 */
	private void createJMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu_txt = new JMenu("文件");
		menuBar.add(menu_txt);

		JMenuItem menuItem_open = new JMenuItem("打开");
		menu_txt.add(menuItem_open);

		JMenu menu_svae = new JMenu("保存");
		menu_txt.add(menu_svae);

		JMenuItem menuItem_savemap = new JMenuItem("保存地图");
		menu_svae.add(menuItem_savemap);

		JMenuItem menuItem_saveall = new JMenuItem("保存全部(含地图)");
		menu_svae.add(menuItem_saveall);

		JMenuItem menuItem_re_save = new JMenuItem("另存为");
		menu_txt.add(menuItem_re_save);
	}

	/**
	 * 左侧配置面板
	 */
	private void createConfigureJPanel() {
		JPanel panel_configure = new JPanel();
		splitPane.setLeftComponent(panel_configure);
		panel_configure.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_configure = new JScrollPane();
		panel_configure.add(scrollPane_configure, BorderLayout.CENTER);

		JPanel panel_configure_configure = new JPanel();
		scrollPane_configure.setViewportView(panel_configure_configure);
		panel_configure_configure.setLayout(null);

		int HEIGHT = 0;
		/**
		 * 操作相关
		 */
		JPanel cell_panel_operate = new JPanel();
		cell_panel_operate.setBorder(BorderFactory.createTitledBorder("操作"));
		cell_panel_operate.setBounds(0, HEIGHT, 215, 80 + AStarApplet.HEAD_HEIGHT);
		panel_configure_configure.add(cell_panel_operate);
		cell_panel_operate.setLayout(new GridLayout(0, 1, 0, 0));

		JButton btn_find = new JButton("寻路");
		btn_find.addActionListener(this);
		cell_panel_operate.add(btn_find);

		JButton btn_clear_path = new JButton("清除路径");
		btn_clear_path.addActionListener(this);
		cell_panel_operate.add(btn_clear_path);

		JButton btn_clear_all = new JButton("清除所有");
		btn_clear_all.addActionListener(this);
		cell_panel_operate.add(btn_clear_all);

		HEIGHT += 80 + AStarApplet.HEAD_HEIGHT + 5;

		/**
		 * 画笔设置
		 */
		JPanel cell_panel_drawType = new JPanel();
		cell_panel_drawType.setLayout(null);
		cell_panel_drawType.setBorder(BorderFactory.createTitledBorder("画笔类型"));
		cell_panel_drawType.setBounds(0, HEIGHT, 215, 125 + AStarApplet.HEAD_HEIGHT);

		ButtonGroup bg_drow_type = new ButtonGroup();

		JRadioButton jrb_start = new JRadioButton("起点");
		jrb_start.setBounds(5, 25, 100, 25);
		cell_panel_drawType.add(jrb_start);
		bg_drow_type.add(jrb_start);
		jrb_start.addActionListener(this);

		JRadioButton jrb_end = new JRadioButton("终点");
		jrb_end.setBounds(110, 25, 100, 25);
		cell_panel_drawType.add(jrb_end);
		bg_drow_type.add(jrb_end);
		jrb_end.addActionListener(this);

		JRadioButton jrb_obs_0 = new JRadioButton("一级障碍物(EASY)");
		jrb_obs_0.setBounds(5, 55, 100, 25);
		cell_panel_drawType.add(jrb_obs_0);
		bg_drow_type.add(jrb_obs_0);
		jrb_obs_0.addActionListener(this);

		JRadioButton jrb_obs_1 = new JRadioButton("二级障碍物(TOUGH)");
		jrb_obs_1.setBounds(110, 55, 100, 25);
		cell_panel_drawType.add(jrb_obs_1);
		bg_drow_type.add(jrb_obs_1);
		jrb_obs_1.addActionListener(this);

		JRadioButton jrb_obs_2 = new JRadioButton("三级障碍物(V_TOUGH)");
		jrb_obs_2.setBounds(5, 85, 100, 25);
		cell_panel_drawType.add(jrb_obs_2);
		bg_drow_type.add(jrb_obs_2);
		jrb_obs_2.addActionListener(this);

		JRadioButton jrb_obs_3 = new JRadioButton("四级障碍物(NO THROW)");
		jrb_obs_3.setBounds(110, 85, 100, 25);
		cell_panel_drawType.add(jrb_obs_3);
		bg_drow_type.add(jrb_obs_3);
		jrb_obs_3.addActionListener(this);

		JRadioButton jrb_ground = new JRadioButton("橡皮擦");
		jrb_ground.setBounds(5, 115, 100, 25);
		cell_panel_drawType.add(jrb_ground);
		bg_drow_type.add(jrb_ground);
		jrb_ground.addActionListener(this);

		switch (AStar.NOW_DRAW_TYPE) {
		case AStar.NODE_START: {
			jrb_start.setSelected(true);
		}
			break;
		case AStar.NODE_END: {
			jrb_end.setSelected(true);
		}
			break;
		case AStar.NODE_OBS_0: {
			jrb_obs_0.setSelected(true);
		}
			break;
		case AStar.NODE_OBS_1: {
			jrb_obs_1.setSelected(true);
		}
			break;
		case AStar.NODE_OBS_2: {
			jrb_obs_2.setSelected(true);
		}
			break;
		case AStar.NODE_OBS_3: {
			jrb_obs_3.setSelected(true);
		}
			break;
		case AStar.NODE_GROUND: {
			jrb_ground.setSelected(true);
		}
			break;
		default:
			break;
		}

		panel_configure_configure.add(cell_panel_drawType);
		HEIGHT += 125 + AStarApplet.HEAD_HEIGHT + 5;
		/**
		 * 曼哈顿算法
		 */
		JPanel cell_panel_mhd = new JPanel();
		cell_panel_mhd.setLayout(null);
		cell_panel_mhd.setBorder(BorderFactory.createTitledBorder("估值算法（TODO:目前只有old可用）"));
		cell_panel_mhd.setBounds(0, HEIGHT, 215, 65 + AStarApplet.HEAD_HEIGHT);

		ButtonGroup bg_mhd = new ButtonGroup();

		JRadioButton jrb_classic = new JRadioButton("Classic A*");
		jrb_classic.setBounds(5, 25, 100, 25);
		cell_panel_mhd.add(jrb_classic);
		bg_mhd.add(jrb_classic);
		jrb_classic.addActionListener(this);

		JRadioButton jrb_old = new JRadioButton("Old");
		jrb_old.setBounds(110, 25, 100, 25);
		cell_panel_mhd.add(jrb_old);
		bg_mhd.add(jrb_old);
		jrb_old.addActionListener(this);

		JRadioButton jrb_fudge = new JRadioButton("Fudge");
		jrb_fudge.setBounds(5, 55, 100, 25);
		cell_panel_mhd.add(jrb_fudge);
		bg_mhd.add(jrb_fudge);
		jrb_fudge.addActionListener(this);

		switch (AStar.INSPIRE_TYPE_OLD) {
		case AStar.INSPIRE_TYPE_CLASSIC: {
			jrb_classic.setSelected(true);
			findObj = new HuristicAStar();
		}
			break;
		case AStar.INSPIRE_TYPE_OLD: {
			jrb_old.setSelected(true);
			findObj = new OneTailAStar();
		}
			break;
		case AStar.INSPIRE_TYPE_FUDGE: {
			jrb_fudge.setSelected(true);
			findObj = new Fudge();
		}
			break;
		default:
			break;
		}

		panel_configure_configure.add(cell_panel_mhd);
		HEIGHT += 65 + AStarApplet.HEAD_HEIGHT + 5;

		/**
		 * 寻路路径类型
		 */
		JPanel cell_panel_wayType = new JPanel();
		cell_panel_wayType.setLayout(null);
		cell_panel_wayType.setBorder(BorderFactory.createTitledBorder("寻路路径类型"));
		cell_panel_wayType.setBounds(0, HEIGHT, 215, 35 + AStarApplet.HEAD_HEIGHT);

		JCheckBox jcb_stright = new JCheckBox("纵横方向");
		jcb_stright.setBounds(5, 25, 100, 25);
		cell_panel_wayType.add(jcb_stright);
		jcb_stright.setSelected(AStar.STRAIGHT);
		jcb_stright.addItemListener(this);

		JCheckBox jcb_skew = new JCheckBox("斜向");
		jcb_skew.setBounds(110, 25, 100, 25);
		cell_panel_wayType.add(jcb_skew);
		jcb_skew.setSelected(AStar.SKEW);
		jcb_skew.addItemListener(this);

		panel_configure_configure.add(cell_panel_wayType);
		HEIGHT += 35 + AStarApplet.HEAD_HEIGHT + 5;

		/**
		 * 数量相关设置
		 */
		JPanel cell_panel_size = new JPanel();
		cell_panel_size.setBorder(BorderFactory.createTitledBorder("数量及大小设置"));
		cell_panel_size.setBounds(0, HEIGHT, 215, 125 + AStarApplet.HEAD_HEIGHT);
		cell_panel_size.setLayout(null);
		panel_configure_configure.add(cell_panel_size);

		JLabel jl_map_size = new JLabel("地图大小");
		jl_map_size.setBounds(5, 25, 100, 25);
		cell_panel_size.add(jl_map_size);
		jl_map_size.setHorizontalAlignment(JLabel.RIGHT);

		jtf_map_size = new JTextField();
		jtf_map_size.setBounds(110, 25, 95, 25);
		cell_panel_size.add(jtf_map_size);
		jtf_map_size.setColumns(10);
		hmJTF.put(jtf_map_size, AStarApplet.JTF_MAP_SIZE_P);
		jtf_map_size.setText(AStar.MAP_SIZE + "");
		jtf_map_size.addFocusListener(this);

		JLabel jl_NODE_SIZE = new JLabel("方块大小");
		jl_NODE_SIZE.setBounds(5, 55, 100, 25);
		cell_panel_size.add(jl_NODE_SIZE);
		jl_NODE_SIZE.setHorizontalAlignment(JLabel.RIGHT);

		jtf_NODE_SIZE = new JTextField();
		jtf_NODE_SIZE.setBounds(110, 55, 95, 25);
		cell_panel_size.add(jtf_NODE_SIZE);
		jtf_NODE_SIZE.setColumns(10);
		hmJTF.put(jtf_NODE_SIZE, AStarApplet.JTF_NODE_SIZE_P);
		jtf_NODE_SIZE.setText(AStar.NODE_SIZE + "");
		jtf_NODE_SIZE.addFocusListener(this);

		JLabel jl_find_max = new JLabel("路径最大长度");
		jl_find_max.setBounds(5, 85, 100, 25);
		cell_panel_size.add(jl_find_max);
		jl_find_max.setHorizontalAlignment(JLabel.RIGHT);

		jtf_max_repeat = new JTextField();
		jtf_max_repeat.setBounds(110, 85, 95, 25);
		cell_panel_size.add(jtf_max_repeat);
		jtf_max_repeat.setColumns(10);
		hmJTF.put(jtf_max_repeat, AStarApplet.JTF_MAX_STEP_P);
		jtf_max_repeat.setText(AStar.STEP_MAX + "");
		jtf_max_repeat.addFocusListener(this);

		JLabel jl_draw_path_interval = new JLabel("绘制路径时间间隔");
		jl_draw_path_interval.setBounds(5, 115, 100, 25);
		cell_panel_size.add(jl_draw_path_interval);
		jl_draw_path_interval.setHorizontalAlignment(JLabel.RIGHT);

		jtf_draw_path_interval = new JTextField();
		jtf_draw_path_interval.setBounds(110, 115, 95, 25);
		cell_panel_size.add(jtf_draw_path_interval);
		jtf_draw_path_interval.setColumns(10);
		hmJTF.put(jtf_draw_path_interval, AStarApplet.JTF_DRAW_PATH_INTERVAL_P);
		jtf_draw_path_interval.setText(AStar.DRAW_PATH_INTERVAL + "");
		jtf_draw_path_interval.addFocusListener(this);

		HEIGHT += 125 + AStarApplet.HEAD_HEIGHT + 5;

		panel_configure_configure.setPreferredSize(new Dimension(230, HEIGHT));

	}

	/**
	 * 中间展示面板
	 */
	private void createShowJPanel() {
		JPanel panel_show = new JPanel();
		splitPane.setRightComponent(panel_show);
		panel_show.setLayout(new BorderLayout(0, 0));

		scrollPane_show = new JScrollPane();
		panel_show.add(scrollPane_show, BorderLayout.CENTER);

		map = new Map();
		scrollPane_show.setViewportView(map);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("起点".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_START;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_START;
		} else if ("终点".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_END;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_END;
		} else if ("一级障碍物(EASY)".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_OBS_0;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_OBS_0;
		} else if ("二级障碍物(TOUGH)".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_OBS_1;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_OBS_1;
		} else if ("三级障碍物(V_TOUGH)".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_OBS_2;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_OBS_2;
		} else if ("四级障碍物(NO THROW)".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_OBS_3;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_OBS_3;
		} else if ("橡皮擦".equals(e.getActionCommand())) {
			AStar.NOW_DRAW_TYPE = AStar.NODE_GROUND;
			AStar.NOW_DRAW_COLOR = AStar.COLOR_START;
		} else if ("Classic A*".equals(e.getActionCommand())) {
			AStar.NOW_INSPIRE_TYPE = AStar.INSPIRE_TYPE_CLASSIC;
			findObj = new HuristicAStar();
		} else if ("Old".equals(e.getActionCommand())) {
			AStar.NOW_INSPIRE_TYPE = AStar.INSPIRE_TYPE_OLD;
			findObj = new OneTailAStar();
		} else if ("Fudge".equals(e.getActionCommand())) {
			AStar.NOW_INSPIRE_TYPE = AStar.INSPIRE_TYPE_FUDGE;
			findObj = new Fudge();
		} else if ("寻路".equals(e.getActionCommand())) {
			if(null != findObj){
				try {
					Node.canDraw = false;
					findObj.findPath(map);
				} catch (AStarException e1) {
					Node.canDraw = true;
					e1.printStackTrace();
				}
			}else{
				try {
					throw new AStarException("findObj为空！");
				} catch (AStarException e1) {
					e1.printStackTrace();
				}
			}
		} else if ("清除路径".equals(e.getActionCommand())) {
			map.clearPath();
			scrollPane_show.repaint();
			scrollPane_show.revalidate();
		} else if ("清除所有".equals(e.getActionCommand())) {
			map.clear();
			scrollPane_show.repaint();
			scrollPane_show.revalidate();
		} else {
			System.out.println(e.getActionCommand());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object item = e.getItem();
		if (item instanceof JCheckBox) {
			JCheckBox jcbItem = (JCheckBox) item;
			if ("纵横方向".equals(jcbItem.getText())) {
				if (e.getStateChange() == 1) {
					AStar.STRAIGHT = true;
				} else {
					AStar.STRAIGHT = false;
				}
			} else if ("斜向".equals(jcbItem.getText())) {
				if (e.getStateChange() == 1) {
					AStar.SKEW = true;
				} else {
					AStar.SKEW = false;
				}
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// System.out.println("focusGained");
	}

	@Override
	public void focusLost(FocusEvent e) {
		Object object = e.getSource();
		if (object instanceof JTextField) {
			JTextField jTextField = (JTextField) object;
			String txt = jTextField.getText();
			int type = hmJTF.get(jTextField);
			switch (type) {
			case AStarApplet.JTF_MAP_SIZE_P: {
				try {
					int number = Integer.parseInt(txt);
					if (AStar.MAP_SIZE == number) {
						return;
					}
					if (number <= 1) {
						jTextField.setFocusable(true);
						jTextField.requestFocus();
						Alert.alertError("请输入合法数字！");
						return;
					}
					if (number % AStar.NODE_SIZE != 0) {
						jTextField.setFocusable(true);
						jTextField.requestFocus();
						Alert.alertError("地图大小必须为方块大小的整数倍！");
						return;
					}
					AStar.MAP_SIZE = number;

					map.clear();
					scrollPane_show.repaint();
					scrollPane_show.revalidate();
					map.setPreferredSize(new Dimension(AStar.MAP_SIZE, AStar.MAP_SIZE));

				} catch (Exception e2) {
					jTextField.setFocusable(true);
					jTextField.requestFocus();
					Alert.alertError("请输入合法数字！");
				}
			}
				break;
			case AStarApplet.JTF_NODE_SIZE_P: {
				try {
					int number = Integer.parseInt(txt);
					if (AStar.NODE_SIZE == number) {
						return;
					}
					if (number <= 1) {
						jTextField.setFocusable(true);
						jTextField.requestFocus();
						Alert.alertError("请输入合法数字！");
						return;
					}
					if (AStar.MAP_SIZE % number != 0) {
						jTextField.setFocusable(true);
						jTextField.requestFocus();
						Alert.alertError("地图大小必须为方块大小的整数倍！");
						return;
					}
					AStar.NODE_SIZE = number;

					map.clear();
					Date d1 = new Date();
					scrollPane_show.repaint();
					scrollPane_show.revalidate();
					Date d2 = new Date();
					System.out.println(UnitConversionUtil.formatMSToASUnit(d2.getTime() - d1.getTime()));
				} catch (Exception e2) {
					jTextField.setFocusable(true);
					jTextField.requestFocus();
					Alert.alertError("请输入合法数字！");
				}
			}

				break;
			case AStarApplet.JTF_MAX_STEP_P: {
				try {
					int number = Integer.parseInt(txt);
					if (AStar.NODE_SIZE == number) {
						return;
					}
					if (number < 1) {
						jTextField.setFocusable(true);
						jTextField.requestFocus();
						Alert.alertError("请输入合法数字！");
						return;
					}
					AStar.STEP_MAX = number;
				} catch (Exception e2) {
					jTextField.setFocusable(true);
					jTextField.requestFocus();
					Alert.alertError("请输入合法数字！");
				}

			}
				break;
			case AStarApplet.JTF_DRAW_PATH_INTERVAL_P: {
				try {
					int number = Integer.parseInt(txt);
					if (AStar.DRAW_PATH_INTERVAL == number) {
						return;
					}
					if (number <= 1) {
						Alert.alertError("请输入合法数字！");
						jTextField.setFocusable(true);
						jTextField.requestFocus();
						return;
					}
					AStar.DRAW_PATH_INTERVAL = number;
				} catch (Exception e2) {
					Alert.alertError("请输入合法数字！");
				}

			}
				break;
			default:
				break;
			}
		}
	}

}
