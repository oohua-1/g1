package h2.fw.core.mobile;

public interface IScreenRecord {

    void stopVideoRecording(String className, String methodName,
                            String videoFileName);

    void startVideoRecording();

}
