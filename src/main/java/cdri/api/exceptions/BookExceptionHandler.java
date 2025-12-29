package cdri.api.exceptions;

import cdri.common.enums.ResponseCode;
import cdri.common.exception.InvalidCursorException;
import cdri.common.exception.InvalidPageSizeException;
import cdri.common.exception.NoSuchBookException;
import cdri.common.exception.NoSuchCategoryException;
import cdri.common.response.ExceptionResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class BookExceptionHandler {
    @ExceptionHandler(exception = {NoSuchBookException.class})
    public ResponseEntity<ExceptionResponse> handleNoSuchBookException(Exception e, WebRequest request) {
        log.info("No Book Found: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(ResponseCode.NO_BOOK_FOUND));
    }

    @ExceptionHandler(exception = {NoSuchCategoryException.class})
    public ResponseEntity<ExceptionResponse> handleNoSuchCategoryException(Exception e, WebRequest request) {
        log.info("No Category Found: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(ResponseCode.NO_CATEGORY_FOUND));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException e) {
        log.info("Validation failed: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(ResponseCode.INVALID_REQUEST));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.info("Type mismatch: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(ResponseCode.INVALID_REQUEST));
    }

    @ExceptionHandler(InvalidCursorException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidCursorException(Exception e) {
        log.info("Invalid cursor: {}", e.getMessage());

        return ResponseEntity.badRequest().body(new ExceptionResponse(ResponseCode.INVALID_CURSOR));
    }
}
