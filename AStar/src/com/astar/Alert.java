package com.astar;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public final class Alert {
	public final static void main(String[] args) {
		
		System.out.println(Integer.parseInt("asjfdisa"));

		
		
		
//		Object[] oo = { "111", "222", "333" };
//		System.out.println(alertSelect("你好", "请输入", oo));
	}

	public final static int SelectFileType_FILE_ONLY = 1;
	public final static int SelectFileType_DIRECTORIES_ONLY = 2;
	public final static int SelectFileType_FILE_AND_DIRECTORIES = 3;

	public final static boolean comfrim(String message) {
		int option = JOptionPane.showConfirmDialog(null, message, "",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
		switch (option) {
		case JOptionPane.YES_NO_OPTION: {
			return true;
		}
		case JOptionPane.NO_OPTION:
			return false;

		}
		return false;

	}

	/**
	 * @author 张超
	 * @date 创建时间 2017-6-10
	 * @since V1.0
	 */
	public final static void alertInformation(String message) {
		JOptionPane.showMessageDialog(null, message, "提示",
				JOptionPane.INFORMATION_MESSAGE);

	}

	public final static void alertWarning(String message) {
		JOptionPane.showMessageDialog(null, message, "警告",
				JOptionPane.WARNING_MESSAGE);

	}

	public final static void alertError(String message) {
		JOptionPane.showMessageDialog(null, message, "错误",
				JOptionPane.ERROR_MESSAGE);

	}

	public final static void alertQuestion(String message) {
		JOptionPane.showMessageDialog(null, message, "系统信息",
				JOptionPane.QUESTION_MESSAGE);

	}

	public final static String alertSelect(String title, String message,
			Object[] select) {
		return (String) JOptionPane.showInputDialog(null, message + ":\n",
				title, JOptionPane.PLAIN_MESSAGE, null, select, select[0]);
	}

	public final static String alertInput(String title, String message) {
		return (String) JOptionPane.showInputDialog(null, message + "：\n",
				title, JOptionPane.PLAIN_MESSAGE, null, null, "请输入");
	}

	/**
	 * 
	 * @author 张超
	 * @date 创建时间 2017-6-10
	 * @since V1.0
	 */
	public final static String alertSelectFile(String defulPath,
			int selectFileType) {
		JFileChooser jf = new JFileChooser();
		jf.setFileSelectionMode(selectFileType == SelectFileType_DIRECTORIES_ONLY ? (JFileChooser.SAVE_DIALOG | JFileChooser.DIRECTORIES_ONLY)
				: (selectFileType == SelectFileType_FILE_ONLY ? JFileChooser.FILES_ONLY
						: JFileChooser.FILES_AND_DIRECTORIES));
		jf.setCurrentDirectory(new File(defulPath));
		jf.showDialog(null, null);
		File f = jf.getSelectedFile();
		if (f != null) {
			// TFTo.setText(f.getAbsolutePath());
			return f.getAbsolutePath();
		}

		return null;

	}

}
