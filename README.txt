Weather Information App

Overview

The Weather Information App is a Java Swing-based desktop application that allows users to fetch and view current weather
data and 5-day forecasts for a specified location. It uses the OpenWeatherMap API and includes features such as:

- Real-time weather display (temperature, humidity, wind speed, condition)
- Weather condition icon representation
- 5-day forecast section
- Unit selector for Metric and Imperial systems
- Dynamic background based on time of day
- History tracking of previous searches with timestamps
- Error handling for invalid inputs and API issues

How to Use

1. Run the Application

Execute EnhancedWeatherInformationApp.java through your IDE or compile via command line.

2. Enter Location

In the top panel, type the name of the city (e.g., London, New York, Tokyo).

3. Select Units

Choose between Metric (C, m/s) or Imperial (F, mph) from the dropdown.

4. Fetch Weather

Click the "Get Weather" button.

The application displays:

- Current weather information
- Weather condition icon
- 5-day forecast (every 24 hours)
- History log on the right side

Project Structure

WeatherInformationApp.java: Main UI and logic for fetching and displaying weather info.

WeatherService.java: Responsible for making HTTP requests to OpenWeatherMap and parsing JSON responses.

HistoryLogger.java: Tracks the search history and adds it to the UI list.

Dependencies

- org.json library for JSON parsing (ensure it's included in your classpath)
- Java 21 for Swing, LocalDateTime, and lambda expressions

API Key Setup

Replace the placeholder API_KEY in WeatherService.java with your own OpenWeatherMap API key.

You can get a free API key at https://openweathermap.org/api

private final String API_KEY = "YOUR_API_KEY_HERE";

Error Handling

The app will notify users via dialog boxes when:

- The location is empty or invalid
- The API call fails (e.g., network issues or invalid city name)

Notes

The app uses OpenWeatherMap's /weather and /forecast endpoints.

The forecast displays one entry every 24 hours (skipping other entries).

Author

Developed by Olesya Kolenchenko

License

This project is open for educational use. Attribution appreciated.