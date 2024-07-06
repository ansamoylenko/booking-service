package com.samoylenko.bookingservice.controller;

import com.samoylenko.bookingservice.model.exception.EntityCreateException;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.model.exception.EntityUpdateException;
import com.samoylenko.bookingservice.model.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.time.LocalDateTime.now;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleException(EntityNotFoundException e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityCreateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleException(EntityCreateException e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleException(EntityUpdateException e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleException(ValidationException e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleException(AccessDeniedException e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(Exception e, HttpServletRequest request) {
        return handleException(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ExceptionResponse handleException(Exception e, HttpServletRequest request, HttpStatus status) {
        var exception = ExceptionResponse.builder()
                .timestamp(now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .status(status.value())
                .message(e.getMessage())
                .cause(e.getCause() != null ? e.getCause().getMessage() : null)
                .rootCause(getRootCause(e))
                .build();
        log.warn(exception.toString());
        return exception;
    }

    private String getRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}
