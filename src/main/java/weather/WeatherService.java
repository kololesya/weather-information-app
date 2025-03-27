package weather;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class WeatherService {
    private final String API_KEY = "5989cb62ae70ee48e3ae524c7b3ce806"; // Your actual API key

    // Get current weather data from OpenWeatherMap
    public JSONObject getWeatherData(String location, String unitLabel) throws Exception {
        String unitsParam = unitLabel.contains("Metric") ? "metric" : "imperial";
        String urlString = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&units=%s&appid=%s",
                location, unitsParam, API_KEY);

        return fetchJson(urlString);
    }

    // Get 5-day forecast data
    public JSONArray getForecastData(String location, String unitLabel) throws Exception {
        String unitsParam = unitLabel.contains("Metric") ? "metric" : "imperial";
        String urlString = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=%s&appid=%s",
                location, unitsParam, API_KEY);

        JSONObject response = fetchJson(urlString);
        return response.getJSONArray("list");
    }

    // Fetch JSON from given URL with error handling
    private JSONObject fetchJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            // Read and return error response if status code != 200
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            String errorMsg = new JSONObject(errorResponse.toString()).optString("message", "Unknown error.");
            throw new Exception("API Error: " + errorMsg);
        }

        // Read successful response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return new JSONObject(response.toString());
    }
}
