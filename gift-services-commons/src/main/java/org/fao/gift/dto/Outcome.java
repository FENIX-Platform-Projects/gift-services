package org.fao.gift.dto;

import javax.ws.rs.core.Response;

public class Outcome {

    private String message;

    public Outcome(String message) {
        this.message = message;
    }

    public static Outcome create(String message) {
        return new Outcome(message);
    }

    public static Outcome create(Response.Status status) {
        return new Outcome(status.getReasonPhrase());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
