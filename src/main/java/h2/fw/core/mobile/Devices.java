package h2.fw.core.mobile;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Devices {
    private static List<Device> instance;

    private Devices() {

    }

    public static List<Device> getConnectedDevices() {
        if (instance == null) {
            try {
                System.out.println(Thread.currentThread().getId());
                URL url = new URL("http://127.0.0.1:31337/wd/hub");
                String response = new Api().getResponse(url.getProtocol()
                        + "://" + url.getHost() + ":" + url.getPort() + "/device-farm/api/device");
                instance = Arrays.asList(new ObjectMapper().readValue(response, Device[].class));
            } catch (MalformedURLException e) {
                e.printStackTrace(); // Handle the MalformedURLException
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Handle the JsonProcessingException
            } catch (Exception e) {
                e.printStackTrace(); // Handle any other exceptions
            }
        }
        return instance;
    }
}
