package com.akcode.weatherforecast.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akcode.weatherforecast.model.WeatherData;
import com.akcode.weatherforecast.model.WeatherDetails;
import com.akcode.weatherforecast.model.WeatherErrorReport;
import com.akcode.weatherforecast.model.WeatherLocation;

@RestController
public class WeatherController {

	private final String apiKey = "Y4gZPhD3VrtAlAreGWjbwyCZjZo9S73C";

	URL url;
	HttpURLConnection httpURLConnection;

	StringBuilder jsonString;
	String strCurrentLine;

	BufferedReader br = null;

	JSONObject jsonObj;
	JSONArray jsonArr;

	@Autowired
	WeatherLocation weatherLocation;

	@Autowired
	WeatherErrorReport weatherErrorReport;

	@Autowired
	WeatherDetails weatherDetails;

	@Autowired
	WeatherData weatherData;

	@RequestMapping(value = "/submitLocation")
	public String getCurrentLocation() {
		return null;
	}

	@RequestMapping(value = "/submitLocation", method = RequestMethod.POST)
	public String submitLocation(@RequestParam("placeName") String placeName) throws IOException {

		weatherData = getWeatherLocation(placeName, weatherLocation, weatherErrorReport);

		if (weatherData.getWeatherErrorReport() != null) {
			weatherData = getDailyData(weatherData.getWeatherLocation().getKey(), weatherErrorReport, 1);
		}

//		HttpUriRequest=new HttpGet(http://api.accuweather.com/locations/v1/search?q=san&apikey={your key});
//			request.addHeader("Accept-Encoding", "gzip");

		// return placeName;
		// return jsonString.toString();

		return weatherData.getWeatherLocation().toString();
	}

	private WeatherData getDailyData(String key, 
			WeatherErrorReport weatherErrorReport, int days) throws IOException {

		if(days==1)
			url = new URL("http://dataservice.accuweather.com/forecasts/v1/daily/1day/" + weatherLocation.getKey()
				+ "?apikey=" + apiKey);
		else {
			weatherErrorReport = new WeatherErrorReport();

			weatherErrorReport.setTimeStamp(new Date());
			weatherErrorReport.setCode(400);
			weatherErrorReport.setMessage("Invalid Number Of Days");
			weatherErrorReport.setPath("forecasts/v1/daily/1day/");
			
			weatherData.setWeatherErrorReport(weatherErrorReport);
			
			return weatherData;
		}
			
		httpURLConnection = (HttpURLConnection) url.openConnection();

		br = null;
		if (httpURLConnection.getResponseCode() == 200) {

			br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			jsonString = new StringBuilder("");

			while ((strCurrentLine = br.readLine()) != null) {

				if (strCurrentLine.trim().equals("[]")) {
					weatherErrorReport = new WeatherErrorReport();

					weatherErrorReport.setTimeStamp(new Date());
					weatherErrorReport.setCode(400);
					weatherErrorReport.setMessage("Data Not Found");
					weatherErrorReport.setPath("forecasts/v1/daily/1day/");
					
					weatherData.setWeatherErrorReport(weatherErrorReport);

					return weatherData;
				}

				jsonString.append(strCurrentLine);
			}

			jsonObj = new JSONObject(jsonString.toString());
			jsonArr = (JSONArray) jsonObj.getJSONArray("DailyForecasts");

			weatherDetails = new WeatherDetails();

			weatherDetails.setCurDate(new Date());
			weatherDetails.setMaxTemp(
					jsonArr.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Maximum").getInt("Value"));
			weatherDetails.setMinTemp(
					jsonArr.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Minimum").getInt("Value"));
			weatherDetails.setDayPhrase(jsonArr.getJSONObject(0).getJSONObject("Day").getString("IconPhrase"));
			weatherDetails.setNightPhrase(jsonArr.getJSONObject(0).getJSONObject("Night").getString("IconPhrase"));
			
			weatherData.setWeatherErrorReport(null);
			weatherData.setWeatherDetails(weatherDetails);

		} else {
			weatherErrorReport = new WeatherErrorReport();

			weatherErrorReport.setTimeStamp(new Date());
			weatherErrorReport.setCode(400);
			weatherErrorReport.setMessage("Data Not Found");
			weatherErrorReport.setPath("forecasts/v1/daily/1day/");
			
			weatherData.setWeatherErrorReport(weatherErrorReport);

			br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
			String strCurrentLine;
			while ((strCurrentLine = br.readLine()) != null) {
				System.out.println(strCurrentLine);
			}			
		}
		
		return weatherData;
	}

	private WeatherData getWeatherLocation(String placeName, WeatherLocation weatherLocation,
			WeatherErrorReport weatherErrorReport) throws IOException {

		url = new URL(
				"http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + apiKey + "&q=" + placeName);

		httpURLConnection = (HttpURLConnection) url.openConnection();

		// InputStream inputStream = httpURLConnection.getInputStream();
		System.out.println("httpURLConnection.getResponseCode():" + httpURLConnection.getResponseCode());

		br = null;
		if (httpURLConnection.getResponseCode() == 200) {
			br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			jsonString = new StringBuilder("");

			while ((strCurrentLine = br.readLine()) != null) {

				if (strCurrentLine.trim().equals("[]")) {
					weatherErrorReport = new WeatherErrorReport();

					weatherErrorReport.setTimeStamp(new Date());
					weatherErrorReport.setCode(400);
					weatherErrorReport.setMessage("Invalid Location");
					weatherErrorReport.setPath("locations/v1/cities/search");

					weatherData.setWeatherErrorReport(weatherErrorReport);
					weatherData.setWeatherLocation(null);

					return weatherData;
				}

				// System.out.println(strCurrentLine);
				// jsonString.append(strCurrentLine + "\n");
				jsonString.append(strCurrentLine);
			}

			jsonObj = new JSONObject(jsonString.toString().substring(1));
			// jsonObj = new JSONArray (jsonString.toString());
			// System.out.println(jsonObj);

			weatherLocation = new WeatherLocation();

			weatherLocation.setKey((String) jsonObj.get("Key"));
			weatherLocation.setName((String) jsonObj.get("EnglishName"));
			weatherLocation.setLongitude((double) jsonObj.getJSONObject("GeoPosition").getDouble("Longitude"));
			weatherLocation.setLatitude((double) jsonObj.getJSONObject("GeoPosition").getDouble("Latitude"));

			weatherData.setWeatherErrorReport(null);
			weatherData.setWeatherLocation(weatherLocation);

		} else {

			weatherErrorReport = new WeatherErrorReport();

			weatherErrorReport.setTimeStamp(new Date());
			weatherErrorReport.setCode(400);
			weatherErrorReport.setMessage("Invalid Response");
			weatherErrorReport.setPath("locations/v1/cities/search");

			weatherData.setWeatherErrorReport(weatherErrorReport);
			weatherData.setWeatherLocation(null);

			br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
			String strCurrentLine;
			while ((strCurrentLine = br.readLine()) != null) {
				System.out.println(strCurrentLine);
			}
		}

		return weatherData;
	}

//	@GetMapping("/limits")
//	public String getData() {
//		return "index";
//	}

	/*
	 * @RequestMapping(value="/submitLocation", method=RequestMethod.POST) public
	 * String submitLocation(@ModelAttribute("locationAttribute")WeatherLocationBean
	 * weatherLocation) { //return "confirmation"; return
	 * weatherLocation.getLocationName(); }
	 */
}
