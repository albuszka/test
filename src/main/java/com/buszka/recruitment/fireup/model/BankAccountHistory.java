package com.buszka.recruitment.fireup.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BankAccountHistory {
    private LocalDateTime created;
    private Integer amount;

    public BankAccountHistory(Integer amount) {
        this.created = LocalDateTime.now();
        this.amount = amount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("==> ").append(this.created).append(" ").append(this.amount);

        return sb.toString();
    }
}
