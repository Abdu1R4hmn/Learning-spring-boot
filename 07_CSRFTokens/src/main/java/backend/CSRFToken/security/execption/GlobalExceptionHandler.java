package backend.Habit_Tracker.security.execption;

import backend.Habit_Tracker.security.execption.handlers.RefreshTokenExpired;
import backend.Habit_Tracker.security.execption.handlers.RefreshTokenReuseDetected;
import backend.Habit_Tracker.security.execption.handlers.ResourseAlreadyExists;
import backend.Habit_Tracker.security.execption.handlers.ResourseNotFound;
import backend.Habit_Tracker.security.responses.ErrorResponseApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ResourseAlreadyExists.class)
    public ResponseEntity<ErrorResponseApi> handleAlreadyExists(ResourseAlreadyExists message){

        return ResponseEntity.status(HttpStatus.CONFLICT.value())
                .body(new ErrorResponseApi(
                    HttpStatus.CONTINUE.value(),
                    message.getMessage()
                ));

    }

    @ExceptionHandler(ResourseNotFound.class)
    public ResponseEntity<ErrorResponseApi> handleNotFound(ResourseNotFound message){

        return ResponseEntity.status(HttpStatus.CONFLICT.value())
                .body(new ErrorResponseApi(
                    HttpStatus.CONFLICT.value(),
                    message.getMessage()
                ));
    }

    @ExceptionHandler(RefreshTokenExpired.class)
    public ResponseEntity<ErrorResponseApi> handleRefreshTokenExpired(BussinessException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseApi(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RefreshTokenReuseDetected.class)
    public ResponseEntity<ErrorResponseApi> handleRefreshTokenRevoked(BussinessException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseApi(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ErrorResponseApi> handleBussinessException(BussinessException ex){
        return ResponseEntity.badRequest()
                .body(new ErrorResponseApi(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseApi> handleValidationException(MethodArgumentNotValidException ex){

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErrorResponseApi(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                ));
    }

    private String formatFieldError(FieldError error) {

        return error.getField() + ": " + error.getDefaultMessage();
    }
}
