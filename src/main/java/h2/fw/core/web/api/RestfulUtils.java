package h2.fw.core.web.api;


import h2.fw.core.web.sql.SQLManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RestfulUtils {
    public static void loadTemplateAndInitRequest(RestfulComponent restfulComponent, String serviceName, String domain) throws SQLException, IOException {
        SQLManager sqlManager = SQLManager.getInstance();
        String query = "SELECT api_protocol, api_method, api_path, api_template FROM restful_templates WHERE api_name = '" + serviceName + "'";
        List<Map<String, Object>> restfulTemplate = sqlManager.executeQuery("LocalDB", query);

        if (restfulTemplate.isEmpty()) {
            throw new RuntimeException("No matching API info found for the given service name");
        }

        Map<String, Object> apiInfo = restfulTemplate.get(0);
        System.out.println("API info: " + apiInfo);

        String protocol = apiInfo.get("api_protocol") != null ? apiInfo.get("api_protocol").toString() : "";
        String path = apiInfo.get("api_path") != null ? apiInfo.get("api_path").toString() : "";
        String method = apiInfo.get("api_method") != null ? apiInfo.get("api_method").toString() : "";
        String template = apiInfo.get("api_template") != null ? apiInfo.get("api_template").toString() : "";

        restfulComponent.initRequest(protocol, domain, path, method, template);
    }
}
