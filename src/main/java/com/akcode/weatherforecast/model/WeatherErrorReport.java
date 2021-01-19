package com.akcode.weatherforecast.model;

import java.util.Date;

import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherErrorReport {
	
	Date timeStamp;
	
	int code;
	String message;
	String path;
	
	JSONObject errorJson;
	
	@Override
	public String toString() {
		
		errorJson = new JSONObject();
		
		errorJson.put("timestamp", timeStamp);
		errorJson.put("status",code);
		errorJson.put("error",message);
		errorJson.put("path",path);
		
		return errorJson.toString();
		
//		return "WeatherErrorReport [timeStamp=" + timeStamp + ", code=" + code + ", message=" + message + ", path="
//				+ path + "]";
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
