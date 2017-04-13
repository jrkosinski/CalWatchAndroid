package com.calwatch.android.api;

import com.calwatch.android.api.models.AppSettings;
import com.calwatch.android.api.models.ErrorInfo;
import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.api.models.Report;
import com.calwatch.android.api.models.User;
import com.calwatch.android.api.requests.ApiRequest;
import com.calwatch.android.api.requests.LoginRequest;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.api.responses.AppSettingsResponse;
import com.calwatch.android.api.responses.MealResponse;
import com.calwatch.android.api.responses.MealListResponse;
import com.calwatch.android.api.responses.ReportResponse;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.api.responses.UserListResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.util.StringUtil;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class ApiService {
    private static final String LogTag = "ApiService";
    private static final String loginUri = "login";
    private static final String logoutUri = "logout";
    private static final String usersUri = "users";
    private static final String mealsUri = "meals";
    private static final String appSettingsUri = "appSettings";
    private static final String reportUri = "report";
    private static String apiUriBase;
    private static UserListResponse cachedUsersList;
    private static final boolean useCompression = true;

    public static void configure(final String uriBase) {
        apiUriBase = uriBase;
    }

    //Login
    public static UserResponse login(final LoginRequest request) {
        UserResponse response = SendRequestWithContent(
                constructLoginUri(),
                "POST",
                request,
                new ParseUserResponseCallback(),
                null
        );

        if (response != null && response.isSuccessful()) {
            doPostLogin(response.getUser(), request.getPassword());
        }

        return response;
    }

    //Logout
    public static ApiResponse logout() {
        return SendRequestWithContent(
                constructLogoutUri(),
                "POST",
                new ApiRequest(),
                new ParseGenericResponseCallback(),
                null
        );
    }

    //GetUsers
    public static UserListResponse getUsers() {
        if (cachedUsersList != null)
            return cachedUsersList;

        final String uri = constructUsersUri();
        final String method = "GET";
        final ParseUserListResponseCallback callback = new ParseUserListResponseCallback();

        RetryCallback<UserListResponse> retryCallback = new RetryCallback<UserListResponse>(){
            @Override
            protected UserListResponse execute()  {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //GetUser
    public static UserResponse getUser(final int userId)
    {
        final String uri = constructUserUri(userId);
        final String method = "GET";
        final ParseUserResponseCallback callback = new ParseUserResponseCallback();

        RetryCallback<UserResponse> retryCallback = new RetryCallback<UserResponse>(){
            @Override
            protected UserResponse execute()  {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //CreateUser
    public static UserResponse createUser(final User user)
    {
        final String uri = constructUsersUri();
        final String method = "POST";
        final ParseCreateUserResponseCallback callback = new ParseCreateUserResponseCallback(user.getPassword());

        return SendRequestWithContent(uri, method, user, callback, null);
    }

    //UpdateUser
    public static UserResponse updateUser(final int userId, final User user)
    {
        final String uri = constructUserUri(userId);
        final String method = "PUT";
        final ParseUserResponseCallback callback = new ParseUserResponseCallback();

        RetryCallback<UserResponse> retryCallback = new RetryCallback<UserResponse>(){
            @Override
            protected UserResponse execute() {
                return SendRequestWithContent(uri, method, user, callback, null);
            }
        };

        return SendRequestWithContent(uri, method, user, callback, retryCallback);
    }

    //GetMeals
    public static MealListResponse getMeals(final int userId) {
        return getMeals(userId, null);
    }

    //GetMeals
    public static MealListResponse getMeals(final int userId, final FilterParams filterParams) {
        final String uri = (filterParams == null || filterParams.isEmpty()) ?
                constructMealsUri(userId) :
                constructFilteredMealsUri(userId, filterParams);
        final String method = "GET";
        final ParseMealListResponseCallback callback = new ParseMealListResponseCallback();

        RetryCallback<MealListResponse> retryCallback = new RetryCallback<MealListResponse>(){
            @Override
            protected MealListResponse execute() {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //GetMeal
    public static MealResponse getMeal(final int userId, final int mealId) {
        final String uri = constructMealUri(userId, mealId);
        final String method = "GET";
        final ParseMealResponseCallback callback = new ParseMealResponseCallback();

        RetryCallback<MealResponse> retryCallback = new RetryCallback<MealResponse>(){
            @Override
            protected MealResponse execute() {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //CreateMeal
    public static MealResponse createMeal(final int userId, final Meal meal) {
        final String uri = constructMealsUri(userId);
        final String method = "POST";
        final ParseMealResponseCallback callback = new ParseMealResponseCallback();

        RetryCallback<MealResponse> retryCallback = new RetryCallback<MealResponse>(){
            @Override
            protected MealResponse execute() {
                return SendRequestWithContent(uri, method, meal, callback, null);
            }
        };

        return SendRequestWithContent(uri, method, meal, callback, retryCallback);
    }

    //UpdateMeal
    public static MealResponse updateMeal(final int userId, final int mealId, final Meal meal) {
        final String uri = constructMealUri(userId, mealId);
        final String method = "PUT";
        final ParseMealResponseCallback callback = new ParseMealResponseCallback();

        RetryCallback<MealResponse> retryCallback = new RetryCallback<MealResponse>(){
            @Override
            protected MealResponse execute() {
                return SendRequestWithContent(uri, method, meal, callback, null);
            }
        };

        return SendRequestWithContent(uri, method, meal, callback, retryCallback);
    }

    //DeleteMeal
    public static ApiResponse deleteMeal(final int userId, final int mealId) {
        final String uri = constructMealUri(userId, mealId);
        final String method = "DELETE";
        final ParseGenericResponseCallback callback = new ParseGenericResponseCallback();

        RetryCallback<ApiResponse> retryCallback = new RetryCallback<ApiResponse>(){
            @Override
            protected ApiResponse execute() {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //GetReport
    public static ReportResponse getReport(final int userId, final FilterParams filterParams) {
        final String uri = constructReportUri(userId, filterParams);
        final String method = "GET";
        final ParseReportResponseCallback callback = new ParseReportResponseCallback();

        RetryCallback<ReportResponse> retryCallback = new RetryCallback<ReportResponse>(){
            @Override
            protected ReportResponse execute() {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //GetAppSettings
    public static AppSettingsResponse getAppSettings() {
        AppSettingsResponse output = SendRequest(
                constructAppSettingsUri(),
                "GET",
                new ParseAppSettingsResponseCallback(),
                null
        );

        return output;
    }


    private static <TResponse extends ApiResponse> TResponse SendRequestWithContent(
            final String uri,
            final String method,
            final Object request,
            final IParseResponseCallback<TResponse> callback,
            final RetryCallback<TResponse> retryCallback
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
            return parseResponse(connection, callback, retryCallback);
        } catch (Exception e) {
            //Log.e("ApiService", e.getMessage());
        }

        return output;
    }

    private static <TResponse extends ApiResponse> TResponse SendRequest (
            final String uri,
            final String method,
            final IParseResponseCallback<TResponse> callback,
            final RetryCallback<TResponse> retryCallback
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
            output = parseResponse(connection, callback, retryCallback);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return output;
    }

    private static void setUpRequest(HttpURLConnection connection, String method, byte[] data) throws ProtocolException
    {
        connection.setRequestMethod(method);

        if (useCompression)
            connection.setRequestProperty("Accept-Encoding", "gzip");
        else
            connection.setRequestProperty("Accept-Encoding", "identity");

        if (data != null)
        {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
        }

        connection.setUseCaches(false);
        if (!StringUtil.isNullOrEmpty(LocalStorage.getAuthToken()))
            connection.setRequestProperty("AuthToken", LocalStorage.getAuthToken());
    }

    private static <TResponse extends ApiResponse> TResponse parseResponse(
            HttpURLConnection connection,
            IParseResponseCallback<TResponse> callback,
            RetryCallback<TResponse> retryCallback
    )  throws IOException
    {
        TResponse output = null;
        int responseCode = connection.getResponseCode();
        int responseLength = connection.getContentLength();

        if (responseCode == 401)
        {
            if (retryCallback != null) {
                output = tryAutoLogin(retryCallback);
                if (output != null)
                    return output;
            }
        }

        //prepare to read the output
        if (responseLength > 0)
        {
            char[] responseChars = new char[responseLength];
            InputStream in = responseIsSuccess(responseCode) ?
                    connection.getInputStream() :
                    connection.getErrorStream();

            if (useCompression)
                in = new GZIPInputStream(in);

            InputStreamReader ips = new InputStreamReader(in);
            StringBuilder buffer = new StringBuilder();

            try {
                int l;

                while ((l = ips.read(responseChars)) != -1) {
                    buffer.append(responseChars, 0, l);
                }

            } finally {
                ips.close();
            }

            String responseString = buffer.toString();

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
        LocalStorage.setCurrentTargetUser(user);
        LocalStorage.setPassword(password);
        LocalStorage.setPermissionLevel(user.getPermissionLevel());
        cachedUsersList = null;
    }

    private static <TResponse extends ApiResponse>TResponse tryAutoLogin(RetryCallback<TResponse> retryCallback)
    {
        UserResponse loginResponse = login(new LoginRequest(LocalStorage.getUsername(), LocalStorage.getPassword()));
        if (loginResponse != null && loginResponse.isSuccessful())
        {
            if (retryCallback != null)
                return retryCallback.execute();
        }

        return null;
    }


    private static String constructUri(String uri)
    {
        String baseUrl = apiUriBase;
        if (!baseUrl.endsWith("/"))
            baseUrl += "/";

        return baseUrl + uri;
    }

    private static String constructAppSettingsUri()
    {
        return constructUri(appSettingsUri);
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

    private static String constructFilteredMealsUri(int userId, FilterParams filterParams)
    {
        String uri = constructMealsUri(userId);
        uri += constructFilterQuerystring(filterParams);

        return uri;
    }

    private static String constructReportUri(int userId)
    {
        String uri = constructUserUri(userId);
        uri += "/" + reportUri;
        return uri;
    }

    private static String constructReportUri(int userId, FilterParams filterParams)
    {
        String uri = constructReportUri(userId);
        uri += constructFilterQuerystring(filterParams);

        return uri;
    }

    private static String constructFilterQuerystring(FilterParams filterParams)
    {
        boolean hasParams = false;
        String querystring = "";

        if (filterParams != null) {
            try {
                if (filterParams.getDateFrom() != null && filterParams.getDateFrom().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "dateFrom=" + (filterParams.getDateFrom());
                    hasParams = true;
                }
                if (filterParams.getDateTo() != null && filterParams.getDateTo().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "dateTo=" + (filterParams.getDateTo());
                    hasParams = true;
                }
                if (filterParams.getTimeFrom() != null && filterParams.getTimeFrom().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "timeFrom=" + (filterParams.getTimeFrom());
                    hasParams = true;
                }
                if (filterParams.getTimeTo() != null && filterParams.getTimeTo().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "timeTo=" + (filterParams.getTimeTo());
                    hasParams = true;
                }
            } catch (Exception e) {
                Log.e(LogTag, e.getMessage());
            }
        }

        return querystring;
    }

    private static boolean responseIsSuccess(int responseCode)
    {
        return (responseCode >= 200 && responseCode < 300);
    }


    //TODO: consolidate these
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

    private static class ParseCreateUserResponseCallback extends ParseUserResponseCallback
    {
        private String password;

        public ParseCreateUserResponseCallback(String password)
        {
            this.password = password;
        }

        public UserResponse parseJson(String json, int responseCode)
        {
            UserResponse output = super.parseJson(json, responseCode);

            if (output != null && output.isSuccessful()) {
                LocalStorage.clear();
                doPostLogin(output.getUser(), password);
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
                    if (array != null) {
                        output.setUsers(new ArrayList<User>(Arrays.asList(array)));
                        cachedUsersList = output;
                    }
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseReportResponseCallback implements IParseResponseCallback<ReportResponse>
    {
        public ReportResponse parseJson(String json, int responseCode)
        {
            ReportResponse output = new ReportResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    Report report = (Report) new Gson().fromJson(json, Report.class);
                    output.setReport(report);
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseAppSettingsResponseCallback implements  IParseResponseCallback<AppSettingsResponse>
    {
        public AppSettingsResponse parseJson(String json, int responseCode)
        {
            AppSettingsResponse output = new AppSettingsResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    AppSettings appSettings = (AppSettings) new Gson().fromJson(json, AppSettings.class);
                    output.setAppSettings(appSettings);
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class RetryCallback<TResponse extends  ApiResponse>
    {
        protected TResponse execute()
        {
            return null;
        }
    }
}
