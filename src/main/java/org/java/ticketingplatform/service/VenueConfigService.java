package org.java.ticketingplatform.service;

import org.java.ticketingplatform.Utils.RedisKeyUtil;
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
		// row count in a zone for the venue
		String rowCountKey = RedisKeyUtil.getRowCountKey(venueId, zoneId);
		redisTemplate.opsForValue().set(rowCountKey, rowCount); // row count for zone

		// seat count for a zone for a row
		String seatPerRowKey = RedisKeyUtil.getSeatPerRowKey(venueId, zoneId);
		redisTemplate.opsForValue().set(seatPerRowKey, colCount); // column for zone

		// get the capacity for zone
		String capacityKey = RedisKeyUtil.getZoneCapacityKey(venueId, zoneId);
		redisTemplate.opsForValue().set(capacityKey, rowCount * colCount); //Zone capacity

		// get all zones in the set for venue
		String venueZonesKey = RedisKeyUtil.getZoneSetKey(venueId);
		redisTemplate.opsForSet().add(venueZonesKey, zoneId);
	}

	// get all zones from the Venue
	public Set<Object> getVenueZones(String venueId) {
		String venueZonesKey = RedisKeyUtil.getZoneSetKey(venueId);
		return redisTemplate.opsForSet().members(venueZonesKey);
	}

	public int getRowCount(String venueId, String zoneId) {
		String rowCountKey = RedisKeyUtil.getRowCountKey(venueId, zoneId);
		return getIntValue(rowCountKey);
	}

	// to get the zone and find the seat in the row
	public int getSeatPerRow(String venueId, String zoneId) {
		String seatKey = RedisKeyUtil.getSeatPerRowKey(venueId, zoneId);
		return getIntValue(seatKey);
	}

	// get zone configuration
	public int getZoneCapacity(String venueId, String zoneId) {
		String zoneKey = RedisKeyUtil.getZoneCapacityKey(venueId, zoneId);
		return getIntValue(zoneKey);
	}

	private int getIntValue(String key) {
		Object value = redisTemplate.opsForValue().get(key);
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
