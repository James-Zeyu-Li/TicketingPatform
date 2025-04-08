package org.java.ticketingplatform.serviceInterface;

import org.java.ticketingplatform.component.DTO.TicketRespondDTO;
import org.java.ticketingplatform.model.TicketInfo;
import org.springframework.http.ResponseEntity;

public interface TicketServiceInterface {
	TicketRespondDTO createTicket(String event, String zone, String Row, String column);
	TicketInfo getTicket(String ticketId);
}
