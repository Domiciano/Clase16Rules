package co.domi.clase10.model;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class User implements Serializable {
    private String id;
    private String username;
    private String city;
    private String email;
    private String photoId;

    public User() {
    }

    public User(String id, String username, String city, String email) {
        this.id = id;
        this.username = username;
        this.city = city;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return this.username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
