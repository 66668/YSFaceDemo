package com.yuevision.tools;

import java.lang.reflect.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonNull;

/**
 * 自定义类，从js中获取需要的数值
 * @author JackSong
 *
 */
public class JSONUtils {
	// 采取单例模式
	private static Gson gson = new Gson();

	private JSONUtils() {
	}

	/**
	 * @MethodName : toJson
	 * @Description : 将对象转为JSON串，此方法能够满足大部分需求
	 * @param src
	 *            :将要被转化的对象
	 * @return :转化后的JSON串
	 */
	public static String toJson(Object src) {
		if (src == null) {
			return gson.toJson(JsonNull.INSTANCE);
		}
		return gson.toJson(src);
	}

	/**
	 * @MethodName : fromJson
	 * @Description : 用来将JSON串转为对象，但此方法不可用来转带泛型的集合
	 * @param json
	 * @param classOfT
	 * @return
	 */
	public static <T> Object fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, (Type) classOfT);
	}

	/**
	 * @MethodName : fromJson
	 * @Description : 用来将JSON串转为对象，此方法可用来转带泛型的集合，如：Type为 new
	 *              TypeToken<List<T>>(){}.getType()
	 *              ，其它类也可以用此方法调用，就是将List<T>替换为你想要转成的类
	 * @param json
	 * @param typeOfT
	 * @return
	 */
	public static Object fromJson(String json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static JSONObject getJsonObject(JSONObject json, String key) {
		try {
			if (json.has(key)) {
				Object jsonObj = json.get(key);
				if (jsonObj instanceof JSONObject) {
					return (JSONObject) jsonObj;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static String getString(JSONObject json, String key) {
		return getString(json, key, "");
	}

	public static String getString(JSONObject json, String key,
			String defaultValue) {
		try {
			return json.has(key) ? json.getString(key) : defaultValue;
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}
	
	public static Integer getInt(JSONObject json, String key,Integer defaultValue) {
		try {
			return json.has(key) ? json.getInt(key) : defaultValue;//true，返回int，false,返回0
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}
	//UserHelper用户登录--APIUtils--该方法，返回int数值
	public static Integer getInt(JSONObject json, String key) {
		//调用上边方法，返回int数值
		return getInt(json, key, 0);
	}

	public static Boolean getBoolean(JSONObject json, String key,
			Boolean defaultValue) {
		try {
			return json.has(key) ? json.getBoolean(key) : defaultValue;
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static Boolean getBoolean(JSONObject json, String key) {
		return getBoolean(json, key, false);
	}

	public static Double getDouble(JSONObject json, String key,
			Double defaultValue) {
		try {
			return json.has(key) ? json.getDouble(key) : defaultValue;
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static Double getDouble(JSONObject json, String key) {
		return getDouble(json, key, 0d);
	}

	public static Long getLong(JSONObject json, String key, Long defaultValue) {
		try {
			return json.has(key) ? json.getLong(key) : defaultValue;
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static Long getLong(JSONObject json, String key) {
		return getLong(json, key, 0l);
	}
	
	public static JSONArray getJSONArray(JSONObject json, String key){
		try {
			return json.getJSONArray(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
