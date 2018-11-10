package com.sept.io.local;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.sept.exception.AppException;
import com.sept.io.exception.IOException;

public final class FileUtil {
	/**
	 * 遍历一个目录中的所有文件，包括子目录,写入文件中（防止文件过多）
	 * 
	 * @param path
	 * @param fliter
	 *            ：要获取的后缀名串
	 * @author 张超
	 * @throws IOException
	 * @date 创建时间 2017-5-27
	 * @since V1.0
	 */
	public final static HashMap<String, Object> getFilesFormPath(String path, String filter, String resultFile)
			throws AppException, IOException {
		File file = new File(path);
		return getFilesFormFile(file, filter, resultFile);
	}

	/**
	 * 遍历一个目录中的所有文件，包括子目录,写入文件中（防止文件过多）
	 * 
	 * @param path
	 * @param fliter
	 *            ：要获取的后缀名串
	 * @author 张超
	 * @throws IOException
	 * @date 创建时间 2017-5-27
	 * @since V1.0
	 */
	public final static HashMap<String, Object> getFilesFormFile(File file, String filter, String resultFile)
			throws AppException, IOException {
		HashMap<String, Object> hm = new HashMap<>();
		long size = 0;
		int fileNumber = 0;
		HashSet<String> hsFilter = new HashSet<String>();
		String[] filters = filter.split(",");

		for (int i = 0; i < filters.length; i++) {
			hsFilter.add(filters[i]);
		}
		// 如果是文件直接返回
		if (file.isFile()) {
			if (hsFilter.size() > 0 && hsFilter.contains(getFileType(file))) {
				hm.put("length", 0L);
				hm.put("number", 0);

				return hm;
			}
			FileLineReader.saveFile(file.getAbsolutePath(), resultFile, true);
			hm.put("length", file.length());
			hm.put("number", 1);

			return hm;
		}

		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				if (hsFilter.size() > 0 && !hsFilter.contains(getFileType(fileList[i]))) {
					continue;
				}
				// alFiles.add(fileList[i]);
				FileLineReader.saveFile(fileList[i].getAbsolutePath(), resultFile, true);
				fileNumber++;
				size += fileList[i].length();

			} else {
				HashMap<String, Object> hmReturn = getFilesFormFile(fileList[i], filter, resultFile);
				size += (long) hmReturn.get("length");
				fileNumber += (int) hmReturn.get("number");

			}
		}
		hm.put("length", size);
		hm.put("number", fileNumber);
		return hm;
	}

	/**
	 * 遍历一个目录中的所有文件，包括子目录
	 * 
	 * @param path
	 * @param fliter
	 *            ：要获取的后缀名串
	 * @author 张超
	 * @date 创建时间 2017-5-27
	 * @since V1.0
	 */
	public final static HashMap<String, Object> getFilesFormPath(String path, String filter) throws AppException {
		File file = new File(path);
		return getFilesFormFile(file, filter);
	}

	/**
	 * 遍历一个目录中的所有文件，包括子目录
	 * 
	 * @param path
	 * @param fliter
	 *            ：要获取的后缀名串
	 * @author 张超
	 * @date 创建时间 2017-5-27
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public final static HashMap<String, Object> getFilesFormFile(File file, String filter) throws AppException {
		HashMap<String, Object> pdo = new HashMap<String, Object>();
		ArrayList<File> alFiles = new ArrayList<File>();
		long size = 0;
		HashSet<String> hsFilter = new HashSet<String>();
		String[] filters = filter.split(",");
		for (int i = 0; i < filters.length; i++) {
			hsFilter.add(filters[i]);
		}
		// 如果是文件直接返回
		if (file.isFile()) {
			if (hsFilter.size() > 0 && !hsFilter.contains(getFileType(file))) {
				pdo.put("files", alFiles);
				pdo.put("length", 0L);
				return pdo;
			}
			alFiles.add(file);
			pdo.put("files", alFiles);
			pdo.put("length", file.length());
			return pdo;
		}

		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				// System.out.println(fileList[i]+"--"+fliter+"--"+getFileType(fileList[i]));
				if (hsFilter.size() > 0 && !hsFilter.contains(getFileType(fileList[i]))) {
					continue;
				}
				alFiles.add(fileList[i]);
				size += fileList[i].length();

			} else {
				HashMap<String, Object> vdoTemp = getFilesFormFile(fileList[i], filter);
				alFiles.addAll((ArrayList<File>) vdoTemp.get("files"));
				size += (Long) vdoTemp.get("length");
			}

		}
		pdo.put("files", alFiles);
		pdo.put("length", size);
		return pdo;
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param file
	 * @return
	 */
	public final static String getFileType(File file) {
		if (!file.isFile()) {
			return "";
		}
		return file.getName().substring(file.getName().lastIndexOf(".") + 1);
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param file
	 * @return
	 */
	public final static String getFileType(String fileName) {

		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	// ///////////////////////////////////
	// /////////以下还未验证正确性//////////////
	// ///////////////////////////////////
	public final static byte[] getContent(String filePath) throws IOException {
		try {
			File file = new File(filePath);
			long fileSize = file.length();
			if (fileSize > Integer.MAX_VALUE) {
				throw new Exception("File is too big" + file.getName());

			}
			FileInputStream fi = new FileInputStream(file);
			byte[] buffer = new byte[(int) fileSize];
			int offset = 0;
			int numRead = 0;
			while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
				offset += numRead;
			}
			fi.close();

			if (offset != buffer.length) {
				throw new Exception("Could not completely read file " + file.getName());
			}
			return buffer;
		} catch (Exception e) {
			throw new IOException(-12, e.getMessage());
		}
	}

	/**
	 * the traditional io way
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public final static byte[] toByteArray(String filename) throws IOException {
		ByteArrayOutputStream bos = null;
		BufferedInputStream in = null;
		try {
			File f = new File(filename);
			if (!f.exists()) {
				throw new IOException("文件[" + filename + "]不存在！");
			}

			bos = new ByteArrayOutputStream((int) f.length());

			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (Exception e) {
			throw new IOException(-12, e.getMessage());
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				throw new IOException(-12, e.getMessage());
			}
			try {
				bos.close();
			} catch (Exception e) {
				throw new IOException(-12, e.getMessage());
			}
		}
	}

	/**
	 * NIO way
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public final static byte[] toByteArray2(String filename) throws IOException {
		FileChannel channel = null;
		FileInputStream fs = null;

		try {
			File f = new File(filename);
			if (!f.exists()) {
				throw new FileNotFoundException(filename);
			}
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (Exception e) {
			throw new IOException(-12, e.getMessage());
		} finally {
			try {
				channel.close();
			} catch (Exception e) {
				throw new IOException(-12, e.getMessage());
			}
			try {
				fs.close();
			} catch (Exception e) {
				throw new IOException(-12, e.getMessage());
			}
		}
	}

	/**
	 * Mapped File way MappedByteBuffer
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public final static byte[] toByteArray3(String filename) throws IOException {

		FileChannel fc = null;
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(filename, "r");
			fc = raf.getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			raf.close();
			fc.close();
			return result;
		} catch (Exception e) {
			throw new IOException(-11, e.getMessage());
		} finally {
			try {
				raf.close();
				fc.close();
			} catch (Exception e) {
				throw new IOException(-11, e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws AppException, IOException {
		HashMap<String, Object> pdo = FileUtil.getFilesFormFile(new File("E:\\Ant_Framework\\SEPT_FRAMEWORK\\sept"),
				"xml", "D://test.data");
		System.out.println(pdo);

	}
}
