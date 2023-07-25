package auto.base.util;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author wsfsp4
 * @version 2023.07.21
 */
public class FileUtil {
    public static File createFile(String path) throws IOException {
        File file = new File(path);

        if (!Objects.requireNonNull(file.getParentFile()).exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    public static boolean isExternalStorageWritable() {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state);
    }
}
