package com.buszka.recruitment.fireup.exception;

public class BankAccountAlreadyExistsException extends RuntimeException {

    public BankAccountAlreadyExistsException() {
        super("Konto bankowe o zadanym identyfikatorze już istnieje!");
    }
}
