package org.java.ticketingplatform.repository;

import org.java.ticketingplatform.model.TicketCreation;
import org.java.ticketingplatform.model.TicketInfo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TicketDAO implements TicketDAOInterface {

	private static final String dynamoTABLE_NAME = "ticketCreation";
	private static final String sqlTABLE_NAME = "ticketInfo";
	private final DynamoDbClient dynamoDbClient;
	private final DataSource sqlDataSource;

	public TicketDAO(DynamoDbClient dynamoDbClient, DataSource sqlDataSource) {
		this.dynamoDbClient = dynamoDbClient;
		this.sqlDataSource = sqlDataSource;
	}

	public String createTicket(TicketCreation ticket) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("id", AttributeValue.fromS(ticket.getId()));
		item.put("eventId", AttributeValue.fromS(ticket.getEventId()));
		item.put("zone", AttributeValue.fromS(ticket.getZone()));
		item.put("row", AttributeValue.fromS(ticket.getRow()));
		item.put("column", AttributeValue.fromS(ticket.getColumn()));
		item.put("status", AttributeValue.fromS(ticket.getStatus()));
		item.put("createTime", AttributeValue.fromS(ticket.getCreateTime().toString()));

		PutItemRequest request = PutItemRequest.builder()
				.tableName(dynamoTABLE_NAME)
				.item(item)
				.build();

		dynamoDbClient.putItem(request);
		return ticket.getId();
	}

	@Override
	public TicketInfo getTicketInfoById(String id) {
		String ticketInfo = "SELECT * FROM " + sqlTABLE_NAME + " WHERE id = ?";

		try (Connection conn = sqlDataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement(ticketInfo);

			statement.setString(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TicketInfo(
							resultSet.getString("ticketID"),
							resultSet.getString("eventID"),
							resultSet.getString("zone"),
							resultSet.getString("column"),
							resultSet.getString("row"),
							resultSet.getTimestamp("createTime"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error querying ticket info: " + e.getMessage(), e);
		}
		return null;
	}
}
