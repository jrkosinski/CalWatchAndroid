package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.User;

/**
 * Created by Home on 21/1/2559.
 */
public class UserResponse extends ApiResponse {
    private User user;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public UserResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
