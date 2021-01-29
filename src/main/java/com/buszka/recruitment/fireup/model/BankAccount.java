package com.buszka.recruitment.fireup.model;

import com.buszka.recruitment.fireup.exception.BankAccountInsufficientCreditsException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Builder
public class BankAccount {
    private String uuid;
    private LocalDateTime created;
    private String ownerFirstName;
    private String ownerLastName;
    private Integer amount;
    private ArrayList<BankAccountHistory> accountHistory;

    public void changeAccountBalance(Integer amount) throws BankAccountInsufficientCreditsException {
        if(amount == 0) {
            return;
        }

        if(amount < 0 && this.amount + amount < 0) {
            throw new BankAccountInsufficientCreditsException();
        }

        this.amount += amount;
        this.accountHistory.add(new BankAccountHistory(amount));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("KONTO: ").append(this.uuid).append(", UTWORZONE: ").append(this.created);
        sb.append(", WŁAŚCICIEL: ").append(this.ownerFirstName).append(" ").append(this.ownerLastName);
        sb.append(", SALDO: ").append(this.amount).append("\n");

        if (this.accountHistory != null && !this.accountHistory.isEmpty()) {
            sb.append("HISTORIA OPERACJI:\n");

            for (BankAccountHistory bankAccountHistory : this.accountHistory) {
                sb.append(bankAccountHistory.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}
