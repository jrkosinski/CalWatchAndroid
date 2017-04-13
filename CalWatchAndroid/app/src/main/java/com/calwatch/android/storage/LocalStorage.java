package com.calwatch.android.storage;

import android.app.Activity;

import com.calwatch.android.api.models.AppSettings;
import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.api.models.User;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by John R. Kosinski on 23/1/2559.
 * Get/set access to global property values, each of which may be persisted in the local database, or simply
 * in app memory (depending on the property).
 */
public class LocalStorage {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String AuthTokenKey = "auth_token";
    private static final String UserIdKey = "user_id";
    private static final String UsernameKey = "username";
    private static final String PasswordKey = "Password";
    private static final String RememberPasswordKey = "remember_password";
    private static final String PermissionLevelKey = "permission_level";

    private static String authToken;
    private static String username;
    private static String password;
    private static String permissionLevel;
    private static int userId;
    private static FilterParams filterParams;
    private static User currentTargetUser;
    private static AppSettings appSettings;

    /*
    Gets/sets the auth token of the currently logged in user.
     */
    public static String getAuthToken() {
        if (sharedPreferences != null) {
            if (authToken == null)
                authToken = sharedPreferences.getString(AuthTokenKey, "");
        }
        return authToken;
    }
    public static void setAuthToken(String value) {
        authToken = value;

        if (editor != null) {
            editor.putString(AuthTokenKey, value);
            editor.commit();
        }
    }

    /*
    Gets/sets the saved id of the currently (or most recently) logged in user.
     */
    public static int getUserId(){
        if (sharedPreferences != null) {
            if (userId == 0)
                userId = sharedPreferences.getInt(UserIdKey, 0);
        }

        return userId;
    }
    public static void setUserId(int value) {
        userId = value;
        if (editor != null) {
            editor.putInt(UserIdKey, value);
            editor.commit();
        }
    }

    /*
    Gets/sets the saved username of the currently (or most recently) logged in user,
    to repopulate the login form.
     */
    public static String getUsername(){
        if (sharedPreferences != null) {
            if (username == null)
                username = sharedPreferences.getString(UsernameKey, "");
        }

        return username;
    }
    public static void setUsername(String value) {
        username = value;
        if (editor != null) {
            editor.putString(UsernameKey, value);
            editor.commit();
        }
    }

    /*
    Gets/sets the saved permission level of the currently (or most recently) logged in user,
    to repopulate the login form.
     */
    public static String getPermissionLevel() {
        if (sharedPreferences != null) {
            if (permissionLevel == null)
                permissionLevel = sharedPreferences.getString(PermissionLevelKey, "");
        }

        return permissionLevel;
    }
    public static void setPermissionLevel(String value) {
        permissionLevel = value;

        if (editor != null) {
            editor.putString(PermissionLevelKey, value);
            editor.commit();
        }
    }


    /*
    Gets/sets the saved Password of the currently (or most recently) logged in user,
    to repopulate the login form.
     */
    public static String getPassword() {
        if (sharedPreferences != null) {
            if (password == null)
                password = sharedPreferences.getString(PasswordKey, "");
        }

        return password;
    }
    public static void setPassword(String value) {
        password = value;
        if (editor != null) {
            editor.putString(PasswordKey, value);
            editor.commit();
        }
    }

    /*
    Gets/sets the id of the current user being edited/viewed. Normally this is the same as the
    currently logged in user, but may be different for users who have permission to view/edit
    other users.
     */
    public static int getCurrentTargetUserId() {
        if (currentTargetUser == null)
            return 0;

        return currentTargetUser.getId();
    }

    /*
    Gets/sets a boolean value indicating whether the application should remember the user's
    Password and repopulate the form with it at login time. Default is false.
     */
    public static boolean getRememberPassword(){
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(RememberPasswordKey, false);
        }
        return false;
    }
    public static void setRememberPassword(boolean value) {
        if (editor != null) {
            editor.putBoolean(RememberPasswordKey, value);
            editor.commit();
        }
    }

    public static FilterParams getFilterParams()  {
        return filterParams;
    }
    public static void setFilterParams(FilterParams value) {
        if (value != null)
        {
            if (value.isEmpty())
                value = null;
        }
        filterParams = value;
    }

    public static User getCurrentTargetUser()  {
        return currentTargetUser;
    }
    public static void setCurrentTargetUser(User value) { currentTargetUser = value;}

    public static AppSettings getAppSettings()  {
        return appSettings;
    }
    public static void setAppSettings(AppSettings value) { appSettings = value;}

    /*
    Clears the database.
     */
    public static void clear()
    {
        setCurrentTargetUser(null);
        setUserId(0);
        setPassword("");
        setUsername("");
        setAuthToken("");
        setRememberPassword(false);
    }

    public static void initialize(Context context)
    {
        sharedPreferences = context.getSharedPreferences("CalWatchStorage", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}
