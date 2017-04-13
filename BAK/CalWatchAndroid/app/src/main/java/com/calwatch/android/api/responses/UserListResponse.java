package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.User;

import java.util.List;

/**
 * Created by Home on 22/1/2559.
 */
public class UserListResponse extends ApiResponse {
    private List<User> users;

    public List<User> getUsers(){return this.users;}
    public void setUsers(List<User> value) {this.users = value;}

    public UserListResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
