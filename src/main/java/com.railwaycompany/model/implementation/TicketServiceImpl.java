package com.railwaycompany.model.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.railwaycompany.model.dto.TicketData;
import com.railwaycompany.model.interfaces.TicketService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class TicketServiceImpl implements TicketService {

    private static final Logger LOG = Logger.getLogger(TicketServiceImpl.class);
    private static final String URL = "http://localhost:8080/InformationSystem/tickets";
    private static final String TOKEN_PARAM = "Rest-Token";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String TIME_FORMAT = "HH:mm";
    private final DateFormat date = new SimpleDateFormat(DATE_FORMAT);
    private final DateFormat time = new SimpleDateFormat(TIME_FORMAT);

    @Override
    public List<TicketData> getTickets(Date dateFrom, Date dateTo, String token) {
        CloseableHttpClient httpClient = null;
        StringBuilder result = null;
        HttpGet getRequest = new HttpGet(URL + "?" + "dateFrom=" + date.format(dateFrom) + "&&" + "dateTo=" +
                date.format(dateTo) + "&&" + "timeFrom=" + time.format(dateFrom) + "&&" + "timeTo=" +
                time.format(dateTo));
        try {
            httpClient = HttpClientBuilder.create().build();
            getRequest.addHeader("accept", "application/json");
            getRequest.addHeader("Content-Type", "text/html; charset=UTF-8");
            getRequest.addHeader(TOKEN_PARAM, token);

            HttpResponse response = httpClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));
                String currLine;
                result = new StringBuilder();
                while ((currLine = br.readLine()) != null) {
                    result.append(currLine);
                }
            } else {
                LOG.warn("Response status code: " + statusCode + " instead of 200");
            }
        } catch (IOException e) {
            LOG.warn("Cannot execute GET request: " + getRequest.toString(), e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    LOG.warn("Cannot close HttpClient", e);
                }
            }
        }
        if (result != null) {
            Type listType = new TypeToken<ArrayList<TicketData>>() {
            }.getType();
            Gson gson = new GsonBuilder().setDateFormat(DATETIME_FORMAT).create();
            return gson.fromJson(result.toString(), listType);
        }
        return null;
    }
}