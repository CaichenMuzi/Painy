package com.gogostar.enstory;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 * <p>
 * 消费记录界面
 */

public class ConsumptionActivity extends BaseActivity {

	private RecyclerView recyclerView;
	private List<ConsumptionInfo> mDatas;
	private ConsumptionAdapter recyclerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consumption);

		initData();
		recyclerView = (RecyclerView) findViewById(R.id.consumption_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(recyclerAdapter = new ConsumptionAdapter(mDatas));
	}

	/**
	 * 加载数据
	 */
	private void initData() {
		// 虚构的数据
		mDatas = new ArrayList<ConsumptionInfo>();
		ConsumptionInfo rechargeInfo1 = new ConsumptionInfo("20170215", "绘本", 100 + "", 1000 +
				"");
		ConsumptionInfo rechargeInfo2 = new ConsumptionInfo("20170215", "绘本", 200 + "", 2000 +
				"");
		ConsumptionInfo rechargeInfo3 = new ConsumptionInfo("20170215", "绘本", 300 + "", 3000 +
				"");
		mDatas.add(rechargeInfo1);
		mDatas.add(rechargeInfo2);
		mDatas.add(rechargeInfo3);
	}

	/**
	 * RecyclerView适配器
	 */
	class ConsumptionAdapter extends RecyclerView.Adapter {

		/**
		 * ViewHolder类
		 */
		class ViewHolder extends RecyclerView.ViewHolder {
			TextView textView1, textView2, textView3, textView4;

			public ViewHolder(View view) {
				super(view);
				// 消费日期
				textView1 = (TextView) view.findViewById(R.id.consumption_date);
				// 消费物品
				textView2 = (TextView) view.findViewById(R.id.consumption_thing);
				// 消费价格
				textView3 = (TextView) view.findViewById(R.id.consumption_price);
				// 消费金额
				textView4 = (TextView) view.findViewById(R.id.consumption_money);
			}
		}

		// 有参构造
		public ConsumptionAdapter(List<ConsumptionInfo> rechargeInfoList) {
			mDatas = rechargeInfoList;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			// 获取RecyclerView的Item
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
					.consumption_item, parent, false);
			ConsumptionAdapter.ViewHolder holder = new ConsumptionAdapter.ViewHolder(view);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
			ConsumptionInfo item = mDatas.get(position);
			ViewHolder holder = (ViewHolder) vh;

			holder.textView1.setText(item.getDate());
			holder.textView2.setText(item.getThing());
			holder.textView3.setText(item.getPrice());
			holder.textView4.setText(item.getMoney());
		}

		@Override
		public int getItemCount() {
			return mDatas.size();
		}
	}
}
