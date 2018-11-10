package com.sept.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JTextArea;

import com.sept.exception.AppException;


public final class CmdUtil {
	/**
	 * 执行一个cmd命令，并返回所有输出
	 * 
	 * @author 张超
	 * @date 创建时间 2017-5-27
	 * @since V1.0
	 */
	public final static ArrayList<String> run(String cmd) throws Exception {
		if (null == cmd || "".equals(cmd)) {
			throw new AppException("命令错误！！");
		}
		BufferedReader brMessage = null, brError = null;
		ArrayList<String> al = new ArrayList<String>();
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(cmd);

			// 输出信息
			InputStream stdMessage = proc.getInputStream();
			// 错误信息
			InputStream stdError = proc.getErrorStream();

			brMessage = new BufferedReader(new InputStreamReader(stdMessage, "GBK"));
			brError = new BufferedReader(new InputStreamReader(stdError, "GBK"));

			String lineMessage = null, lineError = null;
			while ((lineMessage = brMessage.readLine()) != null || (lineError = brError.readLine()) != null) {
				if (lineMessage != null) {
					// lineMessage = new String(lineMessage.getBytes("gbk"),"ISO-8859-1");
					al.add(lineMessage);
				}
				if (lineError != null) {
					al.add("<error>" + lineError + "</error>");
				}
			}
			int exitVal = proc.waitFor();
			al.add("Process exitValue: " + exitVal);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		} finally {
			if (null != brMessage) {
				try {
					brMessage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != brError) {
				try {
					brError.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return al;
	}

	public final static void run(String cmd, JTextArea jta) throws Exception {
		if (null == cmd || "".equals(cmd)) {
			throw new AppException("命令错误！！");
		}
		BufferedReader brMessage = null, brError = null;

		try {

			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(cmd);

			// 输出信息
			InputStream stdMessage = proc.getInputStream();
			// 错误信息
			InputStream stdError = proc.getErrorStream();

			brMessage = new BufferedReader(new InputStreamReader(stdMessage));
			brError = new BufferedReader(new InputStreamReader(stdError));

			String lineMessage = null, lineError = null;
			while ((lineMessage = brMessage.readLine()) != null || (lineError = brError.readLine()) != null) {
				if (lineMessage != null) {
					jta.append(lineMessage + "\n\r");
					jta.revalidate();
					jta.repaint();
				}
				if (lineError != null) {
					jta.append("<error>" + lineError + "</error>\n\r");
					jta.revalidate();
					jta.repaint();
				}
			}
			int exitVal = proc.waitFor();
			jta.append("Process exitValue: " + exitVal + "\n\r");
			jta.revalidate();
			jta.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			jta.append("<error>" + e.getMessage() + "</error>\n\r");
			jta.revalidate();
			jta.repaint();
			throw new AppException(e);
		} finally {
			if (null != brMessage) {
				try {
					brMessage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != brError) {
				try {
					brError.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public final static void run(String[] cmd) throws Exception {
		if (null == cmd || cmd.length == 0) {
			throw new Exception("命令错误！！");
		}
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(cmd);
	}

	public final static void main(String[] args) throws Exception {
		// String[] cmd = { "cd\\", "cd F:\\" };
		System.out.println(run("netsh wlan show drivers"));
	}

}
