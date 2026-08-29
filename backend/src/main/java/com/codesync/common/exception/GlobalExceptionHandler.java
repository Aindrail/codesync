package com.codesync.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage(
            HttpMessageNotReadableException exception) {

        return problemDetail(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "The request body is malformed or invalid."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {

        return problemDetail(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "The request contains invalid data."
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException exception) {

        return problemDetail(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "The request contains invalid data."
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(
            ResourceNotFoundException exception) {

        return problemDetail(
                HttpStatus.NOT_FOUND,
                "Not Found",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicateSubmissionException.class)
    public ProblemDetail handleDuplicateSubmission(
            DuplicateSubmissionException exception) {

        return problemDetail(
                HttpStatus.CONFLICT,
                "Conflict",
                exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {

        return unexpectedError();
    }

    private ProblemDetail unexpectedError() {

        return problemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred."
        );
    }

    private ProblemDetail problemDetail(
            HttpStatus status,
            String title,
            String detail) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(status);

        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);

        return problemDetail;
    }
}