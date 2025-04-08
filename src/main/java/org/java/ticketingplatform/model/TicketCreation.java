package org.java.ticketingplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class TicketCreation {
	private String id;
	private String eventId;
	private String zone;
	private String column;
	private String row;
	private String status; //"CREATED", "PAID", "CANCELLED"
	private Timestamp createTime;
}
