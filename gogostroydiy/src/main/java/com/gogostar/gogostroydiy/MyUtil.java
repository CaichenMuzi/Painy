package com.gogostar.gogostroydiy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/17.
 * <p>
 * 自定义工具类
 */
class MyUtil {

	/**
	 * POST请求操作
	 *
	 * @param jsonObject JSON数据
	 * @param path       接口地址
	 * @return JSONObject
	 */
	public JSONObject sendByPost(String path, JSONObject jsonObject) {
		JSONObject resultJson = null;
		try {
			// 根据地址创建URL对象
			URL url = new URL(path);
			// 根据URL对象打开链接
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			// 设置请求的方式
			urlConnection.setRequestMethod("POST");
			// 设置请求的超时时间
			urlConnection.setReadTimeout(5000);
			urlConnection.setConnectTimeout(5000);
			// 传递的数据
			String data = jsonObject.toString();
			// 设置请求的头
			urlConnection.setRequestProperty("Connection", "keep-alive");
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes()
					.length));
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; " +
					"rv:27.0) Gecko/20100101 Firefox/27.0");

			// 发送POST请求必须设置允许输出
			urlConnection.setDoInput(true);
			// 发送POST请求必须设置允许输入
			urlConnection.setDoOutput(true);

			// 获取输出流
			OutputStream os = urlConnection.getOutputStream();
			os.write(data.getBytes());
			os.flush();
			Log.d("sendPost", os.toString());
			if (urlConnection.getResponseCode() == 200) {
				// 获取响应的输入流对象
				InputStream is = urlConnection.getInputStream();
				// 创建字节输出流对象
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 定义读取的长度
				int len = 0;
				// 定义缓存区
				byte buffer[] = new byte[1024];
				// 按照缓存区的大小，循环读取
				while ((len = is.read(buffer)) != -1) {
					// 根据读取的长度写入到os对象中
					baos.write(buffer, 0, len);
				}
				// 释放资源
				is.close();
				baos.close();
				// 返回字符串
				String result = new String(baos.toByteArray());
				String string = getString(result);
				resultJson = new JSONObject(string);

			} else {
				System.out.println("链接失败......");
				System.out.println(String.valueOf(urlConnection.getResponseCode()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

	/**
	 * GET请求操作
	 *
	 * @param path       接口
	 * @param categoryId 分类ID
	 * @return JSONObject
	 */
	public JSONObject sendByGet(String path, String categoryId) {
		JSONObject jsonObject = new JSONObject();
		// 如果分类id不为空，将分类id拼接到接口url
		if (categoryId != null) {
			path = path + categoryId;
		}
		HttpGet httpGet = new HttpGet(path);
		try {
			// 链接服务器

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 链接成功，获取返回的字符串，并转换为JSON
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				String string = getString(strResult);
				Log.d("RESULT", string);
				jsonObject = new JSONObject(string);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 定义访问修改头像的接口的方法
	 *
	 * @param url   接口url
	 * @param bytes 头像字节数组
	 * @return
	 */
	public JSONObject postBytes(String url, byte[] bytes) {
		JSONObject jsonObject = null;
		try {
			// 字节数组工具
			ByteArrayEntity arrayEntity = new ByteArrayEntity(bytes);
			// 设置访问数据类型
			arrayEntity.setContentType("application/octet-stream");
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(arrayEntity);
			DefaultHttpClient client = new DefaultHttpClient();
			// 获取网络访问结果码
			int resultCode = client.execute(httpPost).getStatusLine().getStatusCode();
			if (resultCode == 200) {
				// 如果访问成功，获取返回的数据字符串，并转换为JSON
				String result = EntityUtils.toString(client.execute(httpPost).getEntity());
				result = result.replace("\"{", "{").replace("}\"", "}").replace("\\", "");
				jsonObject = new JSONObject(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("链接失败......");
		}
		return jsonObject;
	}

	/**
	 * 从服务器获取图片Bitmap
	 *
	 * @param path 图片URL
	 * @return Bitmap
	 */
	public Bitmap getBitmap(String path) {
		Bitmap bitmap1 = null;
		URL url = null;
		try {
			// 链接服务器
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			// 获取返回的输入流并转换成Bitmap
			InputStream in = new BufferedInputStream(conn.getInputStream());
			bitmap1 = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap1;
	}

	/**
	 * 去除字符串中多余的符号
	 *
	 * @param string 返回的JSON字符串
	 * @return String
	 */
	public String getString(String string) {
		return string.replace("\\", "").replace("\"[", "[").replace("]\"",
				"]").replace("\"{", "{").replace("}\"", "}");
	}

	/**
	 * 将字符串MD5加密
	 *
	 * @param str 加密的字符串
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String getMD5(String str) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		// 将字符串转换成加密的字节数组
		byte[] bs = md5.digest(str.getBytes());
		StringBuilder sb = new StringBuilder(40);
		// 拼接字节数组
		for (byte x : bs) {
			if ((x & 0xff) >> 4 == 0) {
				sb.append("0").append(Integer.toHexString(x & 0xff));
			} else {
				sb.append(Integer.toHexString(x & 0xff));
			}
		}
		return sb.toString();
	}

	/**
	 * 定义图片放大消失的动画
	 *
	 * @param imageView 需要添加动画的Image
	 * @return AnimationSet
	 */
	public AnimationSet ImgAnimation(ImageView imageView) {
		AnimationSet animationSet = new AnimationSet(false);
		// 以View中心点作为缩放中心，水平方向和垂直方向都扩大为原来的1.5倍
		float fromXScale = 1.0f;
		float toScaleX = 1.5f;
		float fromYScale = 1.0f;
		float toScaleY = 1.5f;
		float pivotX = imageView.getWidth() / 2;
		float pivotY = imageView.getHeight() / 2;
		Animation animation = new ScaleAnimation(fromXScale, toScaleX, fromYScale,
				toScaleY, pivotX, pivotY);
		// 1.0表示完全不透明，0.0表示完全透明
		float fromAlpha = 1.0f;
		float toAlpha = 0.0f;
		// 1.0 => 0.0表示View从完全不透明渐变到完全透明
		// 设置动画集
		Animation animation1 = new AlphaAnimation(fromAlpha, toAlpha);
		animationSet.addAnimation(animation);
		animationSet.addAnimation(animation1);
		// 设置动画持续时间
		animationSet.setDuration(2000);
		// 动画执行完毕后是否停在结束时的角度上
		animationSet.setFillAfter(true);
		return animationSet;
	}

	/**
	 * 定义图片缩小消失的动画
	 *
	 * @param imageView 需要添加动画的Image
	 * @return AnimationSet
	 */
	public AnimationSet getAnimation(ImageView imageView) {
		AnimationSet animationSet = new AnimationSet(false);
		// 以View中心点作为缩放中心，水平方向和垂直方向都缩小
		float fromXScale = 1.0f;
		float toScaleX = 0.0f;
		float fromYScale = 1.0f;
		float toScaleY = 0.0f;
		float pivotX = imageView.getWidth() / 2;
		float pivotY = imageView.getHeight() / 2;
		Animation animation = new ScaleAnimation(fromXScale, toScaleX, fromYScale,
				toScaleY, pivotX, pivotY);
		// 1.0表示完全不透明，0.0表示完全透明
		float fromAlpha = 1.0f;
		float toAlpha = 0.0f;
		// 1.0 => 0.0表示View从完全不透明渐变到完全透明
		// 设置动画集
		Animation animation1 = new AlphaAnimation(fromAlpha, toAlpha);
		animationSet.addAnimation(animation);
		animationSet.addAnimation(animation1);
		// 设置动画持续时间
		animationSet.setDuration(2000);
		// 动画执行完毕后是否停在结束时的角度上
		animationSet.setFillAfter(true);
		return animationSet;
	}

	// 定义静态变量
	private static MediaPlayer mediaPlayer = new MediaPlayer();
	private static SoundPool soundPool;
	private static int soundId;

	// 封装MediaPlayer
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	/**
	 * 播放音频
	 *
	 * @param path 音频地址
	 */
	public void initMediaPlayer(String path) {
		try {
			mediaPlayer.reset();// 重置mediaPlayer
			mediaPlayer.setDataSource(path); // 指定音频文件的路径
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止播放音频
	 */
	public void stopMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();// 释放
			mediaPlayer = null;
		}
	}

	/**
	 * 定义播放按钮音效方法
	 *
	 * @param context 当前页面
	 * @param musicId 音效id
	 */
	public void playSound(Context context, int musicId) {
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		soundId = soundPool.load(context, musicId, 1);
		// 设置监听器，在加载音乐文件完成时触发该事件
		soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1);
			}
		});
	}

	/**
	 * 播放音效
	 *
	 * @param context 当前页面
	 * @param musicId 音效id
	 */
	public void playMusic(Context context, int musicId) {
		mediaPlayer.reset();
		mediaPlayer = MediaPlayer.create(context, musicId);
		mediaPlayer.start();
	}

	/**
	 * 从txt文件中读取文本
	 *
	 * @param file txt文件
	 * @return
	 */
	public String readTxtFile(File file) {
		String lineTxt = null;
		try {
			String enconding = "UTF-8";
			if (file.isFile() && file.exists()) {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file),
						enconding);
				BufferedReader bufferedReader = new BufferedReader(reader);
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
					return lineTxt.replaceAll("[\\p{Punct}]", " ");
				}
				reader.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return lineTxt;
	}

	/**
	 * 从xml中读取文本
	 *
	 * @param xmlData xml文件
	 * @return
	 */
	public String parseXMLWithPull(String xmlData) {
		String total_score = "";
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlData));
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG: {
						if ("total_score".equals(nodeName)) {
							total_score = xmlPullParser.getAttributeValue(0);
							return total_score;
						}
					}
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total_score;
	}
}

/**
 * 图片三级缓存工具
 */
class MyBitmapUtils {
	private NetCacheUtils mNetCacheUtils;
	private LocalCacheUtils mLocalCacheUtils;
	private MemoryCacheUtils mMemoryCacheUtils;

	public MyBitmapUtils() {
		mMemoryCacheUtils = new MemoryCacheUtils();
		mLocalCacheUtils = new LocalCacheUtils();
		mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
	}

	/**
	 * @param imageView 要展示加载图片的ImageView
	 * @param url       加载图片的链接
	 */
	public void display(ImageView imageView, String url) {
		Bitmap bitmap = null;

		// 先从内存加载
		bitmap = mMemoryCacheUtils.getMemoryCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return;
		}

		// 先从本地加载
		bitmap = mLocalCacheUtils.getLocalCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			mMemoryCacheUtils.setMemoryCache(url, bitmap);
			return;
		}

		// 从网络加载
		mNetCacheUtils.getBitmapFromNet(imageView, url);
	}
}

/**
 * 网络缓存工具
 */
class NetCacheUtils {

	private LocalCacheUtils mLocalCacheUtils;
	private MemoryCacheUtils mMemoryCacheUtils;

	public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
		super();
		this.mLocalCacheUtils = localCacheUtils;
		this.mMemoryCacheUtils = memoryCacheUtils;
	}

	// 从网络加载图片
	public void getBitmapFromNet(ImageView imageView, String url) {
		new BitmapTask().execute(imageView, url);
	}

	/**
	 * 异步处理
	 */
	class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

		private ImageView imageView;
		private String url;

		/**
		 * 1.预加载，运行主线程
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/**
		 * 2.正在加载（核心方法），运行在子线程
		 */
		@Override
		protected Bitmap doInBackground(Object... params) {
			imageView = (ImageView) params[0];
			url = (String) params[1];

			imageView.setTag(url);// 打标记

			Bitmap bitmap = download(url);
			return bitmap;
		}

		/**
		 * 3.进度更新，运行在主线程
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * 4.加载结束，运行在主线程
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				String url = (String) imageView.getTag();
				if (url != null && url.equals(this.url)) {
					// 从网络加载图片
					imageView.setImageBitmap(bitmap);
					// 写本地内存
					mLocalCacheUtils.setLocalCache(url, bitmap);
					// 写本地缓存
					mMemoryCacheUtils.setMemoryCache(url, bitmap);
				}
			}
		}
	}

	/**
	 * 根据url下载图片
	 *
	 * @param url
	 * @return
	 */
	public Bitmap download(String url) {

		HttpURLConnection conn;
		try {
			URL mUrl = new URL(url);
			conn = (HttpURLConnection) mUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			int code = conn.getResponseCode();

			if (code == 200) {
				// 成功后获取流，进行处理
				InputStream inputStream = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

/**
 * 本地缓存工具类
 */
class LocalCacheUtils {
	private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/gogostar/Diy/Resources/image";

	// 写本地缓存
	public void setLocalCache(String url, Bitmap bitmap) {
		File dir = new File(LOCAL_CACHE_PATH);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}

		try {
			String fileName = MD5Encoder.encode(url);// 采用MD5加密
			File cacheFile = new File(dir, fileName);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(cacheFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读本地缓存
	public Bitmap getLocalCache(String url) {
		try {
			File cacheFile = new File(LOCAL_CACHE_PATH, MD5Encoder.encode(url));
			if (cacheFile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

/**
 * 内存缓存工具类
 * 第一次优化，利用软引用解决可能的OOM异常
 */
class MemoryCacheUtils {

	private HashMap<String, SoftReference<Bitmap>> hash;

	// 写内存缓存
	public void setMemoryCache(String url, Bitmap bitmap) {
		if (hash == null) {
			hash = new HashMap<String, SoftReference<Bitmap>>();
		}
		// 使用软引用把Bitmap包装起来
		SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bitmap);
		hash.put(url, softReference);
	}

	// 读内存缓存
	public Bitmap getMemoryCache(String url) {
		if (hash != null && hash.containsKey(url)) {
			SoftReference<Bitmap> softReference = hash.get(url);
			Bitmap bitmap = softReference.get();
			return bitmap;
		}
		return null;
	}
}
