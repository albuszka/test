package com.buszka.recruitment.fireup.repository;

import com.buszka.recruitment.fireup.exception.BankAccountAlreadyExistsException;
import com.buszka.recruitment.fireup.exception.BankAccountInsufficientCreditsException;
import com.buszka.recruitment.fireup.exception.BankAccountNotFoundException;
import com.buszka.recruitment.fireup.model.BankAccount;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Set;

/**
 * Repozytorium trzymające konta bankowe.
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // w sumie jest tak domyślnie
public class BankRepository {

    private HashMap<String, BankAccount> bankAccounts = new HashMap<String, BankAccount>();

    @Transactional(propagation = Propagation.MANDATORY)
    public void createBankAccount(BankAccount bankAccount) throws BankAccountAlreadyExistsException {
        if(this.checkIfBankAccountExists(bankAccount.getUuid())) {
            throw new BankAccountAlreadyExistsException();
        }

        this.bankAccounts.put(bankAccount.getUuid(), bankAccount);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BankAccount getBankAccount(String bankAccountUuid) throws BankAccountNotFoundException {
        if(!this.checkIfBankAccountExists(bankAccountUuid)) {
            throw new BankAccountNotFoundException();
        }
        return this.bankAccounts.get(bankAccountUuid);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void makeBankAccountTransaction(String bankAccountUuid, Integer amount) throws BankAccountNotFoundException, BankAccountInsufficientCreditsException {
        if(!this.checkIfBankAccountExists(bankAccountUuid)) {
            throw new BankAccountNotFoundException();
        }

        this.bankAccounts.get(bankAccountUuid).changeAccountBalance(amount);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean checkIfBankAccountExists(String bankAccountUuid) {
        return this.bankAccounts.containsKey(bankAccountUuid);
    }

    public int countBankAccounts() {
        return this.bankAccounts.size();
    }

    public Set<String> getBankAccountsUuids() {
        return this.bankAccounts.keySet();
    }
}
