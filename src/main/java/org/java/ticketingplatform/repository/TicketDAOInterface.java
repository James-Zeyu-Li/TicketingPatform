package org.java.ticketingplatform.repository;

import org.java.ticketingplatform.component.DTO.TicketInfoDTO;
import org.java.ticketingplatform.mapper.TicketMapper;
import org.java.ticketingplatform.model.TicketCreation;
import org.java.ticketingplatform.model.TicketInfo;

public interface TicketDAOInterface {
	String createTicket(TicketCreation ticket);
	TicketInfo getTicketInfoById(String id);
}
