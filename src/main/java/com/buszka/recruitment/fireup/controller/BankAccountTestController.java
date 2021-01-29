package com.buszka.recruitment.fireup.controller;

import com.buszka.recruitment.fireup.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Na potrzeby testów.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class BankAccountTestController {

    private final BankAccountService bankAccountService;

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces="text/plain")
    public String test() {
        String test = this.bankAccountService.getAllAccountsWithHistory();

        log.info("======= TEST =======");
        log.info(test);
        log.info("====================");

        return test;
    }
}
