package com.buszka.recruitment.fireup.exception;

public class BankAccountNotFoundException extends RuntimeException {

    public BankAccountNotFoundException() {
        super("Konto bankowe o zadanym identyfikatorze nie istnieje!");
    }
}
