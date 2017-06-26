package com.gogostar.gogostroydiy;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017/3/15.
 */

/**
 * 下载压缩包工具
 */
public class DownLoaderTask extends AsyncTask<Void, Integer, String> {

	private final String TAG = "DownLoaderTask";
	private URL mUrl;
	private File mFile;
	private AlertDialog mDialog;
	private int mProgress = 0;
	private ProgressReportingOutputStream mOutputStream;
	private Context mContext;

	/**
	 * 构造函数
	 *
	 * @param url     文件地址
	 * @param out     存储地址
	 * @param context 当前页面
	 */
	public DownLoaderTask(String url, String out, Context context) {
		super();
		if (context != null) {
			// 自定义AlertDialog
			mDialog = new ProgressAlertDialog(context);
			mContext = context;
		} else {
			mDialog = null;
		}

		try {
			mUrl = new URL(url);
			// 获取压缩包文件的名字
			String fileName = new File(mUrl.getFile()).getName();
			// 文件存储地址
			mFile = new File(out, fileName);
			Log.d(TAG, "out=" + out + ", name=" + fileName + ",mUrl.getFile()=" + mUrl.getFile());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		if (mDialog != null) {
			// 点击屏幕其他区域不能取消Dialog
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			mDialog.show();
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		return download();
	}

	@Override
	protected void onPostExecute(String result) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (isCancelled()) {
			return;
		}
		// 下载结束后调用解压功能
		((PictureStoryActivity) mContext).doZipExtractorWork(mFile.getAbsolutePath(), mFile
				.getParent());
	}

	/**
	 * 从Url下载zip
	 *
	 * @return
	 */
	private String download() {
		URLConnection connection = null;
		try {
			// 打开链接
			connection = mUrl.openConnection();
			// 文件大小
			int length = connection.getContentLength();
			// 如果文件已存在，返回文件地址
			if (mFile.exists() && length == mFile.length()) {
				Log.d(TAG, "file " + mFile.getName() + " already exits!!");
				return mFile.getAbsolutePath();
			}
			// 文件输出流
			mOutputStream = new ProgressReportingOutputStream(mFile);
			// 更新进度条
			publishProgress(0, length);
			// 复制文件
			copy(connection.getInputStream(), mOutputStream);
			mOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mFile.getAbsolutePath();
	}

	/**
	 * 自定义复制文件方法
	 *
	 * @param inputStream  输入流
	 * @param outputStream 输出流
	 */
	private void copy(InputStream inputStream, OutputStream outputStream) {
		byte[] buffer = new byte[1024 * 8];
		// 指定文件带缓冲区的读取流且指定缓冲区大小为8KB
		BufferedInputStream in = new BufferedInputStream(inputStream, 1024 * 8);
		int n = 0;
		try {
			// 读取文件
			while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
				outputStream.write(buffer, 0, n);
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
