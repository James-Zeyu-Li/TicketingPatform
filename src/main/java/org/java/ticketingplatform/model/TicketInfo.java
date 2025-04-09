package org.java.ticketingplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketInfo {
	private String ticketId;
	private String venueId; //foreign key
	private String eventID; //foreign key
	private String zoneId; //foreign key
	private String column;
	private String row;
	private Timestamp createTime;
}
