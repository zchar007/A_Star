package com.sept.io.test;

import java.io.File;

import com.sept.io.exception.IOException;
import com.sept.io.local.FileByteReader;

public class FileCopyThread implements Runnable {
	private File fromFile;
	private File toFile;
	private MyProgressBar progressBar;

	public FileCopyThread(File fromFile, File toFile, MyProgressBar progressBar) {
		super();
		this.fromFile = fromFile;
		this.toFile = toFile;
		this.progressBar = progressBar;
	}

	@Override
	public void run() {
		try {
			long size = fromFile.length()/3;
			if(size > 10485760){//10M,100个线程顶多用1G
				size = 10485760;
			}
			if(size < 1024){
				size = 1024;
			}
			FileByteReader fromFileR = new FileByteReader(fromFile.getAbsolutePath(),(int)size);
			FileByteReader toFileR = new FileByteReader(toFile.getAbsolutePath());

			byte[] bytes;
			while ((bytes = fromFileR.read()) != null) {
				progressBar.setTitle(fromFile.getAbsolutePath()+"-->"+toFile.getAbsolutePath());
				toFileR.write(bytes, true);
				progressBar.goStep(bytes.length);
			}
			fromFileR.close();
			toFileR.close();
			progressBar.finshOneNumber();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
