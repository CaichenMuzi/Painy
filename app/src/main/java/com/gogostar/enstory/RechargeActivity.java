package com.gogostar.enstory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/2/15.
 */

/**
 * 充值界面
 */
public class RechargeActivity extends AppCompatActivity implements View.OnClickListener {

	private Button btn_confirm, btn_cancel;
	private EditText edit_money;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);

		// 绑定控件
		btn_confirm = (Button) findViewById(R.id.recharge_confirm);
		btn_cancel = (Button) findViewById(R.id.recharge_cancel);
		edit_money = (EditText) findViewById(R.id.recharge_edit);

		// 绑定点击事件
		btn_confirm.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.recharge_confirm:
				break;
			case R.id.recharge_cancel:
				finish();
				break;
		}
	}
}
