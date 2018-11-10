package com.sept.io.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;

import com.sept.exception.AppException;
import com.sept.io.exception.IOException;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JProgressBar;

public class CopyMain extends JFrame implements ItemListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JPanel panel_1;
	private JScrollPane scrollPane;
	private JPanel panel_2;
	private JLabel label_2;
	private HashSet<String> hsSelect = new HashSet<>();
	private boolean isCopyPath = true;
	private JPanel panel_3;
	private JLabel label_3;
	private JCheckBox checkBox;
	private JButton button;
	private JButton button_1;
	private JPanel panel_4;
	private JPanel panel_5;
	private MyProgressBar progressBar;
	private ArrayList<JCheckBox> alJcb = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CopyMain frame = new CopyMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CopyMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 522, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel label = new JLabel("\u539F\u76EE\u5F55");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panel.add(label, gbc_label);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel.add(textField, gbc_textField);
		textField.setColumns(10);
		textField.setEditable(false);
		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON1) {
					textField.setText(Alert.alertSelectFile("D://WorkSpace//Book//DIYBQ",
							Alert.SelectFileType_FILE_AND_DIRECTORIES));
				}
			}
		});

		JLabel label_1 = new JLabel("\u76EE\u6807\u76EE\u5F55");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 0, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 1;
		panel.add(label_1, gbc_label_1);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		panel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		textField_1.setEditable(false);
		textField_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON1) {
					textField_1.setText(Alert.alertSelectFile("D://新建文件夹", Alert.SelectFileType_DIRECTORIES_ONLY));
				}
			}
		});
		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);// ****
		panel_1.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		panel_2 = new JPanel();
		scrollPane.setViewportView(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		label_2 = new JLabel("\u62F7\u8D1D\u6587\u4EF6\u7C7B\u578B");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 0;
		panel_2.add(label_2, gbc_label_2);

		panel_3 = new JPanel();
		// contentPane.add(panel_3, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		label_3 = new JLabel("\u662F\u5426\u62F7\u8D1D\u539F\u6587\u4EF6\u5939");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 0;
		gbc_label_3.gridy = 0;
		panel_3.add(label_3, gbc_label_3);

		checkBox = new JCheckBox("");
		checkBox.setSelected(true);
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.insets = new Insets(0, 0, 5, 5);
		gbc_checkBox.gridx = 1;
		gbc_checkBox.gridy = 0;
		checkBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Object item = e.getItem();
				if (item instanceof JCheckBox) {
					JCheckBox jcbItem = (JCheckBox) item;
					if (e.getStateChange() == 1) {
						isCopyPath = true;
					} else {
						isCopyPath = false;
					}
				}
			}
		});
		panel_3.add(checkBox, gbc_checkBox);

		button = new JButton("\u91CD\u7F6E");
		button.addActionListener(this);
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 10;
		gbc_button.gridy = 0;
		panel_3.add(button, gbc_button);

		panel_4 = new JPanel();
		contentPane.add(panel_4, BorderLayout.SOUTH);
		panel_4.setLayout(new BorderLayout(0, 0));

		panel_4.add(panel_3, BorderLayout.CENTER);

		button_1 = new JButton("\u62F7\u8D1D");
		button_1.addActionListener(this);

		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 0);
		gbc_button_1.gridx = 12;
		gbc_button_1.gridy = 0;
		panel_3.add(button_1, gbc_button_1);

		progressBar = new MyProgressBar();
		panel_4.add(progressBar, BorderLayout.SOUTH);

		String file_types[] = { "java", "jar", "html", "jsp", "xml", "exe", "docx", "doc", "txt", "pdf","js","css" };

		int x = 1, y = 0;
		for (int i = 0; i < file_types.length; i++) {
			JCheckBox chckbx = new JCheckBox(file_types[i]);
			chckbx.setSelected(true);
			alJcb.add(chckbx);
			hsSelect.add(file_types[i]);
			chckbx.addItemListener(this);
			GridBagConstraints gbc_chckbx = new GridBagConstraints();
			gbc_chckbx.insets = new Insets(0, 0, 5, 5);
			gbc_chckbx.gridx = x;
			gbc_chckbx.gridy = y;
			panel_2.add(chckbx, gbc_chckbx);

			if (x % 5 == 0) {
				x = 1;
				y++;
			} else {
				x++;
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object item = e.getItem();
		if (item instanceof JCheckBox) {
			JCheckBox jcbItem = (JCheckBox) item;
			if (e.getStateChange() == 1) {
				if (!hsSelect.contains(jcbItem.getText())) {
					hsSelect.add(jcbItem.getText());
				}
			} else {
				if (hsSelect.contains(jcbItem.getText())) {
					hsSelect.remove(jcbItem.getText());
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("重置".equals(e.getActionCommand())) {
				progressBar.reset();
				textField.setText("");
				textField_1.setText("");
		} else if ("拷贝".equals(e.getActionCommand())) {
			String types = "";
			for (String string : hsSelect) {
				types += string + ",";
			}
			if (types.length() > 1) {
				types = types.substring(0, types.length() - 1);
			}

			button.setEnabled(false);
			button_1.setEnabled(false);
			textField.setEnabled(false);
			textField_1.setEnabled(false);
			for (int i = 0; i < alJcb.size(); i++) {
				alJcb.get(i).setEnabled(false);
			}
			FileCopy fc = new FileCopy(textField.getText(), textField_1.getText(), types, isCopyPath, progressBar);
			try {
				//fc.startCopy();
				fc.startCopyByFile();
			} catch (AppException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//System.out.println(progressBar.isFinsh());

						if (progressBar.isFinsh()) {
							button.setEnabled(true);
							button_1.setEnabled(true);
							textField.setEnabled(true);
							textField_1.setEnabled(true);
							for (int i = 0; i < alJcb.size(); i++) {
								alJcb.get(i).setEnabled(true);
							}
							break;
						}
					}
				}
			}).start();
		}
	}

}
