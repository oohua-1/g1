package h2.fw.core.web.api;


import okhttp3.Request;

public interface HTTPRequestStrategy {
    Request makeRequest();
}
