package org.java.ticketingplatform.service;

import org.java.ticketingplatform.Utils.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeatOccupiedService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final VenueConfigService venueConfigService;

	public SeatOccupiedService(RedisTemplate<String, Object> redisTemplate,
	                           VenueConfigService venueConfigService) {
		this.redisTemplate = redisTemplate;
		this.venueConfigService = venueConfigService;
	}

	// initialize seats for the event
	public void initializeEventSeat(String eventId, String venueId, String zoneId) {
		// use venue config calculate size
		int rowCount = venueConfigService.getRowCount(venueId, zoneId);
		int seatPerRow = venueConfigService.getSeatPerRow(venueId, zoneId);
		int totalSeats = venueConfigService.getZoneCapacity(venueId, zoneId);

		// initialize bitmap according to zone
		String bitmapKey = RedisKeyUtil.getZoneBitMapKey(eventId, zoneId);
		byte[] initialBitmap = new byte[(totalSeats + 7) / 8];
		redisTemplate.opsForValue().set(bitmapKey, initialBitmap);

		// count for the seat left
		String remainingSeatsKey = RedisKeyUtil.getZoneRemainedSeats(eventId, zoneId);
		redisTemplate.opsForValue().set(remainingSeatsKey, totalSeats);

		// have a row seat remaining
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			String rowKey = RedisKeyUtil.getRowRemainedSeats(eventId, zoneId, rowIndex);
			redisTemplate.opsForValue().set(rowKey, seatPerRow);
		}
	}

	// get covertRow to index
	private int convertRowToIndex(String row) {
		row = row.toUpperCase();
		int index = 0;
		for (int i = 0; i < row.length(); i++) {
			index = index * 26 + (row.charAt(i) - 'A' + 1);
		}
		return index - 1;
	}

	private int calcBitPosition(String venueId, String zoneId, String row, String col) {
		int seatPerRow = venueConfigService.getSeatPerRow(venueId, zoneId);
		int rowIndex = convertRowToIndex(row);
		int colIndex = Integer.parseInt(col) - 1;
		return rowIndex * seatPerRow + colIndex;
	}

	// check if a seat is occupied
	public boolean isSeatOccupied(String venueId, String eventId, String zoneId, String row, String col) {
		int bitPosition = calcBitPosition(venueId, zoneId, row, col);
		String bitmapKey = RedisKeyUtil.getZoneBitMapKey(eventId, zoneId);
		Boolean isOccupied = redisTemplate.opsForValue().getBit(bitmapKey, bitPosition);

		return isOccupied != null && isOccupied;
	}

	// change unoccupied seats to occupy
	public void changeSeatOccupancy(String eventId, String zoneId, String row, String col, String venueId, boolean occupied) {
		int bitPosition = calcBitPosition(venueId, zoneId, row, col); // find position in bitmap
		String bitmapKey = RedisKeyUtil.getZoneBitMapKey(eventId, zoneId); // find key

		// Set bit to set the bit at position in bitmap as occupied.
		// Returns boolean of the original status before set to 'occupied'.
		Boolean currentStatus = redisTemplate.opsForValue().setBit(bitmapKey, bitPosition, occupied);
		// if status before setting is not null and true, previously true
		boolean wasOccupied = currentStatus != null && currentStatus;

		// if not previously occupied, need to update the key count.
		if (wasOccupied != occupied) {
			// get the key for zone remained seat count
			String remainingKey = RedisKeyUtil.getZoneRemainedSeats(eventId, zoneId);
			if (!occupied) { // changed to occupied == false
				redisTemplate.opsForValue().increment(remainingKey);
			} else {
				redisTemplate.opsForValue().decrement(remainingKey);
			}

			int rowIndex = convertRowToIndex(row);
			String rowKey = RedisKeyUtil.getRowRemainedSeats(eventId, zoneId, rowIndex);
			if (!occupied) {
				redisTemplate.opsForValue().increment(rowKey);
			} else {
				redisTemplate.opsForValue().decrement(rowKey);
			}
		}
	}

	//check if the zone is full

	public boolean checkIfZoneFull(String eventId, String zoneId) {
		String remainingKey = RedisKeyUtil.getZoneRemainedSeats(eventId, zoneId);
		Object remaining = redisTemplate.opsForValue().get(remainingKey);
		if (remaining != null) {
			try {
				return Integer.parseInt(remaining.toString()) == 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}
	// check if the row is full
	public boolean checkRowFull(String eventId, String zoneId, String row) {
		int rowIndex = convertRowToIndex(row);
		String rowKey = RedisKeyUtil.getRowRemainedSeats(eventId, zoneId, rowIndex);
		Object remaining = redisTemplate.opsForValue().get(rowKey);
		if (remaining != null) {
			try {
				return Integer.parseInt(remaining.toString()) == 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

}
