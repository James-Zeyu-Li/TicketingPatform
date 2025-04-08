package org.java.ticketingplatform.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeatOccupiedService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final VenueConfigService venueConfigService; // 注入配置服务

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
		int totalSeats = rowCount * seatPerRow;

		// initialize bitmap according to zone
		String bitmapKey = String.format("event:%s:zone:%s:occupied", eventId, zoneId);
		byte[] initialBitmap = new byte[(totalSeats + 7) / 8];
		redisTemplate.opsForValue().set(bitmapKey, initialBitmap);

		// count for the seat left
		String remainingKey = String.format("event:%s:zone:%s:remaining", eventId, zoneId);
		redisTemplate.opsForValue().set(remainingKey, initialBitmap);
	}

	// check if zone still has capacity
	// check
	public boolean isSeatOccupied(String eventId, String zoneId, String row, String col, String venueId) {
		int rowIndex = convertRowToIndex(row);
		int colIndex = Integer.parseInt(col) - 1;
		int seatPerRow = venueConfigService.getSeatPerRow(venueId, zoneId);

		int bitPosition = rowIndex * seatPerRow + colIndex;
		String bitmapKey = String.format("event:%s:zone:%s:occupied", eventId, zoneId);
		Boolean occupied = redisTemplate.opsForValue().getBit(bitmapKey, bitPosition);

		return occupied != null && occupied;
	}

	// change unoccupied seats to occupied
	public void changeSeatOccupied(String eventId, String zoneId, String row, String col, String venueId, boolean occupied) {
		int seatPerRow = venueConfigService.getSeatPerRow(venueId, zoneId);
		int rowIndex = convertRowToIndex(row);
		int colIndex = Integer.parseInt(col) - 1;
		int bitPosition = rowIndex * seatPerRow + colIndex;
		String bitmapKey = String.format("event:%s:zone:%s:occupied", eventId, zoneId);
		redisTemplate.opsForValue().setBit(bitmapKey, bitPosition, occupied);
	}

	//check if the zone is full
	public boolean checkZoneFull(String eventId, String venueId, String zoneId) {
		int rowCount = venueConfigService.getRowCount(venueId, zoneId);
		int seatPerRow = venueConfigService.getSeatPerRow(venueId, zoneId);
		int totalSeats = rowCount * seatPerRow;
		String bitmapKey = String.format("event:%s:zone:%s:full", eventId, zoneId);

		Long occupiedCount = redisTemplate.execute((RedisCallback<Long>) connection ->
				connection.bitCount(bitmapKey.getBytes())
		);

		return occupiedCount != null && occupiedCount.intValue() >= totalSeats;
	}


	// check if the row is full
	public boolean checkRowFull(String eventId, String venueId, String zoneId) {
		return false;
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
}
