package org.java.ticketingplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Event {
	private String venueID; //foreign
	private String eventId; //foreign
	private String eventName;
	private String eventType;
	private LocalDate eventDate;
	private String eventCity;
}
