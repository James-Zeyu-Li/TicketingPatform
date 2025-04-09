package org.java.ticketingplatform.controller;


import org.java.ticketingplatform.component.DTO.TicketRespondDTO;
import org.java.ticketingplatform.model.ErrorMessage;
import org.java.ticketingplatform.model.TicketInfo;
import org.java.ticketingplatform.serviceInterface.TicketServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketPurchaseController {

	private final TicketServiceInterface ticketService;

	public TicketPurchaseController(TicketServiceInterface ticketService) {
		this.ticketService = ticketService;
	}

	/**
	 * Create the ticket using the following parameters.
	 *
	 * @param venueId check all stats by venue
	 * @param eventId  eventID used to identify event
	 * @param zoneId   zone as a first level identification Zone from 1 to 100
	 * @param row    Row as a second level identification row A - Z - AA - ZZ
	 * @param column column being used to find the seat column from 1 to 30
	 * @return String for the TicketID
	 */
	@PostMapping
	private ResponseEntity<?> createTicket(
			@RequestParam("venueId") String venueId,
			@RequestParam("eventId") String eventId,
			@RequestParam("zoneId") String zoneId,
			@RequestParam("row") String row,
			@RequestParam("column") String column) {
		if (eventId == null || zoneId == null || row == null || column == null) {
			return ResponseEntity.badRequest()
					.body("event, zone, row, column are required");
		}

		if (eventId.isEmpty() || zoneId.isEmpty() || row.isEmpty() || column.isEmpty()) {
			return ResponseEntity.badRequest()
					.body("event, zone, row, column can not be blank");
		}

		try {
			TicketRespondDTO ticketResponse = ticketService.createTicket(venueId, eventId, zoneId, row, column);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Ticket Created \n" + ticketResponse);
		} catch (Exception e) {
			ErrorMessage error = new ErrorMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error at Controller createTicket: " + error);
		}
	}


	@GetMapping("/{ticketId}")
	public ResponseEntity<?> getTicket(@PathVariable String ticketId) {
		TicketInfo ticket = ticketService.getTicket(ticketId);

		if (ticket == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorMessage("TicketID not found: " + ticketId));
		}

		return ResponseEntity.status(HttpStatus.OK).body("Ticket Get Successful: " + ticket);
	}
}
