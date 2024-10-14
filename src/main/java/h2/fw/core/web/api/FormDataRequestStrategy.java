package h2.fw.core.web.api;

import h2.fw.core.web.api.HTTPRequestStrategy;
import h2.fw.core.web.api.WSComponents;
import okhttp3.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class FormDataRequestStrategy implements HTTPRequestStrategy {
    private final WSComponents wsComponents;
    private final Set<String> fileExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".pdf", ".doc", ".docx", ".txt");

    public FormDataRequestStrategy(WSComponents wsComponents) {
        this.wsComponents = wsComponents;
    }

    private boolean isFile(String value) {
        return fileExtensions.stream().anyMatch(value::endsWith);
    }

    @Override
    public Request makeRequest() {
        MultipartBody.Builder formDataBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : wsComponents.getParameters().entrySet()) {
            String value = entry.getValue();
            if (isFile(value)) {
                File file = new File(value);
                formDataBuilder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
            } else {
                formDataBuilder.addFormDataPart(entry.getKey(), value);
            }
        }

        RequestBody formData = formDataBuilder.build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(wsComponents.getUri())
                .post(formData);

        Map<String, String> headers = wsComponents.getHeaders();
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        return requestBuilder.build();
    }
}
