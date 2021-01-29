package com.buszka.recruitment.fireup.controller;

import com.buszka.recruitment.fireup.exception.BankAccountAlreadyExistsException;
import com.buszka.recruitment.fireup.exception.BankAccountBadRequestException;
import com.buszka.recruitment.fireup.exception.BankAccountNotFoundException;
import com.buszka.recruitment.fireup.model.BankAccount;
import com.buszka.recruitment.fireup.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Służy do zarządzania kontami bankowymi.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/accounts")
    public String createBankAccount(@RequestParam String ownerFirstName, @RequestParam String ownerLastName) {
        log.debug("Request with ownerFirstName: {}, ownerLastName: {}", ownerFirstName, ownerLastName);

        try {
            String bankAccountUuid = this.bankAccountService.createBankAccount(ownerFirstName, ownerLastName);
            return bankAccountUuid;
        } catch (BankAccountBadRequestException e) {
            log.error("Error!", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        catch (BankAccountAlreadyExistsException e) {
            log.error("Error!", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @GetMapping("/accounts/{bankAccountUuid}")
    public Integer getSBankAccountAmount(@PathVariable String bankAccountUuid) {
        log.debug("Request with bankAccountUuid: {}", bankAccountUuid);

        try {
            BankAccount bankAccount = this.bankAccountService.getBankAccount(bankAccountUuid);
            return bankAccount.getAmount();
        } catch (BankAccountBadRequestException e) {
            log.error("Error!", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        catch (BankAccountNotFoundException e) {
            log.warn("Warning.", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
