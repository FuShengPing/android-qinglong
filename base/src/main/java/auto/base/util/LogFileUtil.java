package auto.base.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

import auto.base.BaseApplication;


public class LogFileUtil {
    private static final String LOG_DIR_NAME = "logs";

    public static synchronized void writeLogToFile(String project, String log) {
        if (!isExternalStorageWritable()) {
            return;
        }

        try {
            String currentDate = TimeUnit.formatDate();
            String currentTime = TimeUnit.formatTime();

            File logFile = new File(getLogFilePath(project, currentDate));

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

    private static String getLogFilePath(String project, String date) {
        return BaseApplication.getContext().getExternalFilesDir(null)
                + File.separator
                + LOG_DIR_NAME
                + File.separator
                + (project != null ? project + File.separator : "")
                + date
                + ".log";
    }
}
