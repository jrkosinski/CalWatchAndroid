package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.User;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class UserResponse extends ApiResponse {
    private User user;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful() && this.user != null;
    }

    public UserResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
