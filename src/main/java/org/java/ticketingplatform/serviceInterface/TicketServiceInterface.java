package org.java.ticketingplatform.serviceInterface;

import org.java.ticketingplatform.component.DTO.TicketRespondDTO;
import org.java.ticketingplatform.model.TicketInfo;
import org.springframework.http.ResponseEntity;

public interface TicketServiceInterface {
	TicketRespondDTO createTicket(String venueId, String eventId, String zoneId, String Row, String column);
	// call get from DAO
	TicketInfo getTicket(String ticketId);
}
