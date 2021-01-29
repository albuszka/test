package com.buszka.recruitment.fireup;

import com.buszka.recruitment.fireup.exception.*;
import com.buszka.recruitment.fireup.model.BankAccount;
import com.buszka.recruitment.fireup.model.BankAccountHistory;
import com.buszka.recruitment.fireup.service.BankAccountMoneyService;
import com.buszka.recruitment.fireup.service.BankAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest
public class BankServiceTests {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankAccountMoneyService bankAccountMoneyService;

    private BankAccount bankAccount1;
    private BankAccount bankAccount2;

    public BankServiceTests() {
        this.bankAccount1 = BankAccount
                .builder()
                .ownerFirstName("Imię 1")
                .ownerLastName("Nazwisko 1")
                .created(LocalDateTime.now())
                .uuid(UUID.randomUUID().toString())
                .amount(100)
                .accountHistory(new ArrayList<BankAccountHistory>())
                .build();

        this.bankAccount2 = BankAccount
                .builder()
                .ownerFirstName("Imię 2")
                .ownerLastName("Nazwisko 2")
                .created(LocalDateTime.now())
                .uuid(UUID.randomUUID().toString())
                .amount(200)
                .accountHistory(new ArrayList<BankAccountHistory>())
                .build();
    }

    @Test
    void bankAccountServiceTests() {
        String uuid = null;
        BankAccount bankAccount = null;
        Throwable e = null;

        // createBankAccount - OK
        try {
            e = null;
            uuid = this.bankAccountService.createBankAccount("Imię", "Nazwisko");
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertNull(e);
        Assertions.assertNotNull(uuid);

        // createBankAccount - BankAccountBadRequestException
        try {
            e = null;
            uuid = this.bankAccountService.createBankAccount(" ", "Nazwisko");
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountBadRequestException);

        try {
            e = null;
            uuid = this.bankAccountService.createBankAccount("Imię", " ");
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountBadRequestException);

        // getBankAccount - OK
        try {
            e = null;
            bankAccount = this.bankAccountService.getBankAccount(uuid);
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertNull(e);
        Assertions.assertNotNull(bankAccount);
        Assertions.assertEquals(bankAccount.getUuid(), uuid);
        Assertions.assertEquals(bankAccount.getOwnerFirstName(), "Imię");
        Assertions.assertEquals(bankAccount.getOwnerLastName(), "Nazwisko");
        Assertions.assertEquals(bankAccount.getAmount(), 0);
        Assertions.assertNotNull(bankAccount.getCreated());
        Assertions.assertNotNull(bankAccount.getAccountHistory());
        Assertions.assertEquals(bankAccount.getAccountHistory().size(), 0);

        // getBankAccount - BankAccountNotFoundException
        try {
            e = null;
            this.bankAccountService.getBankAccount("xyz");
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountNotFoundException);

        // getBankAccount - BankAccountBadRequestException
        try {
            e = null;
            this.bankAccountService.getBankAccount(" ");
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountBadRequestException);
    }

    @Test
    void bankAccountMoneyService() {
        BankAccount bankAccount = null;
        Throwable e = null;
        String uuid = null;

        uuid = this.bankAccountService.createBankAccount("Imię", "Nazwisko");

        // 0 + 100 = 100
        this.bankAccountMoneyService.makeTransaction(uuid, 100);
        bankAccount = this.bankAccountService.getBankAccount(uuid);
        Assertions.assertEquals(bankAccount.getAmount(), 100);
        Assertions.assertNotNull(bankAccount.getAccountHistory());
        Assertions.assertEquals(bankAccount.getAccountHistory().size(), 1);
        Assertions.assertEquals(bankAccount.getAccountHistory().get(0).getAmount(), 100);
        Assertions.assertNotNull(bankAccount.getAccountHistory().get(0).getCreated());

        // 100 - 100 = 0
        this.bankAccountMoneyService.makeTransaction(uuid, -100);
        bankAccount = this.bankAccountService.getBankAccount(uuid);
        Assertions.assertEquals(bankAccount.getAmount(), 0);
        Assertions.assertNotNull(bankAccount.getAccountHistory());
        Assertions.assertEquals(bankAccount.getAccountHistory().size(), 2);
        Assertions.assertEquals(bankAccount.getAccountHistory().get(1).getAmount(), -100);
        Assertions.assertNotNull(bankAccount.getAccountHistory().get(1).getCreated());

        // 0 - 0 = BankAccountNothingChangedException
        try {
            e = null;
            this.bankAccountMoneyService.makeTransaction(uuid, 0);
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountNothingChangedException);
        bankAccount = this.bankAccountService.getBankAccount(uuid);
        Assertions.assertEquals(bankAccount.getAmount(), 0);
        Assertions.assertNotNull(bankAccount.getAccountHistory());
        Assertions.assertEquals(bankAccount.getAccountHistory().size(), 2);

        // 0 - 1 = BankAccountInsufficientCreditsException
        try {
            e = null;
            this.bankAccountMoneyService.makeTransaction(uuid, -1);
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountInsufficientCreditsException);
        bankAccount = this.bankAccountService.getBankAccount(uuid);
        Assertions.assertEquals(bankAccount.getAmount(), 0);
        Assertions.assertNotNull(bankAccount.getAccountHistory());
        Assertions.assertEquals(bankAccount.getAccountHistory().size(), 2);

        // BankAccountBadRequestException
        try {
            e = null;
            this.bankAccountMoneyService.makeTransaction(" ", 1);
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountBadRequestException);

        // BankAccountNotFoundException
        try {
            e = null;
            this.bankAccountMoneyService.makeTransaction("xyz", 1);
        } catch (Throwable ex) {
            e = ex;
        }
        Assertions.assertTrue(e instanceof BankAccountNotFoundException);
    }
}
