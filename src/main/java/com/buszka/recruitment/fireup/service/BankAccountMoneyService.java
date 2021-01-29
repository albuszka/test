package com.buszka.recruitment.fireup.service;

import com.buszka.recruitment.fireup.exception.BankAccountBadRequestException;
import com.buszka.recruitment.fireup.exception.BankAccountInsufficientCreditsException;
import com.buszka.recruitment.fireup.exception.BankAccountNotFoundException;
import com.buszka.recruitment.fireup.exception.BankAccountNothingChangedException;
import com.buszka.recruitment.fireup.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serwis do wykonywania transakcji na kontach bankowych.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountMoneyService {

    private final BankRepository bankRepository;

    /**
     * Wykonuje transakcję na koncie bankowym.
     *
     * @param bankAccountUuid Identyfikator konta.
     * @param amount Kwota transakcji.
     * @throws BankAccountNotFoundException W przypadku gdy konto o wygenerowanym identyfikatorze już istnieje.
     * @throws BankAccountBadRequestException W przypadku błędnego zapytania (błędne parametry lub ich brak).
     * @throws BankAccountInsufficientCreditsException W przypadku braku środków na koncie.
     * @throws BankAccountNothingChangedException W przypadku gdy kwota transakcji jest równa zero.
     */
    @Transactional
    public void makeTransaction(String bankAccountUuid, Integer amount)
            throws BankAccountNotFoundException, BankAccountBadRequestException, BankAccountInsufficientCreditsException, BankAccountNothingChangedException {

        log.debug("Starting makeTransaction with bankAccountUuid: {}, amount: {}", bankAccountUuid, amount);

        if (bankAccountUuid == null || amount == null) {
            log.error("One or more parameters are NULL!");
            throw new BankAccountBadRequestException();
        }

        if (bankAccountUuid.trim().isEmpty()) {
            log.error("One or more parameters are empty!");
            throw new BankAccountBadRequestException();
        }

        if (amount == 0) { // Liczę, że JAVA zrobi sama amount.intValue() == 0
            log.warn("Transaction with zero amount.");
            throw new BankAccountNothingChangedException();
        }

        this.bankRepository.makeBankAccountTransaction(bankAccountUuid, amount);
    }
}
