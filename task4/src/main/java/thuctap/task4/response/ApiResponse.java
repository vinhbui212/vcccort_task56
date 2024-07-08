package thuctap.task4.response;

public class ApiResponse<T> {
    private int status;
    private String message;
    private int code;
    private T data;

    public ApiResponse() {}

    public ApiResponse(int status, String message, int code, T data) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(1, message, 200, data);
    }

    public static <T> ApiResponse<T> error(String message, int code) {
        return (ApiResponse<T>) new ApiResponse<>(1, "fail", code, "{}");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
