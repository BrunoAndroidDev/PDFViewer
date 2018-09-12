package bzh.buno.pdfviewer.utils;

import java.util.Locale;
import java.util.Random;

/**
 * Definition of the RandomUtils object.
 */
public class RandomUtils {

    private static final Random sRandom = new Random();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;

    public static int randomInt() {
        return sRandom.nextInt(9999);
    }

    public static String randomString(int length) {
        final char[] buf = new char[length];
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = ALPHANUM.charAt(sRandom.nextInt(ALPHANUM.length()));
        }
        return new String(buf);
    }
}

