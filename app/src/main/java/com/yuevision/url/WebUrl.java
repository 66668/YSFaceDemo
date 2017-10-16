package com.yuevision.url;

public class WebUrl {
	public static final String MAINURL = "api/Attend/";
	/**
	 * 跟接口地址
	 */
	private static final String API_URL = "http://192.168.1.127:2016/";
	public class User{
		/**
		 *  人脸注册地址
		 */
		public static final String URLPOST =API_URL + MAINURL + "RegAttFace";//http://192.168.1.127:2017/api/Attend/RegAttFace
		/**
		 *  人脸库接口
		 */
		//http://192.168.1.127:2017/api/Attend/GetGroupByCyID?
		public static final String FACEDBGET = API_URL + MAINURL + "GetGroupByCyID?";
		/**
		 * 采集端接口
		 */
		public static final String CAPTUREURL = API_URL + MAINURL +"CapComFaceGpApp?"+"strCompanyID=1";//http://192.168.1.127:2016/api/Attend/CapComFaceGpApp?strCompanyID=1
		/**
		 * 登录地址
		 */
		public static final String LOGINURL = API_URL + MAINURL +"";
	}
}
