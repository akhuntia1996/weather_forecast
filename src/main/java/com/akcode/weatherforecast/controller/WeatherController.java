package com.akcode.weatherforecast.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.akcode.weatherforecast.model.WeatherData;
import com.akcode.weatherforecast.model.WeatherDetails;
import com.akcode.weatherforecast.model.WeatherErrorReport;
import com.akcode.weatherforecast.model.WeatherLocation;

@Controller
public class WeatherController {

	private final String apiKey = "cu26GmLCgGO7TRRbdu7K0iItZaafl7Gy";

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
	
	private ServletContext servletContext;

    public WeatherController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

//    @GetMapping(value = "/submitLocation")
//	public String getCurrentLocation(Model model) {
//		model.addAttribute("mylocation","linku");
//		return "result";
//	}
	
	@RequestMapping(value = "/submitLocation", method = RequestMethod.POST)
	public String submitLocation(HttpSession session,
			@RequestParam("placeName") String placeName) throws IOException {

		weatherData = getWeatherLocation(placeName, weatherLocation, weatherErrorReport);
		weatherData = getDailyData(weatherData.getWeatherLocation().getKey(), weatherErrorReport, 1);

		session.setAttribute("mylocation",weatherData.getWeatherLocation().getName());
		session.setAttribute("mymaxtemp",weatherData.getWeatherLocation().getLatitude());
		session.setAttribute("mymintemp",weatherData.getWeatherDetails().getMinTemp());
		session.setAttribute("mydayphrase",weatherData.getWeatherDetails().getDayPhrase());
		session.setAttribute("mycurrtemp",weatherData.getWeatherDetails().getCurrTemp());

// 		return weatherData.getWeatherLocation().toString();

		return "result";
	}

	private WeatherData getDailyData(String key, WeatherErrorReport weatherErrorReport, int days) throws IOException {

		if (days == 1) {
			url = new URL("http://dataservice.accuweather.com/forecasts/v1/daily/1day/" + key
					+ "?apikey=" + apiKey);
		}else {
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
					jsonArr.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Maximum").getDouble("Value"));
			weatherDetails.setMinTemp(
					jsonArr.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Minimum").getDouble("Value"));
			weatherDetails.setDayPhrase(jsonArr.getJSONObject(0).getJSONObject("Day").getString("IconPhrase"));
			weatherDetails.setNightPhrase(jsonArr.getJSONObject(0).getJSONObject("Night").getString("IconPhrase"));
			
			url = new URL("http://dataservice.accuweather.com/currentconditions/v1/"+key+"?apikey="+apiKey);
			
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
						weatherErrorReport.setPath("currentconditions/v1/");

						weatherData.setWeatherErrorReport(weatherErrorReport);

						return weatherData;
					}

					jsonString.append(strCurrentLine);
				}
				
//				jsonObj = new JSONObject(jsonString.toString());
//				jsonArr = (JSONArray) jsonObj.getJSONArray("DailyForecasts");
				
				jsonObj = new JSONObject(jsonString.toString().substring(1));
				
				weatherDetails.setCurrTemp(jsonObj.getJSONObject("Temperature")
						.getJSONObject("Metric").getDouble("Value"));
				
			}else {
				weatherErrorReport = new WeatherErrorReport();

				weatherErrorReport.setTimeStamp(new Date());
				weatherErrorReport.setCode(400);
				weatherErrorReport.setMessage("Data Not Found");
				weatherErrorReport.setPath("currentconditions/v1/");

				weatherData.setWeatherErrorReport(weatherErrorReport);

				br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
				String strCurrentLine;
//				while ((strCurrentLine = br.readLine()) != null) {
//					System.out.println(strCurrentLine);
//				}
			}


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
//			while ((strCurrentLine = br.readLine()) != null) {
//				System.out.println(strCurrentLine);
//			}
		}

		return weatherData;
	}

	private WeatherData getWeatherLocation(String placeName, WeatherLocation weatherLocation,
			WeatherErrorReport weatherErrorReport) throws IOException {

		url = new URL(
				"http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + apiKey + "&q=" + placeName);

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
//			while ((strCurrentLine = br.readLine()) != null) {
//				System.out.println(strCurrentLine);
//			}
		}

		return weatherData;
	}
}
