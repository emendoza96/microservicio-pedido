package com.microservice.order.error;

public class ErrorResponse {

    private ErrorDetails error;

    public ErrorResponse() {}

    public ErrorResponse(ErrorDetails errorDetails) {
        this.error = errorDetails;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorResponse [error=" + error + "]";
    }

}
