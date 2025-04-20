package com.CorpConnec.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private Map<String, Object> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    body.put("path", request.getDescription(false).replace("uri=", ""));
    return body;
  }

  @ExceptionHandler({
          AuthenticationException.class,
          AuthorizationException.class,
          EntityNotFoundException.class,
          ResourceNotFoundException.class,
          DuplicateRoomException.class,
          InvalidRoomException.class,
          DuplicateEntityException.class,
          InvalidStatusException.class,
          UnauthorizedException.class,
          FileProcessingException.class,
          InvalidMessageException.class
  })
  public ResponseEntity<Object> handleBusinessExceptions(RuntimeException ex, WebRequest request) {
    HttpStatus status = determineHttpStatus(ex);
    return ResponseEntity
            .status(status)
            .body(buildErrorResponse(status, ex.getMessage(), request));
  }

  @ExceptionHandler(KeyLoadingException.class)
  public ResponseEntity<Object> handleKeyLoadingException(KeyLoadingException ex, WebRequest request) {
    log.error("Key loading error", ex);
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to load security keys. Contact support.",
                    request
            ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    log.error("Unhandled exception", ex);
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error",
                    request
            ));
  }

  private HttpStatus determineHttpStatus(RuntimeException ex) {
    if (ex instanceof AuthenticationException) {
      return HttpStatus.UNAUTHORIZED; // 401
    } else if (ex instanceof AuthorizationException || ex instanceof UnauthorizedException) {
      return HttpStatus.FORBIDDEN; // 403
    } else if (ex instanceof EntityNotFoundException || ex instanceof ResourceNotFoundException) {
      return HttpStatus.NOT_FOUND; // 404
    } else if (ex instanceof DuplicateRoomException || ex instanceof DuplicateEntityException) {
      return HttpStatus.CONFLICT; // 409
    } else {
      return HttpStatus.BAD_REQUEST; // 400
    }
  }
}