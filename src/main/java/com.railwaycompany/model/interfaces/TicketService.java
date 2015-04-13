package com.railwaycompany.model.interfaces;

import com.railwaycompany.model.dto.TicketData;

import java.util.Date;
import java.util.List;

public interface TicketService {

    List<TicketData> getTickets(Date dateFrom, Date dateTo, String token);

}
