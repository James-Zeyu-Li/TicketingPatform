package org.java.ticketingplatform.mapper;

import org.java.ticketingplatform.component.DTO.TicketInfoDTO;
import org.java.ticketingplatform.component.DTO.TicketRespondDTO;
import org.java.ticketingplatform.model.TicketCreation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

	@Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
	@Mapping(target = "status", constant = "CREATED")
	@Mapping(target = "createTime", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
	TicketCreation ticketInfoDTOToTicketCreation(TicketInfoDTO dto);

	@Mapping(source = "id", target = "ticketId")
	TicketRespondDTO ticketCreationToTicketRespondDTO(TicketCreation ticket);
}
