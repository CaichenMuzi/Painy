package com.gogostar.enstory;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/3/31.
 * <p>
 * ViewPager适配器
 */

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> viewList;

	/**
	 * 构造函数
	 *
	 * @param viewList View列表
	 */
	public ViewPagerAdapter(List<View> viewList) {
		this.viewList = viewList;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position % viewList.size()));
		return viewList.get(position % viewList.size());
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position % viewList.size()));
	}
}
