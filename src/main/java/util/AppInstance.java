package util;

public class AppInstance {

    public static final int USER_ID_SIZE = 8;
    public static final int ROOM_ID_SIZE = 12;

    private static AppInstance appInstance = null;

    public AppInstance() {
        // nothing
    }

    public static AppInstance getInstance() {
        if (appInstance == null) {
            appInstance = new AppInstance();
        }
        return appInstance;
    }
}
