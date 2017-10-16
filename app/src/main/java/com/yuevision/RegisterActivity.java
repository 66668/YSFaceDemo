package com.yuevision;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yuevision.annotation.ViewInject;
import com.yuevision.photo.CameraGalleryUtils;
import com.yuevision.photo.ImageLoadingConfig;
import com.yuevision.tools.Loading;
import com.yuevision.tools.MyApplication;
import com.yuevision.tools.MyException;
import com.yuevision.tools.PageUtil;
import com.yuevision.tools.UserManagers;
import com.yuevision.url.UserHelper;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 需要调用外部jar包：universal-image-loader-1.9.2.jar/异步加载图片
 * 
 * @author JackSong
 *
 */
public class RegisterActivity extends BaseActivity implements CameraGalleryUtils.ChoosePicCallBack {
	// 上传照片
	@ViewInject(id = R.id.Register_btn_snapshot, click = "btnSnapShot")
	Button btnSnapShot;
	// 注册
	@ViewInject(id = R.id.Register_btn_register, click = "btnRegister")
	Button btnRegister;
	// 姓名
	@ViewInject(id = R.id.RegisterActivity_et_name)
	EditText etName;
	// 编号
	@ViewInject(id = R.id.RegisterActivity_et_strUserIDNumber)
	EditText etIDNumber;
	// 性别
	@ViewInject(id = R.id.registerActivity_radiogroup)
	RadioGroup rgroup_gender;
	// 男
	@ViewInject(id = R.id.radioBtn_male)
	RadioButton radioBtn_male;
	// 女
	@ViewInject(id = R.id.radioBtn_female)
	RadioButton radioBtn_female;
	// imageView
	@ViewInject(id = R.id.imageView_picture)
	ImageView imgView;
	//返回登录界面
	@ViewInject(id = R.id.imgBack, click = "backBefore")
	ImageView backBefore;
	
	public static final int FACE_DATABASE_ID = -99;// 人脸库
	public static final int SUCCESS_REGISTER = -89;// 注册
	private CameraGalleryUtils updateAvatarUtil;// 头像上传工具
	// 外部jar包：universal-image-loader-1.9.2.jar/异步加载图片
	private DisplayImageOptions imgOption;
	private ImageLoader imgLoader;

	private String avatarBase64 = "";
	// 注册
	private String strGender;
	private String strGroupID;// 人脸库id
	private File picPath;
	private String code;
	/**
	 * 暂定，需要根据登录决定
	 */
	private String CompanyID = "CompanyID=1";// 公司数据库类别

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		setContentView(R.layout.activity_register);
		//获取人脸库
		Intent intent = getIntent();
		strGroupID = intent.getStringExtra("strGroupID");
		// 自动登录
		if (!MyApplication.getInstance().isLogin()) {
			this.finish();// 没有正确登录就退出该页面
			startActivity(LoginActivity.class);// 跳转到欢迎界面
			return;
		}
		// 性别监听
		rgroup_gender.setOnCheckedChangeListener(listener);
		// 图片处理
		updateAvatarUtil = new CameraGalleryUtils(this, this);// 实例化
		// 全局初始化外部包：个推推送消息使用
		imgLoader = ImageLoader.getInstance();// 实例化
		imgLoader.init(ImageLoaderConfiguration.createDefault(this));
		// 异步加载图片
		imgOption = ImageLoadingConfig.generateDisplayImageOptionsNoCatchDisc(R.drawable.face);
		
	}

	/**
	 * 返回登录界面
	 */
	public void backBefore(View view) {
		
		updateAvatarUtil.backBefore(CameraGalleryUtils.BACKBEFORE);
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void btnRegister(View view) {
		Loading.run(this, new Runnable() {
			@Override
			public void run() {
				try {
					// 对数据处理
					code = UserHelper.registerOnline(RegisterActivity.this,
							etName.getText().toString().trim(), // 姓名
							strGender, // 性别
							strGroupID, // 人脸库
							etIDNumber.getText().toString().trim(), // 编号
							picPath);
					// 消息处理
					sendMessage(SUCCESS_REGISTER);
				} catch (MyException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} // 图片路径
			}
		});
	}

	@Override
	protected void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case SUCCESS_REGISTER:
			if (code != null && !code.equals("")) {
				if (code.trim().equals("1")) {
					new AlertDialog.Builder(this).setTitle("注册成功！" ).setPositiveButton("确定", null).create()
							.show();
				} else if (code.trim().equals("-1")) {
					new AlertDialog.Builder(this).setTitle("注册失败，重新注册！" ).setNegativeButton("取消", null)
							.create().show();
				} else {
					new AlertDialog.Builder(this).setTitle("注册失败，重新注册！" ).setNegativeButton("取消", null)
							.create().show();
				}
			}
			break;
		case FACE_DATABASE_ID:
			PageUtil.DisplayToast("已连接数据库");
			break;
		}
	};

	/**
	 * 拍一张，选一张
	 */
	public void btnSnapShot(View view) {
		updateAvatarUtil.showChoosePhotoDialog(CameraGalleryUtils.IMG_TYPE_CAMERA);// -99,处理弹窗调用
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		updateAvatarUtil.onActivityResultAction(requestCode, resultCode, data);
	}

	/**
	 * 性别监听
	 */
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int id = group.getCheckedRadioButtonId();
			switch (id) {
			case R.id.radioBtn_female:
				strGender = "0";
				break;
			case R.id.radioBtn_male:
				strGender = "1";
				break;
			default:
				break;
			}
		}

	};

	// 图片展示
	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（1）
	@Override
	public void updateAvatarSuccess(int updateType, String picPath, String avatarBase64) {
		imgView.setImageResource(R.drawable.face);// imageView添加图片
		//获取图片路径
		this.picPath = new File(picPath);
		if(!this.picPath.exists()){
			try {
				this.picPath.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (picPath.toLowerCase().startsWith("http")) {// 将字符参数改写成小写的方式
			// 外部jar包方法
			ImageLoader.getInstance().displayImage(picPath, // 已处理图片地址
					imgView, // imageView
					imgOption);// jar包类
		} else {
			ImageLoader.getInstance().displayImage("file://" + picPath, imgView, // imageView
					imgOption);// jar包类
		}
		this.avatarBase64 = avatarBase64;
	}

	@Override
	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（2）
	public void updateAvatarFailed(int updateType) {
		Toast.makeText(this, "头像上传失败", Toast.LENGTH_SHORT).show();// 头像上传失败
	}

	@Override
	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（3）
	public void cancel() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 获取登录用户信息
		initUserMessage();
	}

	// 获取登录用户信息
	public void initUserMessage() {
		// 实例化当前用户
		UserManagers userManagers = UserHelper.getCurrentUser();
		if (userManagers != null) {// 非空
			// 将获取的用户信息可以展示到界面中
		}
	}

	
	//双击退出
	private static Boolean isExit = false;
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Timer tExit = null;
			if (isExit == false) {
				isExit = true; // 准备退出
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				tExit = new Timer();
				tExit.schedule(new TimerTask() {
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
}
