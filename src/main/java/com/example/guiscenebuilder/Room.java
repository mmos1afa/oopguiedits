package com.example.guiscenebuilder;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Room {
    private String roomName;
    private boolean available;
    private List<String> bookedtimes;


    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public void addbookedtime(String s, Date d)
    {
        bookedtimes.add(s + " " + d.toString() );
    }


    public Room(String roomName)
    {
        this.roomName = roomName;
        this.available = true;
        this.bookedtimes = new ArrayList<>();
    }
    public String getRoomName()
    {
        return roomName;
    }

    public boolean isAvailable(String s, Date d) {
        return !bookedtimes.contains(s + " " + d.toString());

    }

    public void book() {
        this.available = false;

    }

    public void addBookedTime(String s, Date d)
    {
        if (!bookedtimes.contains(s + " " + d.toString())) {
            bookedtimes.add(s + " " + d.toString());
        }
    }
    public void removeBookedTime(String s, Date d)
    {
        bookedtimes.remove(s + " " + d.toString());
    }
    @Override
    public String toString()
    {
        return roomName;
    }

}


