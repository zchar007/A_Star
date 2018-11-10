package com.sept.io.web.ftp;

import java.io.File;

import org.apache.commons.net.ftp.FTPClient;

import com.sept.datastructure.pool.MessagePool;
import com.sept.datastructure.set.DataStore;
import com.sept.exception.AppException;

/**
 * 类描述-上传线程
 * 
 * @author 张超
 * @version 1.0 创建时间 2017-6-7
 */
public class UploadThread implements Runnable {
	private DataStore vdsFiles;
	private MessagePool mp;
	private String url;
	private int port;
	private String username;
	private String password;

	/**
	 * 构造方法简介.
	 * <p>
	 * 构造方法详述
	 * </p>
	 * 
	 * @author 张超
	 * @date 创建时间 2017-6-7
	 * @since V1.0
	 * @param vdsFiles
	 *            : file,tourl
	 * @param mp
	 * @param client
	 */
	public UploadThread(DataStore vdsFiles, MessagePool mp, String url, int port, String username, String password) {
		super();
		this.vdsFiles = vdsFiles;
		this.mp = mp;
		this.url = url;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < vdsFiles.rowCount(); i++) {

				File file = (File) vdsFiles.getObject(i, "file");
				String toUrl = vdsFiles.getString(i, "tourl");
				FTPClient client = FTPUtil.getFtpConnection(url, port, username, password);
				// 创建文件夹
				FTPUtil.mkdirs(client, toUrl);
				client = FTPUtil.getFtpConnection(url, port, username, password);
				// 执行上传
				FTPUtil.FTPUploadFile(file, toUrl, client, mp);
			}
		} catch (AppException e) {
			e.printStackTrace();
		}

	}

}