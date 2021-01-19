package com.akcode.weatherforecast.model;

import java.util.Date;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherDetails {
	
	Date curDate;
	
	int maxTemp, minTemp;
	String dayPhrase, nightPhrase;
	
	@Override
	public String toString() {
		return "WeatherDetails [curDate=" + curDate + ", maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", dayPhrase="
				+ dayPhrase + ", nightPhrase=" + nightPhrase + "]";
	}
	
	
	public Date getCurDate() {
		return curDate;
	}
	public void setCurDate(Date curDate) {
		this.curDate = curDate;
	}
	public int getMaxTemp() {
		return maxTemp;
	}
	public void setMaxTemp(int maxTemp) {
		this.maxTemp = maxTemp;
	}
	public int getMinTemp() {
		return minTemp;
	}
	public void setMinTemp(int minTemp) {
		this.minTemp = minTemp;
	}
	public String getDayPhrase() {
		return dayPhrase;
	}
	public void setDayPhrase(String dayPhrase) {
		this.dayPhrase = dayPhrase;
	}
	public String getNightPhrase() {
		return nightPhrase;
	}
	public void setNightPhrase(String nightPhrase) {
		this.nightPhrase = nightPhrase;
	}
	
	
	
}
