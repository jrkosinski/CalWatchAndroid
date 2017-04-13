package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class UserListResponse extends ApiResponse {
    private List<User> users;

    public List<User> getUsers(){return this.users;}
    public void setUsers(List<User> value) {this.users = value;}

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful();
    }

    public UserListResponse(int responseCode) {
        super.setResponseCode(responseCode);
        this.users = new ArrayList<>();
    }
}
