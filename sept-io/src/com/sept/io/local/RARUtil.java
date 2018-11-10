package com.sept.io.local;

import java.io.File;

import com.sept.exception.AppException;
import com.sept.util.CmdUtil;

/**
 * 不适用于web使用
 * 
 * @author zchar
 * 
 */
public final class RARUtil {
	private static String rarExeFile = "./exe/Rar.exe";
	private static String unrarExeFile = "./exe/UnRaR.exe";

	public final static boolean unRarFile(String urlFrom, String urlTo, String passWord) throws AppException {
		//
		if (urlFrom == null || !(urlFrom.endsWith("rar") || urlFrom.endsWith("RAR"))) {
			throw new AppException("要解压的文件不存在或不是RAR文件！！");
		}
		File fileFrom = new File(urlFrom);
		File fileTo = urlTo == null ? fileFrom.getParentFile() : new File(urlTo);
		File fRar = new File(RARUtil.unrarExeFile);
		String path = fileTo.getAbsolutePath();
		File filePath = new File(path);
		int index = 1;
		while (filePath.exists()) {
			filePath = new File(path + "(" + index + ")");
			index++;
		}
		filePath.mkdirs();
		String pwStr = "";
		if (null != passWord && !"".equals(passWord)) {
			pwStr = " -p" + passWord + "  ";
		}
		String cmd = fRar.getAbsolutePath() + " X  " + pwStr + fileFrom.getAbsolutePath() + " "
				+ filePath.getAbsolutePath() + "\\";
		try {
			CmdUtil.run(cmd);
		} catch (Exception e) {
			String error = "未找到Rar.exe";
			throw new AppException(e.getMessage() + error);
		}

		return true;

	}

	/**
	 * 压缩文件
	 * 
	 * @param urlFrom
	 *            :文件来源 可以使文件夹，也可以是文件
	 * @param urlTo
	 *            : 不可以是文件 应该是文件夹
	 * @return
	 * @throws Exception
	 */
	public final static boolean RarFile(String urlFrom, String urlTo, String passWord) throws AppException {
		if (urlFrom == null) {
			throw new AppException("入参错误！！");
		}
		File fileFrom = new File(urlFrom);
		urlTo = urlTo == null ? fileFrom.getParentFile().getAbsolutePath() : urlTo;
		File fileTo = new File(urlTo);
		// 文件来源是否存在
		if (!fileFrom.exists()) {
			throw new AppException("要压缩的文件不存在！！");
		}
		// 如果文件去向不是RAR结尾的，那么，在其文件夹下新增以源文件命名并且以rar结尾的文件

		fileTo = new File(fileTo.getAbsolutePath() + "//" + fileFrom.getName() + ".rar");
		// 获取其父文件夹
		File fTemp = fileTo.getParentFile();
		// 如果不存在，则创建
		if (!fTemp.exists()) {
			fTemp.mkdirs();
		}
		// 如果rar文件不存在，则创建(不能创建，如果创建了会提示文件错误)
		File fRar = new File(RARUtil.rarExeFile);
		//
		String pwStr = "";
		if (null != passWord && !"".equals(passWord)) {
			pwStr = " -p" + passWord + "  ";
		}
		String cmd = fRar.getAbsolutePath() + " a  " + pwStr + fileTo.getAbsolutePath() + " \""
				+ fileFrom.getAbsolutePath() + "\"";
		try {
			CmdUtil.run(cmd);
		} catch (Exception e) {
			String error = "未找到Rar.exe文件";
			throw new AppException(e.getMessage() + error);
		}

		return true;

	}

	public static String getRarExeFile() {
		return rarExeFile;
	}

	public static void setRarExeFile(String rarExeFile) {
		RARUtil.rarExeFile = rarExeFile;
	}

	public static String getUnrarExeFile() {
		return unrarExeFile;
	}

	public static void setUnrarExeFile(String unrarExeFile) {
		RARUtil.unrarExeFile = unrarExeFile;
	}

	// WinRAR a Info.rar "c:\latest data"
	public final static void main(String[] args) throws Exception {
		//RarFile("D://AAA.jpg", "D://", "12345");
		unRarFile("D://AAA.jpg.rar", "D://test//", "12345");
	}
}
