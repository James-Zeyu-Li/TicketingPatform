package org.java.ticketingplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Zone {
	private String zoneID;
	private String zoneName;
	private Integer ticketPrice;
	private Integer rowCount;
	private Integer colCount;
	private String rowName;
	private String columnName;
}
