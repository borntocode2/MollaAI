package com.molla.mollaai.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

data class ApiErrorResponse(
    val message: String,
)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiErrorResponse(message = exception.message ?: "인증 실패"),
        )
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(exception: ResponseStatusException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(exception.statusCode).body(
            ApiErrorResponse(message = exception.reason ?: "요청 처리에 실패했습니다."),
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(exception: Exception): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiErrorResponse(message = exception.message ?: "알 수 없는 서버 오류가 발생했습니다."),
        )
    }
}
