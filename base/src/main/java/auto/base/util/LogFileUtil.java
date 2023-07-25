package auto.base.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import auto.base.BaseApplication;


public class LogFileUtil extends FileUtil {
    private static final String LOG_DIR_NAME = "logs";

    public static synchronized void writeLog(String project, String content) {
        if (!isExternalStorageWritable()) {
            return;
        }

        try {
            String currentDate = TimeUnit.formatDate();
            String currentTime = TimeUnit.formatTime();
            String filePath = getLogFilePath(project, currentDate);

            File logFile = createFile(filePath);

            FileWriter fileWriter = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(currentTime + "\t" + content + "\n");
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLogFilePath(String project, String fileName) {
        return BaseApplication.getContext().getExternalFilesDir(null)
                + File.separator
                + LOG_DIR_NAME
                + File.separator
                + (project != null ? project + File.separator : "")
                + fileName
                + ".log";
    }

    public static String getLogFileDir(String project) {
        return BaseApplication.getContext().getExternalFilesDir(null)
                + File.separator
                + LOG_DIR_NAME
                + File.separator
                + (project != null ? project + File.separator : "");
    }
}
