package com.buszka.recruitment.fireup.exception;

public class BankAccountNothingChangedException extends RuntimeException {

    public BankAccountNothingChangedException() {
        super("Nie zmieniono salda na koncie.");
    }
}
