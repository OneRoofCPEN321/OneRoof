package ca.oneroof.oneroof.api;

import retrofit2.Call;

public class ApiResponse<T> {
    public final T data;
    public final String message;

    public ApiResponse(T data) {
        this.data = data;
        this.message = null;
    }

    public ApiResponse(String message) {
        this.data = null;
        this.message = message;
    }
}
