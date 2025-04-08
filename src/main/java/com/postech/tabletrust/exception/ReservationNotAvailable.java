package com.postech.tabletrust.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "No tables available")
public class ReservationNotAvailable extends RuntimeException {
    public ReservationNotAvailable(String errorMessage) {
        super(errorMessage);
    }
}
