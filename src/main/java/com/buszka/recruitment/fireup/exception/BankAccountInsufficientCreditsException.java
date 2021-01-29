package com.buszka.recruitment.fireup.exception;

public class BankAccountInsufficientCreditsException extends RuntimeException {

    public BankAccountInsufficientCreditsException() {
        super("Brak wystarczających środków na koncie!");
    }
}
