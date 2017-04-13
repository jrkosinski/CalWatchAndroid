package com.calwatch.android.api.requests;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class LoginRequest extends ApiRequest {
    private String username;
    private String password;

    public String getUsername(){
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUsername(String value){
        this.username = value;
    }

    public void setPassword(String value){
        this.password = value;
    }

    public LoginRequest(final String username, final String password)
    {
        this.setUsername(username);
        this.setPassword(password);
    }
}
