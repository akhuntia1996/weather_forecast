package com.akcode.weatherforecast.model;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherLocation {
	
	private String key;
	private String name;
	private double longitude,latitude;

	@Override
	public String toString() {
		return "WeatherLocation [key=" + key + ", name=" + name + ", longitude=" + longitude + ", latitude=" + latitude
				+ "]";
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
