package org.java.ticketingplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VenueConfigService {
	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public VenueConfigService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// Add the venue and venue's zone into Redis
	public void initializeVenueZone(String venueId, String zoneId, int rowCount, int colCount) {
		String rowCountKey = String.format("venue:%s:zone:%s:rowCount", venueId, zoneId);
		redisTemplate.opsForValue().set(rowCountKey, rowCount); // row count for zone

		String seatPerRowKey = String.format("venue:%s:zone:%s:seatPerRow", venueId, zoneId);
		redisTemplate.opsForValue().set(seatPerRowKey, colCount); // column for zone

		String capacityKey = String.format("venue:%s:zone:%s:capacity", venueId, zoneId);
		redisTemplate.opsForValue().set(capacityKey, rowCount * colCount); //Zone capacity

//		String zoneSetKey = String.format("venue:%s:zone:%s", venueId, zoneId);
		redisTemplate.opsForSet().add("venue:" + venueId + ":zones", zoneId);
	}

	// get all zones form the Venue
	public Set<Object> getVenueZones(String venueId) {
		return redisTemplate.opsForSet().members("venue:" + venueId + ":zones");
	}

	// get how many Rows per zone
	public int getRowCount(String venueId, String zoneId) {
		String rowCountKey = String.format("venue:%s:zone:%s", venueId, zoneId);
		Object value = redisTemplate.opsForValue().get(rowCountKey);
		if (value != null) {
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	// to get the zone and find the seat in the row
	public int getSeatPerRow(String venueId, String zoneId) {
		String seatKey = String.format("venue:%s:zone:%s:seatPerRow", venueId, zoneId);
		Object value = redisTemplate.opsForValue().get(seatKey);
		if (value != null) {
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	// get zone configuration
	public int getZoneCapacity(String venueId, String zoneId) {
		String zoneKey = String.format("venue:%s:zone:%s:capacity", venueId, zoneId);
		Object value = redisTemplate.opsForValue().get(zoneKey);
		if (value != null) {
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}
}
