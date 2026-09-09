package co.com.glocation.dto;

import com.google.gson.Gson;

public class ErrorResponse {

    private String error; // Mensaje principal del error
    private String errorCode; // Opcional: un código de error interno/documentado
    private String details; // Opcional: más detalles, si es seguro exponerlos (generalmente no para el cliente)

    // Constructor para un error simple
    public ErrorResponse(String error) {
        this.error = error;
    }

    // Constructor con código de error
    public ErrorResponse(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    // Constructor completo
    public ErrorResponse(String error, String errorCode, String details) {
        this.error = error;
        this.errorCode = errorCode;
        this.details = details;
    }

    // Getters (y Setters si los necesitas, aunque para DTOs inmutables a veces se omiten los setters)
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // Método para convertir este objeto a una cadena JSON
    // Necesitarás una biblioteca JSON como Gson o Jackson.
    // Aquí un ejemplo con Gson (asegúrate de tener la dependencia)
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Método estático de fábrica para conveniencia
    public static String createJson(String errorMessage) {
        return new ErrorResponse(errorMessage).toJson();
    }

    public static String createJson(String errorMessage, String errorCode) {
        return new ErrorResponse(errorMessage, errorCode).toJson();
    }
}
