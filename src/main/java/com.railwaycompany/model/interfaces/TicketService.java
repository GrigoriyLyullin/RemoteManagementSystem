package com.railwaycompany.model.interfaces;

import com.railwaycompany.model.entities.Ticket;

import java.util.Date;
import java.util.List;

public interface TicketService {

    List<Ticket> getTickets(Date dateFrom, Date dateTo, String token);

}
