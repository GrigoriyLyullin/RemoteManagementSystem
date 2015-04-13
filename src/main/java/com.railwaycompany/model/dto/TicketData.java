package com.railwaycompany.model.dto;

import java.util.Date;

public class TicketData {

    private int ticketId;
    private Date saleTime;
    private String stationFrom;
    private String stationTo;
    private Date departureDate;
    private Date arrivalDate;
    private int trainNumber;
    private TrainData trainData;
    private PassengerData passengerData;

    public String getStationFrom() {
        return stationFrom;
    }

    public void setStationFrom(String stationFrom) {
        this.stationFrom = stationFrom;
    }

    public String getStationTo() {
        return stationTo;
    }

    public void setStationTo(String stationTo) {
        this.stationTo = stationTo;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public PassengerData getPassengerData() {
        return passengerData;
    }

    public void setPassengerData(PassengerData passengerData) {
        this.passengerData = passengerData;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public TrainData getTrainData() {
        return trainData;
    }

    public void setTrainData(TrainData trainData) {
        this.trainData = trainData;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }
}
