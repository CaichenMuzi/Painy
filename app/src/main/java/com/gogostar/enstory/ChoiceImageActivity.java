package com.gogostar.enstory;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/2/21.
 * <p>
 * 选择头像Activity
 */

public class ChoiceImageActivity extends BaseActivity implements View.OnClickListener {

	private Button album_btn, camera_btn;
	private Uri imageUri;

	// 选择的操作方式
	private static final int TAKE_PHOTO = 1;// 打开照相机
	private static final int SELECT_PHOTO = 2;// 打开相册
	private static final int CROP_PHOTO = 3;// 调用截图

	// 设置裁剪的默认大小x，y轴方向
	private final int DEFAULT_CROP_X = 360;
	private final int DEFAULT_CROP_Y = 360;
	// 设置默认的生产图片的大小
	private final int DEFAULT_IMAGE_WIDTH = 360;
	private final int DEFAULT_IMAGE_HEIGHT = 360;
	// 处理后的bitmap
	private Bitmap mBitmap;
	// 设置生成图片的大小
	private int mImageWidth;
	private int mImageHeight;
	// 标记图片是否使用return-data属性
	private boolean mReturnData;

	private Context context;
	// 头像图片保存的地址文件
	private String PATH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_image_item);

		// 查找控件
		album_btn = (Button) findViewById(R.id.album_btn);
		camera_btn = (Button) findViewById(R.id.camera_btn);

		// 点击事件
		album_btn.setOnClickListener(this);
		camera_btn.setOnClickListener(this);

		// 设置生成图片的大小
		mImageWidth = DEFAULT_IMAGE_WIDTH;
		mImageHeight = DEFAULT_IMAGE_HEIGHT;

		context = this;
		PATH = context.getCacheDir().toString() + "/gogostar/enstory/register/";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.album_btn:
				openAlbum();// 打开相册
				break;
			case R.id.camera_btn:
				openCamera();// 打开照相机
				break;
			default:
				break;
		}
	}

	/**
	 * 打开照相机方法
	 */
	private void openCamera() {
		// 存储的头像
		String path = PATH + "tempImage.jpg";
		File outputImage = new File(path);
		// 创建文件夹
		if (!outputImage.exists()) {
			outputImage.mkdirs();
		}
		try {
			if (outputImage.exists()) {
				outputImage.delete();
			}
			outputImage.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		imageUri = Uri.fromFile(outputImage);
		// 打开相机
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, TAKE_PHOTO);
	}

	/**
	 * 打开相册
	 */
	private void openAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 如果有返回结果
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case TAKE_PHOTO:
					// 对指定路径下的图片进行裁剪
					cropCameraImage(imageUri, DEFAULT_CROP_X, DEFAULT_CROP_Y, CROP_PHOTO);
					break;
				case SELECT_PHOTO:
					// 对图片进行裁剪
					Uri selectPhotoUri = data.getData();
					cropAlbumImage(selectPhotoUri, imageUri, DEFAULT_CROP_X, DEFAULT_CROP_Y,
							CROP_PHOTO);
					break;
				case CROP_PHOTO:
					if (data != null) {
						/**
						 * 如果想使用data.getParcelableExtra("data")获取bitmap
						 * 只需要在裁剪时设置return-data为true即可
						 */
						if (mReturnData) {
							// 从返回的数据中获取bitmap
							mBitmap = data.getParcelableExtra("data");
							if (mBitmap == null) {
								return;
							}
						} else {
							// 从返回的数据中获取URI
							Uri originalUri = data.getData();
							if (originalUri == null) {
								originalUri = imageUri;
							}
							// 获取缩略图
							mBitmap = getThumbnail(originalUri, mImageWidth, mImageHeight);
						}
						// 将bitmap以字节流的形式返回给上一个界面
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
						// 设置返回到上一个活动的数据
						Intent intent = new Intent();
						intent.putExtra("image_byte", baos.toByteArray());
						setResult(RESULT_OK, intent);
						finish();
					}
					break;
				default:
					break;
			}
		} else {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 对照相机拍摄的图片进行裁剪
	 *
	 * @param uri         保存路径
	 * @param outputX     裁剪的宽
	 * @param outputY     裁剪的高
	 * @param requestCode 返回码
	 */
	private void cropCameraImage(Uri uri, int outputX, int outputY, int requestCode) {
		// 不使用return-data属性
		mReturnData = false;
		// 裁剪图片
		cropImage(uri, uri, outputX, outputY, mReturnData, requestCode);
	}

	/**
	 * 对相册中选择的图片进行裁剪
	 *
	 * @param sourceUri   图片路径
	 * @param uri         保存路径
	 * @param outputX     裁剪的宽
	 * @param outputY     裁剪的高
	 * @param requestCode 返回码
	 */
	private void cropAlbumImage(Uri sourceUri, Uri uri, int outputX, int outputY, int
			requestCode) {
		// 使用return-data属性
		mReturnData = true;
		// 裁剪图片
		cropImage(sourceUri, uri, outputX, outputY, mReturnData, requestCode);
	}

	/**
	 * 裁剪照片
	 *
	 * @param sourceUri   图片路径
	 * @param outputUri   保存路径
	 * @param outputX     裁剪的宽
	 * @param outputY     裁剪的高
	 * @param returnData  返回的数据
	 * @param requestCode 返回码
	 */
	private void cropImage(Uri sourceUri, Uri outputUri, int outputX, int outputY, boolean
			returnData, int requestCode) {
		try {
			// 发送裁剪信号
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(sourceUri, "image/*");
			intent.putExtra("crop", true);
			// X,Y方向上的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// 裁剪区的宽高
			intent.putExtra("outputX", outputX);
			intent.putExtra("outputY", outputY);
			// 是否保留比例
			intent.putExtra("scale", true);
			// 是否将数据保留在Bitmap中返回(注意:小图片可以使用return-data:true的设置,大图片的话建议使用extra_output)
			if (returnData) {
				intent.putExtra("return-data", returnData);
			} else {
				// 设置裁剪后的图片路径覆盖相机拍摄图片路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
			}
			// 裁剪后图片的后缀名
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			// 没有人脸识别
			intent.putExtra("noFaceDetection", true);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取缩略图
	 *
	 * @param uri    图片路径
	 * @param width  缩略图宽
	 * @param height 缩略图高
	 * @return
	 */
	public Bitmap getThumbnail(Uri uri, int width, int height) {
		Bitmap bitmap = null;
		InputStream input = null;
		try {
			// 内容提供器
			ContentResolver resolver = getContentResolver();
			// 从uri中获取输入流
			input = resolver.openInputStream(uri);
			BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
			// 不返回实际的bitmap
			onlyBoundsOptions.inJustDecodeBounds = true;
			// 图片解码时使用的颜色模式为ARGB_8888
			onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 获取图片
			BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
			// 如果输入流不等于空关闭输入流
			if (input != null) {
				input.close();
			}
			// 生成的图片的宽高出现错误，返回图片
			if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
				return bitmap;
			}
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			// 图片压缩比例
			bitmapOptions.inSampleSize = calculateSampleSize(onlyBoundsOptions, width, height);
			// 图片解码时使用的颜色模式为ARGB_8888
			bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
			input = resolver.openInputStream(uri);
			// 获取图片
			bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		} catch (Exception e) {
			return bitmap;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		return bitmap;
	}

	/**
	 * 计算bitmap的缩放比例
	 *
	 * @param options
	 * @param reqWidth  目标尺寸宽
	 * @param reqHeight 目标尺寸高
	 * @return
	 */
	public int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 原始图片的宽高
		final int height = options.outHeight;
		final int width = options.outWidth;
		// 压缩比例
		int inSampleSize = 1;
		if (height > reqHeight || width > height) {
			// 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的isSampleSize的最大值
			while ((height / inSampleSize) > reqHeight && (width / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
}
