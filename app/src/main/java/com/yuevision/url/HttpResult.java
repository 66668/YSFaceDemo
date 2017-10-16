package com.yuevision.url;

import com.yuevision.tools.MyException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 访问服务端的响应对象
 * @author JackSong
 *
 */
public class HttpResult {
	
	public Object returnObject;
	public int Status = 0;
	public String Message = "";
	public JSONObject jsonObject;
	public JSONArray jsonArray;

	public static HttpResult createError(String msg) {
		HttpResult result = new HttpResult();
		result.Status = 1;
		result.Message = msg;
		return result;
	}

	public static HttpResult createError(Exception ex) {
		return createError(ex.getMessage());
	}

	// UserHelper调用该方法
	public boolean hasError() {
		return Status == 0;
	}

	// 抛异常
	public MyException getError() {
		return new MyException(Message, Status);
	}
}
