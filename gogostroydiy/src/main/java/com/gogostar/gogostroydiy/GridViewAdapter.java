package com.gogostar.gogostroydiy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 * <p>
 * 定义GridView适配器
 */

public class GridViewAdapter extends BaseAdapter {

	// 定义变量
	private Context context;
	private List<PictureInfo> data;

	private MyBitmapUtils myBitmapUtils = new MyBitmapUtils();
//	private MyUtil myUtil = new MyUtil();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 获取从子线程传递过来的数据，并修改UI
					final HandlerUtil handlerUtil = (HandlerUtil) msg.obj;
					handlerUtil.getHolder().iv.setImageBitmap(handlerUtil.getBitmap());
					// 如果课程价格为0，隐藏小锁图案
					if (handlerUtil.getPictureInfo().getPrice() == 0) {
						handlerUtil.getHolder().iv2.setVisibility(View.GONE);
					}
					break;
			}
		}
	};

	/**
	 * 构造函数
	 *
	 * @param context 当前页面
	 * @param data    PictureInfo集合
	 */
	public GridViewAdapter(Context context, List<PictureInfo> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// 选中的PictureInfo信息
		final PictureInfo pictureInfo = data.get(position);
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			// 查找控件
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv1_gridView_item);
			holder.iv2 = (ImageView) convertView.findViewById(R.id.iv2_gridView_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			myBitmapUtils.display(holder.iv, pictureInfo.getPicture_img());
			if (pictureInfo.getPrice() == 0) {
				holder.iv2.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		final ViewHolder finalHolder = holder;
//		final PictureInfo finalPictureInfo = pictureInfo;
//		// 开启子线程
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				HandlerUtil handlerUtil = new HandlerUtil();
//				Bitmap bitmap = null;
//				// 从服务器获取Bitmap
//				bitmap = myUtil.getBitmap(finalPictureInfo.getPicture_img());
//				// 设置handlerUtil
//				handlerUtil.setBitmap(bitmap);
//				handlerUtil.setHolder(finalHolder);
//				handlerUtil.setPictureInfo(finalPictureInfo);
//				// 向handler发送信息
//				mHandler.obtainMessage(1, handlerUtil).sendToTarget();
//			}
//		}).start();

		return convertView;
	}

	/**
	 * 自定义ViewHolder
	 */
	static class ViewHolder {
		ImageView iv;
		ImageView iv2;
	}

	/**
	 * 封装向Handler发送的信息类
	 */
	class HandlerUtil {
		public ViewHolder holder;
		public Bitmap bitmap;
		private PictureInfo pictureInfo;

		public void setHolder(ViewHolder viewHolder) {
			this.holder = viewHolder;
		}

		public ViewHolder getHolder() {
			return holder;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setPictureInfo(PictureInfo pictureInfo) {
			this.pictureInfo = pictureInfo;
		}

		public PictureInfo getPictureInfo() {
			return pictureInfo;
		}
	}
}
