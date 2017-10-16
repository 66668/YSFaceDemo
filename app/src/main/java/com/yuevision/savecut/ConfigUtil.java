package com.yuevision.savecut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yuevision.tools.UserManagers;
/**
 * 中断保存设置(需要大改)
 * @author JackSong
 *
 */
public class ConfigUtil {
	protected SharedPreferences sp;
	protected SharedPreferences.Editor editor;
	
	
	private static final String USERID = "userid";
	private static final String ACCOUNT = "account";
	private static final String IS_FIRST_USE = "is_first_use";
	private static final String AUTO_LOGIN = "auto_login";
	private static final String MEMBER_ENTITY = "user_entity";
	private static final String PUSH_ID = "push_id";
	
	private static final String IF_NO_DIATURBING = "if_no_diaturbing";
	private static final String NO_DIATURBING_START_TIME = "no_diaturbing_start_time";
	private static final String NO_DIATURBING_END_TIME = "no_diaturbing_end_time";

	//登录内容保存
	@SuppressLint("CommitPrefEdits")
	public ConfigUtil(Context context) {
		try {
			//获取SharePreferences实例，保存位置是config.xml文件，模式是私有模式
			//文件存放在/data/data/<package name>/shared_prefs/config.xml
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			//获得编辑器 ,方便将内容添加到Sharepreferences中
			editor = sp.edit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//充值结果
	public void resetConfig() {
		setUserId(null);
		setAccount("");
		setIsFirstUse(true);
		setAutoLogin(true);
	}
	//调用该方法，将值保存
	public void put(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}
	//获取值，没有对应值返回""
	public String get(String key) {
		return sp.getString(key, "");
	}
	//保存boolean值
	public void putBoolean(String key, Boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}
	//返回boolean值，没有对应值，返回false
	public Boolean getBoolean(String key) {
		return sp.getBoolean(key, false);
	}
	
	/**
	 * 将js转换成对象，需要调用外部jar包，
	 * 获取所有当前用户的信息
	 */
	public UserManagers getUserEntity() {
		//返回member_entity的信息，没有就返回null
		String string = sp.getString(MEMBER_ENTITY, null);
		if(string!=null && string.length()>0){
			try {
				return new Gson().fromJson(string,UserManagers.class);
			} catch (Exception e) {
				e.printStackTrace(); 
			} 
		}
		return null;
	}
	//保存对象，转换成js格式保存，需要调用外部jar包
	//CreateUserActivity--UserHelper--ConfigUtil该方法
	public void setUserEntity(UserManagers userManagers) {
		editor.putString(MEMBER_ENTITY, new Gson().toJson(userManagers));//"member_entity"
		editor.commit();
	}
	
	
	//返回用户名
	public String getUserId() {
		return sp.getString(USERID, null);//userid
	}
	//保存用户名
	public void setUserId(String id) {
		editor.putString(USERID, id);
		editor.commit();
	}
	//从SharePrefrences存储中获取保存的内容，获取ACCOUNT对应的值-->如果不存在就返回""
	//在LoginActivity中调用
	public String getAccount() {
		return sp.getString(ACCOUNT, "");
	}
	//保存用户名/UserHelper中保存登录时的用户名
	public void setAccount(String account) {
		editor.putString(ACCOUNT, account);
		editor.commit();
	}
	//返回自动登录状态
	public boolean getAutoLogin() {
		return sp.getBoolean(AUTO_LOGIN, true);
	}
	//保存登录状态
	//MainActivity--UserHelper--ConfigUtil该方法：若是点击退出，就不再自动自动登录
	public void setAutoLogin(boolean play) {
		editor.putBoolean(AUTO_LOGIN, play);
		editor.commit();
	}
	//返回 是否第一次使用
	public boolean getIsFirstUse() {
		return sp.getBoolean(IS_FIRST_USE, true);//is_first_use
	}
	//保存第一次登录状态
	public void setIsFirstUse(boolean isFirstUse) {
		editor.putBoolean(IS_FIRST_USE, isFirstUse);
		editor.commit();
	}

	public boolean getIfNoDiaturbing() {
		return sp.getBoolean(IF_NO_DIATURBING, false);
	}
	
	public void setIfNoDiaturbing(boolean ifNoDiaturbing){
		editor.putBoolean(IF_NO_DIATURBING, ifNoDiaturbing);
		editor.commit();
	}

	public String getNoDiaturbingStartTime() {
		return sp.getString(NO_DIATURBING_START_TIME,"23:00");
	}
	
	public void setNoDiaturbingStartTime(String noDiaturbingStartTime){
		editor.putString(NO_DIATURBING_START_TIME, noDiaturbingStartTime);
		editor.commit();
	}


	public String getNoDiaturbingEndTime() {
		return sp.getString(NO_DIATURBING_END_TIME,"8:00");
	}
	
	public void setNoDiaturbingEndTime(String noDiaturbingEndTime){
		editor.putString(NO_DIATURBING_END_TIME, noDiaturbingEndTime);
		editor.commit();
	}

	public void setPushId(String pushId) {
		editor.putString(PUSH_ID, pushId);
		editor.commit();
	}
	
	public String getPushId() {
		return sp.getString(PUSH_ID, "");
	}
	
	
}
