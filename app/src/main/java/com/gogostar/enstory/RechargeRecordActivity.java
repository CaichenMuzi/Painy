package com.gogostar.enstory;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */

/**
 * 充值记录页面
 */
public class RechargeRecordActivity extends BaseActivity {

	private RecyclerView recyclerView;
	private List<RechargeInfo> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_record);

		initData();
		recyclerView = (RecyclerView) findViewById(R.id.recharge_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new RecyclerAdapter(mDatas));
	}

	/**
	 * 虚构的数据
	 */
	protected void initData() {
		mDatas = new ArrayList<RechargeInfo>();
		Date date = new Date();
		RechargeInfo rechargeInfo1 = new RechargeInfo("20170215", 100 + "", 1000);
		RechargeInfo rechargeInfo2 = new RechargeInfo("20170215", 200 + "", 2000);
		RechargeInfo rechargeInfo3 = new RechargeInfo("20170215", 300 + "", 3000);
		mDatas.add(rechargeInfo1);
		mDatas.add(rechargeInfo2);
		mDatas.add(rechargeInfo3);
	}

	/**
	 * RecyclerView适配器
	 */
	class RecyclerAdapter extends RecyclerView.Adapter {

		class ViewHolder extends RecyclerView.ViewHolder {
			TextView textView1, textView2, textView3;

			public ViewHolder(View view) {
				super(view);
				textView1 = (TextView) view.findViewById(R.id.recharge_date);
				textView2 = (TextView) view.findViewById(R.id.recharge_money);
				textView3 = (TextView) view.findViewById(R.id.recharge_GO);
			}
		}

		public RecyclerAdapter(List<RechargeInfo> rechargeInfoList) {
			mDatas = rechargeInfoList;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
					.recharge_record_item, parent, false);
			ViewHolder holder = new ViewHolder(view);
			return holder;
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
			RechargeInfo item = mDatas.get(position);
			ViewHolder holder = (ViewHolder) vh;

			holder.textView1.setText(item.getDate());
			holder.textView2.setText(item.getMoney());
			holder.textView3.setText(item.getNumber() + "");
		}

		@Override
		public int getItemCount() {
			return mDatas.size();
		}
	}
}
