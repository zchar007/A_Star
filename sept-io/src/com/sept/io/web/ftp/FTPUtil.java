package com.sept.io.web.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.sept.datastructure.pool.MessagePool;
import com.sept.datastructure.set.DataObject;
import com.sept.datastructure.set.DataStore;
import com.sept.exception.AppException;
import com.sept.io.local.FileUtil;
import com.sept.io.web.ftp.tree.FTPFileNode;
import com.sept.io.web.ftp.tree.FTPFileTree;
import com.sept.util.RandomManager;


/**
 * 仅支持单个文件的上传下载
 * 
 * @author 张超
 * @version 1.0 创建时间 2017-6-6
 */
public class FTPUtil {
	private static Object clientKey = new Object();
	private static String UPLOAD_FILE_SIZE = "U_F_S";// 当前上传文件大小的key
	private static String UPLOAD_NOW_SIZE = "U_N_S";// 当前已上传大小的key
	private static String UPLOAD_ALL_SIZE = "U_A_S";// 所有上传大小的key
	private static String UPLOAD_FILE_NAME = "U_F_N";// 当前正在上传的文件名的key
	private static String UPLOAD_DIRECTORY_NAME = "U_D_N";// 当前上传的总文件/目录的名字

	private static String DOWNLOAD_FILE_SIZE = "D_F_S";// 当前下载文件大小的key
	private static String DOWNLOAD_NOW_SIZE = "D_N_S";// 当前已下载大小的key
	private static String DOWNLOAD_ALL_SIZE = "D_A_S";// 所有下载大小的key
	private static String DOWNLOAD_FILE_NAME = "D_F_N";// 当前正在下载的文件名的key
	private static String DOWNLOAD_DIRECTORY_NAME = "D_D_N";// 当前下载的总文件/目录的名字

	/**
	 * ftp 上传文件/文件夹
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017-6-6
	 * @since V1.0
	 */
	public static boolean FTPUploadFile(String fromUrl, String toUrl, FTPClient client) throws AppException {

		return FTPUploadFile(new File(fromUrl), toUrl, client);
	}

	/**
	 * ftp 上传文件
	 * 
	 * @author 张超
	 * @date 创建时间 2017-6-6
	 * @param fromFile
	 * @param toUrl
	 *            -- 严格按照--如果ft主目录为 xxx 的,要上传到aaa文件夹下 ---/xxx/aa/
	 * @param client
	 * @since V1.0
	 */
	public static boolean FTPUploadFile(File fromFile, String toUrl, FTPClient client) throws AppException {
		return FTPUploadFile(fromFile, toUrl, client, null);
	}

	/**
	 * ftp 上传文件
	 * 
	 * @author 张超
	 * @date 创建时间 2017-6-6
	 * @param fromFile
	 * @param toUrl
	 *            -- 严格按照--如果ft主目录为 xxx 的,要上传到aaa文件夹下 ---/xxx/aa/
	 * @param client
	 * @since V1.0
	 */
	public static boolean FTPUploadFile(File fromFile, String toUrl, FTPClient client, MessagePool mp)
			throws AppException {
		if (!fromFile.isFile()) {
			throw new AppException("单个上传仅支持单个文件的上传！");
		}
		InputStream in = null;
		OutputStream os = null;
		try {
			if (null != mp) {
				MessagePool.asynchPutMessage(UPLOAD_FILE_SIZE, fromFile.length(), mp);
				MessagePool.asynchPutMessage(UPLOAD_FILE_NAME, fromFile.getAbsolutePath(), mp);
			}
			String fileName = new String((toUrl + File.separator + fromFile.getName()).getBytes("GBK"), "iso-8859-1");
			// System.out.println(client.isConnected());
			client.changeWorkingDirectory(new String(toUrl.getBytes("GBK"), "iso-8859-1"));
			client.enterLocalPassiveMode();
			client.setFileType(FTPClient.BINARY_FILE_TYPE);
			in = new FileInputStream(fromFile);
			os = client.storeFileStream(fileName);
			byte[] datas = new byte[1024];
			int tempbyte;
			while ((tempbyte = in.read(datas)) != -1) {
				if (null != mp) {
					MessagePool.asynchPutAndAddMessage(UPLOAD_NOW_SIZE, Long.parseLong(tempbyte + ""), mp);
				}
				os.write(datas, 0, tempbyte);
				os.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		} finally {
			try {
				os.flush();
				os.close();
				in.close();
				client.logout();
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * ftp 下载文件
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017-6-6
	 * @since V1.0
	 */
	public static boolean FTPDownloadFile(String fromUrl, String toUrl, FTPClient client) throws AppException {
		return FTPDownloadFile(fromUrl, toUrl, client, null);
	}

	/**
	 * ftp 下载文件
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017-6-6
	 * @since V1.0
	 */
	public static boolean FTPDownloadFile(String fromUrl, String toUrl, FTPClient client, MessagePool mp)
			throws AppException {
		InputStream in = null;
		FileOutputStream os = null;
		try {

			if (client.changeWorkingDirectory(fromUrl)) {
				throw new AppException("只能下载文件！");
			}
			FTPFile[] filesTemp = client.listFiles(new String(fromUrl.getBytes("GBK"), "iso-8859-1"));
			if (null != mp) {
				MessagePool.asynchPutMessage(DOWNLOAD_FILE_SIZE, filesTemp[0].getSize(), mp);
				MessagePool.asynchPutMessage(DOWNLOAD_FILE_NAME, filesTemp[0].getName(), mp);
			}
			File file = new File(toUrl + File.separator + fromUrl.substring(fromUrl.lastIndexOf(File.separator)));
			client.enterLocalPassiveMode();
			in = client.retrieveFileStream(new String(fromUrl.getBytes("GBK"), "iso-8859-1"));
			os = new FileOutputStream(file);
			byte[] datas = new byte[1024];
			int tempbyte;
			while ((tempbyte = in.read(datas)) != -1) {
				if (null != mp) {
					MessagePool.asynchPutAndAddMessage(DOWNLOAD_NOW_SIZE, Long.parseLong(tempbyte + ""), mp);
				}
				os.write(datas, 0, tempbyte);
				os.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);

		} finally {
			try {
				os.flush();
				os.close();
				in.close();
				client.logout();
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

	/**
	 * 不挑file类型，多线程上传
	 * 
	 * @author 张超
	 * @return
	 * @throws AppException
	 * @date 创建时间 2017-6-7
	 * @since V1.0
	 */
	public static ExecutorService FTPUpload(File fromFile, String toUrl, String host, int port, String username,
			String password) throws AppException {
		return FTPUpload(fromFile, toUrl, null, host, port, username, password);
	}

	/**
	 * 不挑file类型，多线程上传
	 * 
	 * @author 张超
	 * @return
	 * @throws AppException
	 * @date 创建时间 2017-6-7
	 * @since V1.0
	 */
	public static ExecutorService FTPUpload(File fromFile, String toUrl, MessagePool mp, String host, int port,
			String username, String password) throws AppException {
		try {

			String needRemovePath = fromFile.getAbsolutePath().substring(0,
					fromFile.getAbsolutePath().lastIndexOf(File.separator));
			HashMap<String, Object> pdo = FileUtil.getFilesFormPath(fromFile.getAbsolutePath(), "");
			long length = (long) pdo.get("length");
			@SuppressWarnings("unchecked")
			ArrayList<File> alFiles = (ArrayList<File>) pdo.get("files");
			if (null != mp) {
				MessagePool.asynchPutMessage(UPLOAD_ALL_SIZE, length, mp);
				MessagePool.asynchPutMessage(UPLOAD_DIRECTORY_NAME, fromFile.getAbsolutePath(), mp);
			}
			DataStore vdsFiles = new DataStore();
			for (int i = 0; i < alFiles.size(); i++) {
				File file = alFiles.get(i);
				// 整理出ftp上对应位置
				String toUrlCell = toUrl + File.separator + file.getAbsolutePath().substring(needRemovePath.length());
				toUrlCell = toUrlCell.substring(0, toUrlCell.lastIndexOf(File.separator));
				// System.out.println(toUrlCell);
				vdsFiles.addRow();
				vdsFiles.put(vdsFiles.rowCount() - 1, "file", file);
				vdsFiles.put(vdsFiles.rowCount() - 1, "tourl", toUrlCell);
			}

			// 为什么
			int[] everys = new int[10];
			// 小于等于10，开10个线程，每个线程处理一个
			int every = vdsFiles.rowCount() / 10;
			int yushu = vdsFiles.rowCount() % 10;

			for (int i = 0; i < everys.length; i++) {
				everys[i] = every;
				if (i < yushu) {
					everys[i] = everys[i] + 1;
				}
			}
			int index = 0;
			ExecutorService uploadThreadPool = Executors.newFixedThreadPool(1000);
			for (int i = 0; i < everys.length; i++) {
				if (everys[i] > 0) {
					DataStore ds = (DataStore) vdsFiles.subDataStore(index, index + everys[i]);
					index += everys[i];
					UploadThread upload = new UploadThread(ds, mp, host, port, username, password);
					uploadThreadPool.execute(upload);
				}

			}
			return uploadThreadPool;
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	/**
	 * 不挑file类型，多线程下载
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017-6-7
	 * @since V1.0
	 */
	public static ExecutorService FTPDownload(String fromUrl, String toUrl, String host, int port, String username,
			String password) throws AppException {
		return FTPDownload(fromUrl, toUrl, null, host, port, username, password);
	}

	/**
	 * 不挑file类型，多线程下载
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017-6-7
	 * @since V1.0
	 */
	public static ExecutorService FTPDownload(String fromUrl, String toUrl, MessagePool mp, String host, int port,
			String username, String password) throws AppException {
		FTPClient client = null;
		try {
			client = getFtpConnection(host, port, username, password);
			DataObject pdo = getFTPFilesFromPath(fromUrl, client);
			try {
				client.logout();
				client.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			long length = pdo.getLong("length");
			@SuppressWarnings("unchecked")
			ArrayList<String> alFiles = (ArrayList<String>) pdo.get("files");
			if (null != mp) {
				MessagePool.asynchPutMessage(DOWNLOAD_ALL_SIZE, length, mp);
				MessagePool.asynchPutMessage(DOWNLOAD_DIRECTORY_NAME, fromUrl, mp);
			}

			while (fromUrl.endsWith(File.separator)) {
				fromUrl = fromUrl.substring(0, fromUrl.length() - 1);
			}
			String needRemovePath = fromUrl.substring(0, fromUrl.lastIndexOf(File.separator));
			DataStore vdsFiles = new DataStore();
			for (int i = 0; i < alFiles.size(); i++) {

				String furl = alFiles.get(i);
				// System.out.println(furl);
				String toUrlCell = toUrl + File.separator + furl.substring(needRemovePath.length());
				toUrlCell = toUrlCell.substring(0, toUrlCell.lastIndexOf(File.separator));
				while (furl.indexOf(File.separator + File.separator) >= 0) {
					furl = furl.replace(File.separator + File.separator, File.separator);
				}
				while (toUrlCell.indexOf(File.separator + File.separator) >= 0) {
					toUrlCell = toUrlCell.replace(File.separator + File.separator, File.separator);
				}

				File f = new File(toUrlCell);
				if (!f.exists()) {
					f.mkdirs();
				}
				vdsFiles.addRow();
				vdsFiles.put(vdsFiles.rowCount() - 1, "fromurl", furl);
				vdsFiles.put(vdsFiles.rowCount() - 1, "tourl", toUrlCell);
			}

			// 为什么
			int[] everys = new int[10];
			// 小于等于10，开10个线程，每个线程处理一个
			int every = vdsFiles.rowCount() / 10;
			int yushu = vdsFiles.rowCount() % 10;

			for (int i = 0; i < everys.length; i++) {
				everys[i] = every;
				if (i < yushu) {
					everys[i] = everys[i] + 1;
				}
			}
			int index = 0;
			ExecutorService downloadThreadPool = Executors.newFixedThreadPool(1000);
			for (int i = 0; i < everys.length; i++) {
				if (everys[i] > 0) {
					DataStore ds = (DataStore) vdsFiles.subDataStore(index, index + everys[i]);
					index += everys[i];
					DownloadThread download = new DownloadThread(ds, mp, host, port, username, password);
					downloadThreadPool.execute(download);
				}

			}
			return downloadThreadPool;

		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}
	}

	/**
	 * 获取ftp文件目录
	 * 
	 * @param client
	 * @param file
	 * @param path:要找的路径
	 * @return
	 * @throws AppException
	 */
	public static FTPFileTree getFTPDirectoryTreeModel(FTPClient client, FTPFile file, String path)
			throws AppException {
		FTPFileTree root = null;
		FTPFileNode node = null;
		try {
			if (null == path) {
				path = "";
			}
			node = new FTPFileNode(RandomManager.getRandomStrNoHan(8));
			if (null == file || null == path) {
				node.setAttribute("text", "FTP文件夹");
				node.setAttribute("target", "target");
				node.setAttribute("state", "open");
				node.setAttribute("path", "");
			} else {
				node.setAttribute("text", file.getName());
				node.setAttribute("path", path + File.separator + file.getName());
				node.setAttribute("size", file.getSize() + "");
			}
			root = new FTPFileTree(node);

			if (null == file || file.isDirectory()) {
				String nowpath;

				nowpath = new String((File.separator + node.getAttribute("path")).getBytes("GBK"), "iso-8859-1");
				FTPFile[] files = client.listFiles(nowpath);
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						// FTPFileTree leaf = getFTPDirectoryTreeModel(client,
						// files[i], node.getAttribute("path") + "");
						// node.addChildNode(leaf.getNode(leaf.getRootIds().get(0)));
					} else {
						FTPFileNode nodeChild = new FTPFileNode(RandomManager.getRandomStrNoHan(8));
						nodeChild.setAttribute("text", files[i].getName());
						nodeChild.setAttribute("path", node.getAttribute("path") + File.separator + files[i].getName());
						nodeChild.setAttribute("size", files[i].getSize() + "");
						node.addChildNode(nodeChild);
					}
				}
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			throw new AppException(e);
		}

		return root;

	}

	/**
	 * 获取ftp上的文件名称及大小信息
	 * 
	 * @author 张超
	 * @date 创建时间 2017-6-8
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public static DataObject getFTPFilesFromPath(String url, FTPClient client) throws AppException {
		DataObject pdo = null;
		ArrayList<String> alFiles = new ArrayList<>();
		long size = 0l;
		try {
			pdo = new DataObject();
			FTPFile[] files = client.listFiles(new String(url.getBytes("GBK"), "iso-8859-1"));
			// 如果不是路径
			if (!client.changeWorkingDirectory(url)) {
				if (files.length == 1 && files[0].isFile() && files[0].getSize() > 0) {
					alFiles.add(url);
					pdo.put("files", alFiles);
					pdo.put("length", files[0].getSize());
					// System.out.println(files[0].getRawListing());
					return pdo;
				}
			}

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					alFiles.add(url + File.separator + files[i].getName());
					size += files[i].getSize();
				} else {
					DataObject rdo = getFTPFilesFromPath(url + File.separator + files[i].getName(), client);
					alFiles.addAll((ArrayList<String>) rdo.getObject("files"));
					size += rdo.getLong("length");
				}

			}

		} catch (Exception e) {
			throw new AppException(e);
		}
		pdo.clear();
		pdo.put("files", alFiles);
		pdo.put("length", size);
		return pdo;
	}

	/**
	 * 获取FTP连接
	 * 
	 * @return
	 * @throws AppException
	 * @author 张超
	 * @date 创建时间 2017年6月6日
	 * @since V1.0
	 */
	public static FTPClient getFtpConnection(String host, int port, String username, String password)
			throws AppException {
		synchronized (clientKey) {
			if (null == host || host.trim().isEmpty()) {
				throw new AppException("url不能为空");
			}
			if (null == username || username.trim().isEmpty()) {
				throw new AppException("username不能为空");
			}
			if (null == password || password.trim().isEmpty()) {
				throw new AppException("password不能为空");
			}

			FTPClient client = new FTPClient();
			try {
				client.connect(host, port);
				client.login(username, password);
				client.enterLocalPassiveMode();
				client.setControlEncoding("GBK");
				int reply = client.getReplyCode();
				if ((!FTPReply.isPositiveCompletion(reply))) {
					client.disconnect();
					throw new AppException("登录ftp服务器失败,请检查ftp配置是否正确!");
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
			return client;
		}
	}

	/**
	 * 在ftp上创建目录
	 * 
	 * @param path
	 *            -- 严格按照--如果ftp文件夹下，名为为 xxx的文件夹下创建aa文件夹---/xxx/aa
	 * @author 张超
	 * @date 创建时间 2017-6-6
	 * @since V1.0
	 */
	public static boolean mkdirs(FTPClient client, String path) throws AppException {
		try {
			String pt = new String(path.getBytes("GBK"), "iso-8859-1");
			StringTokenizer s = new StringTokenizer(pt, File.separator);
			s.countTokens();
			String pathName = "";
			while (s.hasMoreElements()) {
				pathName = pathName + File.separator + (String) s.nextElement();
				try {
					client.mkd(pathName);
				} catch (Exception e) {
					throw new AppException("ftp文件夹创建失败");
				}
			}
			client.logout();
			client.disconnect();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		// FTPFileTree fileTree =
		// FTPUtil.getFTPDirectoryTreeModel(
		// FTPUtil.getFtpConnection("192.168.0.103", 21, "zchar@outlook.com",
		// "193835!zczh"), null, null);
		// FTPFileTree fileTree = FTPUtil
		// .getFTPDirectoryTreeModel(FTPUtil.getFtpConnection("61.147.97.227",
		// 2199, "m", "m"), null, null);

		// System.out.println(fileTree.getTreeDs());
		final MessagePool mess = new MessagePool();
		// final ExecutorService es = FTPUpload(new File("D:" + File.separator +
		// "桌面暂存"),
		// File.separator + "sss" + File.separator, mess, DOWNLOAD_ALL_SIZE,
		// DOWNLOAD_ALL_SIZE, DOWNLOAD_ALL_SIZE);
		final ExecutorService es = FTPUtil.FTPDownload(File.separator+"百分之三第二季03.mp4", "D:"+File.separator, mess, "61.147.97.227", 2199, "m",
				"m");
		new Thread() {
			public void run() {
				while (true) {
					Object objNow = mess.getMessage(DOWNLOAD_NOW_SIZE);
					Object objAll = mess.getMessage(DOWNLOAD_ALL_SIZE);
					if (null == objAll || null == objNow) {
						continue;
					}
					long nowSzie = (long) objNow;
					long allsize = (long) objAll;
					System.out.println(nowSzie + "/" + allsize + "--" + mess.getMessage(DOWNLOAD_FILE_NAME));
					if (nowSzie == allsize) {
						mess.killMine();
						es.shutdownNow();
						break;
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
		}.start();
	}
}
