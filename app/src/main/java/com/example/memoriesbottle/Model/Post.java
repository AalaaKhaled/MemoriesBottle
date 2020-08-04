package com.example.memoriesbottle.Model;

public class Post {
    private String postId;
    private String postImage;
    private String description;
    private String publisher;
    private String locationId;

    public Post(String postId, String postImage, String description, String publisher, String locationId) {
        this.postId = postId;
        this.postImage = postImage;
        this.description = description;
        this.publisher = publisher;
        this.locationId = locationId;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
