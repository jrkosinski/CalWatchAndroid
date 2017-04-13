package com.calwatch.android.tests.testsupport;

import org.junit.Assert;

import com.calwatch.android.api.ApiService;
import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.api.models.User;
import com.calwatch.android.api.requests.LoginRequest;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.api.responses.MealListResponse;
import com.calwatch.android.api.responses.MealResponse;
import com.calwatch.android.api.responses.UserListResponse;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.util.StringUtil;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */
public class TestLogic  {

    public static void signUp()
    {
        //create new user
        User user = new User();
        user.setPassword(TestData.getPassword());
        user.setTargetCaloriesPerDay(100);
        UserResponse response = createUser(user.getPassword(), user.getTargetCaloriesPerDay());

        assert(response.isSuccessful());
        assert(response.getUser() != null);

        User newUser = response.getUser();
        TestData.setTestUser(newUser);

        //test values in returned user
        assert(newUser.getId() > 0);
        Assert.assertEquals(100, newUser.getTargetCaloriesPerDay());
        assert(!StringUtil.isNullOrEmpty(newUser.getAuthToken()));

        //test values in local storage
        Assert.assertEquals(newUser.getAuthToken(), LocalStorage.getAuthToken());
        Assert.assertEquals(newUser.getUsername(), LocalStorage.getUsername());
        Assert.assertEquals(newUser.getId(), LocalStorage.getUserId());
        Assert.assertEquals(newUser.getId(), LocalStorage.getCurrentTargetUserId());
        Assert.assertEquals(newUser.getPermissionLevel(), LocalStorage.getPermissionLevel());

        //try again, and ensure that user name is taken
        response = createUser(TestData.getTestUser().getUsername(), "passwd11", 1000);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 409);

        //try with bad username
        response = createUser("X", "passwd11", 1000);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);

        //try with bad password
        response = createUser("X", 1000);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);
    }

    public static void logOut()
    {
        //log out
        ApiResponse response = ApiService.logout();
        assert(response.isSuccessful());
        assert(response.getResponseCode() == 200);

        //try to log out again - should not be allowed
        response = ApiService.logout();
        assert(!response.isSuccessful());

        //try to do something that requires login and verify that you cannot
        UserResponse userResponse = ApiService.getUser(TestData.getTestUser().getId());
        assert(!userResponse.isSuccessful());
        assert(userResponse.getUser() == null);
        Assert.assertEquals(403, userResponse.getResponseCode());
    }

    public static void logIn()
    {
        //attempt a login with bad Password
        LoginRequest request = new LoginRequest(TestData.getTestUser().getUsername(), TestData.getPassword() + "!");
        UserResponse response = ApiService.login(request);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 409);

        //attempt a login with bad username
        request = new LoginRequest(TestData.getTestUser().getUsername() + "L", TestData.getPassword());
        response = ApiService.login(request);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 409);

        //attempt a good login
        request = new LoginRequest(TestData.getTestUser().getUsername(), TestData.getPassword());
        response = ApiService.login(request);
        assert(response.isSuccessful());
        assert(response.getResponseCode() == 200);

        //test user properties
        User user = response.getUser();
        Assert.assertEquals(TestData.getTestUser().getUsername(), user.getUsername());
        Assert.assertEquals(TestData.getTestUser().getTargetCaloriesPerDay(), user.getTargetCaloriesPerDay());

        TestData.setTestUser(user);

        //test values in local storage
        Assert.assertEquals(TestData.getTestUser().getAuthToken(), LocalStorage.getAuthToken());
        Assert.assertEquals(TestData.getTestUser().getUsername(), LocalStorage.getUsername());
        Assert.assertEquals(TestData.getTestUser().getId(), LocalStorage.getUserId());
        Assert.assertEquals(TestData.getTestUser().getId(), LocalStorage.getCurrentTargetUserId());
        Assert.assertEquals(TestData.getTestUser().getPermissionLevel(), LocalStorage.getPermissionLevel());

        //get meals
        MealListResponse meals = ApiService.getMeals(TestData.getTestUser().getId());
        assert(meals.getResponseCode() == 204);
        assert(meals.getMeals().size() == 0);
    }

    public static void createMeals()
    {
        //create a good meal
        MealResponse response = createMeal("description 1", 2000, "01/01/2016 00:00", true);
        assert(response != null);

        //attempt to create meal without description
        response = createMeal("", 200, "01/01/2016 00:00", false);
        assert(response != null);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);

        //attempt to create meal with negative calories
        response = createMeal("descrip", -200, "01/01/2016 00:00", false);
        assert(response != null);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);

        //attempt to create meal with no date
        response = createMeal("descrip", 200, "", false);
        assert(response != null);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);

        //attempt to create meal with invalid date
        response = createMeal("descrip", 200, "123abc", false);
        assert(response != null);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);
    }

    public static void updateMeal()
    {
        Meal meal = TestData.getMeals().get(0);

        meal.setDescription(meal.getDescription() + "X");
        meal.setCalories(meal.getCalories() + 1);
        meal.setDateTime("01/01/2010 01:01");

        //test the positive case
        MealResponse response = updateMeal(meal.getId(), meal.getDescription() + "X", meal.getCalories() + 1, "01/01/2015 01:01", true);

        //try with bad description
        response = updateMeal(meal.getId(), "X", meal.getCalories() + 1, "01/01/2010 01:01", false);
        assert (response != null);
        assert (!response.isSuccessful());
        assert (response.getResponseCode() == 400);

        //try with negative calories
        response = updateMeal(meal.getId(), "abcdefg", meal.getCalories() * -1, "01/01/2010 01:01", false);
        assert (response != null);
        assert (!response.isSuccessful());
        assert (response.getResponseCode() == 400);

        //try with no date
        response = updateMeal(meal.getId(), "abcdefg", meal.getCalories(), "", false);
        assert (response != null);
        assert (!response.isSuccessful());
        assert (response.getResponseCode() == 400);

        //try with invlid date
        response = updateMeal(meal.getId(), "abcdefg", meal.getCalories(), "ancbhdd", false);
        assert(response != null);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);
    }

    public static void deleteMeals()
    {
        MealListResponse meals = ApiService.getMeals(TestData.getTestUser().getId());
        TestData.setMeals(meals.getMeals());

        if (TestData.getMeals() != null && TestData.getMeals().size() > 0)
        {
            while(TestData.getMeals().size() > 0) {
                Meal meal = TestData.getMeals().get(0);
                deleteMeal(meal.getId(), true);
            }
        }
    }

    public static void filterMeals()
    {
        //breakfasts for 5 days
        int b1 = createMeal("description a0", 2000, "01/01/2016 09:00", true).getMeal().getId();
        int b2 = createMeal("description a1", 2000, "01/02/2016 09:00", true).getMeal().getId();
        int b3 = createMeal("description a2", 2000, "01/03/2016 09:00", true).getMeal().getId();
        int b4 = createMeal("description a3", 2000, "01/04/2016 09:00", true).getMeal().getId();
        int b5 = createMeal("description a4", 2000, "01/05/2016 09:00", true).getMeal().getId();

        //lunches for 5 day
        int l1 = createMeal("description b0", 2000, "01/01/2016 13:00", true).getMeal().getId();
        int l2 = createMeal("description b1", 2000, "01/02/2016 13:00", true).getMeal().getId();
        int l3 = createMeal("description b2", 2000, "01/03/2016 13:00", true).getMeal().getId();
        int l4 = createMeal("description b3", 2000, "01/04/2016 13:00", true).getMeal().getId();
        int l5 = createMeal("description b4", 2000, "01/05/2016 13:00", true).getMeal().getId();

        //dinners for 5 days
        int d1 = createMeal("description c0", 2000, "01/01/2016 18:00", true).getMeal().getId();
        int d2 = createMeal("description c1", 2000, "01/02/2016 18:00", true).getMeal().getId();
        int d3 = createMeal("description c2", 2000, "01/03/2016 18:00", true).getMeal().getId();
        int d4 = createMeal("description c3", 2000, "01/04/2016 18:00", true).getMeal().getId();
        int d5 = createMeal("description c4", 2000, "01/05/2016 18:00", true).getMeal().getId();

        MealListResponse response;

        //should return 0 items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/01/2016", "01/01/2016", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return 0 items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/01/2017", "01/01/2016", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return 0 items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/01/2017", "", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return 0 items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, "01/01/2015", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return all items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/01/2016", null, null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(15, response.getMeals().size());

        //should return all items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, "01/01/2017", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(15, response.getMeals().size());

        //should return all items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, "08:00", null));
        assert(response.isSuccessful());
        Assert.assertEquals(15, response.getMeals().size());

        //should return all items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, null, "23:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(15, response.getMeals().size());

        //should return no items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, null, "08:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return no items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, "23:00", "08:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return no items
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, "23:00", ""));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return all meals for 2 days
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/01/2016", "01/03/2016", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(6, response.getMeals().size());
        Assert.assertEquals(b1, response.getMeals().get(0).getId());
        Assert.assertEquals(l1, response.getMeals().get(1).getId());
        Assert.assertEquals(d1, response.getMeals().get(2).getId());
        Assert.assertEquals(b2, response.getMeals().get(3).getId());
        Assert.assertEquals(l2, response.getMeals().get(4).getId());
        Assert.assertEquals(d2, response.getMeals().get(5).getId());

        //should return all meals for 2 days
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/02/2016", "01/04/2016", null, null));
        assert(response.isSuccessful());
        Assert.assertEquals(6, response.getMeals().size());
        Assert.assertEquals(b2, response.getMeals().get(0).getId());
        Assert.assertEquals(l2, response.getMeals().get(1).getId());
        Assert.assertEquals(d2, response.getMeals().get(2).getId());
        Assert.assertEquals(b3, response.getMeals().get(3).getId());
        Assert.assertEquals(l3, response.getMeals().get(4).getId());
        Assert.assertEquals(d3, response.getMeals().get(5).getId());

        //should return all breakfast and lunches
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, "07:00", "14:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(10, response.getMeals().size());
        Assert.assertEquals(b1, response.getMeals().get(0).getId());
        Assert.assertEquals(l1, response.getMeals().get(1).getId());
        Assert.assertEquals(b2, response.getMeals().get(2).getId());
        Assert.assertEquals(l2, response.getMeals().get(3).getId());
        Assert.assertEquals(b3, response.getMeals().get(4).getId());
        Assert.assertEquals(l3, response.getMeals().get(5).getId());
        Assert.assertEquals(b4, response.getMeals().get(6).getId());
        Assert.assertEquals(l4, response.getMeals().get(7).getId());
        Assert.assertEquals(b5, response.getMeals().get(8).getId());
        Assert.assertEquals(l5, response.getMeals().get(9).getId());

        //should return all lunches
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, "12:00", "14:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(5, response.getMeals().size());
        Assert.assertEquals(l1, response.getMeals().get(0).getId());
        Assert.assertEquals(l2, response.getMeals().get(1).getId());
        Assert.assertEquals(l3, response.getMeals().get(2).getId());
        Assert.assertEquals(l4, response.getMeals().get(3).getId());
        Assert.assertEquals(l5, response.getMeals().get(4).getId());

        //should return all dinners
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                null, null, "15:00", null));
        assert(response.isSuccessful());
        Assert.assertEquals(5, response.getMeals().size());
        Assert.assertEquals(d1, response.getMeals().get(0).getId());
        Assert.assertEquals(d2, response.getMeals().get(1).getId());
        Assert.assertEquals(d3, response.getMeals().get(2).getId());
        Assert.assertEquals(d4, response.getMeals().get(3).getId());
        Assert.assertEquals(d5, response.getMeals().get(4).getId());

        //should return all breakfast and lunches for 2 days
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/03/2016", "01/05/2016", "07:00", "14:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(4, response.getMeals().size());
        Assert.assertEquals(b3, response.getMeals().get(0).getId());
        Assert.assertEquals(l3, response.getMeals().get(1).getId());
        Assert.assertEquals(b4, response.getMeals().get(2).getId());
        Assert.assertEquals(l4, response.getMeals().get(3).getId());

        //should return 0 meals
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/03/2016", "01/05/2016", "07:00", "08:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        //should return 0 meals
        response = ApiService.getMeals(TestData.getTestUser().getId(), new FilterParams(
                "01/03/2015", "01/05/2015", "07:00", "23:00"));
        assert(response.isSuccessful());
        Assert.assertEquals(0, response.getMeals().size());

        deleteMeals();
    }

    public static void caloriesPerDay()
    {
        int m1_1 = createMeal("description", 0,     "01/01/2016 09:00", true).getMeal().getId();
        int m1_2 = createMeal("description", 1,     "01/01/2016 11:00", true).getMeal().getId();
        int m1_3 = createMeal("description", 2,     "01/01/2016 18:00", true).getMeal().getId();

        int m2_1 = createMeal("description", 1,     "01/02/2016 09:00", true).getMeal().getId();
        int m2_2 = createMeal("description", 99,    "01/02/2016 11:00", true).getMeal().getId();
        int m2_3 = createMeal("description", 23,    "01/02/2016 18:00", true).getMeal().getId();

        int m3_1 = createMeal("description", 200,   "01/03/2016 09:00", true).getMeal().getId();
        int m3_2 = createMeal("description", 100,   "01/03/2016 11:00", true).getMeal().getId();
        int m3_3 = createMeal("description", 100,   "01/03/2016 18:00", true).getMeal().getId();

        int m4_1 = createMeal("description", 1,     "01/04/2016 09:00", true).getMeal().getId();
        int m4_2 = createMeal("description", 1,     "01/04/2016 11:00", true).getMeal().getId();
        int m4_3 = createMeal("description", 100,   "01/04/2016 18:00", true).getMeal().getId();

        int m5_1 = createMeal("description", 1,     "01/05/2016 09:00", true).getMeal().getId();
        int m5_2 = createMeal("description", 100,   "01/05/2016 11:00", true).getMeal().getId();
        int m5_3 = createMeal("description", 100,   "01/05/2016 18:00", true).getMeal().getId();

        MealListResponse response = ApiService.getMeals(TestData.getTestUser().getId());
        deleteMeals();

        Assert.assertEquals(3, response.getMeals().get(0).getTotalCaloriesForDay());
        Assert.assertEquals(3, response.getMeals().get(1).getTotalCaloriesForDay());
        Assert.assertEquals(3, response.getMeals().get(2).getTotalCaloriesForDay());

        Assert.assertEquals(123, response.getMeals().get(3).getTotalCaloriesForDay());
        Assert.assertEquals(123, response.getMeals().get(4).getTotalCaloriesForDay());
        Assert.assertEquals(123, response.getMeals().get(5).getTotalCaloriesForDay());

        Assert.assertEquals(400, response.getMeals().get(6).getTotalCaloriesForDay());
        Assert.assertEquals(400, response.getMeals().get(7).getTotalCaloriesForDay());
        Assert.assertEquals(400, response.getMeals().get(8).getTotalCaloriesForDay());

        Assert.assertEquals(102, response.getMeals().get(9).getTotalCaloriesForDay());
        Assert.assertEquals(102, response.getMeals().get(10).getTotalCaloriesForDay());
        Assert.assertEquals(102, response.getMeals().get(11).getTotalCaloriesForDay());

        Assert.assertEquals(201, response.getMeals().get(12).getTotalCaloriesForDay());
        Assert.assertEquals(201, response.getMeals().get(13).getTotalCaloriesForDay());
        Assert.assertEquals(201, response.getMeals().get(14).getTotalCaloriesForDay());
    }

    public static void securityTest()
    {
        UserResponse response = createUser("passwd", 1000);
        assert(response != null && response.isSuccessful());

        int userId = response.getUser().getId();

        UserResponse loginResponse = ApiService.login(new LoginRequest(TestData.getTestUser().getUsername(), TestData.getPassword()));
        assert (loginResponse.isSuccessful());

        String permissionLevel = TestData.getTestUser().getPermissionLevel();

        //verify that i cannot see other users
        ApiResponse r = ApiService.getUsers();
        if (permissionLevel.equalsIgnoreCase("User"))
            assert403(r);
        else
        {
            assert(r.isSuccessful());
            assert(((UserListResponse)r).getUsers().size() > 0);
        }

        //verify that i cannot see this user
        r = ApiService.getUser(userId);
        if (permissionLevel.equalsIgnoreCase("User"))
            assert403(r);
        else
            assert(r.isSuccessful());

        //verify that i cannot get meals for this user
        r = ApiService.getMeals(userId);
        if (permissionLevel.equalsIgnoreCase("User"))
            assert403(r);
        else
            assert(r.isSuccessful());

        //verify that i cannot create meals for this user
        r = createMeal(userId, "descr", 0, "01/01/2014 00:00", false);
        if (!permissionLevel.equalsIgnoreCase("Admin"))
            assert403(r);
        else
            assert(r.isSuccessful());

        //verify that i cannot update meals for this user
        r = updateMeal(userId, 1, "descr", 0, "01/01/2015 00:00", false);
        if (!permissionLevel.equalsIgnoreCase("Admin"))
            assert403(r);
        else
            assert(r.getResponseCode() == 404 || r.isSuccessful());

        //verify that i cannot delete meals for this user
        r = deleteMeal(userId, 1, false);
        if (!permissionLevel.equalsIgnoreCase("Admin"))
            assert403(r);
        else
            assert(r.getResponseCode() == 404 || r.isSuccessful());

        //verify that i cannot update this user
        User user = response.getUser();
        r = ApiService.updateUser(userId, user);
        if (!permissionLevel.equalsIgnoreCase("Admin"))
            assert403(r);
        else
            assert(r.isSuccessful());
    }


    public static MealResponse createMeal(String description, int calories, String dateTime, boolean doTests)
    {
        return createMeal(TestData.getTestUser().getId(), description, calories, dateTime, doTests);
    }
    public static MealResponse createMeal(int userId, String description, int calories, String dateTime, boolean doTests)
    {
        Meal meal = new Meal();
        meal.setCalories(calories);
        meal.setDateTime(dateTime);
        meal.setDescription(description);
        MealResponse response = ApiService.createMeal(userId, meal);

        Meal newMeal = response.getMeal();

        if (doTests) {
            assert (response.isSuccessful());
            assert (response.getMeal() != null);
            assert (response.getMeal().getId() > 0);
            assert (response.getResponseCode() == 201);

            //test new meal properties
            Assert.assertEquals(meal.getCalories(), newMeal.getCalories());
            Assert.assertEquals(meal.getDateTime(), newMeal.getDateTime());
            Assert.assertEquals(meal.getDescription(), newMeal.getDescription());
        }

        if (newMeal != null)
            TestData.getMeals().add(newMeal);

        return response;
    }

    public static ApiResponse deleteMeal(int mealId, boolean doTests)
    {
        return deleteMeal(TestData.getTestUser().getId(), mealId, doTests);
    }
    public static ApiResponse deleteMeal(int userId, int mealId, boolean doTests)
    {
        //delete meal
        ApiResponse response = ApiService.deleteMeal(userId, mealId);

        if (doTests) {
            assert (response != null);
            assert (response.isSuccessful());
            assert (response.getResponseCode() == 204);

            //verify that meal is not retrievable
            MealResponse mealResponse = ApiService.getMeal(TestData.getTestUser().getId(), mealId);
            assert (mealResponse != null);
            assert (!mealResponse.isSuccessful());
            assert (mealResponse.getResponseCode() == 404);
        }

        if (response.isSuccessful())
        {
            for(int n=0; n<TestData.getMeals().size(); n++)
            {
                if (TestData.getMeals().get(n).getId() == mealId)
                {
                    TestData.getMeals().remove(n);
                    break;
                }
            }
        }

        return response;
    }

    public static MealResponse updateMeal(int mealId, String description, int calories, String dateTime, boolean doTests)
    {
        return updateMeal(TestData.getTestUser().getId(), mealId, description, calories, dateTime, doTests);
    }
    public static MealResponse updateMeal(int userId, int mealId, String description, int calories, String dateTime, boolean doTests)
    {
        Meal meal = new Meal();
        meal.setDescription(description);
        meal.setCalories(calories);
        meal.setDateTime(dateTime);

        MealResponse response = ApiService.updateMeal(userId, mealId, meal);

        if (doTests) {
            //test response fields
            assert (response.isSuccessful());
            assert (response.getResponseCode() == 200);
            assert (response.getMeal() != null);

            Meal updatedMeal = response.getMeal();

            //test meal properties
            Assert.assertEquals(mealId, updatedMeal.getId());
            Assert.assertEquals(meal.getDescription(), updatedMeal.getDescription());
            Assert.assertEquals(meal.getDateTime(), updatedMeal.getDateTime());
            Assert.assertEquals(meal.getCalories(), updatedMeal.getCalories());
        }

        return response;
    }

    public static UserResponse createUser(String password, int calories)
    {
        return createUser(null, password, calories);
    }
    public static UserResponse createUser(String username, String password, int calories)
    {
        User user = new User();

        if (username != null)
            user.setUsername(username);
        else
            user.setUsername("abcde");

        user.setPassword(password);
        user.setPermissionLevel("User");
        user.setTargetCaloriesPerDay(calories);

        UserResponse response = ApiService.createUser(user);

        int usernameIndex = 0;
        if (username == null) {
            while (!response.isSuccessful() && response.getResponseCode() == 409) {
                user.setUsername("abcde" + Integer.toString(usernameIndex));
                response = ApiService.createUser(user);
                usernameIndex++;
            }
        }

        return response;
    }

    public static void assert403(ApiResponse response)
    {
        assert (!response.isSuccessful());
        assert (response.getResponseCode() == 403);
    }
}
