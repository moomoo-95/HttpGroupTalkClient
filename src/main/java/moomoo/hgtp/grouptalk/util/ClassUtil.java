package moomoo.hgtp.grouptalk.util;

public class ClassUtil {

    public ClassUtil() {
        // nothing
    }

    public static boolean isInteger(String integer) {

        try {
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String integer) {

        try {
            Long.parseLong(integer);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
