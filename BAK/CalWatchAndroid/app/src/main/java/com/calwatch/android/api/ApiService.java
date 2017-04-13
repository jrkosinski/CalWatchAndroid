package com.calwatch.android.api;

import com.calwatch.android.R;
import com.calwatch.android.api.models.ErrorInfo;
import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.models.MealFilterParams;
import com.calwatch.android.api.models.User;
import com.calwatch.android.api.requests.LoginRequest;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.api.responses.MealResponse;
import com.calwatch.android.api.responses.MealListResponse;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.api.responses.UserListResponse;
import com.calwatch.android.storage.LocalStorage;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Home on 21/1/2559.
 */
public class ApiService {
    private static final String LogTag = "ApiService";

    private static final String loginUri = "login";
    private static final String logoutUri = "logout";
    private static final String usersUri = "users";
    private static final String mealsUri = "meals";
    private static String apiUriBase;

    public static void configure(String uriBase)
    {
        apiUriBase = uriBase;
    }

    //Login
    public static UserResponse login(LoginRequest request)
    {
        UserResponse response = SendRequestWithContent(
                constructLoginUri(),
                "POST",
                request,
                new ParseUserResponseCallback()
        );

        if (response != null && response.isSuccessful()) {
            doPostLogin(response.getUser(), request.getPassword());
        }

        return response;
    }

    //Logout
    public static ApiResponse logout()
    {
        return SendRequest(
                constructLogoutUri(),
                "POST",
                new ParseGenericResponseCallback()
        );
    }

    //GetUsers
    public static UserListResponse getUsers()
    {
        return SendRequest(
                constructUsersUri(),
                "GET",
                new ParseUserListResponseCallback()
        );
    }

    //GetUser
    public static UserResponse getUser(int userId)
    {
        return SendRequest(
                constructUserUri(userId),
                "GET",
                new ParseUserResponseCallback()
        );
    }

    //CreateUser
    public static UserResponse createUser(User user)
    {
        UserResponse response = SendRequestWithContent(
                constructUsersUri(),
                "POST",
                user,
                new ParseUserResponseCallback()
        );

        if (response != null && response.isSuccessful()) {
            LocalStorage.clear();
            doPostLogin(response.getUser(), user.getPassword());
        }

        return response;
    }

    //UpdateUser
    public static UserResponse updateUser(int userId, User user)
    {
        return SendRequestWithContent(
                constructUserUri(userId),
                "PUT",
                user,
                new ParseUserResponseCallback()
        );
    }

    //GetMeals
    public static MealListResponse getMeals(int userId, MealFilterParams filterParams)
    {
        String uri = (filterParams == null || filterParams.isEmpty()) ?
                constructMealsUri(userId) :
                constructFilteredMealsUri(userId, filterParams);

        return SendRequest(
                uri,
                "GET",
                new ParseMealListResponseCallback()
        );
    }

    //GetMeal
    public static MealResponse getMeal(int userId, int mealId)
    {
        return SendRequest(
                constructMealUri(userId, mealId),
                "GET",
                new ParseMealResponseCallback()
        );
    }

    //CreateMeal
    public static MealResponse createMeal(int userId, Meal meal)
    {
        return SendRequestWithContent(
                constructMealsUri(userId),
                "POST",
                meal,
                new ParseMealResponseCallback()
        );
    }

    //UpdateMeal
    public static MealResponse updateMeal(int userId, int mealId, Meal meal)
    {
        return SendRequestWithContent(
                constructMealUri(userId, mealId),
                "PUT",
                meal,
                new ParseMealResponseCallback()
        );
    }

    //DeleteMeal
    public static ApiResponse deleteMeal(int userId, int mealId)
    {
        ApiResponse output = SendRequest(
                constructMealUri(userId, mealId),
                "DELETE",
                new ParseGenericResponseCallback()
        );

        return output;
    }


    private static <TResponse extends ApiResponse> TResponse SendRequestWithContent(
            String uri,
            String method,
            Object request,
            IParseResponseCallback<TResponse> callback
        )
    {
        TResponse output = null;

        try {
            //serialize input
            Gson gson = new Gson();
            String req = gson.toJson(request);
            byte[] sendData = req.getBytes();

            //make connection
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set up to send
            setUpRequest(connection, method, sendData);

            //send data
            OutputStream out = connection.getOutputStream();
            out.write(sendData);
            out.close();

            //get response
            output = parseResponse(connection, callback);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return output;
    }

    private static <TResponse extends ApiResponse> TResponse SendRequest (
            String uri,
            String method,
            IParseResponseCallback<TResponse> callback
    )
    {
        TResponse output = null;

        try {
            //make connection
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set up request
            setUpRequest(connection, method, null);

            //get response
            output = parseResponse(connection, callback);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return output;
    }

    private static void setUpRequest(HttpURLConnection connection, String method, byte[] data) throws ProtocolException
    {
        connection.setRequestMethod(method);
        if (data != null)
        {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
        }

        connection.setUseCaches(false);
        connection.setRequestProperty("AuthToken", LocalStorage.getAuthToken());
    }

    private static <TResponse extends ApiResponse> TResponse parseResponse(
            HttpURLConnection connection,
            IParseResponseCallback<TResponse> callback
    )  throws IOException
    {
        TResponse output = null;
        int responseCode = connection.getResponseCode();
        int responseLength = connection.getContentLength();

        //prepare to read the output
        if (responseLength > 0)
        {
            char[] responseChars = new char[responseLength];
            InputStream in = responseIsSuccess(responseCode) ?
                    connection.getInputStream() :
                    connection.getErrorStream();

            //read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader((in)));
            reader.read(responseChars);
            String responseString = new String(responseChars);

            //parse response json
            if (callback != null) {
                output = callback.parseJson(responseString, responseCode);
            }
            else {
                //output = (TResponse) gson.fromJson(responseString, TResponse);
            }
        }
        else
            output = callback.parseJson(null, responseCode);

        return output;
    }

    private static void doPostLogin(User user, String password)
    {
        LocalStorage.setAuthToken(user.getAuthToken());
        LocalStorage.setUsername(user.getUsername());
        LocalStorage.setUserId(user.getId());
        LocalStorage.setCurrentTargetUserId(0);
        LocalStorage.setCurrentTargetUsername("");
        LocalStorage.setPassword(password);
        LocalStorage.setPermissionLevel(user.getPermissionLevel());
    }

    private static String constructUri(String uri)
    {
        String baseUrl = apiUriBase;
        if (!baseUrl.endsWith("/"))
            baseUrl += "/";

        return baseUrl + uri;
    }

    private static String constructLoginUri()
    {
        return constructUri(loginUri);
    }

    private static String constructLogoutUri()
    {
        return constructUri(logoutUri);
    }

    private static String constructUsersUri()
    {
        return constructUserUri(-1);
    }

    private static String constructUserUri(int userId)
    {
        String uri = usersUri;
        if (userId >= 0)
            uri += "/" + Integer.toString(userId);

        return constructUri(uri);
    }

    private static String constructMealsUri(int userId)
    {
        return constructMealUri(userId, 0);
    }

    private static String constructMealUri(int userId, int mealId)
    {
        String uri = constructUserUri(userId);
        uri += "/" + mealsUri;

        if (mealId > 0)
            uri += "/" + Integer.toString(mealId);

        return uri;
    }

    private static String constructFilteredMealsUri(int userId, MealFilterParams filterParams)
    {
        String uri = constructMealsUri(userId);

        boolean hasParams = false;

        try {
            if (filterParams.getFromDate() != null && filterParams.getFromDate().length() > 0) {
                uri += (hasParams ? "&" : "?") + "dateFrom=" + (filterParams.getFromDate());
                hasParams = true;
            }
            if (filterParams.getToDate() != null && filterParams.getToDate().length() > 0) {
                uri += (hasParams ? "&" : "?") + "dateTo=" + (filterParams.getToDate());
                hasParams = true;
            }
            if (filterParams.getFromTime() != null && filterParams.getFromTime().length() > 0) {
                uri += (hasParams ? "&" : "?") + "timeFrom=" + (filterParams.getFromTime());
                hasParams = true;
            }
            if (filterParams.getToTime() != null && filterParams.getToTime().length() > 0) {
                uri += (hasParams ? "&" : "?") + "timeTo=" + (filterParams.getToTime());
                hasParams = true;
            }
        }
        catch (Exception e)
        {
            Log.e(LogTag, e.getMessage());
        }

        return uri;
    }


    private static interface IParseResponseCallback<T extends ApiResponse>
    {
        public T parseJson(String json, int responseCode);
    }

    private static class ParseMealResponseCallback implements IParseResponseCallback<MealResponse>
    {
        public MealResponse parseJson(String json, int responseCode)
        {
            MealResponse output = new MealResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    Meal meal = (Meal) new Gson().fromJson(json, Meal.class);
                    output.setMeal(meal);
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseUserResponseCallback implements IParseResponseCallback<UserResponse>
    {
        public UserResponse parseJson(String json, int responseCode)
        {
            UserResponse output = new UserResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    User user = (User) new Gson().fromJson(json, User.class);
                    output.setUser(user);
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseGenericResponseCallback implements IParseResponseCallback<ApiResponse>
    {
        public ApiResponse parseJson(String json, int responseCode)
        {
            ApiResponse output = new ApiResponse(responseCode);
            if (json != null) {
                if (!responseIsSuccess(responseCode)) {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseMealListResponseCallback implements IParseResponseCallback<MealListResponse>
    {
        public MealListResponse parseJson(String json, int responseCode)
        {
            MealListResponse output = new MealListResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    Meal[] array = new Gson().fromJson(json, Meal[].class);
                    if (array != null)
                        output.setMeals(new ArrayList<Meal>(Arrays.asList(array)));
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseUserListResponseCallback implements IParseResponseCallback<UserListResponse>
    {
        public UserListResponse parseJson(String json, int responseCode)
        {
            UserListResponse output = new UserListResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    User[] array = new Gson().fromJson(json, User[].class);
                    if (array != null)
                        output.setUsers(new ArrayList<User>(Arrays.asList(array)));
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static boolean responseIsSuccess(int responseCode)
    {
        return (responseCode >= 200 && responseCode < 300);
    }
}
