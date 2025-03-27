package weather;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalDateTime;

public class WeatherInformationApp extends JFrame {
    private final JTextField locationInput;
    private final JTextArea weatherDisplay;
    private final JComboBox<String> unitSelector;
    private final JButton fetchButton;
    private final DefaultListModel<String> historyModel;
    private final JList<String> historyList;
    private final JLabel iconLabel;

    // Services
    private final WeatherService weatherService;
    private final HistoryLogger historyLogger;
    private final String[] units = {"Metric (C, m/s)", "Imperial (F, mph)"};

    private final JPanel topPanel;
    private final JTextArea forecastDisplay;

    public WeatherInformationApp() {
        setTitle("Weather App");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        weatherService = new WeatherService();
        historyLogger = new HistoryLogger();

        // Top panel for input controls
        topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 20, 50)); // Dark header for contrast

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setForeground(Color.WHITE); // Make label readable

        locationInput = new JTextField(20);
        unitSelector = new JComboBox<>(units);
        fetchButton = new JButton("Get Weather");

        topPanel.add(locationLabel);
        topPanel.add(locationInput);
        topPanel.add(unitSelector);
        topPanel.add(fetchButton);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for weather and forecast display
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel weatherDisplayPanel = new JPanel(new BorderLayout());
        weatherDisplay = new JTextArea(5, 30);
        weatherDisplay.setEditable(false);
        weatherDisplayPanel.add(new JScrollPane(weatherDisplay), BorderLayout.CENTER);

        iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(100, 100));
        weatherDisplayPanel.add(iconLabel, BorderLayout.EAST);

        centerPanel.add(weatherDisplayPanel, BorderLayout.NORTH);

        forecastDisplay = new JTextArea(8, 30);
        forecastDisplay.setEditable(false);
        centerPanel.add(new JScrollPane(forecastDisplay), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Right panel for search history
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        add(new JScrollPane(historyList), BorderLayout.EAST);

        applyDynamicBackground();

        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        fetchWeather();
                        return null;
                    }
                }.execute();
            }
        });
    }

    private void applyDynamicBackground() {
        int hour = LocalDateTime.now().getHour();
        Color backgroundColor;
        if (hour >= 6 && hour < 12) {
            backgroundColor = new Color(255, 250, 200); // Morning
        } else if (hour >= 12 && hour < 18) {
            backgroundColor = new Color(200, 230, 255); // Afternoon
        } else if (hour >= 18 && hour < 21) {
            backgroundColor = new Color(255, 180, 100); // Evening
        } else {
            backgroundColor = new Color(30, 30, 60); // Night
        }
        getContentPane().setBackground(backgroundColor);
        topPanel.setBackground(backgroundColor);
    }

    private void fetchWeather() {
        String location = locationInput.getText().trim();
        if (location.isEmpty()) {
            showError("Please enter a location.");
            return;
        }

        String selectedUnit = (String) unitSelector.getSelectedItem();

        try {
            JSONObject currentWeather = weatherService.getWeatherData(location, selectedUnit);
            JSONArray forecastArray = weatherService.getForecastData(location, selectedUnit);

            displayWeather(currentWeather, selectedUnit);
            displayForecast(forecastArray, selectedUnit);

            historyLogger.log(historyModel, location);
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    // Get custom icon URL based on weather condition
    private ImageIcon getCustomIcon(String condition) {
        condition = condition.toLowerCase();
        String iconFile = "cloudy-grey.png";
        if (condition.contains("clear")) iconFile = "sun.png";
        else if (condition.contains("cloud") && condition.contains("rain")) iconFile = "cloudy-rainy.png";
        else if (condition.contains("rain")) iconFile = "rainy.png";
        else if (condition.contains("snow")) iconFile = "snow.png";
        else if (condition.contains("cloud")) iconFile = "sun-cloudy.png";

        try {
            URL iconUrl = getClass().getResource("/icons/" + iconFile);
            if (iconUrl != null) {
                ImageIcon rawIcon = new ImageIcon(iconUrl);
                Image scaled = rawIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void displayWeather(JSONObject json, String unitLabel) {
        JSONObject main = json.getJSONObject("main");
        JSONObject wind = json.getJSONObject("wind");
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

        String condition = weather.getString("main");
        ImageIcon icon = getCustomIcon(condition);
        iconLabel.setIcon(icon);

        StringBuilder sb = new StringBuilder();
        sb.append("Location: ").append(json.getString("name")).append("\n");
        sb.append("Temperature: ").append(main.getDouble("temp"))
                .append(unitLabel.contains("Metric") ? " °C\n" : " °F\n");
        sb.append("Humidity: ").append(main.getInt("humidity")).append("%\n");
        sb.append("Wind Speed: ").append(wind.getDouble("speed"))
                .append(unitLabel.contains("Metric") ? " m/s\n" : " mph\n");
        sb.append("Condition: ").append(condition);

        weatherDisplay.setText(sb.toString());
    }

    private void displayForecast(JSONArray forecastArray, String unitLabel) {
        StringBuilder sb = new StringBuilder("\n5-Day Forecast:\n");
        for (int i = 0; i < forecastArray.length(); i += 8) { // Every 8 steps = 24 hours
            JSONObject entry = forecastArray.getJSONObject(i);
            String dtTxt = entry.getString("dt_txt");
            JSONObject main = entry.getJSONObject("main");
            String condition = entry.getJSONArray("weather").getJSONObject(0).getString("main");
            double temp = main.getDouble("temp");
            sb.append(dtTxt).append(" - ")
                    .append(temp).append(unitLabel.contains("Metric") ? " °C" : " °F")
                    .append(" - ").append(condition).append("\n");
        }
        forecastDisplay.setText(sb.toString());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
