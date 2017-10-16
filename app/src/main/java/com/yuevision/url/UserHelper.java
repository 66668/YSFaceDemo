package com.yuevision.url;

import android.content.Context;

import com.google.gson.Gson;
import com.yuevision.R;
import com.yuevision.savecut.ConfigUtil;
import com.yuevision.tools.MyApplication;
import com.yuevision.tools.MyException;
import com.yuevision.tools.NetworkManager;
import com.yuevision.tools.PageUtil;
import com.yuevision.tools.UserManagers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理访问服务端信息类 解析js对象，调用Gson外部包：gson-2.2.2.jar
 * 
 * @author JackSong
 *
 */
public class UserHelper {
	static UserManagers currentUserManagers;

	/**
	 * 获取当前用户信息
	 * 
	 * @return
	 */
	public static UserManagers getCurrentUser() {
		// 调用下边的方法
		return getCurrentUser(true);
	}

	public static UserManagers getCurrentUser(boolean isAutoLoad) {

		if (currentUserManagers == null && isAutoLoad) {// 判断MemberModel类是否为空
			// 中断保存
			ConfigUtil config = new ConfigUtil(MyApplication.getInstance());// 中断保存获取信息
			// 获取用户名
			String account = config.getAccount();
			if (!"".equals(account)) {
				// 获取所有当前用户信息，保存到mCurrentUser对象中
				currentUserManagers = config.getUserEntity();
			}
		}
		return currentUserManagers;
	}

	/**
	 * 在线登录LoginActivity--UserHelper该方法
	 */
	public static void loginOnline(Context context, String companyName, String pswd) throws MyException {
		// 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
		if (!NetworkManager.isNetworkAvailable(context)) {
			throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
		}
		// url:
		HttpResult httpResult = APIUtils.postForObject(WebUrl.User.URLPOST,
				HttpParameter.create().add("companyName", companyName).add("Password", pswd));// 用户名和密码封装到map中
		// 如果code！=200,返回code=0companyID
		if (httpResult.hasError()) {
			throw httpResult.getError();
		}
		// Gson.jar包,解析js给对象
		UserManagers uerManagers = new Gson().fromJson(httpResult.jsonObject.toString(), UserManagers.class);// js解析"result"的value值

		// ConfigUtil中断保存，在退出后重新登录用getAccount()调用
		ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
		config.setAccount(companyName);// 保存账户名
		config.setUserEntity(uerManagers);// 保存已经登录成功的对象信息

		currentUserManagers = uerManagers;// 将登陆成功的对象信息，赋值给全局变量
	}

	/**
	 * 获取人脸数据库id
	 * 
	 * @param context
	 * @param companyID
	 * @throws MyException
	 */

	public static String doGetFaceDB(Context context, String companyID) throws MyException {
		// 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
		if (!NetworkManager.isNetworkAvailable(context)) {
			throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
		}
		// url http://192.168.1.127:2016/api/Attend/GetGroupByCyID?CompanyID=1
		String result = APIUtils.GetFaceDB(WebUrl.User.FACEDBGET + companyID);//
		// 如果code！=200,返回code=0
//		if (httpResult.hasError()) {
//			throw httpResult.getError();
//		}
//		// Gson.jar包,解析js给对象
//		UserManagers uerManagers = new Gson().fromJson(httpResult.jsonObject.toString(), UserManagers.class);// js解析"result"的value值
//
//		// ConfigUtil中断保存
//		ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
//		config.setUserEntity(uerManagers);// 保存已经登录成功的对象信息
//		//
//		currentUserManagers = uerManagers;// 将登陆成功的对象信息，赋值给全局变量
		String strGroupID = "";
		if (result == null || result.equals("")) {
			PageUtil.DisplayToast("没有连接数据库！请检查数据库");
		} else {
			//对获取数据处理
			try {
				JSONObject j = new JSONObject(result);
				JSONArray js = new JSONArray(j.getString("groupObjs"));
				if (js.length() > 0) {
					JSONObject jj = js.getJSONObject(0);
					// 将获取的数值显示
					strGroupID = jj.getString("groupID");
					PageUtil.DisplayToast("已连接数据库:"+ jj.getString("groupID"));
				} else {
					PageUtil.DisplayToast("服务器出现问题，请检查服务器设置！");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return strGroupID;
	}

	/**
	 * 注册人脸
	 * 
	 * @throws MyException
	 * @throws JSONException
	 * @throws IOException
	 */
	
	public static String registerOnline(Context context, String strName, String strGender, String strGroupId,
			String strNameNumber, File picPath) throws MyException, IOException, JSONException {
		// 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
		if (!NetworkManager.isNetworkAvailable(context)) {
			throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
		}
		final Map<String, String> params = new HashMap<String, String>();
		params.put("strName", strName);
		params.put("strGender", strGender);
		params.put("strGroupID", strGroupId);
		params.put("strUserCardNo", strNameNumber);
		
		// 访问服务端：http://192.168.1.127:2016/api/Attend/RegAttFace
		String httpResult = APIUtils.facePostForObject(WebUrl.User.URLPOST, // 01url
				params, // 02集合（可以继续加参数）
				picPath);// 03图片路径
		return httpResult;
	}

}
