package h2.fw.core.web.api;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

public class POSTRequestStrategy implements HTTPRequestStrategy {
    private final WSComponents wsComponents;

    public POSTRequestStrategy(WSComponents wsComponents) {
        this.wsComponents = wsComponents;
    }

    @Override
    public Request makeRequest() {
        String fullUri = buildUriWithPathParams(wsComponents.getUri(), wsComponents.getPathParameters());
        System.out.println("Full URI: " + fullUri); // Debug: Print full URI

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(wsComponents.getBody(), mediaType);

        Request.Builder requestBuilder = new Request.Builder()
                .url(fullUri)
                .post(body);

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