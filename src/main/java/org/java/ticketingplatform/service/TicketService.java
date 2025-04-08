package org.java.ticketingplatform.service;

import org.java.ticketingplatform.component.DTO.TicketInfoDTO;
import org.java.ticketingplatform.component.DTO.TicketRespondDTO;
import org.java.ticketingplatform.mapper.TicketMapper;
import org.java.ticketingplatform.model.TicketCreation;
import org.java.ticketingplatform.model.TicketInfo;
import org.java.ticketingplatform.repository.TicketDAOInterface;
import org.java.ticketingplatform.serviceInterface.TicketServiceInterface;
import org.springframework.stereotype.Service;

@Service
public class TicketService implements TicketServiceInterface {

	private final TicketMapper ticketMapper;
	private final TicketDAOInterface ticketDAO;

	public TicketService(TicketMapper ticketMapper, TicketDAOInterface ticketDAO) {
		this.ticketMapper = ticketMapper;
		this.ticketDAO = ticketDAO;
	}

	// transfer input data into Respond DTO object and save to Database through DAO and Mapper
	public TicketRespondDTO createTicket(String event, String zone, String row, String column) {
		TicketInfoDTO ticketInfoDTO = new TicketInfoDTO(event, zone, row, column);

		//turn the information to Mapper to create DTO object
		TicketCreation ticketCreation = ticketMapper.ticketInfoDTOToTicketCreation(ticketInfoDTO);

		//Use mapper turn DTO into model data and save to database throughDAO
		String savedTicket = ticketDAO.createTicket(ticketCreation);

		// update ID to ticket creation class
		ticketCreation.setId(savedTicket);

		// Kafka to use MQ sync to SQL



		// use ticketCreationToTicketRespond return respond
		return ticketMapper.ticketCreationToTicketRespondDTO(ticketCreation);
	}

	// call get from DAO
	@Override
	public TicketInfo getTicket(String ticketId){
		return ticketDAO.getTicketInfoById(ticketId);
	}


}
