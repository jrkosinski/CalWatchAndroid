package com.calwatch.android.api.requests;

import com.calwatch.android.api.models.User;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class SignupRequest extends ApiRequest {
    private User user;

    public User getUser(){
        return this.user;
    }

    public String getUsername() {
        return this.user.getUsername();
    }
    public void setUsername(String value) {
        this.user.setUsername(value);
    }

    public String getPassword() {
        return this.user.getPassword();
    }
    public void setPassword(String value) {
        this.user.setPassword(value);
    }

    public int getTargetCaloriesPerDay() {
        return this.user.getTargetCaloriesPerDay();
    }
    public void setTargetCaloriesPerDay(int value) {
        this.user.setTargetCaloriesPerDay(value);
    }

    public SignupRequest()
    {
        this.user = new User();
    }
}
