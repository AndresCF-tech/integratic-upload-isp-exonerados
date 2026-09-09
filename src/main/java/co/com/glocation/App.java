package co.com.glocation;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import co.com.glocation.config.Cors;
import co.com.glocation.dto.ErrorResponse;
import co.com.glocation.dto.NewFile;
import co.com.glocation.dto.soap_model.CargarArchivo;
import co.com.glocation.dto.soap_model.RespuestaOperacion;
import co.com.glocation.dto.soap_model.SoapBody;
import co.com.glocation.dto.soap_model.SoapEnvelope;
import co.com.glocation.dto.soap_model.SoapFault;
import co.com.glocation.security.Authentication;

public class App implements HttpFunction {

    // Can be null based on your validation
    private static final Gson gson = new Gson();

    private static List<String> getEnvList(String key, List<String> defaultValue) {
        String value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            // Separa el texto por comas y quita espacios extra
            return Arrays.asList(value.split("\\s*,\\s*"));
        }
        return defaultValue;
    }

    // --- Configuración CORS
    private static final List<String> ALLOWED_ORIGINS = getEnvList("ALLOWED_ORIGINS", Arrays.asList(
            "http://localhost:8080",
            "https://central-de-cuentas-prod-dot-mintic-models-dev.uc.r.appspot.com",
            "https://central-de-cuentas-qa-dot-mintic-models-dev.uc.r.appspot.com",
            "https://central-de-cuentas-mvp-dot-mintic-models-dev.uc.r.appspot.com",
            "https://swagger-integratic-dot-mintic-models-dev.uc.r.appspot.com"));

    // --- Configuración de Autenticación (Adaptada del código Go) ---
    private static final List<String> VALID_AUDIENCES = getEnvList("VALID_AUDIENCES", Arrays.asList(
            "32555940559.apps.googleusercontent.com", // Posiblemente Client ID
            "618104708054-9r9s1c4alg36erliucho9t52n32n6dgq.apps.googleusercontent.com",
            "https://us-central1-mintic-models-dev.cloudfunctions.net/integratic_upload_file"));

    private final Cors cors = new Cors(ALLOWED_ORIGINS);

    private final Authentication authentication = new Authentication(
            VALID_AUDIENCES);

    /*
     * private SoapResponseData upload_file(NewFile dataNewFile) throws Exception {
     * String endpoint =
     * "https://cert-integratic.mintic.gov.co/AZDigital_Funcionales/WebServices/SOAP/";
     * String soapBodyTemplate =
     * """
     * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
     * xmlns:xsd="http://www.analitica.com.co/AZDigital/xsds/">
     * <soapenv:Header/>
     * <soapenv:Body>
     * <xsd:CargarArchivo
     * PathRelativoAZ="%s"
     * IdDirectorio="%s"
     * Nombre="%s"
     * TipoMime="%s"
     * Codificacion="%s">
     * <Archivo>%s</Archivo>
     * </xsd:CargarArchivo>
     * </soapenv:Body>
     * </soapenv:Envelope>
     * """;
     * String soapBody = soapBodyTemplate.formatted(
     * (dataNewFile.getPathRelativoAZ() != null)
     * ? dataNewFile.getPathRelativoAZ()
     * : "",
     * dataNewFile.getIdDirectorio(),
     * dataNewFile.getNombre(),
     * dataNewFile.getTipoMime(),
     * dataNewFile.getCodificacion(),
     * dataNewFile.getBase64()
     * );
     *
     * java.net.URI uri = new java.net.URI(endpoint);
     * URL url = uri.toURL();
     * HttpURLConnection connection = (HttpURLConnection) url.openConnection();
     * connection.setRequestMethod("POST");
     * connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
     * connection.setDoOutput(true);
     *
     * try (OutputStream os = connection.getOutputStream()) {
     * byte[] input = soapBody.getBytes("utf-8");
     * os.write(input, 0, input.length);
     * }
     *
     * int responseCode = connection.getResponseCode();
     * String responseXml;
     *
     * // Leemos la respuesta, ya sea del flujo de entrada (éxito) o del de error.
     * if (responseCode >= 200 && responseCode < 300) {
     * try (
     * BufferedReader br = new BufferedReader(
     * new InputStreamReader(connection.getInputStream(), "utf-8")
     * )
     * ) {
     * responseXml = br.lines().collect(Collectors.joining("\n"));
     * }
     * } else {
     * try (
     * BufferedReader br = new BufferedReader(
     * new InputStreamReader(connection.getErrorStream(), "utf-8")
     * )
     * ) {
     * responseXml = br.lines().collect(Collectors.joining("\n"));
     * }
     * }
     *
     * System.out.println("respuesta " + responseXml);
     * // --- INICIO DE LA LÓGICA CON JAXB ---
     * try {
     * // 1. Crear el contexto JAXB con TODAS las clases que pueden ser un elemento
     * raíz en el Body
     * JAXBContext jaxbContext = JAXBContext.newInstance(
     * RespuestaOperacion.class,
     * SoapFault.class
     * );
     * Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
     *
     * // 2. Usar SAAJ para parsear el sobre SOAP y obtener el Body
     * MessageFactory factory = MessageFactory.newInstance();
     * SOAPMessage soapMessage = factory.createMessage(
     * null,
     * new ByteArrayInputStream(responseXml.getBytes("utf-8"))
     * );
     * SOAPBody soapBodyResponse = soapMessage.getSOAPBody();
     *
     * // 3. Obtener el primer elemento hijo del Body (será RespuestaOperacion o
     * Fault)
     * Node responseNode = (Node) soapBodyResponse.getFirstChild();
     *
     * // 4. Deserializar (unmarshal) el nodo a un objeto Java
     * Object result = unmarshaller.unmarshal(responseNode);
     *
     * // 5. Comprobar de qué tipo es el objeto resultante
     * if (result instanceof RespuestaOperacion) {
     * RespuestaOperacion successResponse =
     * (RespuestaOperacion) result;
     * // Mapear al DTO que usas para la respuesta de la función
     * // NOTA: Los nombres de los atributos en tu RespuestaOperacion.java no
     * coinciden con los de SoapResponseData.java. ¡Hay que corregirlos!
     * // Asumo que la respuesta correcta es la que pusiste en
     * RespuestaOperacion.java
     * return new SoapResponseData(
     * successResponse.getEstado(),
     * successResponse.getNuevoArId(), // Corregido de NuevoDiIId a NuevoArId
     * successResponse.getDirectorioPadreDild() // Corregido de DiColeccion
     * );
     * } else if (result instanceof SoapFault) {
     * SoapFault faultResponse = (SoapFault) result;
     * // Lanzar una excepción con el mensaje de error detallado del Fault
     * throw new Exception(
     * "Error del servicio SOAP: " +
     * faultResponse.getDetailedMessage()
     * );
     * } else {
     * // Caso inesperado
     * throw new Exception(
     * "Formato de respuesta SOAP desconocido. Respuesta parcial: " +
     * responseXml.substring(
     * 0,
     * Math.min(500, responseXml.length())
     * )
     * );
     * }
     * } catch (Exception e) {
     * // Relanzar la excepción para que sea capturada por el manejador global en
     * service()
     * throw new Exception(
     * "Fallo al procesar la respuesta SOAP: " + e.getMessage(),
     * e
     * );
     * }
     * }
     */
    private String escapeXml(String str) {
        if (str == null)
            return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private Map<String, Object> upload_file(NewFile dataNewFile) {
        Map<String, Object> responseMap = new HashMap<>();
        String envEndpoint = System.getenv("SOAP_ENDPOINT");
        String endpoint = (envEndpoint != null && !envEndpoint.trim().isEmpty()) ? envEndpoint
                : "https://cert-integratic.mintic.gov.co/AZDigital_Funcionales/WebServices/SOAP/";

        HttpURLConnection connection = null;
        int responseCode = -1;
        String responseXml = "";
        try {
            java.net.URI uri = new java.net.URI(endpoint);
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            connection.setRequestProperty("SOAPAction", "http://www.analitica.com.co/AZDigital/xsds/CargarArchivo");
            connection.setDoOutput(true);

            // OPTIMIZACIÓN DE MEMORIA: Escribir el XML directamente en el flujo de red
            // (Stream)
            // Esto evita crear árboles DOM pesados con JAXB y SAAJ.
            try (OutputStream os = connection.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8")) {

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.write(
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.analitica.com.co/AZDigital/xsds/\">");
                writer.write("<soapenv:Header/>");
                writer.write("<soapenv:Body>");

                writer.write("<xsd:CargarArchivo");
                if (dataNewFile.getRadNumero() != null) {
                    writer.write(" RadNumero=\"" + escapeXml(dataNewFile.getRadNumero()) + "\"");
                }
                if (dataNewFile.getPathRelativoAZ() != null) {
                    writer.write(" PathRelativoAZ=\"" + escapeXml(dataNewFile.getPathRelativoAZ()) + "\"");
                }
                writer.write(" IdDirectorio=\"" + escapeXml(dataNewFile.getIdDirectorio()) + "\"");
                writer.write(" Nombre=\"" + escapeXml(dataNewFile.getNombre()) + "\"");
                writer.write(" TipoMime=\"" + escapeXml(dataNewFile.getTipoMime()) + "\"");
                writer.write(" Codificacion=\"" + escapeXml(dataNewFile.getCodificacion()) + "\">");

                writer.write("<Archivo>");
                // Se envía el base64 sin escapar, ya que es texto seguro y enorme
                writer.write(dataNewFile.getBase64());
                writer.write("</Archivo>");

                writer.write("</xsd:CargarArchivo>");
                writer.write("</soapenv:Body>");
                writer.write("</soapenv:Envelope>");
                writer.flush(); // Fuerza el envío de los datos sin almacenar el texto gigante
            }

            responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                responseXml = br.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            // Log de la respuesta (no colapsa memoria porque la respuesta XML que devuelve
            // es pequeña)
            System.out.println("====== INICIO: RESPUESTA SOAP RECIBIDA ======");
            System.out.println(responseXml);
            System.out.println("======= FIN: RESPUESTA SOAP RECIBIDA =======");

            // --- LÓGICA DE RESPUESTA --- (La respuesta sí se procesa con JAXB/SAAJ de
            // manera segura)
            JAXBContext jaxbContext = JAXBContext.newInstance(SoapEnvelope.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            MessageFactory factoryResponse = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage soapMessageResponse = factoryResponse.createMessage(null,
                    new ByteArrayInputStream(responseXml.getBytes("utf-8")));
            SOAPBody soapBodyResponse = soapMessageResponse.getSOAPBody();
            SoapBody bodyObject = unmarshaller.unmarshal(soapBodyResponse, SoapBody.class).getValue();

            if (bodyObject.getRespuestaOperacion() != null) {
                RespuestaOperacion successResponse = bodyObject.getRespuestaOperacion();
                responseMap.put("Estado", successResponse.getEstado());
                responseMap.put("NuevoArId", successResponse.getNuevoArId());
                responseMap.put("DirectorioPadreDiId", successResponse.getDirectorioPadreDiId());
            } else if (bodyObject.getFault() != null) {
                SoapFault faultResponse = bodyObject.getFault();
                responseMap.put("status", "error");
                responseMap.put("faultcode", faultResponse.getFaultcode());
                responseMap.put("faultstring", faultResponse.getFaultstring());
                if (faultResponse.getDetail() != null && faultResponse.getDetail().getErrores() != null) {
                    responseMap.put("errores", faultResponse.getDetail().getErrores());
                }
            } else {
                responseMap.put("status", "error");
                responseMap.put("message", "Formato de respuesta SOAP desconocido.");
                responseMap.put("rawResponse", responseXml);
            }
            responseMap.put("statusCode", responseCode);
        } catch (Exception e) {
            responseMap.put("status", "error");
            responseMap.put("message", "Fallo la solicitud SOAP: " + e.getMessage());
            if (responseCode != -1) {
                responseMap.put("statusCode", responseCode);
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return responseMap;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws Exception {
        // se aplica logica interna de validacion de estado
        boolean corsHandled = cors.handleCors(request, response);
        if (corsHandled) {
            return;
        }

        String method = request.getMethod();
        if (!"POST".equals(method)) {
            response.setStatusCode(405); // Use constant for clarity
            response
                    .getWriter()
                    .write(ErrorResponse.createJson("Method Not Allowed"));
            response.setContentType("application/json");
            return; // Stop processing for non-POST requests
        }
        // se aplica logica interna de validacion de credenciales
        boolean is_autorization = authentication.is_authentication(
                request,
                response);
        if (is_autorization) {
            return;
        }

        NewFile newFile = null;
        String body = null;

        try {
            // body = request.getReader().lines().collect(Collectors.joining());
            // Attempt to parse the JSON body into a Directorio object
            newFile = gson.fromJson(request.getReader(), NewFile.class);
            // Basic validation: check if required fields are present/valid
            if (newFile == null ||
            // newFile.getRadNumero() == null ||
            // newFile.getRadNumero().trim().isEmpty() ||
                    newFile.getIdDirectorio() == null ||
                    newFile.getIdDirectorio().trim().isEmpty() ||
                    newFile.getNombre() == null ||
                    newFile.getNombre().trim().isEmpty() ||
                    newFile.getTipoMime() == null ||
                    newFile.getTipoMime().trim().isEmpty() ||
                    newFile.getCodificacion() == null ||
                    newFile.getCodificacion().trim().isEmpty() ||
                    newFile.getBase64() == null ||
                    newFile.getBase64().trim().isEmpty()) {
                // Note: RadNumero is optional based on your SOAP template "?"
                response.setStatusCode(400); // Bad Request
                response.setContentType("application/json");
                response
                        .getWriter()
                        .write(
                                ErrorResponse.createJson(
                                        "Missing or empty required fields in request body. Expected: RadNumero, IdDirectorio, Nombre, TipoMime, Codificacion, Base64."));
                return;
            }

            // --- Now you have the data in the 'newFile' object ---
            // You would now use this object to build and send your SOAP request
            // instead of using hardcoded values.

            Map<String, Object> soapData = upload_file(newFile);

            response.setStatusCode((int) soapData.get("statusCode"));
            response.setContentType("application/json");
            // You might want to return the SOAP response instead
            response.getWriter().write(gson.toJson(soapData));
        } catch (JsonSyntaxException e) {
            // Handle cases where the request body is not valid JSON or doesn't match
            // Directorio structure
            response.setStatusCode(400); // Bad Request
            response.setContentType("application/json");
            response
                    .getWriter()
                    .write(
                            ErrorResponse.createJson(
                                    "Invalid JSON format in request body"));
            e.printStackTrace(); // Log the stack trace for debugging
        } catch (Exception e) {
            // Handle other potential exceptions during processing (like the SOAP call)
            response.setStatusCode(500); // Internal Server Error
            response.setContentType("application/json");
            response
                    .getWriter()
                    .write(
                            ErrorResponse.createJson(
                                    "An internal error occurred: " + e.getMessage()));
        }
    }
}
