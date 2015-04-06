package com.railwaycompany.model.entities;

import java.io.Serializable;

public class Train implements Serializable {

    private int id;
    private int number;
    private int seats;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
