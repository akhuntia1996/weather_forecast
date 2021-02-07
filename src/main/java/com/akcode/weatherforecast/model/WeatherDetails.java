package com.akcode.weatherforecast.model;

import java.util.Date;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherDetails {
	
	Date curDate;
	
	double maxTemp, minTemp;
	double currTemp;
	String dayPhrase, nightPhrase;
	
	public double getCurrTemp() {
		return currTemp;
	}


	public void setCurrTemp(double d) {
		this.currTemp = d;
	}


	@Override
	public String toString() {
		return "WeatherDetails [curDate=" + curDate + ", maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", currTemp="
				+ currTemp + ", dayPhrase=" + dayPhrase + ", nightPhrase=" + nightPhrase + "]";
	}
	
	
	public Date getCurDate() {
		return curDate;
	}
	public void setCurDate(Date curDate) {
		this.curDate = curDate;
	}
	public double getMaxTemp() {
		return maxTemp;
	}
	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}
	public double getMinTemp() {
		return minTemp;
	}
	public void setMinTemp(double minTemp) {
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
