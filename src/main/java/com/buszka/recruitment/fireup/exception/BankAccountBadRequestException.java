package com.buszka.recruitment.fireup.exception;

public class BankAccountBadRequestException extends RuntimeException {

    public BankAccountBadRequestException() {
        super("Błędny request lub parametry requesta!");
    }
}
