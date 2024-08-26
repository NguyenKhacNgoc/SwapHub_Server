package com.server.ecommerce.exception;

import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ecommerce.dto.response.ApiResponse;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<?> handlingAppExeption(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        @SuppressWarnings("rawtypes")
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    // bắt lỗi ngoại lệ
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<?> handlingExeption(Exception exception) {
        @SuppressWarnings("rawtypes")
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // bắt lỗi Access Denied
    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<?> handlingAccessDenied(AuthorizationDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        @SuppressWarnings("rawtypes")
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @SuppressWarnings("null")
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<?> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        @SuppressWarnings("rawtypes")
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Bắt lỗi keycloak 409
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = ClientErrorException.class)
    ResponseEntity<?> handingConflict(ClientErrorException exception)
            throws JsonMappingException, JsonProcessingException {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(exception.getResponse().getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode errorJsonNode = objectMapper.readTree(exception.getResponse().readEntity(String.class));

        apiResponse.setMessage(errorJsonNode.get("errorMessage").asText());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    // Bắt lỗi keycloak 404
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = NotFoundException.class)
    ResponseEntity<?> handingUserNotFound(NotFoundException exception)
            throws JsonMappingException, JsonProcessingException {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(exception.getResponse().getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode errorJsonNode = objectMapper.readTree(exception.getResponse().readEntity(String.class));
        apiResponse.setMessage(errorJsonNode.get("error").asText());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

}