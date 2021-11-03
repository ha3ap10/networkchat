package server;

import settings.Settings;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static Logger logger;
    private static String logFileName;

    private Logger() {
        logFileName = Settings.getInstance().getLOG();
    }

    public static Logger getInstance() {
        if (logger == null) logger = new Logger();
        return logger;
    }

    public void log(String threadName, String msg) {
        String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date());
        String record = String.format("%s: [%s] %s\n", date, threadName, msg);
        System.out.print(record);
        try (FileWriter fw = new FileWriter(logFileName, true)) {
            fw.write(record);
            fw.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}