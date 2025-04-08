package org.java.ticketingplatform.controller;

import org.java.ticketingplatform.model.TicketInfo;
import org.java.ticketingplatform.serviceInterface.TicketServiceInterface;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketPurchaseController.class)
public class TicketPurchaseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TicketServiceInterface ticketService;

	@Test
	public void testGetTicketFound() throws Exception {
		TicketInfo dummy = new TicketInfo(
				"TICKET-123",
				"EVENT-999",
				"1",
				"10",
				"A",
				new Timestamp(System.currentTimeMillis())
		);

		when(ticketService.getTicket(ArgumentMatchers.eq("TICKET-123")))
				.thenReturn(dummy);

		mockMvc.perform(get("/ticket/TICKET-123")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("TICKET-123")));
	}

	@Test
	public void testGetTicketNotFound() throws Exception {
		when(ticketService.getTicket(ArgumentMatchers.anyString()))
				.thenReturn(null);

		mockMvc.perform(get("/ticket/TICKET-404")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}
