package com.calwatch.android.api.requests;

import com.calwatch.android.api.models.User;

/**
 * Created by Home on 23/1/2559.
 */
public class UpdateUserRequest extends RequestBase {
    private User user;

    public User getUser(){
        return this.user;
    }

    public int getTargetCaloriesPerDay(){
        return user.getTargetCaloriesPerDay();
    }
    public void setTargetCaloriesPerDay(int value){
        user.setTargetCaloriesPerDay(value);
    }

    public String getPermissionLevel(){
        return user.getPermissionLevel();
    }
    public void setPermissionLevel(String value){
        user.setPermissionLevel(value);
    }

    public UpdateUserRequest(){
        this.user = new User();
    }
}
