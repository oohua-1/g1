package h2.fw.core.web.api;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Map;

public class WSComponents {
    private static WSComponents instance;
    private String uri;
    private String method;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private Map<String, String> pathParameters;

    private WSComponents() {
    }

    public static synchronized WSComponents getInstance() {
        if (instance == null) {
            instance = new WSComponents();
        }
        return instance;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers != null ? headers : Collections.emptyMap();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParameters() {
        return parameters != null ? parameters : Collections.emptyMap();
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters != null ? pathParameters : Collections.emptyMap();
    }

    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }

    public MultiValueMap<String, String> getMultiValueParameters() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                multiValueMap.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
        }
        return multiValueMap;
    }
}
