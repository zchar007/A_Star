package com.sept.io.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sept.exception.AppException;
import com.sept.io.exception.IOException;
import com.sept.io.local.FileLineReader;
import com.sept.io.local.FileUtil;

public class FileCopy {
	private String fromUrl;
	private String toUrl;
	private String types;
	private boolean isCopyPath;
	private MyProgressBar progressBar;
	private static int every = 20;
	private static String filePathFilePath = "./data.data";

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 10, TimeUnit.MILLISECONDS,
			new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());

	public FileCopy(String fromUrl, String toUrl, String types, boolean isCopyPath, MyProgressBar progressBar) {
		super();
		this.fromUrl = fromUrl;
		this.toUrl = toUrl;
		this.types = types;
		this.isCopyPath = isCopyPath;
		this.progressBar = progressBar;
	}

	public void startCopy() throws AppException {
		progressBar.reset();
		File fromFile = new File(fromUrl);
		File toFile = new File(toUrl);
		HashMap<String, Object> filess = FileUtil.getFilesFormFile(fromFile, types);
		long allSize = (long) filess.get("length");
		ArrayList<File> alFile = (ArrayList<File>) filess.get("files");
		progressBar.setAllSize(allSize);
		progressBar.setAllNumber(alFile.size());

		if (fromFile.isDirectory()) {
			toUrl = toUrl + File.separator + fromFile.getName();
			File fileT = new File(toUrl);
			fileT.mkdirs();
			int number = alFile.size() / every;
			if (number == 0) {
				for (int i = 0; i < alFile.size(); i++) {
					File fromFile_now = alFile.get(i);
					String toUrl_now = toUrl + fromFile_now.getAbsolutePath().substring(fromUrl.length());
					File toFile_now = new File(toUrl_now);
					// 检查是否存在
					if (!toFile_now.getParentFile().exists()) {
						toFile_now.getParentFile().mkdirs();
					}
					executor.execute(new FileCopyThread(fromFile_now, toFile_now, progressBar));
				}
			} else {// 考虑分布式，虽然在一台机器上没用，但还是写一下吧
				ArrayList<ArrayList<File>> alFiles = new ArrayList<>();
				alFile = sortFileSizeForThis(alFile);

				for (int i = 0; i < alFile.size(); i++) {
					ArrayList<File> al = new ArrayList<File>();
					alFiles.add(al);
					for (int j = 0; j < every; j++) {
						al.add(alFile.get(i));
						i++;
						if (i >= alFile.size()) {
							break;
						}
					}
					i--;
				}
				for (int i = 0; i < alFiles.size(); i++) {
					System.out.println(alFiles.get(i).size());
					executor.execute(new CopyThread(alFiles.get(i), progressBar));
				}
			}
		} else {
			executor.execute(new FileCopyThread(fromFile, toFile, progressBar));
		}
	}

	class CopyThread implements Runnable {
		private ArrayList<File> alFileIn;
		private MyProgressBar progressBar;

		public CopyThread(ArrayList<File> alFileIn, MyProgressBar progressBar) {
			super();
			this.alFileIn = alFileIn;
			this.progressBar = progressBar;
		}

		@Override
		public void run() {
			for (int i = 0; i < alFileIn.size(); i++) {
				File fromFile_now = alFileIn.get(i);
				String toUrl_now = toUrl + fromFile_now.getAbsolutePath().substring(fromUrl.length());
				File toFile_now = new File(toUrl_now);
				// 检查是否存在
				if (!toFile_now.getParentFile().exists()) {
					toFile_now.getParentFile().mkdirs();
				}
				executor.execute(new FileCopyThread(fromFile_now, toFile_now, progressBar));
			}
		}

	}

	private ArrayList<File> sortFileSizeForThis(ArrayList<File> alFile) {

		ArrayList<File> alFile_temp = new ArrayList<>();

		// 排序
		for (int i = 0; i < alFile.size() - 1; i++) {
			for (int j = i + 1; j < alFile.size(); j++) {
				if (alFile.get(i).length() > alFile.get(j).length()) {
					File f_t = alFile.get(i);
					alFile.set(i, alFile.get(j));
					alFile.set(j, f_t);
				}
			}
		}

		// 穿插对调
		for (int i = 0; i < alFile.size() / 2; i++) {
			alFile_temp.add(alFile.get(i));
			alFile_temp.add(alFile.get(alFile.size() - i - 1));
		}
		if (alFile.size() % 2 != 0) {
			alFile_temp.add(alFile.get(alFile.size() / 2));
		}
		return alFile_temp;

	}

	public void startCopyByFile() throws AppException, IOException {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File f = new File(filePathFilePath);
				if (f.exists()) {
					f.delete();
				}
				progressBar.reset();
				progressBar.setTitle("正在查找文件，请稍后... ...");
				File fromFile = new File(fromUrl);
				File toFile = new File(toUrl);
				HashMap<String, Object> hmReturn = null;
				try {
					hmReturn = FileUtil.getFilesFormFile(fromFile, types, filePathFilePath);
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long allSize = (long) hmReturn.get("length");
				int number = (int) hmReturn.get("number");
				progressBar.setAllSize(allSize);
				progressBar.setAllNumber(number);

				System.gc();
				System.gc();
				System.gc();
				System.gc();

				if (fromFile.isDirectory()) {
					FileLineReader flr = null;
					try {
						flr = new FileLineReader(filePathFilePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					toUrl = toUrl + File.separator + fromFile.getName();
					File fileT = new File(toUrl);
					fileT.mkdirs();

					String itPath = null;
					while ((itPath = flr.readLine()) != null) {
						File fromFile_now = new File(itPath);
						String toUrl_now = toUrl + fromFile_now.getAbsolutePath().substring(fromUrl.length());
						File toFile_now = new File(toUrl_now);
						// 检查是否存在
						if (!toFile_now.getParentFile().exists()) {
							toFile_now.getParentFile().mkdirs();
						}
						executor.execute(new FileCopyThread(fromFile_now, toFile_now, progressBar));

					}
					try {
						flr.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					executor.execute(new FileCopyThread(fromFile, toFile, progressBar));
				}
			}
		}).start();

	}

	public static void main(String[] args) {
		int[] index = { 2, 3, 6, 4, 33, 434, 4355, 123, 353, 5645, 3453, 8976, 46754, 455, 654, 23, 754, 565, };

		for (int i = 0; i < index.length - 1; i++) {
			for (int j = i + 1; j < index.length; j++) {
				if (index[i] > index[j]) {
					int t = index[i];
					index[i] = index[j];
					index[j] = t;
				}
			}
		}
		int[] index2 = new int[index.length];
		for (int i = 0; i < index.length / 2; i++) {
			for (int j = index.length / 2; j < index.length; j++) {
				int t = index[i];
				index[i] = index[i + 1];
				index[j] = t;
			}
		}
		for (int i = 0; i < index.length; i++) {
			System.out.println(index[i] + ",");
		}
	}
}
