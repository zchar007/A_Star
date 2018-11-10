package com.sept.io.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import com.sept.io.exception.IOException;

public class FileByteReader {
	private File file;
	private int byteSize;
	private FileInputStream fileForByte;
	private FileInputStream fileForJump;

	private byte[] readByte;
	private static final int defaulSize = 1024;
	private int readNumber = 0;

	private Object key = new Object();

	public FileByteReader(String url, int byteSize) throws IOException {
		try {
			if (byteSize <= 0 || byteSize > 1024*1024*1024) {
				throw new IOException("每次读取大小最小是1byte,最大是1GB(1073741824byte)！！");
			}
			this.byteSize = byteSize;
			this.readByte = new byte[byteSize];
			this.file = new File(url);
			if (!this.file.exists()) {
				this.file.createNewFile();
			}
			this.fileForByte = new FileInputStream(file);
		} catch (Exception e) {
			throw new IOException(-11, e.getMessage());
		}
	}

	/**
	 * 
	 * 
	 * @param url
	 * @throws IOException
	 */
	public FileByteReader(String url) throws IOException {
		this(url, defaulSize);
	}

	public byte[] read() throws IOException {
		synchronized (key) {
			try {
				byte[] returnByte;
				int length = fileForByte.read(readByte, 0, this.byteSize);
				if (length <= 0) {
					return null;
				}
				returnByte = new byte[length];
				for (int i = 0; i < returnByte.length; i++) {
					returnByte[i] = readByte[i];
				}
				readByte = new byte[byteSize];
				this.readNumber++;
				return returnByte;
			} catch (Exception e) {
				throw new IOException(-11, e.getMessage());
			}
		}
	}

	public byte[] readJump(long start, long end) throws IOException {
		synchronized (key) {

			try {
				long size = this.file.length();
				byte[] byteTemp = null;
				if (start > size || end > size || start < 0 || end < 0) {
					throw new com.sept.io.exception.IOException("文件大小为：" + size + "，请检查！！");
				}
				if (end < start) {
					throw new com.sept.io.exception.IOException("开始大于结束！！");
				}
				fileForJump = new FileInputStream(file);
				long getSize = end - start;

				if (start > 0) {
					byteTemp = new byte[(int) start];
					fileForJump.read(byteTemp, 0, (int) start);
				}

				byteTemp = new byte[(int) getSize];
				int leng = fileForJump.read(byteTemp, 0, (int) getSize);
				fileForJump.close();

				if (leng == (int) getSize)
					return byteTemp;
				return null;
			} catch (Exception e) {

				throw new IOException(-11, e.getMessage());
			}
		}
	}

	public void close() throws IOException {
		synchronized (key) {
			try {
				fileForByte.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				fileForJump.close();
			} catch (Exception e) {
			}
		}
	}

	public long getSize() {
		synchronized (key) {
			return this.file.length();
		}
	}

	public void write(byte[] bytes, boolean isAppend) throws IOException {
		synchronized (key) {
			FileOutputStream fos = null;

			try {
				fos = new FileOutputStream(this.file, isAppend);
				fos.write(bytes);
				fos.flush();
				fos.close();
				//读取到指定位置
				this.fileForByte = new FileInputStream(this.file);
				if (isAppend) {
					for (int i = 0; i < readNumber; i++) {
						fileForByte.read(readByte, 0, this.byteSize);
					}
				} else {
					this.readNumber = 0;
				}
			} catch (Exception e) {
				throw new IOException(-12, e.getMessage());
			}finally {
				try {
					fos.flush();
					fos.close();
					
				} catch (java.io.IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void reset() throws IOException {
		synchronized (key) {
			try {
				this.fileForByte = new FileInputStream(file);
				this.readNumber = 0;
			} catch (Exception e) {
				throw new IOException(-11, e.getMessage());
			}
		}
	}

	/**
	 * 新的追加写入不会更改原读取顺序，但非追加性写入会重置原读写顺序
	 * 
	 * @param alSave
	 * @param isAppend
	 * @throws IOException
	 */
	public static void saveFile(byte[] bytes, String url, boolean isAppend) throws IOException {
		try {
			File file = new File(url);
			if (!isAppend) {// 不是追加
				if (file.exists()) {
					throw new IOException("文件[" + url + "]已存在且不为追加！");
				} else {
					file.createNewFile();
				}
			} else {
				if (!file.exists()) {
					file.createNewFile();
				}
			}

			FileOutputStream fos = new FileOutputStream(file, isAppend);
			fos.write(bytes);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			throw new IOException(-12, e.getMessage());
		}
	}

	public static void main(String[] args) throws IOException, UnsupportedEncodingException {
		FileByteReader fsb = new FileByteReader("D://test.txt", 10);
		byte[] bb = fsb.read();
		for (int i = 0; i < bb.length; i++) {
			System.out.println(bb[i]);
		}
		System.out.println(new String(bb, "GBK"));
		byte[] bb2 = fsb.read();
		for (int i = 0; i < bb2.length; i++) {
			System.out.println(bb2[i]);
		}
		System.out.println(new String(bb2, "GBK"));
		byte[] bb3 = fsb.read();
		for (int i = 0; i < bb3.length; i++) {
			System.out.println(bb3[i]);
		}
		System.out.println(new String(bb3, "GBK"));
		System.out.println(fsb.getSize());
		String str = "123455612432143214325423535432trewrtewt54wt435432t435321r34gf3tgergeqtgregrewyte5htrgh45yw54yh5";
		fsb.write(str.getBytes(), true);
		byte[] bb4 = fsb.read();
		for (int i = 0; i < bb4.length; i++) {
			System.out.println(bb4[i]);
		}
		System.out.println(new String(bb4, "GBK"));
		System.out.println(fsb.getSize());

		// byte[] b = new byte[5];
		// System.out.println(new String(fsb.readJump(5, 10), "ISO-8859-1"));
	}

}
