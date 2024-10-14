package h2.fw.core.mobile;


import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.io.IOException;

public class ImageUtils {

    public void wrapDeviceFrames(String deviceFrame, String deviceScreenToBeFramed,
                                 String framedDeviceScreen)
            throws InterruptedException, IOException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.addImage(deviceFrame);
        op.addImage(deviceScreenToBeFramed);
        op.gravity("center");
        op.composite();
        op.opaque("none");
        op.addImage(framedDeviceScreen);
        ConvertCmd cmd = new ConvertCmd();
        cmd.run(op);
    }
}

