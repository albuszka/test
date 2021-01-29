package com.buszka.recruitment.fireup.service;

import com.buszka.recruitment.fireup.exception.BankAccountAlreadyExistsException;
import com.buszka.recruitment.fireup.exception.BankAccountBadRequestException;
import com.buszka.recruitment.fireup.exception.BankAccountNotFoundException;
import com.buszka.recruitment.fireup.model.BankAccount;
import com.buszka.recruitment.fireup.model.BankAccountHistory;
import com.buszka.recruitment.fireup.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serwis do zarządzania kontami bankowymi.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private final BankRepository bankRepository;

    /**
     * Tworzy nowe konto bankowe.
     *
     * @param ownerFirstName Imię właściciela konta.
     * @param ownerLastName Nazwisko właściciela konta.
     * @return Identyfikator utworzonego konta.
     * @throws BankAccountAlreadyExistsException W przypadku gdy konto o wygenerowanym identyfikatorze już istnieje.
     * @throws BankAccountBadRequestException W przypadku błędnego zapytania (błędne parametry lub ich brak).
     */
    @Transactional
    public String createBankAccount(String ownerFirstName, String ownerLastName) throws BankAccountAlreadyExistsException, BankAccountBadRequestException {
        log.debug("Starting createBankAccount with ownerFirstName: {}, ownerLastName: {}", ownerFirstName, ownerLastName);

        if (ownerFirstName == null || ownerLastName == null) {
            log.error("One or more parameters are NULL!");
            throw new BankAccountBadRequestException();
        }

        if (ownerFirstName.trim().isEmpty() || ownerLastName.trim().isEmpty()) {
            log.error("One or more parameters are empty!");
            throw new BankAccountBadRequestException();
        }

        BankAccount bankAccount = BankAccount
                .builder()
                .ownerFirstName(ownerFirstName)
                .ownerLastName(ownerLastName)
                .created(LocalDateTime.now())
                .uuid(UUID.randomUUID().toString())
                .amount(0)
                .accountHistory(new ArrayList<BankAccountHistory>())
                .build();

        this.bankRepository.createBankAccount(bankAccount);

        log.debug("Bank account created: {}", bankAccount); // Oczywiście normalnie nie pchamy takich rzeczy w logi - wrażliwe dane.

        return bankAccount.getUuid();
    }

    /**
     * Wyszukuje i zwraca konto bankowe o zadanym identyfikatorze.
     *
     * @param bankAccountUuid Identyfikator szukanego konta.
     * @return Konto bankowe.
     * @throws BankAccountNotFoundException W przypadku gdy konto o zadanym identyfikatorze nie zostanie odnalezione.
     * @throws BankAccountBadRequestException W przypadku błędnego zapytania (błędne parametry lub ich brak).
     */
    @Transactional
    public BankAccount getBankAccount(String bankAccountUuid) throws BankAccountNotFoundException, BankAccountBadRequestException {
        log.debug("Starting getBankAccount with bankAccountUuid: {}", bankAccountUuid);

        if (bankAccountUuid == null) {
            log.error("One or more parameters are NULL!");
            throw new BankAccountBadRequestException();
        }

        if (bankAccountUuid.trim().isEmpty()) {
            log.error("One or more parameters are empty!");
            throw new BankAccountBadRequestException();
        }

        BankAccount bankAccount = this.bankRepository.getBankAccount(bankAccountUuid);

        log.debug("Bank account found: {}", bankAccount); // Oczywiście normalnie nie pchamy takich rzeczy w logi - wrażliwe dane.

        return bankAccount;
    }

    /**
     * Testowo...
     *
     * @return Zawartość tablicy z kontami bankowymi wraz z ich historią w formie tekstowej.
     */
    public String getAllAccountsWithHistory() {
        StringBuilder sb = new StringBuilder();

        sb.append("Konta bankowe:\n\n");

        Set<String> uuids = this.bankRepository.getBankAccountsUuids();

        if (uuids != null && !uuids.isEmpty()) {
            for (String uuid : uuids) {
                try {
                    sb.append(this.bankRepository.getBankAccount(uuid).toString()).append("\n\n");
                } catch (Exception e) {
                    // BankAccountNotFoundException, NullPointerException - to się nie wydarzy...
                    sb.append("Error: ").append(e.getMessage()).append("\n\n");;
                }
            }
        }

        return sb.toString();
    }
}
