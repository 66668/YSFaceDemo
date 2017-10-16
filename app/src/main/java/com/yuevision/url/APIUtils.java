package com.yuevision.url;

import android.util.Log;

import com.yuevision.tools.MyException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 访问服务端类
 * 
 * @author JackSong
 *
 */
public class APIUtils {

	/**
	 * Get请求，获取人脸库 返回数据str（已使用，很好用2016.4.20） 获取人脸库
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	private static final int TIMEOUT_IN_MILLIONS = 3000;

	// 001
	public static String GetFaceDB(String faceDBURL) throws MyException {
		return GetFaceDB(faceDBURL,true);
	}

	// 002
	public static String GetFaceDB(String faceDBURL,Boolean flag) throws MyException {
		String jsonObject = "";
		try {
			//访问服务端，获取人脸库
			jsonObject = HttpUtils.getInstance().getJSONObject(faceDBURL);//返回响应//JsonObject
			Log.d("HTTP", jsonObject.toString());// 查看响应的信息
			return jsonObject;
		} catch (MyException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
//			return HttpResult.createError(e);
		}
		return jsonObject;
	}

	/**
	 * UserHelper在线登录
	 */
	// 01LoginActivity--UserHelper--APIUtils//登录
	public static HttpResult postForObject(String url, HttpParameter parameters) throws MyException {
		// 调用下边方法,登录
		return postForObject(url, parameters, true);
	}

	// 02UserHelper在线登录
	public static HttpResult postForObject(String url, HttpParameter parameters, boolean withLogin) throws MyException {
		try {
			// 登录服务器方法，并返回响应/注册方法，并返回响应/获取验证码，并返回响应
			JSONObject jsonObject = HttpUtils.getInstance().postForm(url, parameters, withLogin); // true
			Log.d("HTTP", jsonObject.toString());// 查看响应的信息
			// 调用本类方法，返回读取的信息，封装在HttpResult中返回给调用方法(登录/注册/验证码)
			return null; // 将JSONObject对象-->HttpResult
		} catch (MyException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 人脸注册
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws MyException
	 */
	public static String  facePostForObject(String url, Map<String, String> params, File filePath)
			throws IOException, JSONException, MyException {
		String jsonObject = "";
		try {
			//服务端 
			jsonObject = HttpUtils.getInstance().facePost(url, params, filePath); // true
			Log.d("HTTP", jsonObject.toString());// 查看响应的信息
			//03
		} catch (Exception e) {
			e.printStackTrace();
//			return HttpResult.createError(e);
		}
		return jsonObject;
	}
}
