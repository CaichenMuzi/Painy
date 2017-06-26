package com.gogostar.enstory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2017/2/15.
 */

/**
 * 星星币界面
 */
public class GoMoneyActivity extends BaseActivity implements View.OnClickListener {

	// 定义Button
	private Button btn_recharge, btn_recharge_record, btn_consumption_record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gomoney);

		// 查找控件
		btn_recharge = (Button) findViewById(R.id.recharge);
		btn_recharge_record = (Button) findViewById(R.id.recharge_record);
		btn_consumption_record = (Button) findViewById(R.id.consumption_record);

		// 绑定点击事件
		btn_recharge.setOnClickListener(this);
		btn_recharge_record.setOnClickListener(this);
		btn_consumption_record.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.recharge:
				// 打开充值页面
				Intent intent1 = new Intent(GoMoneyActivity.this, RechargeActivity.class);
				startActivity(intent1);
				break;
			case R.id.recharge_record:
				// 充值记录页面
				Intent intent2 = new Intent(GoMoneyActivity.this, RechargeRecordActivity.class);
				startActivity(intent2);
				break;
			case R.id.consumption_record:
				// 消费记录页面
				Intent intent3 = new Intent(GoMoneyActivity.this, ConsumptionActivity.class);
				startActivity(intent3);
				break;
			default:
				break;
		}
	}
}
