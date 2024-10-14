package h2.fw.core.mobile;

public class AppiumScreenRecordFactory {

    public static IScreenRecord recordScreen() {
        return new AppiumScreenRecorder();
    }
}
