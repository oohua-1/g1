package h2.fw.core.web.api;

import okhttp3.Request;

import java.util.Map;

public class DELETERequestStrategy implements HTTPRequestStrategy {
    private final WSComponents wsComponents;

    public DELETERequestStrategy(WSComponents wsComponents) {
        this.wsComponents = wsComponents;
    }

    @Override
    public Request makeRequest() {
        String fullUri = buildUriWithPathParams(wsComponents.getUri(), wsComponents.getPathParameters());
        System.out.println("Full URI: " + fullUri); // Debug: Print full URI

        Request.Builder requestBuilder = new Request.Builder()
                .url(fullUri)
                .delete();

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
