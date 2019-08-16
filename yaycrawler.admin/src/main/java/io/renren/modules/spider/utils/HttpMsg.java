package io.renren.modules.spider.utils;

public class HttpMsg {

	private int responseCode;
	private boolean reachAble;
	private String errorMsg;
	public HttpMsg() {
		
	}
	public HttpMsg(int responseCode,boolean reachAble,String errorMsg) {
		this.responseCode = responseCode;
		this.reachAble = reachAble;
		this.errorMsg = errorMsg;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public boolean isReachAble() {
		return reachAble;
	}
	public void setReachAble(boolean reachAble) {
		this.reachAble = reachAble;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
    
   
}
