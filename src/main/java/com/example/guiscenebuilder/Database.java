package com.example.guiscenebuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
public class Database {
    private static List<Admin> admins = new ArrayList<>();
    private static List<Organizer> organizers = new ArrayList<>();
    private static List<Attendee> attendees = new ArrayList<>();
    private static List<Event> events = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static List<Category> categories = new ArrayList<>();

    public static List<Admin> getAdmins() { return admins; }
    public static List<Organizer> getOrganizers() { return organizers; }
    public static List<Attendee> getAttendees() { return attendees; }
    public static List<Event> getEvents() { return events; }
    public static List<Room> getRooms() { return rooms; }
    public static List<Category> getCategories() { return categories; }

    public static void initializeDummyData() {

        Admin admin = new Admin("a1", "1234", new Date(2025, 1, 2), "SuperAdmin", 40);
        Admin admin2 = new Admin("a2", "4567", new Date(2024, 3, 5), "SuperAdmin", 3);


        Organizer org = new Organizer("o1", "pass", new Date(1997, 4, 5));
        Organizer org2 = new Organizer("o2", "123", new Date(1958, 4, 5));


        Attendee att = new Attendee("at1", "pass", new Date(2007, 12, 23), 1000.0, "123 Street", Gender.MALE, "Music");
        Attendee att2 = new Attendee("hana", "124", new Date(2007, 12, 2), 10000.0, "Rehab", Gender.FEMALE, "Music");

        String time = "Day";
        String time2 = "Night";
        Date date = new Date(2025, 5, 9);
        Date date2 = new Date(2025, 5, 10);
        Room r1 = new Room("Room A");
        Room r2 = new Room("Room B");
        r1.addBookedTime(time, date);
        r1.addBookedTime(time2, date);
        r2.addBookedTime(time2, date2);


        Category cat1 = new Category("Music");
        Category cat2 = new Category("Reading");

        Event sampleEvent = new Event("Prom", org, r1, 600.0,new ArrayList<>(), cat1);
        Event sampleEvent2 = new Event("Graduation", org, r2, 600.0,new ArrayList<>(), cat2);
        Event sampleEvent3 = new Event("Dinner",org,r1,1500,new ArrayList<>(),cat1);

        admins.add(admin);
        admins.add(admin2);
        organizers.add(org);
        organizers.add(org2);
        attendees.add(att);
        attendees.add(att2);
        rooms.add(r1);
        rooms.add(r2);
        categories.add(cat1);
        categories.add(cat2);
        events.add(sampleEvent);
        events.add(sampleEvent2);
        events.add(sampleEvent3);
    }
}

