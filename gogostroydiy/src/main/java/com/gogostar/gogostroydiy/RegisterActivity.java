package com.gogostar.gogostroydiy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/2/16.
 */

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity implements View.OnTouchListener {

	// 定义变量
	private EditText edit_account, edit_password, edit_password_again, edit_tel, edit_name,
			edit_note;
	private ImageView btn_cancel, btn_confirm;

	private JSONObject jsonObject;
	private JSONObject resultJSON;

	private String uAccount, uPassword, rePassword, uTel, uNote, uName;

	private MyUtil myUtil;

	private LocalBroadcastManager localBroadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		myUtil = new MyUtil();

		edit_account = (EditText) findViewById(R.id.register_account_edit);
		edit_password = (EditText) findViewById(R.id.register_password_edit);
		edit_password_again = (EditText) findViewById(R.id.register_password_again);
		edit_name = (EditText) findViewById(R.id.register_name_edit);
		edit_tel = (EditText) findViewById(R.id.register_tel_edit);
		edit_note = (EditText) findViewById(R.id.register_note_edit);

		btn_cancel = (ImageView) findViewById(R.id.register_cancel);
		btn_confirm = (ImageView) findViewById(R.id.register_confirm);

		btn_cancel.setOnTouchListener(this);
		btn_confirm.setOnTouchListener(this);
	}

	/**
	 * 定义访问接口方法
	 */
	private void postJSON() {
		// 获取输入的账号信息
		uAccount = edit_account.getText().toString();
		uPassword = edit_password.getText().toString();
		uName = edit_name.getText().toString();
		rePassword = edit_password_again.getText().toString();
		uTel = edit_tel.getText().toString();
		uNote = edit_note.getText().toString();
		// 密码只能为6到16位的数字或字母
		Pattern p = Pattern.compile("^[0-9][a-zA-Z0-9]{6,16}$");
		Matcher matcher = p.matcher(uPassword);
		if (uAccount.equals("")) {
			Toast.makeText(RegisterActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (uPassword.equals("") || matcher.find()) {
			Toast.makeText(RegisterActivity.this, "密码格式错误", Toast.LENGTH_SHORT).show();
			return;
		}
		if (uName.equals("")) {
			Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (uTel.equals("")) {
			Toast.makeText(RegisterActivity.this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!uPassword.equals(rePassword)) {
			Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			// 定义访问接口时传递的参数
			jsonObject = new JSONObject();
			jsonObject.put(Common.ACCOUNT, uAccount);
			jsonObject.put(Common.PASSWORD, myUtil.getMD5(uPassword));
			jsonObject.put(Common.NICK_NAME, uName);
			jsonObject.put(Common.CONTACT, uTel);
			jsonObject.put(Common.NOTE, uNote);

			final Message message = new Message();
			message.what = 1;

			// 获取访问接口返回的JSON
			resultJSON = myUtil.sendByPost(Common.REGISTER_URL, jsonObject);

			if (resultJSON != null) {
				String result = resultJSON.getString(Common.RESULT);
				if (result.equals(Common.IS_RESULT)) {
					// 如果返回的有数据，从JSON中取出账号信息
					JSONObject content = resultJSON.getJSONObject(Common.CONTENT);

					int id = Integer.valueOf(content.getString(Common.ID));
					String account = content.getString(Common.ACCOUNT);
					String password = content.getString(Common.PASSWORD);
					String name = content.getString(Common.NICK_NAME);
					String contact = content.getString(Common.CONTACT);
					int level = Integer.valueOf(content.getString(Common.LEVEL));
					int coin = Integer.valueOf(content.getString(Common.COIN));
					String note = content.getString(Common.NOTE);
					boolean valid = Boolean.valueOf(content.getString(Common.VALID));
					String registerTime = content.getString(Common.REGISTER_TIME);
					String lastAccess = content.getString(Common.LAST_ACCESS);

					// 将账号信息存储到本地
					SharedPreferences.Editor editor = getSharedPreferences(Common.SP_USER_INFO,
							MODE_PRIVATE).edit();
					editor.putInt(Common.ID, id);
					editor.putString(Common.ACCOUNT, account);
					editor.putString(Common.PASSWORD, password);
					editor.putString(Common.NAME, name);
					editor.putString(Common.CONTACT, contact);
					editor.putInt(Common.LEVEL, level);
					editor.putInt(Common.COIN, coin);
					editor.putString(Common.NOTE, note);
					editor.putBoolean(Common.VALID, valid);
					editor.putString(Common.REGISTER_TIME, registerTime);
					editor.putString(Common.LAST_ACCESS, lastAccess);
					editor.putBoolean(Common.IS_FIRST_RUN, false);
					editor.commit();

					Message message1 = new Message();
					message1.what = 2;
					mHandler.sendMessage(message1);

					// 发送本地广播
					localBroadcastManager = LocalBroadcastManager.getInstance(this);
					Intent intent = new Intent(Common.BROADCAST_STR);
					intent.putExtra(Common.IS_LOGIN, true);
					localBroadcastManager.sendBroadcast(intent);
					finish();
				} else {
					mHandler.sendMessage(message);
				}
			} else {
				mHandler.sendMessage(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d("RegisterActivity", jsonObject.toString());
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 提示注册失败
					Toast toast1 = Toast.makeText(RegisterActivity.this, "注册失败！请重试", Toast
							.LENGTH_SHORT);
					toast1.setGravity(Gravity.CENTER, 0, 0);
					toast1.show();
					break;
				case 2:
					// 提示注册成功
					Toast toast2 = Toast.makeText(RegisterActivity.this, "注册成功！", Toast
							.LENGTH_SHORT);
					toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
					break;
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.register_confirm:     // 注册按钮点击事件
				myUtil.playSound(RegisterActivity.this, R.raw.click);
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						btn_confirm.setImageResource(R.drawable.zhuce_2_1);
						break;
					case MotionEvent.ACTION_UP:
						btn_confirm.setImageResource(R.drawable.zhuce_1_1);
						new Thread(new Runnable() {
							@Override
							public void run() {
								postJSON();
							}
						}).start();
						break;
					default:
						break;
				}
				break;
			case R.id.register_cancel:    // 取消按钮点击事件
				myUtil.playSound(RegisterActivity.this, R.raw.click);
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						btn_cancel.setImageResource(R.drawable.quxiao_2);
						break;
					case MotionEvent.ACTION_UP:
						btn_cancel.setImageResource(R.drawable.quxiao_1);
						finish();
						break;
				}
				break;
			default:
				break;
		}
		return true;
	}
}
