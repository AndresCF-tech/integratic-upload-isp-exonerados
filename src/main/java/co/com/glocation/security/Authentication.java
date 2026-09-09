package co.com.glocation.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import co.com.glocation.dto.ErrorResponse;

public class Authentication {

    private final List<String> VALID_AUDIENCES;
    private static final String BEARER_PREFIX = "Bearer ";
    private final GoogleIdTokenVerifier verifier;
    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new GsonFactory();

    public Authentication(List<String> validAudiences) {
        if (validAudiences == null || validAudiences.isEmpty()) {
            // Es crucial que validAudiences no sea nulo o vacío para que el verifier funcione correctamente.
            // GoogleIdTokenVerifier.Builder().setAudience() espera una Collection no nula.
            // Si la lista está vacía, ningún token coincidirá.
            throw new IllegalArgumentException(
                "Valid audiences list cannot be null or empty for Authentication setup."
            );
        }
        this.VALID_AUDIENCES = validAudiences;
        // --- CORRECCIÓN 1: Inicializar el verifier ---
        this.verifier = new GoogleIdTokenVerifier.Builder(
            transport,
            jsonFactory
        )
            .setAudience(this.VALID_AUDIENCES) // Usar la lista de audiencias proporcionada
            // Opcionalmente, puedes ser más estricto con el emisor, aunque el verifier ya lo comprueba:
            // .setIssuer("accounts.google.com")
            .build();
    }

    public boolean is_authentication(
        HttpRequest request,
        HttpResponse response
    ) throws IOException {
        Optional<String> authHeaderOptional = request.getFirstHeader(
            "Authorization"
        );

        if (!authHeaderOptional.isPresent()) {
            response.setStatusCode(401);
            // Usar ErrorResponse
            response
                .getWriter()
                .write(
                    ErrorResponse.createJson("Authorization header missing")
                );
            response.setContentType("application/json");
            return true;
        }

        String authHeader = authHeaderOptional.get();

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            response.setStatusCode(401);
            // Usar ErrorResponse
            response
                .getWriter()
                .write(
                    ErrorResponse.createJson(
                        "Invalid Authorization header format. Expected Bearer token."
                    )
                );
            response.setContentType("application/json");
            return true;
        }

        String idTokenString = authHeader.substring(BEARER_PREFIX.length());

        if (idTokenString.isEmpty()) {
            System.err.println("Authentication failed: Bearer token is empty.");
            response.setStatusCode(401);
            // Usar ErrorResponse
            response
                .getWriter()
                .write(ErrorResponse.createJson("Bearer token is empty"));
            response.setContentType("application/json");
            return true;
        }

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                // ... (autenticación exitosa) ...
                return false;
            } else {
                System.err.println(
                    "Authentication failed: Invalid ID token. Verification returned null."
                );
                response.setStatusCode(401);
                // Usar ErrorResponse
                response
                    .getWriter()
                    .write(
                        ErrorResponse.createJson("Invalid authentication token")
                    );
                response.setContentType("application/json");
                return true;
            }
        } catch (GeneralSecurityException e) {
            System.err.println(
                "Authentication failed: Token verification security error. Message: " +
                e.getMessage()
            );
            response.setStatusCode(401);
            // Usar ErrorResponse (no exponer e.getMessage() al cliente)
            response
                .getWriter()
                .write(
                    ErrorResponse.createJson(
                        "Token verification security error"
                    )
                );
            response.setContentType("application/json");
            return true;
        } catch (IOException e) {
            System.err.println(
                "Authentication failed: Token could not be parsed (malformed). Message: " +
                e.getMessage()
            );
            response.setStatusCode(401);
            // Usar ErrorResponse
            response
                .getWriter()
                .write(
                    ErrorResponse.createJson("Invalid authentication token")
                );
            response.setContentType("application/json");
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println(
                "Authentication failed: Invalid argument to token verifier. Message: " +
                e.getMessage()
            );
            response.setStatusCode(401);
            // Usar ErrorResponse
            response
                .getWriter()
                .write(
                    ErrorResponse.createJson("Invalid authentication token")
                );
            response.setContentType("application/json");
            return true;
        }
    }
    // makeGetRequest makes a GET request to the specified Cloud Run or
    // Cloud Functions endpoint `serviceUrl` (must be a complete URL), by
    // authenticating with an ID token retrieved from Application Default
    // Credentials using the specified `audience`.
    //
    // Example `audience` value (Cloud Run): https://my-cloud-run-service.run.app/
    /* public static HttpResponse makeGetRequest(
        String serviceUrl,
        String audience
    ) throws IOException {
        GoogleCredentials credentials =
            GoogleCredentials.getApplicationDefault();
        if (!(credentials instanceof IdTokenProvider)) {
            throw new IllegalArgumentException(
                "Credentials are not an instance of IdTokenProvider."
            );
        }
        IdTokenCredentials tokenCredential = IdTokenCredentials.newBuilder()
            .setIdTokenProvider((IdTokenProvider) credentials)
            .setTargetAudience(audience)
            .build();

        GenericUrl genericUrl = new GenericUrl(serviceUrl);
        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(
            tokenCredential
        );
        HttpTransport transport = new NetHttpTransport();
        HttpRequest request = transport
            .createRequestFactory(adapter)
            .buildGetRequest(genericUrl);
        return request.execute();
    }*/
}
