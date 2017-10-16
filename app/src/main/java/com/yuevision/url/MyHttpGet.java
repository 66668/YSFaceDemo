package com.yuevision.url;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;
import java.util.HashMap;

public class MyHttpGet extends HttpGet{
	public MyHttpGet(){
		super();
		initHeader();
	}
	//CreateUserActivity--ConfigHelper--APIUtils--HttpUtils-MyHttpGet该方法
	public MyHttpGet(String url){
		super(url);
		//调用下边的方法，添加报文信息
		initHeader();
	}
	
	public MyHttpGet(URI uri ){
		super(uri);
		initHeader();
	}
	//获取附加头信息
	public void initHeader(){
		HashMap<String, String> map =  HttpUtils.getInstance().getHeaders();//实例化
		for (String key : map.keySet()) {
			addHeader(key, map.get(key));
		} 
	}
 
}

