package com.sqli.matchmaking.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exceptions.EntityNotFound.class)
    public ResponseEntity<Object> handleEntityNotFoundException(Exceptions.EntityNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.EntityCannotBeSaved.class)
    public ResponseEntity<Object> handleEntityCannotBeSavedException(Exceptions.EntityCannotBeSaved ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.EntityCannotBeDeleted.class)
    public ResponseEntity<Object> handleEntityCannotBeDeletedException(Exceptions.EntityCannotBeDeleted ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.EntityCannotBeUpdated.class)
    public ResponseEntity<Object> handleEntityCannotBeUpdatedException(Exceptions.EntityCannotBeUpdated ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.TwoEntitiesLinkNotFound.class)
    public ResponseEntity<Object> handleLinkException(Exceptions.TwoEntitiesLinkNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.OnlyAdmin.class)
    public ResponseEntity<Object> handleOnlyAdmin(Exceptions.OnlyAdmin ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.OnlyOrganizerAndAdmin.class)
    public ResponseEntity<Object> handleOnlyOrganizerAndAdmin(Exceptions.OnlyOrganizerAndAdmin ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.MatchMustBeOnStatus.class)
    public ResponseEntity<Object> handleMatchMustBeOnStatus(Exceptions.MatchMustBeOnStatus ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.EntityIsNull.class)
    public ResponseEntity<Object> handleEntityIsNull(Exceptions.EntityIsNull ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(Map.of("error", ex.getMessage()));
    }

}
