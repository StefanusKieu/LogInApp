package com.example.user.loginapp;

public class UserInformation {

    public String name;
    public String email;
    public String password;
    public String imageUrl;

    public UserInformation(){
            //jangan delete
    }

    public UserInformation(String name, String email, String password, String imageUrl){
        this.name=name;
        this.email=email;
        this.password=password;
        this.imageUrl=imageUrl;//Maybe I don't need this line
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
