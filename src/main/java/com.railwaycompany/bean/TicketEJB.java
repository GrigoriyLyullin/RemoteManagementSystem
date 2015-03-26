package com.railwaycompany.bean;

import org.json.JSONArray;

import javax.ejb.Stateless;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TicketEJB {

    public List<String> getAll() {

        String url = "http://localhost:8080/InformationSystem/ticketRest/getAll";
        JSONArray response = null;
        try {
            response = JsonReader.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> ticketList = new ArrayList<>();
        ticketList.add("testTicket");
        if (response != null) {
            ticketList.add(response.toString());
        }
        return ticketList;
    }
}
