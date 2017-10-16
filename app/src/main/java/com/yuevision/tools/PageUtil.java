package com.yuevision.tools;

import android.widget.Toast;
/**
 *吐司 
 * @author JackSong
 *
 */
public class PageUtil {
	public static void DisplayToast(String loginactivityCompanyid) {
		Toast toast = Toast.makeText(MyApplication.getInstance(), loginactivityCompanyid,Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void DisplayToast(int loginactivityCompanyid) {
		Toast toast = Toast.makeText(MyApplication.getInstance(), loginactivityCompanyid,Toast.LENGTH_SHORT);
		toast.show();
	}
}
