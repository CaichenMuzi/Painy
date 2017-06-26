package com.gogostar.enstory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/14.
 * <p>
 * ListView适配器
 */

public class ListViewAdapter extends ArrayAdapter<Profiles> {

	private int resourceId;

	/**
	 * 构造函数
	 *
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public ListViewAdapter(Context context, int textViewResourceId, List<Profiles> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Profiles profiles = getItem(position);
		View view;
		final ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) view.findViewById(R.id.list_text);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.textView.setText(profiles.getName());
		return view;
	}

	class ViewHolder {

		TextView textView;
		ImageView imageView;
	}
}
