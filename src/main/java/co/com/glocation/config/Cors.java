package co.com.glocation.config;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Cors {

    private final List<String> ALLOWED_ORIGINS;

    public Cors(List<String> allowedOrigins) {
        this.ALLOWED_ORIGINS = allowedOrigins;
    }

    public boolean handleCors(HttpRequest request, HttpResponse response)
        throws IOException {
        Optional<String> originHeader = request.getFirstHeader("Origin");
        String origin = originHeader.orElse(null);

        String allowOrigin = null;
        if (origin != null) {
            for (String allowed : ALLOWED_ORIGINS) {
                if (allowed.equals(origin)) {
                    allowOrigin = allowed;
                    break;
                }
            }
        }

        if (allowOrigin != null) {
            response.appendHeader("Access-Control-Allow-Origin", allowOrigin);
            response.appendHeader("Access-Control-Allow-Credentials", "true");
        }

        if ("OPTIONS".equals(request.getMethod())) {
            if (allowOrigin != null) {
                response.appendHeader(
                    "Access-Control-Allow-Methods",
                    "POST, GET, OPTIONS"
                );
                Optional<String> requestHeadersOptional =
                    request.getFirstHeader("Access-Control-Request-Headers");
                String requestHeaders = requestHeadersOptional.orElse(
                    "Content-Type, Authorization"
                );
                response.appendHeader(
                    "Access-Control-Allow-Headers",
                    requestHeaders
                );
                response.appendHeader("Access-Control-Max-Age", "3600");
            }
            response.setStatusCode(204);
            return true;
        }
        return false;
    }
}
