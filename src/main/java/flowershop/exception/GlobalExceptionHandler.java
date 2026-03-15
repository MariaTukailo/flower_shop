package flowershop.exception;

import flowershop.dto.ErrorDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception exception) {
        log.error("Произошла ошибка с кодом {}", exception.getMessage());
        ErrorDto error = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(EntityNotFoundException exception) {
        log.error("Произошла  ошибка с кодом {}", exception.getMessage());
        ErrorDto error = new ErrorDto(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(org.springframework.http.converter.HttpMessageNotReadableException exception) {
        log.error(" Произошла ошибка с кодом  {}", exception.getMessage());
        ErrorDto error = new ErrorDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(jakarta.validation.ConstraintViolationException exception) {
        log.error("Произошла ошибка  с кодом {}", exception.getMessage());

        ErrorDto error = new ErrorDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
