package com.calwatch.android.api.models;

/**
 * Created by Home on 21/1/2559.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String permissionLevel;
    private String authToken;
    private int targetCaloriesPerDay;
    //private Date dateCreated;
    //private Date dateModified;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String value) {
        this.username = value;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String value) {
        this.password = value;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }
    public void setPermissionLevel(String value) {
        this.permissionLevel = value;
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String value) {
        this.authToken = value;
    }

    public int getTargetCaloriesPerDay() {
        return targetCaloriesPerDay;
    }
    public void setTargetCaloriesPerDay(int value) {
        this.targetCaloriesPerDay = value;
    }

    /*
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date value) {
        this.dateCreated = value;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date value) {
        this.dateModified = value;
    }
    */
}
