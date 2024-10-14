package h2.fw.core.web.api;

import h2.fw.core.web.api.HTTPRequestStrategy;
import h2.fw.core.web.api.WSComponents;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.Map;

public class GETRequestStrategy implements HTTPRequestStrategy {
    private final WSComponents wsComponents;

    public GETRequestStrategy(WSComponents wsComponents) {
        this.wsComponents = wsComponents;
    }

    @Override
    public Request makeRequest() {
        String fullUri = buildUriWithPathParams(wsComponents.getUri(), wsComponents.getPathParameters());
        System.out.println("Full URI: " + fullUri); // Debug: Print full URI

        HttpUrl.Builder urlBuilder = HttpUrl.parse(fullUri).newBuilder();
        wsComponents.getMultiValueParameters().forEach((key, values) -> values.forEach(value -> urlBuilder.addQueryParameter(key, value)));

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());

        Map<String, String> headers = wsComponents.getHeaders();
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        return requestBuilder.build();
    }

    private String buildUriWithPathParams(String uri, Map<String, String> pathParams) {
        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            uri = uri.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return uri;
    }
}
