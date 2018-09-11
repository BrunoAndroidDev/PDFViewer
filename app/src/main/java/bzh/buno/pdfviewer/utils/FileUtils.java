package bzh.buno.pdfviewer.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Definition of the FileUtils object.
 */
public class FileUtils {

    /**
     * Get a random file path in the cache directory
     *
     * @param context Context to retrieve the cache directory
     *
     * @return File path
     */
    public static String getRandomFileCachePath(@NonNull Context context) {
        return context.getCacheDir() + File.separator + System.currentTimeMillis();
    }

}
