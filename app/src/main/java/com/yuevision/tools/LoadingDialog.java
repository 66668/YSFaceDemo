package com.yuevision.tools;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yuevision.R;

/**
 * 登录时有动画显示
 * 
 */
public class LoadingDialog extends Dialog {
	private Context context;
	private int icon = 0;
	private Callback callback = null;
	private boolean cancelable = true;

	// 01 构造赋值
	public LoadingDialog(Context context) {
		// 自定义弹窗形式
		super(context, R.style.LoadingDialog);// 加载自定义资源位置
		this.context = context;
		// 动画效果显示登录状态
		init();
	}

	// 02
	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		init();
	}

	// 03
	public LoadingDialog(Context context, int theme, int icon) {
		super(context, theme);
		this.context = context;
		this.icon = icon;
		init();
	}

	// 04
	public LoadingDialog(Context context, Callback callback, int theme, int icon) {
		super(context, theme);
		this.context = context;
		this.icon = icon;
		this.callback = callback;
		// 调用下方法,动画效果显示登录状态
		init();
	}
	//??
	public boolean isCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}
	void init() {
		LinearLayout contentView = new LinearLayout(context);
		contentView.setMinimumHeight(60);// 最小的高设置
		contentView.setGravity(Gravity.CENTER);
		contentView.setOrientation(LinearLayout.HORIZONTAL);
		ImageView image = null;

		if (icon == -1) {
			return;
		} else {
			// 加载登录图片
			image = new ImageView(context);// 实例化
			image.setImageResource(R.drawable.loading);// 加载图片资源
		}
		// 动画类，向控件中添加动画效果，rotate_repeat.xml是动画布局设置
		// 加载动画布局
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate_repeat);
		// 动画匀速改变
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		image.setAnimation(anim);
		// 布局中添加图片
		contentView.addView(image);
		// 加载该布局
		setContentView(contentView);
	}

	// back返回键响应
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && cancelable) {
			if (null != callback)
				callback.update();
			this.dismiss();
		}
		return true;
	}

	// 接口
	public interface Callback {
		// 接口方法
		public void update();
	}
}
