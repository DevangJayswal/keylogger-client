package com.example.keyloggerclient;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
    // ./data/
    public static final String DATA_DIRECTORY = "." + File.separator + "data" + File.separator;

    // ./data/timestamp.txt
    public static final String DATA_FILE = "." + File.separator + "data" + File.separator + getDateTime() + ".txt";

    private static String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
    }
}
