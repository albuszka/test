package com.buszka.recruitment.fireup;

import com.buszka.recruitment.fireup.exception.BankAccountAlreadyExistsException;
import com.buszka.recruitment.fireup.exception.BankAccountInsufficientCreditsException;
import com.buszka.recruitment.fireup.exception.BankAccountNotFoundException;
import com.buszka.recruitment.fireup.model.BankAccount;
import com.buszka.recruitment.fireup.model.BankAccountHistory;
import com.buszka.recruitment.fireup.repository.BankRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest
@Slf4j
class BankRepositoryTests {

	@Autowired
	private BankRepository bankRepository;

	private BankAccount bankAccount1;
	private BankAccount bankAccount2;

	public BankRepositoryTests() {
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
	void createBankAccountTests() {
		Assertions.assertEquals(this.bankRepository.countBankAccounts(), 0);

		this.bankRepository.createBankAccount(this.bankAccount1);
		this.bankRepository.createBankAccount(this.bankAccount2);

		Assertions.assertEquals(this.bankRepository.countBankAccounts(), 2);
	}

	@Test
	void getBankAccountTests() {
		this.bankRepository.createBankAccount(this.bankAccount1);
		this.bankRepository.createBankAccount(this.bankAccount2);

		BankAccount ba1 = this.bankRepository.getBankAccount(bankAccount1.getUuid());
		BankAccount ba2 = this.bankRepository.getBankAccount(bankAccount2.getUuid());

		log.info(ba1.toString());
		log.info(ba2.toString());

		Assertions.assertNotNull(ba1);
		Assertions.assertNotNull(ba2);

		Assertions.assertEquals(this.bankAccount1.getUuid(), ba1.getUuid());
		Assertions.assertEquals(this.bankAccount1.getOwnerFirstName(), ba1.getOwnerFirstName());
		Assertions.assertEquals(this.bankAccount1.getOwnerLastName(), ba1.getOwnerLastName());
		Assertions.assertEquals(this.bankAccount1.getAmount(), 100);
		Assertions.assertEquals(this.bankAccount1.getCreated(), ba1.getCreated());
		Assertions.assertNotNull(this.bankAccount1.getAccountHistory());
		Assertions.assertEquals(this.bankAccount1.getAccountHistory().size(), 0);

		Assertions.assertEquals(this.bankAccount2.getUuid(), ba2.getUuid());
		Assertions.assertEquals(this.bankAccount2.getOwnerFirstName(), ba2.getOwnerFirstName());
		Assertions.assertEquals(this.bankAccount2.getOwnerLastName(), ba2.getOwnerLastName());
		Assertions.assertEquals(this.bankAccount2.getAmount(), 200);
		Assertions.assertEquals(this.bankAccount2.getCreated(), ba2.getCreated());
		Assertions.assertNotNull(this.bankAccount2.getAccountHistory());
		Assertions.assertEquals(this.bankAccount2.getAccountHistory().size(), 0);

		Throwable e = null;

		try {
			this.bankRepository.createBankAccount(this.bankAccount1);
		} catch (Throwable ex) {
			e = ex;
		}

		Assertions.assertTrue(e instanceof BankAccountAlreadyExistsException);
	}

	@Test
	void getBankAccountTestsNotFound() {
		Throwable e = null;

		try {
			this.bankRepository.getBankAccount("xyz");
		} catch (Throwable ex) {
			e = ex;
		}

		Assertions.assertTrue(e instanceof BankAccountNotFoundException);
	}

	@Test
	void makeBankAccountTransactionAndHistoryTests() {
		this.bankRepository.createBankAccount(this.bankAccount1);

		BankAccount ba = null;

		/* Transakcje */

		// 100 + 50 = 150
		this.bankRepository.makeBankAccountTransaction(bankAccount1.getUuid(), 50);
		ba = this.bankRepository.getBankAccount(bankAccount1.getUuid());
		log.info(ba.toString());
		Assertions.assertEquals(ba.getAmount(), 150);

		// 150 - 150 = 0
		this.bankRepository.makeBankAccountTransaction(bankAccount1.getUuid(), -150);
		ba = this.bankRepository.getBankAccount(bankAccount1.getUuid());
		log.info(ba.toString());
		Assertions.assertEquals(ba.getAmount(), 0);

		Throwable e = null;

		// 0 - 1 = BankAccountInsufficientCreditsException
		try {
			this.bankRepository.makeBankAccountTransaction(bankAccount1.getUuid(), -1);
		} catch (Throwable ex) {
			e = ex;
		}

		/* Historia transakcji */

		Assertions.assertTrue(e instanceof BankAccountInsufficientCreditsException);

		ba = this.bankRepository.getBankAccount(bankAccount1.getUuid());

		ArrayList<BankAccountHistory> bankAccountHistory = ba.getAccountHistory();

		Assertions.assertNotNull(bankAccountHistory);
		Assertions.assertEquals(bankAccountHistory.size(), 2);
		Assertions.assertEquals(bankAccountHistory.get(0).getAmount(), 50);
		Assertions.assertEquals(bankAccountHistory.get(1).getAmount(), -150);
		Assertions.assertNotNull(bankAccountHistory.get(0).getCreated());
		Assertions.assertNotNull(bankAccountHistory.get(1).getCreated());
	}
}
