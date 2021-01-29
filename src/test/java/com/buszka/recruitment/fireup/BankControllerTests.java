package com.buszka.recruitment.fireup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void accountCreateAndReturnTests() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/accounts")
					.param("ownerFirstName", "Imię")
					.param("ownerLastName", "Nazwisko")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		String uuid = result.getResponse().getContentAsString();

		result = this.mockMvc.perform(get("/accounts/" + uuid))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		Assertions.assertEquals(result.getResponse().getContentAsString(), "0");
	}

	@Test
	void accountErrorsTests() throws Exception {
		// POST
		this.mockMvc.perform(post("/accounts")
					.param("ownerFirstName", "Imię")
				)
				.andDo(print())
				.andExpect(status().isBadRequest());

		this.mockMvc.perform(post("/accounts")
					.param("ownerLastName", "Nazwisko")
				)
				.andDo(print())
				.andExpect(status().isBadRequest());

		this.mockMvc.perform(post("/accounts")
					.param("ownerFirstName", " ")
				)
				.andDo(print())
				.andExpect(status().isBadRequest());

		this.mockMvc.perform(post("/accounts")
					.param("ownerLastName", " ")
				)
				.andDo(print())
				.andExpect(status().isBadRequest());

		// GET
		this.mockMvc.perform(get("/accounts/xyz"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void transactionTests() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/accounts")
					.param("ownerFirstName", "Imię")
					.param("ownerLastName", "Nazwisko")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		String uuid = result.getResponse().getContentAsString();

		// 0 + 50 = 50
		this.mockMvc.perform(post("/money")
					.param("bankAccountUuid", uuid)
					.param("amount", "50")
				)
				.andDo(print())
				.andExpect(status().isOk());

		// 50 - 50 = 0
		this.mockMvc.perform(post("/money")
					.param("bankAccountUuid", uuid)
					.param("amount", "-50")
				)
				.andDo(print())
				.andExpect(status().isOk());

		// 0 - 1 = HTTP-406
		this.mockMvc.perform(post("/money")
					.param("bankAccountUuid", uuid)
					.param("amount", "-1")
				)
				.andDo(print())
				.andExpect(status().isNotAcceptable());

		// 0 + 0 = HTTP-304
		this.mockMvc.perform(post("/money")
					.param("bankAccountUuid", uuid)
					.param("amount", "0")
				)
				.andDo(print())
				.andExpect(status().isNotModified());

		// HTTP-404
		this.mockMvc.perform(post("/money")
					.param("bankAccountUuid", "xyz")
					.param("amount", "1")
				)
				.andDo(print())
				.andExpect(status().isNotFound());
	}
}
