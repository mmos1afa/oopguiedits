package com.example.guiscenebuilder;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String title;
    private Organizer organizer;
    private Room room;
    private Category category;
    private double price;
    private List<Attendee> attendees = new ArrayList<>();

    public void setPrice(double price)
    {
        this.price = price;
    }
    public double getTicketPrice()
    {
        return price;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public Event(Room room, String title, double price, Category category)
    {
        this.room = room;
        this.title = title;
        this.category = category;
        this.price = price;

    }

    public Room getRoom() {
        return room;
    }

    public Event(String title, Organizer organizer, Room room, double price, List<Attendee> attendees, Category category) {
        this.title = title;
        this.organizer = organizer;
        this.room = room;
        this.category = category;
        this.price = price;
        this.attendees = new ArrayList<>(attendees);
    }

    public void addAttendee(Attendee attendee) {
        attendees.add(attendee);
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setRoom(Room room) {
        this.room = room;
    }


    public double getPrice() {
        return price;
    }
    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    @Override
    public String toString() {
        return "Event Title: " + title + ", Ticket Price: " + price + ", Category: " + (category != null ? category.getName() : "N/A") + ", Room: " + (room != null ? room.getRoomName() : "N/A") + ", Organizer: " + (organizer != null ? organizer.getUsername() : "N/A");
    }

    public String getEventName() {
        return title;
    }
    public String getCategoryName(){
        return category.getName();
    }
}
