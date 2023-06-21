package auto.ssh;

/**
 * @author wsfsp4
 * @version 2023.06.21
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class LogFileUtil {
    private static final String LOG_DIR_NAME = "logs";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

    public static synchronized void writeLogToFile(String project, String log) {
        if (!isExternalStorageWritable()) {
            return;
        }

        Date date = new Date();

        try {
            String currentDate = dateFormat.format(date);
            String currentTime = timeFormat.format(date);

            File logFile;
            if (project == null) {
                logFile = new File(getLogFilePath(currentDate));
            } else {
                logFile = new File(getLogFilePath(project, currentDate));
            }

            if (!Objects.requireNonNull(logFile.getParentFile()).exists()) {
                logFile.getParentFile().mkdirs();
            }

            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(currentTime + "\t" + log + "\n");
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state);
    }

    private static String getLogFilePath(String date) {
        return MyApplication.getContext().getExternalFilesDir(null)
                + File.separator
                + LOG_DIR_NAME
                + File.separator
                + date
                + ".log";
    }

    private static String getLogFilePath(String project, String date) {
        return MyApplication.getContext().getExternalFilesDir(null)
                + File.separator
                + LOG_DIR_NAME
                + File.separator
                + (project != null ? project + File.separator : "")
                + date
                + ".log";
    }
}
