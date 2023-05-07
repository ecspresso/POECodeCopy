package ecspresso.pathofexile;

import java.util.Date;

public class PathOfExile {
    private static String CONTENT;
    private static Date SENT = new Date(0);
    private static boolean NO_EMAIL = true;

    public static String getContent() {
        return CONTENT;
    }

    public static void setContent(String CONTENT) {
        PathOfExile.CONTENT = CONTENT;
    }

    public static Date getSent() {
        return SENT;
    }

    public static void setSent(Date SENT) {
        PathOfExile.SENT = SENT;
    }

    public static boolean noEmail() {
        return NO_EMAIL;
    }

    public static void emailFound() {
        PathOfExile.NO_EMAIL = false;
    }
}
