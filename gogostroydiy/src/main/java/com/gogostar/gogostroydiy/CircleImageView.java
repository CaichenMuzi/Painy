package com.gogostar.gogostroydiy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/2/17.
 * <p>
 * 自定义圆形ImageView控件
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {

	// 填充类型
	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	// 定义图片的常量
	private static final int COLOR_DRAWABLE_DIMENSION = 1;
	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
	// 图片矩形区域
	private final RectF mDrawableRect = new RectF();
	// 边框区域
	private final RectF mBorderRect = new RectF();

	private final Matrix mShaderMatrix = new Matrix();
	// 画笔
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	// 边框颜色和宽度
	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;

	// 图片半径
	private float mDrawableRadius;
	// 边框半径
	private float mBorderRadius;

	private boolean mReady;
	private boolean mSetupPending;

	/**
	 * 三个构造函数
	 */
	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);
		// 通过obtainStyledAttributes 获得一组值赋给 TypedArray,
		// 这一组值来自于res/values/attrs.xml中的name="CircleImageView"的declare-styleable中
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView,
				defStyle, 0);
		// 通过TypedArray提供的一系列方法取得我们在xml里定义的参数
		// 获取边界的宽度
		mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width,
				DEFAULT_BORDER_WIDTH);
		// 获取边界的颜色
		mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
		// 调用 recycle() 回收TypedArray，以便后面重用
		a.recycle();

		// 保证第一次执行setup函数里下面代码要在构造函数执行完毕时调用
		// 在这里ScaleType被强制设定为CENTER_CROP，就是将图片水平垂直居中，进行缩放
		super.setScaleType(SCALE_TYPE);
		mReady = true;
		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	@Override
	public ScaleType getScaleType() {
		return SCALE_TYPE;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (scaleType != SCALE_TYPE) {
			throw new IllegalArgumentException(String.format("ScaleType %s not supported.",
					scaleType));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 如果图片不存在就不画
		if (getDrawable() == null) {
			return;
		}
		// 绘制内圆形，参数内半径，图片画笔为mBitmapPaint
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
		// 如果圆形边缘的宽度不为0，我们还要绘制带边界的外圆形
		if (mBorderWidth != 0) {
			// 参数外圆半径，边界画笔为mBorderPaint
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}

//	public int getBorderColor() {
//		return mBorderColor;
//	}
//
//	public void setBorderColor(int borderColor) {
//		if (borderColor == mBorderColor) {
//			return;
//		}
//
//		mBorderColor = borderColor;
//		mBorderPaint.setColor(mBorderColor);
//		invalidate();
//	}
//
//	public int getBorderWidth() {
//		return mBorderWidth;
//	}
//
//	public void setBorderWidth(int borderWidth) {
//		if (borderWidth == mBorderWidth) {
//			return;
//		}
//
//		mBorderWidth = borderWidth;
//		setup();
//	}

	/**
	 * 以下四个函数都是
	 * 复写ImageView的setImageXxx()方法
	 * 注意这个函数先于构造函数调用之前
	 *
	 * @param bm
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	/**
	 * 获取图片的Bitmap
	 *
	 * @param drawable 资源
	 * @return
	 */
	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		// 如果drawable为空返回空
		if (drawable == null) {
			return null;
		}

		// 强制类型装换返回Bitmap
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				// 获取bitmap
				bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION,
						BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
						.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	/**
	 * 这个函数很关键，进行图片画笔边界画笔（Paint）一些重绘参数初始化
	 * 构建渲染器BitmapShader用Bitmap来填充绘制区域,设置样式以及内外圆半径计算等
	 * 以及调用updateShaderMatrix()函数和 invalidate()函数
	 */
	private void setup() {
		// 因为mReady默认值为false,所以第一次进这个函数的时候if语句为真进入括号体内
		// 设置mSetupPending为true然后直接返回，后面的代码并没有执行。
		if (!mReady) {
			mSetupPending = true;
			return;
		}
		// 防止空指针异常
		if (mBitmap == null) {
			return;
		}
		// 构建渲染器，用mBitmap来填充绘制区域，参数值代表如果图片太小的话就直接拉伸
		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		// 设置图片画笔反锯齿
		mBitmapPaint.setAntiAlias(true);
		// 设置图片画笔渲染器
		mBitmapPaint.setShader(mBitmapShader);
		// 设置边界画笔样式
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		// 画笔颜色
		mBorderPaint.setColor(mBorderColor);
		// 画笔边界宽度
		mBorderPaint.setStrokeWidth(mBorderWidth);
		// 获取原图片的宽高
		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();
		// 设置含边界显示区域，取的是CircleImageView的布局实际大小，为方形，查看xml也就是160dp(240px)  getWidth得到是某个view的实际尺寸
		mBorderRect.set(0, 0, getWidth(), getHeight());
		// 计算圆形带边界部分（外圆）的最小半径，取mBorderRect的宽高减去一个边缘大小的一半的较小值
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() -
				mBorderWidth) / 2);
		// 初始图片显示区域
		mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth,
				mBorderRect.height() - mBorderWidth);
		// 计算内圆的最小半径，即去除边界宽度的半径
		mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

		updateShaderMatrix();
		invalidate();
	}

	/**
	 * 这个函数为设置BitmapShader的Matrix参数，设置最小缩放比例，平移参数
	 * 保证图片损失度最小和始终绘制图片正中央的那部分
	 */
	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);
		// 取最小的缩放比例
		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
			// y轴缩放 x轴平移 使得图片的y轴方向的边的尺寸缩放到图片显示区域
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			// x轴缩放 y轴平移 使得图片的x轴方向的边的尺寸缩放到图片显示区域
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}
		// shader的变换矩阵，我们这里主要用于放大或者缩小
		mShaderMatrix.setScale(scale, scale);
		// 平移
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) +
				mBorderWidth);
		// 设置变换矩阵
		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}
}
