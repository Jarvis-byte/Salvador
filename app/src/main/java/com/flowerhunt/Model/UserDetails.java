package com.flowerhunt.Model;

public class UserDetails {
   private String name;
    private String Uid;

    public UserDetails(String name, String uid) {
        this.name = name;
        Uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
