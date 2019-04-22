package com.example.signupactivity;

import android.net.Uri;

public class Model_ListUserItem {


    private String uID;
    private String name;
    private String email;
    private Uri uri;

    public Model_ListUserItem()
    {

    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getuID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Uri getUri() {
        return uri;
    }
}
