package bf.kvill.spring_phone_book.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<ApiError> handleContactNotFoundException(ContactNotFoundException e)
    {
        ApiError error = new ApiError();
        error.setMessage(e.getMessage());
        error.setCode(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(NoResourceFoundException e)
    {
        ApiError error = new ApiError();
        error.setMessage("Resource not found");
        error.setCode(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ApiError error = new ApiError();
        error.setMessage("Erreur de validation: " + message);
        error.setCode(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        
        ApiError error = new ApiError();
        error.setMessage("Erreur de validation: " + message);
        error.setCode(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        ApiError error = new ApiError();
        error.setMessage("Une erreur interne s'est produite");
        error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
