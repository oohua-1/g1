package h2.fw.core.web.api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import h2.fw.core.web.sql.SQLManager;
import h2.fw.exceptions.ExceptionUtils;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class RestfulComponent {
    private static final Logger LOGGER = LogManager.getLogger(RestfulComponent.class.getName());

    private final OkHttpClient client;
    private final WSComponents wsComponents;
    private final ObjectMapper objectMapper;
    private Response response;
    private String cachedResponseBody;

    public RestfulComponent(WSComponents wsComponents) {
        this.client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
        this.wsComponents = wsComponents;
        this.objectMapper = new ObjectMapper();
    }

    public void initRequest(String protocol, String domain, String path, String method, String body) {
        String uri = String.format("%s://%s%s", protocol, domain, path);
        wsComponents.setUri(uri);
        wsComponents.setMethod(method);
        if (!body.isEmpty()) {
            wsComponents.setBody(body);
        }
        // Log in console
        LOGGER.info("Uri: {}", wsComponents.getUri());
        LOGGER.info("Method: {}", wsComponents.getMethod());
        if (!body.isEmpty()) {
            LOGGER.info("Body: {}", wsComponents.getBody());
        }
    }

    public void setParameters(Map<String, String> params) {
        wsComponents.setParameters(params);
        LOGGER.info("Params: {}", wsComponents.getParameters());
    }

    public void setPathParameters(Map<String, String> pathParams) {
        wsComponents.setPathParameters(pathParams);
        LOGGER.info("Path Params: {}", pathParams);
    }

    public void setHeaders(Map<String, String> headers) {
        wsComponents.setHeaders(headers);
        LOGGER.info("Headers: {}", wsComponents.getHeaders());
    }

    public void setParsedBody(String body) {
        wsComponents.setBody(body);
    }

    public String getParsedBody() {
        return wsComponents.getBody();
    }

    public void sendRequest(){
        ExceptionUtils.handleCheckedException(()->{
            long startTime = System.currentTimeMillis();
            Request request = HTTPRequestStrategyFactory.createRequestStrategy(wsComponents.getMethod(), wsComponents).makeRequest();
            try (Response response = client.newCall(request).execute()) {
                handleResponse(response, startTime);
            }
        });

    }

    private void handleResponse(Response response, long startTime) throws IOException {
        this.response = response;
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        cachedResponseBody = Objects.requireNonNull(response.body()).string();
        LOGGER.info("API completed in {} ms", elapsedTime);
        LOGGER.info("Respone body: {}", cachedResponseBody);
    }

    public int getHttpCode() {
        if (response == null) {
            return 0; // Or handle it as per your requirement
        }
        return response.code();
    }

    public String getResponseAsString() {
        return cachedResponseBody;
    }

    public JsonNode getResponseAsJson() {
        try {
            return objectMapper.readTree(cachedResponseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendRequestWithRetry(int maxRetries, Duration delay, List<Integer> retryStatusCodes) throws IOException, InterruptedException {
        Predicate<Integer> retryPredicate = retryStatusCodes::contains;

        long startTime = System.currentTimeMillis();
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                Request request = HTTPRequestStrategyFactory.createRequestStrategy(wsComponents.getMethod(), wsComponents).makeRequest();
                try (Response response = client.newCall(request).execute()) {
                    this.response = response;
                    cachedResponseBody = Objects.requireNonNull(response.body()).string();
                    if (!retryPredicate.test(response.code())) {
                        handleResponse(response, startTime);
                        return;
                    }
                }
            } catch (IOException e) {
                if (attempt == maxRetries - 1) {
                    throw e;
                }
            }
            attempt++;
            Thread.sleep(delay.toMillis());
        }
    }

    public Object getSingleValueFromJsonPath(String jsonPath) {
        try {
            JsonNode jsonNode = objectMapper.readTree(cachedResponseBody);
            JsonNode valueNode = jsonNode.at(jsonPath);
            if (valueNode.isMissingNode()) {
                return null;
            }
            return valueNode.isValueNode() ? valueNode.asText() : valueNode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getValueFromResponse(String jsonPath) {
        try {
            return JsonPath.read(cachedResponseBody, jsonPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}