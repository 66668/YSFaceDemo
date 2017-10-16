package com.yuevision;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuevision.annotation.ViewInject;
import com.yuevision.savecut.ConfigUtil;
import com.yuevision.tools.Loading;
import com.yuevision.tools.MyApplication;
import com.yuevision.tools.PageUtil;
import com.yuevision.url.HttpUtils;
import com.yuevision.url.WebUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity {
	// 登录
	@ViewInject(id = R.id.btnlogin, click = "btn_login")
	Button btn_login;
	// 账号
	@ViewInject(id = R.id.et_companyID)
	EditText et_companyName;
	// 密码
	@ViewInject(id = R.id.et_pswd)
	EditText et_pswd;

	String str_companyName;
	String str_pswd;
	/**
	 * 暂定，需要根据登录决定
	 */
	private String CompanyID = "CompanyID=1";// 公司数据库类别
	private String strGroupID;
	private final int LOGIN_SUCESS = 1001; // 登陆成功的常量
	private final int LOGIN_FALSE = 1011; // 登陆失败的常量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		setContentView(R.layout.activity_login);

		// 中断保存： 使用SharePreferences方式，当程序中途退出，再返回后，数据不丢失
		ConfigUtil configUtil = new ConfigUtil(this);
		et_companyName.setText(configUtil.getAccount());// setAccout()赋值是在UserHelper中

		// 如果有上次没写完的内容，聚焦继续编辑
		if (et_companyName.getText().toString().length() > 0) {
			et_pswd.setFocusable(true);
			et_pswd.requestFocus();
			et_pswd.setFocusableInTouchMode(true);
		}
//		// 连接人脸数据库
//		new GetFaceDB().execute();
	}

	/**
	 * 跳转: 注册界面
	 */
	public void btn_login(View view) {
		// //非空验证
		// if(!checkInput()){
		// return;
		// }
		//
		// str_companyName = et_companyName.getText().toString().trim();
		// str_pswd =et_pswd.getText().toString().trim();
		// //线程处理登录，run（e1,e2）在Loading的run方法中运行
		// Loading.run(this, new Runnable() {
		// @Override
		// public void run() {
		// try {
		// //访问服务端：获取某个公司用户自己的数据库信息
		// UserHelper.loginOnline(LoginActivity.this,
		// str_companyName,//账户
		// str_pswd);//密码
		// MyApplication.getInstance().setIsLogin(true);//登录成功，赋值true
		// //登陆成功，消息处理
		// sendMessage(LOGIN_SUCESS);//1001,
		// } catch (MyException e) {
		// e.printStackTrace();
		// //登录判断
		// if(TextUtils.isEmpty(e.getMessage())){
		// //--BaseActivity的ui处理
		// sendToastMessage("用户名或密码错误!");
		// }else{
		// sendToastMessage(e.getMessage());//false=非空
		// }
		// }
		// }
		// });
		
		//获取人脸库
		Loading.run(this, new Runnable() {
			@Override
			public void run() {
				String result = "";
				// 访问服务器数据库
				result = HttpUtils.doGet(WebUrl.User.FACEDBGET + CompanyID);
				
				if (result == null || result.equals("")) {
					sendMessage(LOGIN_FALSE);
				} else {
					//处理js
					String strResult = result.substring(1, result.length() - 1).replace("\\", "");
					try {
						JSONObject j = new JSONObject(strResult);
						JSONArray js = new JSONArray(j.getString("groupObjs"));
						if (js.length() > 0) {
							JSONObject jj = js.getJSONObject(0);
							// 将获取的数值显示
							strGroupID = jj.getString("groupID");
							sendMessage(LOGIN_SUCESS);
						} else {
							sendMessage(LOGIN_FALSE);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	// 重写父类BaseActivity的方法，消息处理
	@Override
	protected void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case LOGIN_SUCESS: // 1001
			PageUtil.DisplayToast("已连接数据库！");
			this.finish();// 页面注销
			MyApplication.getInstance().setIsLogin(true);
			Intent intent = new Intent(this, RegisterActivity.class);
			intent.putExtra("strGroupID", strGroupID);
			startActivity(intent);
			break;
		case LOGIN_FALSE: // 1011
			MyApplication.getInstance().setIsLogin(false);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示：");
			builder.setMessage("没有连接 数据库，请再次登录！");
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		default:
			break;
		}
	}

	
	
	/**
	 * 双击back退出
	 */
	private static Boolean isExit = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Timer timer = null;
			if (isExit == false) {
				isExit = true; // 准备退出
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						isExit = false; // 取消退出
					}
				}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}

	/**
	 * 非空验证
	 */
	private boolean checkInput() {
		if (TextUtils.isEmpty(et_companyName.getText())) {
			PageUtil.DisplayToast(R.string.LoginActivity_companyID);// 请输入账号！
			return false;
		}
		if (TextUtils.isEmpty(et_pswd.getText())) {
			PageUtil.DisplayToast(R.string.LoginActivity_pswd);// 请输入密码!
			return false;
		}
		return true;
	}
}
