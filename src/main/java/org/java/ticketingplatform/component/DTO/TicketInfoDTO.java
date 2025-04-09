package org.java.ticketingplatform.component.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

// Following the model TicketCreation
@Data
@AllArgsConstructor
public class TicketInfoDTO {
	private String eventId;
	private String venueId;
	private String zone;
	private String row;
	private String column;
}
