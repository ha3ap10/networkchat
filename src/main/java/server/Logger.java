package server;

import settings.Settings;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static Logger logger;
    private static String logFileName;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");

    private Logger() {
        logFileName = Settings.getInstance().getLog();
    }

    public static Logger getInstance() {
        if (logger == null) logger = new Logger();
        return logger;
    }

    public void log(String threadName, String msg) {
        String date = simpleDateFormat.format(new Date());
        String record = String.format("%s INFO: [%s] %s\n", date, threadName, msg);
        System.out.print(record);
        try (FileWriter fw = new FileWriter(logFileName, true)) {
            fw.write(record);
            fw.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void error(String threadName, String msg) {
        String date = simpleDateFormat.format(new Date());
        String record = String.format("%s ERROR: [%s] %s\n", date, threadName, msg);
        System.out.print(record);
        try (FileWriter fw = new FileWriter(logFileName, true)) {
            fw.write(record);
            fw.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
