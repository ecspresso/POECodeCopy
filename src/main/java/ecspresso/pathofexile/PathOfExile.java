package ecspresso.pathofexile;

import java.util.Date;

public class PathOfExile {
    private static String content;
    private static Date received = new Date(0);

    public static String getContent() {
        return content;
    }

    public static void setContent(String content) {
        PathOfExile.content = content;
    }

    public static Date getReceived() {
        return received;
    }

    public static void setReceived(Date received) {
        PathOfExile.received = received;
    }
}
