package org.java.ticketingplatform.service;

import org.java.ticketingplatform.component.DTO.TicketInfoDTO;
import org.java.ticketingplatform.component.DTO.TicketRespondDTO;
import org.java.ticketingplatform.exception.RowFullException;
import org.java.ticketingplatform.exception.SeatOccupiedException;
import org.java.ticketingplatform.exception.ZoneFullException;
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
	private final SeatOccupiedService seatOccupiedService;

	public TicketService(TicketMapper ticketMapper, TicketDAOInterface ticketDAO, SeatOccupiedService seatOccupiedService) {
		this.ticketMapper = ticketMapper;
		this.ticketDAO = ticketDAO;
		this.seatOccupiedService = seatOccupiedService;
	}

	// transfer input data into Respond DTO object and save to Database through DAO and Mapper
	public TicketRespondDTO createTicket(String venueId, String eventId, String zoneId, String row, String column) {
		redisCheck(venueId, eventId, zoneId, row, column);

		//Ticket DTO, Database Write Model through mapper
		TicketInfoDTO ticketInfoDTO = new TicketInfoDTO(venueId, eventId, zoneId, row, column);
		//turn the information to Mapper to create a DTO object
		TicketCreation ticketCreation = ticketMapper.ticketInfoDTOToTicketCreation(ticketInfoDTO);
		//Use mapper turn DTO into model data and save to database throughDAO
		String savedTicket = ticketDAO.createTicket(ticketCreation);
		// update ID to ticket creation class
		ticketCreation.setId(savedTicket);

		//Redis - Set Redis seat occupancy to True
		seatOccupiedService.changeSeatOccupancy(eventId, zoneId, row, column, venueId, true);

		// Kafka to use MQ sync to SQL

		// use ticketCreationToTicketRespond return respond
		return ticketMapper.ticketCreationToTicketRespondDTO(ticketCreation);
	}

	// call get from DAO
	@Override
	public TicketInfo getTicket(String ticketId) {
		return ticketDAO.getTicketInfoById(ticketId);
	}

	//check Redis if Zone info recorded
	private void redisCheck(String venueId, String eventId, String zoneId, String row, String column) {
		if (seatOccupiedService.checkIfZoneFull(eventId, zoneId)) {
			throw new ZoneFullException("Zone " + zoneId + " is All Occupied");
		}
		if (seatOccupiedService.checkRowFull(eventId, zoneId, row)) {
			throw new RowFullException("Row " + row + " in Zone " + zoneId + " is All Occupied");
		}
		if (seatOccupiedService.isSeatOccupied(venueId, eventId, zoneId, row, column)) {
			throw new SeatOccupiedException("Seat " + row + "-" + column + " is already occupied");
		}
	}


}
