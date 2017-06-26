package com.gogostar.enstory;

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

import static android.widget.Toast.makeText;

/**
 * Created by Administrator on 2017/2/16.
 */

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnTouchListener {

	// 定义变量
	private ImageView btn_login, btn_cancel, btn_reg;
	private EditText edit_account, edit_password;

	private JSONObject jsonObject;
	private MyUtil myUtil = new MyUtil();
	private LocalBroadcastManager localBroadcastManager;

	// 定义接口常量
	private final String LOGIN_URL = "http://123.57.60.247:802/api/User_Login";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		edit_account = (EditText) findViewById(R.id.login_account_edit);
		edit_password = (EditText) findViewById(R.id.login_password_edit);
		btn_reg = (ImageView) findViewById(R.id.login_reg);
		btn_login = (ImageView) findViewById(R.id.login_confirm);
		btn_cancel = (ImageView) findViewById(R.id.login_cancel);

		// 绑定点击事件
		btn_login.setOnTouchListener(this);
		btn_cancel.setOnTouchListener(this);
		btn_reg.setOnTouchListener(this);
	}

	/**
	 * 定义访问接口方法
	 */
	private void postJSON() {
		// 获取输入的账号和密码
		String strAccount = edit_account.getText().toString();
		String strPassword = edit_password.getText().toString();
		if (strAccount.equals("")) {
			makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
			return;
		}
		if (strPassword.equals("")) {
			makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!strAccount.equals("") && !strPassword.equals("")) {
			try {
				Message message = new Message();
				message.what = 1;

				// 定义post的JSON
				jsonObject = new JSONObject();
				jsonObject.put("UserName", edit_account.getText().toString());
				jsonObject.put("Password", myUtil.getMD5(edit_password.getText().toString()));
				// 获取从接口返回的JSON
				JSONObject resultJSON = myUtil.sendByPost(LOGIN_URL, jsonObject);
				// 如果返回的JSON不为空
				if (resultJSON != null) {
					String result = resultJSON.getString("Result");
					// 如果登录失败，发送handler
					if (result.equals("False")) {
						mHandler.sendMessage(message);
					} else if (result.equals("True")) {
						// 登录成功，从JSON中获取返回的账号信息
						JSONObject content = resultJSON.getJSONObject("Content");
						int id = Integer.valueOf(content.getString("ID"));
						String account = content.getString("Account");
						String password = content.getString("Password");
						String name = content.getString("Nickname");
						String contact = content.getString("Contact");
						int level = Integer.valueOf(content.getString("Level"));
						int coin = Integer.valueOf(content.getString("Coin"));
						String note = content.getString("Note");
						boolean valid = Boolean.valueOf(content.getString("Valid"));
						String registerTime = content.getString("RegisterTime");
						String lastAccess = content.getString("LastAccess");
						String headerImgURL = content.getString("HeadImage");

						// 将返回的账号信息存储到SharedPreferences
						SharedPreferences.Editor editor = getSharedPreferences("Login",
								MODE_PRIVATE).edit();
						editor.putInt("ID", id);
						editor.putString("Account", account);
						editor.putString("Password", password);
						editor.putString("Name", name);
						editor.putString("Contact", contact);
						editor.putInt("Level", level);
						editor.putInt("Coin", coin);
						editor.putString("Note", note);
						editor.putBoolean("Valid", valid);
						editor.putString("RegisterTime", registerTime);
						editor.putString("LastAccess", lastAccess);
						editor.putString("HeadImage", headerImgURL);
						editor.putBoolean("isFirstRun", false);
						editor.commit();

						// 发送本地广播
						localBroadcastManager = LocalBroadcastManager.getInstance(this);
						Intent intent = new Intent("com.gogostar.enstory.LOCAL_BROADCAST");
						intent.putExtra("isLogin", true);
						localBroadcastManager.sendBroadcast(intent);
						finish();
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
			Log.d("LoginActivity", jsonObject.toString());
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 提示登录失败
					Toast toast = Toast.makeText(LoginActivity.this, "登陆失败！请重试", Toast
							.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
			}
		}
	};

	// 在子线程中访问接口
	Runnable postRequest = new Runnable() {
		@Override
		public void run() {
			postJSON();
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			// 登录按钮的Touch事件
			case R.id.login_confirm:
				// 播放点击音效
				myUtil.playSound(LoginActivity.this, R.raw.click);
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						btn_login.setImageResource(R.drawable.login_btn1);
						break;
					case MotionEvent.ACTION_UP:
						btn_login.setImageResource(R.drawable.login_btn2);
						new Thread(postRequest).start();
						break;
					default:
						break;
				}
				break;
			// 取消按钮的Touch事件
			case R.id.login_cancel:
				myUtil.playSound(LoginActivity.this, R.raw.click);
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						btn_cancel.setImageResource(R.drawable.quxiao_2);
						break;
					case MotionEvent.ACTION_UP:
						btn_cancel.setImageResource(R.drawable.quxiao_1);
						finish();
						break;
					default:
						break;
				}
				break;
			// 注册按钮的Touch事件
			case R.id.login_reg:
				myUtil.playSound(LoginActivity.this, R.raw.click);
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						btn_reg.setImageResource(R.drawable.zhuce_2);
						break;
					case MotionEvent.ACTION_UP:
						btn_reg.setImageResource(R.drawable.zhuce_1);
						Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//						startActivityForResult(intent, 1);
						startActivity(intent);
						finish();
						break;
					default:
						break;
				}
			default:
				break;
		}
		return true;
	}
}
