package api.pojo.weather;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDetail {
    private String description;
    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
