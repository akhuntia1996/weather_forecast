package com.akcode.weatherforecast.model;

import org.springframework.stereotype.Component;

@Component
public class WeatherData {
	
	WeatherDetails weatherDetails;
	WeatherErrorReport weatherErrorReport;
	WeatherLocation weatherLocation;
	
	public WeatherDetails getWeatherDetails() {
		return weatherDetails;
	}
	public void setWeatherDetails(WeatherDetails weatherDetails) {
		this.weatherDetails = weatherDetails;
	}
	public WeatherErrorReport getWeatherErrorReport() {
		return weatherErrorReport;
	}
	public void setWeatherErrorReport(WeatherErrorReport weatherErrorReport) {
		this.weatherErrorReport = weatherErrorReport;
	}
	public WeatherLocation getWeatherLocation() {
		return weatherLocation;
	}
	public void setWeatherLocation(WeatherLocation weatherLocation) {
		this.weatherLocation = weatherLocation;
	}	
}
