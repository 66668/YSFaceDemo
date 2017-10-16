package com.yuevision.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
/**
 * 获取存储空间
 * @author JackSong
 *
 */
public class Utils {
	/**
	 * 获取SD卡可用空间
	 * 
	 * @return availableSDCardSpace 可用空间(MB)。-1L:没有SD卡
	 */
	public static long getExternalStorageSpace() {
		long availableSDCardSpace = -1L;
		// 存在SD卡
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			StatFs sf = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			long blockSize = sf.getBlockSize();// 块大小,单位byte
			long availCount = sf.getAvailableBlocks();// 可用块数量
			availableSDCardSpace = availCount * blockSize / 1024 / 1024;// 可用SD卡空间，单位MB
		}

		return availableSDCardSpace;
	}
	/**
	 * 获取机器内部可用空间
	 * 
	 * @return availableSDCardSpace 可用空间(MB)。-1L:没有SD卡
	 */
	//CreateUserActivity--UpdateAvatar--MyApplication--Utils该方法
	public static long getInternalStorageSpace() {
		long availableInternalSpace = -1L;//-1L:没有SD卡

		StatFs sf = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = sf.getBlockSize();// 块大小,单位byte
		long availCount = sf.getAvailableBlocks();// 可用块数量
		availableInternalSpace = availCount * blockSize / 1024 / 1024;// 可用SD卡空间，单位MB

		return availableInternalSpace;
	}
	/**
	 * get the screen width(px) for specific-mobile
	 * 
	 * @param activity
	 * @return px
	 */
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return (dm.widthPixels);
	}
	/**
	 * 获取手机唯一设备号
	 * 
	 * @return
	 */
	private static String deviceId = null;
	public synchronized static String getDeviceId() {
		if (deviceId == null || deviceId.length() == 0) {
			//实例化通讯类对象
			final TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
			//获取Android设备(手机)唯一标识码，注意:获取DEVICE_ID需要注册READ_PHONE_STATE权限
			deviceId = tm.getDeviceId();//IMEI,MEID
		}
		return deviceId;
	}
}

