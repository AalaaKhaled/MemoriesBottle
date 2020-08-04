package com.example.memoriesbottle.Model;

public class Room {
    String id;
    String name;
    String description;
    String publisher;
    String room_img;

    public Room(String id, String name, String description, String publisher, String room_img) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.publisher = publisher;
        this.room_img = room_img;
    }

    public Room() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRoom_img() {
        return room_img;
    }

    public void setRoom_img(String room_img) {
        this.room_img = room_img;
    }
}
