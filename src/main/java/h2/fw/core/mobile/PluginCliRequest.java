package h2.fw.core.mobile;


import com.fasterxml.jackson.databind.ObjectMapper;
import h2.fw.utils.ConfigReader;

import java.io.IOException;
import java.net.URL;

public class PluginCliRequest {
    private static final ConfigReader config = ConfigReader.getInstance("/Users/h2/Downloads/h2_fw/configs/mobile/dev_local_config.properties");
    Api api;

    public PluginCliRequest() {
        api = new Api();
    }

    public PluginClI getCliArgs() throws IOException {
//        String remoteWDHubIP = config.getProperty("APPIUM_SERVER");
        String remoteWDHubIP = "http://127.0.0.1:31337/wd/hub";

        URL url = new URL(remoteWDHubIP);
        String response = api.getResponse(url.getProtocol() + "://" + url.getHost()
                + ":" + url.getPort() + "/device-farm/api/cliArgs");
        final PluginClI[] pluginClIS = new ObjectMapper().readValue(response, PluginClI[].class);
        return pluginClIS[0];
    }
}
