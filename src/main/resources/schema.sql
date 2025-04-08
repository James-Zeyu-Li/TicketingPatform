-- event information table.
CREATE TABLE Event
(
    eventId    VARCHAR(36) PRIMARY KEY,
    eventName  VARCHAR(100) NOT NULL,
    eventType  VARCHAR(50),
    eventDate  DATE         NOT NULL,
    eventCity  VARCHAR(100),
    venue      VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- zone information table with assigned ticket price
CREATE TABLE Zone
(
    zoneId      INT            NOT NULL, -- 1 - 100
    eventId     VARCHAR(36)    NOT NULL,
    capacity    INT            NOT NULL,
    ticketPrice DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (eventId, zoneId),
    FOREIGN KEY (eventId) REFERENCES Event (eventId) ON DELETE CASCADE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Ticket info table
CREATE TABLE TicketInfo
(
    ticketId    VARCHAR(36) PRIMARY KEY,
    eventId     VARCHAR(36) NOT NULL,
    zoneID      INT         NOT NULL,
    seat_column VARCHAR(5),
    seat_row    VARCHAR(10),
    createTime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (eventId) REFERENCES Event (eventId) ON DELETE CASCADE,
    FOREIGN KEY (eventId, zoneKey) REFERENCES Zone (eventId, zoneId) ON DELETE CASCADE,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- index between different tables
CREATE INDEX Idx_event_date ON Event (eventDate);
CREATE INDEX Idx_ticket_eventId ON TicketInfo (eventId);
CREATE INDEX Idx_zone_eventId ON Zone (eventId);