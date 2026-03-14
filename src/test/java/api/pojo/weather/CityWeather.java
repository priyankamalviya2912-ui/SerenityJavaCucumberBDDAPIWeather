package api.pojo.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CityWeather {
	
	@JsonProperty("city_name") 
    private String cityName;
	
	private float temp;
	
	@JsonProperty("state_code")
	private String stateCode;
	
	private WeatherDetail weather;

	
	public String getCityName() {
		return cityName;
	}

	public float getTemp() {
		return temp;
	}

	public String getStateCode() {
		return stateCode;
	}

	public WeatherDetail getWeather() {
		return weather;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public void setWeather(WeatherDetail weather) {
		this.weather = weather;
	}
}