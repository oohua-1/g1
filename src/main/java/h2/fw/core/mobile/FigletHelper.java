package h2.fw.core.mobile;


import com.github.lalyos.jfiglet.FigletFont;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class FigletHelper {
    private static final Logger LOGGER = LogManager.getLogger(FigletHelper.class.getName());

    public static void figlet(String text) {
        String asciiArt1 = null;
        try {
            asciiArt1 = FigletFont.convertOneLine(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info(asciiArt1);
    }
}
