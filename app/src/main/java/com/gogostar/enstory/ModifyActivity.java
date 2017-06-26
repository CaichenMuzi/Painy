package com.gogostar.enstory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * Created by Administrator on 2017/2/14.
 */

/**
 * 修改资料界面
 */
public class ModifyActivity extends BaseActivity implements View.OnClickListener, View
		.OnTouchListener {

	// 定义变量
	private EditText edit_name, edit_tel, edit_note;
	private ImageView image_head, btn_confirm, btn_cancel;

	private String uName, uTel, uNote;
	private byte[] uHeader;
	private JSONObject jsonObject;

	private SharedPreferences pref;
	private int id;
	private String name, note, contact;

	private LocalBroadcastManager localBroadcastManager;
	private MyUtil myUtil;

	// 定义常量
	private final String MODIFY_URL = "http://123.57.60.247:802/api/User_Modify";
	private String MODIFY_HEADER_URL = "http://123.57.60.247:802/api/User_Picture/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify);

		myUtil = new MyUtil();

		edit_name = (EditText) findViewById(R.id.modify_name_edit);
		edit_tel = (EditText) findViewById(R.id.modify_tel_edit);
		edit_note = (EditText) findViewById(R.id.modify_note_edit);
		image_head = (ImageView) findViewById(R.id.head_image_modify);
		btn_confirm = (ImageView) findViewById(R.id.confirm_modify);
		btn_cancel = (ImageView) findViewById(R.id.cancel_modify);

		btn_cancel.setOnTouchListener(this);
		btn_confirm.setOnTouchListener(this);
		image_head.setOnClickListener(this);

		// 从本地SharedPreferences中获取账号信息
		pref = getSharedPreferences("Login", MODE_PRIVATE);
		id = pref.getInt("ID", -1);
		name = pref.getString("Name", "");
		contact = pref.getString("Contact", "");
		note = pref.getString("Note", "");

		edit_name.setText(name);
		edit_note.setText(note);
		edit_tel.setText(contact);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			// 修改头像的点击事件
			case R.id.head_image_modify:
				Intent intent = new Intent(ModifyActivity.this, ChoiceImageActivity.class);
				startActivityForResult(intent, 1);
			default:
				break;
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
			// 取消按钮的Touch事件
			case R.id.cancel_modify:
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
			// 确认按钮的Touch事件
			case R.id.confirm_modify:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						btn_confirm.setImageResource(R.drawable.queding_2);
						break;
					case MotionEvent.ACTION_UP:
						btn_confirm.setImageResource(R.drawable.queding_1);
						// 在子线程中访问接口，并发送本地广播
						new Thread(new Runnable() {
							@Override
							public void run() {
								postJSON();

								localBroadcastManager = LocalBroadcastManager.getInstance
										(ModifyActivity.this);
								Intent intent = new Intent("com.gogostar.enstory.LOCAL_BROADCAST");
								localBroadcastManager.sendBroadcast(intent);
								finish();
							}
						}).start();
						break;
				}
				break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case RESULT_OK:
				Bitmap bitmap = null;
				// 获取返回的字节数组
				uHeader = data.getByteArrayExtra("image_byte");
				BitmapFactory.Options opts = new BitmapFactory.Options();
				// 如果字节数组不为空将字节数组转换成Bitmap
				if (uHeader != null) {
					if (opts != null) {
						bitmap = BitmapFactory.decodeByteArray(uHeader, 0, uHeader.length, opts);
					} else {
						bitmap = BitmapFactory.decodeByteArray(uHeader, 0, uHeader.length);
					}
				}
				image_head.setImageBitmap(bitmap);
				break;
			default:
				break;
		}
	}

	/**
	 * 自定义Post方法访问接口
	 */
	private void postJSON() {
		SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
		uName = edit_name.getText().toString();
		uTel = edit_tel.getText().toString();
		uNote = edit_note.getText().toString();

		if (uName.equals("")) {
			Toast.makeText(ModifyActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (uTel.equals("")) {
			Toast.makeText(ModifyActivity.this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		jsonObject = new JSONObject();
		try {
			Message message = new Message();
			message.what = 1;

			// 定义post传送的JSON
			jsonObject.put("ID", String.valueOf(id));
			jsonObject.put("Nickname", uName);
			jsonObject.put("Contact", uTel);
			jsonObject.put("Note", uNote);

			// 获取接口返回的JSON
			JSONObject resultJson = myUtil.sendByPost(MODIFY_URL, jsonObject);
			if (resultJson != null) {
				String result = resultJson.getString("Result");
				if (result.equals("False")) {
					// 修改失败，发送handler
					mHandler.sendMessage(message);
				} else if (result.equals("True")) {
					// 修改成功，将修改的信息存储到本地SharedPreferences
					editor.putString("Name", uName);
					editor.putString("Contact", uTel);
					editor.putString("Note", uNote);
//					JSONObject content = resultJson.getJSONObject("Content");
//					Log.d("MODIFY_RESULT", content.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (uHeader != null) {
			// 修改头像接口
			MODIFY_HEADER_URL = MODIFY_HEADER_URL + id;
			// 将扩展名和uHeader合并，扩展名在前
			String string = ".jpg";
			byte[] bytes1 = string.getBytes();
			byte[] bytes = byteMerger(bytes1, uHeader);
			// 获取修改头像返回的JSON
			JSONObject jsonObject1 = myUtil.postBytes(MODIFY_HEADER_URL, bytes);
			if (jsonObject1 != null) {
				try {
					String result = jsonObject1.getString("Result");
					if (result.equals("True")) {
						// 如果修改成功将修改信息存储到本地SharedPreferences
						String headUrl = jsonObject1.getString("Content");
						editor.putString("ImgHeader", headUrl);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		editor.commit();
		Log.d("ModifyActivity", jsonObject.toString());
	}

	/**
	 * 自定义合并两个字节数组方法
	 *
	 * @param byte_1 字节数组1
	 * @param byte_2 字节数组2
	 * @return
	 */
	private byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 提示修改失败
					Toast toast = Toast.makeText(ModifyActivity.this, "修改失败！请重试", Toast
							.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					break;
			}
		}
	};
}
