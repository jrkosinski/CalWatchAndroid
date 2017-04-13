package com.calwatch.android.storage;

import android.app.Activity;
import com.calwatch.android.api.models.MealFilterParams;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Home on 23/1/2559.
 */
public class LocalStorage {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String AuthTokenKey = "auth_token";
    private static final String UserIdKey = "user_id";
    private static final String UsernameKey = "username";
    private static final String PasswordKey = "password";
    private static final String RememberPasswordKey = "remember_password";
    private static final String PermissionLevelKey = "permission_level";

    private static String authToken;
    private static String username;
    private static String password;
    private static String permissionLevel;
    private static String currentTargetUsername;
    private static int currentTargetUserId;
    private static int userId;
    private static MealFilterParams mealFilterParams;

    /*
    Gets/sets the auth token of the currently logged in user.
     */
    public static String getAuthToken() {
        if (authToken == null)
            authToken = sharedPreferences.getString(AuthTokenKey, "");

        return authToken;
    }
    public static void setAuthToken(String value) {
        authToken = value;
        editor.putString(AuthTokenKey, value);
        editor.commit();
    }

    /*
    Gets/sets the saved id of the currently (or most recently) logged in user.
     */
    public static int getUserId(){
        if (userId == 0)
            userId = sharedPreferences.getInt(UserIdKey, 0);

        return userId;
    }
    public static void setUserId(int value) {
        userId = value;
        editor.putInt(UserIdKey, value);
        editor.commit();
    }

    /*
    Gets/sets the saved username of the currently (or most recently) logged in user,
    to repopulate the login form.
     */
    public static String getUsername(){
        if (username == null)
            username = sharedPreferences.getString(UsernameKey, "");

        return username;
    }
    public static void setUsername(String value) {
        username = value;
        editor.putString(UsernameKey, value);
        editor.commit();
    }

    /*
    Gets/sets the saved permission level of the currently (or most recently) logged in user,
    to repopulate the login form.
     */
    public static String getPermissionLevel() {
        if (permissionLevel == null)
            permissionLevel = sharedPreferences.getString(PermissionLevelKey, "");

        return permissionLevel;
    }
    public static void setPermissionLevel(String value) {
        permissionLevel = value;
        editor.putString(PermissionLevelKey, value);
        editor.commit();
    }


    /*
    Gets/sets the saved password of the currently (or most recently) logged in user,
    to repopulate the login form.
     */
    public static String getPassword() {
        if (password == null)
            password = sharedPreferences.getString(PasswordKey, "");

        return password;
    }
    public static void setPassword(String value) {
        password = value;
        editor.putString(PasswordKey, value);
        editor.commit();
    }

    /*
    Gets/sets the id of the current user being edited/viewed. Normally this is the same as the
    currently logged in user, but may be different for users who have permission to view/edit
    other users.
     */
    public static int getCurrentTargetUserId() {
        if (currentTargetUserId <= 0)
            currentTargetUserId = userId;

        return currentTargetUserId;
    }
    public static void setCurrentTargetUserId(int value) {
        currentTargetUserId = value;
    }

    public static String getCurrentTargetUsername() {
        return currentTargetUsername;
    }
    public static void setCurrentTargetUsername(String value) {
        currentTargetUsername = value;
    }

    /*
    Gets/sets a boolean value indicating whether the application should remember the user's
    password and repopulate the form with it at login time. Default is false.
     */
    public static boolean getRememberPassword(){
        return sharedPreferences.getBoolean(RememberPasswordKey, false);
    }
    public static void setRememberPassword(boolean value) {
        editor.putBoolean(RememberPasswordKey, value);
        editor.commit();
    }

    public static MealFilterParams getMealFilterParams()  {
        return mealFilterParams;
    }
    public static void setMealFilterParams(MealFilterParams value) {
        if (value != null)
        {
            if (value.isEmpty())
                value = null;
        }
        mealFilterParams = value;
    }

    /*
    Clears the database.
     */
    public static void clear()
    {
        setCurrentTargetUserId(0);
        setUserId(0);
        setPassword("");
        setUsername("");
        setAuthToken("");
        setCurrentTargetUsername("");
        setRememberPassword(false);
    }

    public static void initialize(Context context)
    {
        sharedPreferences = context.getSharedPreferences("CalWatchStorage", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}
