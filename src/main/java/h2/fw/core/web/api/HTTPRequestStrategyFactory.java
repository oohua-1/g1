package h2.fw.core.web.api;


public class HTTPRequestStrategyFactory {
    public static HTTPRequestStrategy createRequestStrategy(String method, WSComponents wsComponents) {
        switch (method.toUpperCase()) {
            case "GET":
                return new GETRequestStrategy(wsComponents);
            case "POST":
                return new POSTRequestStrategy(wsComponents);
            case "DELETE":
                return new DELETERequestStrategy(wsComponents);
            case "FORMDATA":
                return new FormDataRequestStrategy(wsComponents);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
}
