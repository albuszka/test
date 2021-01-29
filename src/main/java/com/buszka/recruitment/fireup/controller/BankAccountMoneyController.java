package com.buszka.recruitment.fireup.controller;

import com.buszka.recruitment.fireup.exception.BankAccountBadRequestException;
import com.buszka.recruitment.fireup.exception.BankAccountInsufficientCreditsException;
import com.buszka.recruitment.fireup.exception.BankAccountNotFoundException;
import com.buszka.recruitment.fireup.exception.BankAccountNothingChangedException;
import com.buszka.recruitment.fireup.service.BankAccountMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Służy do wykonywania transakcji na kontach bankowych.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class BankAccountMoneyController {

    private final BankAccountMoneyService bankAccountMoneyService;

    @PostMapping("/money")
    public void makeTransaction(@RequestParam String bankAccountUuid, @RequestParam Integer amount) {
        log.debug("Request with bankAccountUuid: {}, amount: {}", bankAccountUuid, amount);

        try {
            this.bankAccountMoneyService.makeTransaction(bankAccountUuid, amount);
        } catch (BankAccountNotFoundException e) {
            log.warn("Warning.", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (BankAccountBadRequestException e) {
            log.error("Error!", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (BankAccountInsufficientCreditsException e) {
            log.warn("Warning.", e);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e);
        } catch (BankAccountNothingChangedException e) {
            log.warn("Warning.", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, e.getMessage(), e);
        }
    }
}
