package weather;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherInformationApp app = new WeatherInformationApp();
            app.setVisible(true);
        });
    }
}