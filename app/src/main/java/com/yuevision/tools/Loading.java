package com.yuevision.tools;

import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;

public class Loading {
	// 登录的耗时操作运行，
	public static LoadingDialog run(Context context, boolean cancelable, final Runnable runnable) {
		// 弹窗显示登录状态
		final LoadingDialog loadingDialog = new LoadingDialog(context);// 登录弹窗提示
		loadingDialog.setCanceledOnTouchOutside(false);// 点击弹窗之外的界面，弹窗不消失
		loadingDialog.setCancelable(cancelable);// true:可以按返回键back取消
		loadingDialog.show();
		// 异步线程处理登录状态
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
					// 运行完后，弹窗取消
					loadingDialog.dismiss();
				}
			}
		}).start();
		return loadingDialog;
	}

	// 赋值，跳转上方法运行
	public static LoadingDialog run(Context context, final Runnable runnable) {
		return run(context, true, runnable);
	}

	// ？UserHelper中调用这里报错？
	public interface HttpResult {
		void onSuccess(JSONObject result);

		void onError(Exception e);
	}
	//接口
	public interface Callback {
		//接口方法
		public void update();
	}
}
