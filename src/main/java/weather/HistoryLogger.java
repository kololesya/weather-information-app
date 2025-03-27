package weather;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class HistoryLogger {
    public void log(DefaultListModel<String> model, String location) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        model.addElement(location + " @ " + timestamp);
    }
}
