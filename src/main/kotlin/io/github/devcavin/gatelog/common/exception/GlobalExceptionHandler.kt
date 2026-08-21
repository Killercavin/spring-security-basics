package io.github.devcavin.gatelog.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return build(HttpStatus.UNAUTHORIZED, e.message ?: "Unauthorized", request)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return build(HttpStatus.FORBIDDEN, e.message ?: "Forbidden", request)
    }

    @ExceptionHandler(AccountDisabledException::class)
    fun handleForbidden(e: AccountDisabledException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return build(HttpStatus.FORBIDDEN, e.message ?: "Forbidden", request)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(e: ResourceNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> = build(HttpStatus.NOT_FOUND, e.message ?: "Not found", request)

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(e: ConflictException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return build(HttpStatus.CONFLICT, e.message ?: "Conflict", request)
    }

    @ExceptionHandler(InvalidStateException::class)
    fun handleInvalidState(e: InvalidStateException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, e.message ?: "Invalid state", request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val fieldErrors = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = HttpStatus.BAD_REQUEST.reasonPhrase,
                    message = "Validation failed",
                    path = request.requestURI,
                    fieldErrors = fieldErrors
                )
            )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(e: BadCredentialsException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return build(HttpStatus.BAD_REQUEST, e.message ?: "Invalid username or password", request)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected exception on {}", request.requestURI, e)

        return build(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            request
        )
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDenied(
        e: AuthorizationDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Authorization denied for {}: {}", request.requestURI, e.message)

        return build(
            HttpStatus.FORBIDDEN,
            "Access denied",
            request
        )
    }


    private fun build(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = request.requestURI
            )
        )
    }
}