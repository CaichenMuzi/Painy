package com.gogostar.gogostroydiy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 解压工具
 */
public class ZipExtractorTask extends AsyncTask<Void, Integer, Long> {

	private final String TAG = "ZipExtractorTask";
	private File mInput;
	private File mOutput;
	private int mProgress = 0;
	private Context mContext;

	/**
	 * 构造函数
	 *
	 * @param in      需要解压的文件地址
	 * @param out     解压后的文件地址
	 * @param context 当前页面
	 */
	public ZipExtractorTask(String in, String out, Context context) {
		super();
		this.mContext = context;
		mInput = new File(in);
		mOutput = new File(out);
		if (!mOutput.exists()) {
			if (!mOutput.mkdirs()) {
				Log.e(TAG, "Failed to make directories:" + mOutput.getAbsolutePath());
			}
		}
	}

	@Override
	protected Long doInBackground(Void... params) {
		return unzip();
	}

	@Override
	protected void onPostExecute(Long result) {
		// 解压缩后调用播放页面的startVideo方法播放视频
		((PictureStoryActivity) mContext).initGridView();
		if (mInput.exists()) {
			mInput.delete();
		}
	}

	/**
	 * 解压zip方法
	 *
	 * @return long
	 */
	private long unzip() {
		long extractedSize = 0L;
		Enumeration<ZipEntry> entries;
		ZipFile zip = null;
		try {
			zip = new ZipFile(mInput);
			long uncompressedSize = getOriginalSize(zip);
			publishProgress(0, (int) uncompressedSize);
			entries = (Enumeration<ZipEntry>) zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				File destination = new File(mOutput, entry.getName());
				if (!destination.getParentFile().exists()) {
					Log.e(TAG, "make=" + destination.getParentFile().getAbsolutePath());
					destination.getParentFile().mkdirs();
				}
				ProgressReportingOutputStream outStream = new ProgressReportingOutputStream
						(destination);
				extractedSize += copy(zip.getInputStream(entry), outStream);
				outStream.close();
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				zip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return extractedSize;
	}

	/**
	 * 获取原文件的大小
	 *
	 * @param file
	 * @return
	 */
	private long getOriginalSize(ZipFile file) {
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
		long originalSize = 0l;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getSize() >= 0) {
				originalSize += entry.getSize();
			}
		}
		return originalSize;
	}

	/**
	 * 自定义复制文件方法
	 *
	 * @param input  输入流
	 * @param output 输出流
	 */
	private int copy(InputStream input, OutputStream output) {
		// 指定文件带缓冲区的读取流且指定缓冲区大小为8KB
		byte[] buffer = new byte[1024 * 8];
		BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
		BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
		int count = 0, n = 0;
		try {
			// 读取文件
			while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
				out.write(buffer, 0, n);
				count += n;
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}


	/**
	 * 自定义文件输出流
	 */
	private final class ProgressReportingOutputStream extends FileOutputStream {

		/**
		 * 构造函数
		 *
		 * @param file
		 * @throws FileNotFoundException
		 */
		public ProgressReportingOutputStream(File file) throws FileNotFoundException {
			super(file);
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
			super.write(buffer, byteOffset, byteCount);
			mProgress += byteCount;
			publishProgress(mProgress);// 更新进度条
		}
	}
}
