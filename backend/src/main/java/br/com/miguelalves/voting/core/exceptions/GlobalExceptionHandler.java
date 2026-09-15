package br.com.miguelalves.voting.core.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.miguelalves.voting.core.api.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(CpfAlreadyExistsException.class)
        public ResponseEntity<ApiErrorResponse> handleCpfAlreadyExists(
                        CpfAlreadyExistsException exception,
                        HttpServletRequest request) {

                return buildResponse(
                                HttpStatus.CONFLICT,
                                exception.getMessage(),
                                request.getRequestURI());
        }

        @ExceptionHandler(ProposalNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleProposalNotFound(
                        ProposalNotFoundException exception,
                        HttpServletRequest request) {
                return buildResponse(
                                HttpStatus.NOT_FOUND,
                                exception.getMessage(),
                                request.getRequestURI());
        }

        @ExceptionHandler(VotingSessionAlreadyExistsException.class)
        public ResponseEntity<ApiErrorResponse> handleVotingSessionAlreadyExists(
                        VotingSessionAlreadyExistsException exception,
                        HttpServletRequest request) {
                return buildResponse(
                                HttpStatus.CONFLICT,
                                exception.getMessage(),
                                request.getRequestURI());
        }

        @ExceptionHandler({
                        InvalidCpfException.class,
                        AssociateUnableToVoteException.class
        })
        public ResponseEntity<ApiErrorResponse> handleVotingEligibility(
                        RuntimeException exception,
                        HttpServletRequest request) {
                return buildResponse(
                                HttpStatus.NOT_FOUND,
                                exception.getMessage(),
                                request.getRequestURI());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationException(
                        MethodArgumentNotValidException exception,
                        HttpServletRequest request) {
                var message = exception
                                .getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Invalid request");
                return buildResponse(
                                HttpStatus.BAD_REQUEST,
                                message,
                                request.getRequestURI());
        }

        private ResponseEntity<ApiErrorResponse> buildResponse(
                        HttpStatus status,
                        String message,
                        String path) {
                var error = new ApiErrorResponse(
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                path,
                                LocalDateTime.now());
                return ResponseEntity
                                .status(status)
                                .body(error);
        }
}
