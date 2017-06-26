package com.gogostar.gogostroydiy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 * <p>
 * 横向滑动ListView的适配器
 */

public class HorizontalListViewAdapter extends BaseAdapter {

	// 定义变量
	private List<CategoryInfo> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	private int selectIndex = 0;

	/**
	 * 构造函数
	 *
	 * @param context 当前页面
	 * @param list    CategoryInfo信息集合
	 */
	public HorizontalListViewAdapter(Context context, List<CategoryInfo> list) {
		this.mContext = context;
		this.mList = list;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			// 查找控件
			convertView = mInflater.inflate(R.layout.horizontal_item, null);
			holder.mTitle = (TextView) convertView.findViewById(R.id.horizontal_tv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 如果convertView被选中，设置背景色
		if (position == selectIndex) {
			convertView.setSelected(true);
			convertView.setBackgroundResource(R.drawable.on);
		} else {
			convertView.setSelected(false);
			convertView.setBackgroundResource(R.drawable.off);
		}

		holder.mTitle.setText(mList.get(position).getName());

		return convertView;
	}

	/**
	 * 自定义ViewHolder
	 */
	private static class ViewHolder {
		private TextView mTitle;
	}

	/**
	 * 定义设置选中的下标的方法
	 *
	 * @param i
	 */
	public void setSelectIndex(int i) {
		selectIndex = i;
	}
}
