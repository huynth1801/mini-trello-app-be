package com.huydev.skipli_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ApiResponse <T>{
    boolean success;
    String message;
    T data;

    public static <T> ApiResponse<T> success(String message, T data){
        return ApiResponse.<T>builder().success(true).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> error(String message){
        return ApiResponse.<T>builder().success(false).message(message).build();
    }
}
