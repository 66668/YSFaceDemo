package com.yuevision.url;

import android.util.Log;

import com.yuevision.R;
import com.yuevision.tools.MyApplication;
import com.yuevision.tools.MyException;
import com.yuevision.tools.NetworkManager;
import com.yuevision.tools.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 访问网络方法
 * 
 * @author JackSong
 *
 */
public class HttpUtils {
	private static HttpUtils httpUtils = null;
	/**
	 * 超时设置
	 */
	public static final int TIMEOUT = 6000;

	private HttpUtils() {
	}

	public static HttpUtils getInstance() {
		if (null == httpUtils) {
			httpUtils = new HttpUtils();
		}
		return httpUtils;
	}

	/**
	 * 检查网络
	 * 
	 * @throws MyException
	 */
	void checkNetwork() throws MyException {
		if (!NetworkManager.isNetworkAvailable(MyApplication.getInstance())) {
			throw new MyException(R.string.network_invalid);
		}
	}
	/**
	 * Get请求，获取人脸库（已使用，很好用2016.4.20）
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) 
	{
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try
		{
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT);//读取超时
			conn.setConnectTimeout(TIMEOUT);//链接超时
			conn.setRequestMethod("GET");//get
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			
			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else
			{
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
			}
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
			}
			//关闭连接
			conn.disconnect();
		}
		
		return null ;

	}

	/**
	 * 人脸注册：用于注册的post方法:需要上传文本和图片路径
	 * @throws JSONException 
	 */
	private int code;
	public String facePost(String urlPost, Map<String, String> params, File files) throws IOException, JSONException {
		
		String BOUNDARY = java.util.UUID.randomUUID().toString();// 边界标识 随机生成
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FORM_DATA = "multipart/form-data";// 内容类型
		String CHARSET = "UTF-8";

		URL url = new URL(urlPost);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setReadTimeout(5 * 1000);// 缓存最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false);// 不允许缓存
		conn.setRequestMethod("POST");// 请求方式
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charset", "UTF-8");// 设置编码
		conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);
		conn.connect();
		/*
		 * 首先组拼文本类型参数
		 */
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition:form-data;name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes());

		/*
		 * 拼接文件数据
		 */
		if (files != null) {
			StringBuilder sb2 = new StringBuilder();
			sb2.append(PREFIX);
			sb2.append(BOUNDARY);
			sb2.append(LINEND);
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */
			sb2.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + files.getName() + "\"" + LINEND);
			sb2.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
			sb2.append(LINEND);

			outStream.write(sb2.toString().getBytes());

			InputStream is = new FileInputStream(files);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			outStream.write(LINEND.getBytes());
			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
		}

		// 得到响应码:根据公司的js编写
		int res = conn.getResponseCode();
		InputStream in = null;
		
		if (res == 200) {
			in = conn.getInputStream();
			int ch;
			StringBuilder sb3 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb3.append((char) ch);
			}
			String s = sb3.substring(1, sb3.length() - 1).replace("\\", "");// 公司js不标准，{}外头的“”需要消除
			try {
				JSONObject j = new JSONObject(s);
				code = (Integer) j.get("code");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "" + code;
		}
		return "" + code;
	}

	/**
	 * 01 get jsonObject based on url
	 * 
	 * @param url
	 * @return int
	 */
	public int getIntegerResult(final String url) {
		// LogUtil.d("HTTP", "GET: " + url);
		int result = 0;
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		try {
			MyHttpGet get = new MyHttpGet(url);
			HttpResponse response = client.execute(get);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				result = Integer.parseInt(EntityUtils.toString(response.getEntity()));
				// LogUtil.d("HTTP", "Result: " + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 02 get jsonObject based on url()
	 * 
	 * @param url
	 * @return jsonObject
	 * @throws MyException
	 */
	public String getJSONObject(final String url) throws MyException {
		checkNetwork();
		Log.d("HTTP", "GET—FACEID" + url);
		JSONObject jsonObject = null;
		StringBuilder result = new StringBuilder();

		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		try {
			//访问服务端
			MyHttpGet get = new MyHttpGet(url);
			HttpResponse response = client.execute(get);//获取响应
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String responseBody = reader.readLine(); responseBody != null; responseBody = reader.readLine()) {
					result.append(responseBody);
				}
				reader.close();
				String faceResult = "";
				if(result !=null && result.equals("")){
					faceResult = result.toString().substring(1, result.length() - 1).replace("\\", "");
					Log.d("HTTP", "Result_FACEID:result:" +faceResult );
				}
//				jsonObject = new JSONObject(FaceResult);//string转js格式
//				return jsonObject;
				return faceResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			 Log.d("HTTP", "ERROR_FACEID:" + e.getMessage()+e.getStackTrace().toString());
		}
		return null;
	}

	/**
	 * 03 get JSONArray function
	 * 
	 * @param url
	 * @return jsonArray
	 */
	public JSONArray getJSONArray(final String url) {
		Log.d("HTTP", "GET: " + url);
		JSONArray jsonArray = null;
		StringBuilder result = new StringBuilder();
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		try {
			MyHttpGet get = new MyHttpGet(url);
			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String responseBody = reader.readLine(); responseBody != null; responseBody = reader.readLine()) {
					result.append(responseBody);
				}
				reader.close();
				// LogUtil.d("HTTP", "Result: " + result.toString());
				jsonArray = new JSONArray(result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	/**
	 * 04 post JSON data to back end service
	 * 
	 * @param url
	 * @param json
	 * @return boolean
	 */
	public boolean postJSON(final String url, final String json) {
		// LogUtil.d("HTTP", "POST: " + url + " \r\n " + json);
		HttpResponse response = null;
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		try {
			MyHttpPost post = new MyHttpPost(url);
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(entity);
			response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 05 提交数据
	 * 
	 * @param url
	 * @param formData
	 * @return
	 * @throws MyException
	 * @throws DeviceErrorException
	 */
	public JSONObject postForm(final String url, final HashMap<String, String> formData) throws MyException {
		return postForm(url, formData, true);
	}

	public JSONObject postString(final String url, final HashMap<String, Object> formData) throws MyException {
		return postString(url, formData, true);
	}

	/**
	 * 06 使用UrlEncodedForm提交数据
	 * 
	 * @param url
	 * @param formData
	 * @param withLogin
	 * @return
	 * @throws MyException
	 * @throws DeviceErrorException
	 */
	// 01LoginActivity--UserHelper--该方法，登录服务端方法
	public JSONObject postForm(String url, HashMap<String, String> formData, boolean withLogin) throws MyException {
		// 如果传过来的map是空（用户名和密码是空），就会是null,需要实例化
		if (formData == null) {
			formData = new HashMap<String, String>();
		}
		// 第一次进入都没有手机号获取，if(!false)，
		// if (!formData.containsKey("device_id")) {
		// formData.put("device_id", Utils.getDeviceId());// 手机号
		// }
		// 调用下边方法，提交数据给服务端,返回响应
		JSONObject jsonObject = postUrlEncodedForm(url, formData);
		if (withLogin) {// 已经是true
			try {
				if (jsonObject != null && jsonObject.has("status")) {
					int status = jsonObject.getInt("status");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/**
	 * 07 02提交数据(登录调用)
	 * 
	 * @param url
	 * @param formData
	 * @return
	 * @throws MyException
	 */
	protected JSONObject postUrlEncodedForm(final String url, final HashMap<String, String> formData)
			throws MyException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		// LogUtil.d("HTTP","URL:"+url);
		for (String key : formData.keySet()) {
			NameValuePair pair = new BasicNameValuePair(key, formData.get(key));
			list.add(pair);
			// LogUtil.d("HTTP",key+":"+formData.get(key));
		}
		UrlEncodedFormEntity ety;
		try {
			// UrlEncodedFormEntity存放键值，局限性大
			ety = new UrlEncodedFormEntity(list, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		// 调用下边方法，访问服务端，并返回响应
		JSONObject jsonObject = postForm(url, ety);
		return jsonObject;
	}

	// 03登录调用，用于访问服务端，并返回响应：LoginActivity--UserHelper--APIUtils--HttpUtils
	public JSONObject postForm(final String url, final UrlEncodedFormEntity formEntity) throws MyException {
		checkNetwork();
		HttpResponse response = null;
		JSONObject jsonObject = null;
		StringBuilder result = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		try {
			MyHttpPost post = new MyHttpPost(url);
			post.setEntity(formEntity);
			response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				org.apache.http.Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(new BufferedInputStream(is));
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				for (String responseBody = reader.readLine(); responseBody != null; responseBody = reader.readLine()) {
					result.append(responseBody);
				}
				reader.close();
				// LogUtil.d("HTTP", "Result: " + result.toString());
				jsonObject = new JSONObject(result.toString());
				return jsonObject;
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
		} catch (JSONException e) {
			e.printStackTrace();
			throw new MyException(R.string.data_error);// 返回数据错误
		} catch (Exception e) {
			e.printStackTrace();
			throw new MyException(R.string.connect_host_fail);// 连接服务器失败
		} finally {
		}
		return null;
	}

	/**
	 * 使用UrlEncodedForm提交数据
	 *
	 * @param url
	 * @param formData
	 * @param withLogin
	 * @return
	 * @throws MyException
	 * @throws DeviceErrorException
	 */
	public JSONObject postString(String url, HashMap<String, Object> formData, boolean withLogin) throws MyException {
		if (formData == null) {
			formData = new HashMap<String, Object>();
		}
		if (!formData.containsKey("device_id")) {
			formData.put("device_id", Utils.getDeviceId());
		}
		// 调用下边方法，提交数据给服务端,返回响应
		JSONObject jsonObject = postStringData(url, formData);
		if (withLogin) {
			try {
				if (jsonObject != null && jsonObject.has("status")) {
					int status = jsonObject.getInt("status");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/**
	 * 提交数据
	 *
	 * @param url
	 * @param formData
	 *            是否自动登录，若为true，则会在session过期后自动登录
	 * @return
	 * @throws MyException
	 */
	protected JSONObject postStringData(final String url, final HashMap<String, Object> formData) throws MyException {
		JSONObject jsonObject = new JSONObject();
		// LogUtil.d("HTTP","URL:"+url);
		try {
			for (String key : formData.keySet()) {
				// LogUtil.d("HTTP",key+":"+formData.get(key));
				jsonObject.put(key, formData.get(key));
			}
			StringEntity ety = new StringEntity(jsonObject.toString(), "UTF-8");
			return postForm(url, ety);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	// /**
	// * 提交数据
	// *
	// * @param url
	// * @param formData
	// * @return
	// * @throws MyException
	// */
	// protected JSONObject postMultipartForm(final String url, final
	// HashMap<String, String> formData)
	// throws MyException {
	// MultipartEntity ety = new MultipartEntity();
	// if (formData != null) {
	// try {
	// // LogUtil.d("HTTP", "POST: " + url);
	// for (String key : formData.keySet()) {
	// ety.addPart(key, new StringBody(formData.get(key).toString(),
	// Charset.forName("UTF-8")));
	// // LogUtil.d("HTTP", "POST: " + key + " "+
	// // formData.get(key).toString());
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }
	// JSONObject jsonObject = postForm(url, ety);
	// return jsonObject;
	// }

	public JSONObject postForm(final String url, final HttpEntity formEntity) throws MyException {
		checkNetwork();
		HttpResponse response = null;
		JSONObject jsonObject = null;
		StringBuilder result = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
		try {
			MyHttpPost post = new MyHttpPost(url);
			post.setEntity(formEntity);
			response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				org.apache.http.Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(new BufferedInputStream(is));
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				for (String responseBody = reader.readLine(); responseBody != null; responseBody = reader.readLine()) {
					result.append(responseBody);
				}
				reader.close();
				// LogUtil.d("HTTP", "Result: " + result.toString());
				jsonObject = new JSONObject(result.toString());
				return jsonObject;
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			throw new MyException(R.string.network_invalid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MyException(R.string.connect_host_fail);
		} finally {
			// client.getConnectionManager().shutdown();
		}
		return null;
	}

	/**
	 * 对JSON结果统一处理
	 * 
	 * @param jsonObject
	 * @param successRunnable
	 * @param errorRunnable
	 * @return
	 */
	public static String handleResult(JSONObject jsonObject, Runnable successRunnable, Runnable errorRunnable) {
		String result = "";
		try {
			String status = jsonObject.getString("status");
			result = jsonObject.getString("message");
			if (status.equals("1")) {
				if (successRunnable != null) {
					successRunnable.run();
				}
			} else {
				if (errorRunnable != null) {
					errorRunnable.run();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			result = MyApplication.getInstance().getString(R.string.common_error);
		}
		return result;
	}

	/**
	 * 获取附加头信息
	 * 
	 * @return
	 */
	public HashMap<String, String> getHeaders() {
		HashMap<String, String> map = new HashMap<String, String>();

		return map;
	}
}
