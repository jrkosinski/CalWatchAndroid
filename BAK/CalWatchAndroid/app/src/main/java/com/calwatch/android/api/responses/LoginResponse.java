package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.User;

/**
 * Created by Home on 21/1/2559.
 */
public class LoginResponse extends ResponseBase {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoginResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
