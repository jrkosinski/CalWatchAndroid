using System;
using System.Collections.Generic;
using System.Linq;
using System.Data.Entity;

using CalWatch.Data;
using CalWatch.Api.Utilities;
using CalWatch.Api.Exceptions;

namespace CalWatch.Api.Models
{
    public static class AppDbContext
    {
        public static List<User> GetUsers()
        {
            List<User> output = new List<User>();
            using (var dbContext = new CalWatchEntities())
            {
                //TODO: will not scale well 
                foreach (var user in dbContext.Users)
                {
                    output.Add(UserDataToUser(user));
                }
            }

            return output;
        }

        public static User GetUser(int userId)
        {
            User output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var user = dbContext.Users.FirstOrDefault(u => u.Id == userId);
                if (user != null)
                {
                    output = UserDataToUser(user);
                }
            }

            return output;
        }

        public static User GetUser(string username)
        {
            User output = null;
            using (var dbContext = new CalWatchEntities())
            {
                if (username != null)
                    username = username.Trim().ToLower();

                var user = dbContext.Users.FirstOrDefault(u => u.Username.ToLower() == username);
                if (user != null)
                {
                    output = UserDataToUser(user);
                }
            }

            return output;
        }

        public static User GetUserByAuthToken(string authToken)
        {
            User output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var user = dbContext.Users.FirstOrDefault(u => u.AuthToken == authToken);
                if (user != null)
                {
                    output = UserDataToUser(user);
                }
            }

            return output;
        }

        public static User UpdateUser(User user)
        {
            User output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var existingUser = dbContext.Users.FirstOrDefault(u => u.Id == user.Id);

                if (existingUser != null)
                {
                    //map user object to existing user
                    existingUser.PermissionLevel = (byte)user.PermissionLevel;
                    existingUser.TargetCaloriesPerDay = user.TargetCaloriesPerDay;
                    existingUser.DateModified = DateTime.Now;

                    //save to db
                    dbContext.Entry(existingUser).State = EntityState.Modified;
                    dbContext.SaveChanges();

                    //map output 
                    output = UserDataToUser(existingUser);
                }
                else
                {
                    throw new EntityNotFoundException(String.Format("User {0} not found.", user.Id));
                }
            }

            return output;
        }

        public static User UpdateAuthData(User user)
        {
            User output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var existingUser = dbContext.Users.FirstOrDefault(u => u.Id == user.Id);

                if (existingUser != null)
                {
                    //map user object to existing user
                    existingUser.AuthToken = user.AuthToken;
                    existingUser.LastLogin = user.LastLogin;

                    //save changes 
                    dbContext.Entry(existingUser).State = EntityState.Modified;
                    dbContext.SaveChanges();

                    //map output 
                    output = UserDataToUser(existingUser);
                }
                else
                {
                    throw new EntityNotFoundException(String.Format("User {0} not found.", user.Id));
                }
            }

            return output;
        }

        public static User CreateUser(User user)
        {
            User output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var data = UserToUserData(user);

                //map input 
                data.AuthToken = AuthTokenUtil.GenerateNewAuthToken();
                data.DateCreated = DateTime.Now;
                data.DateModified = data.DateCreated;
                data.LastLogin = data.DateCreated;
                data.PasswordSalt = PasswordUtil.GenerateNewSalt();
                data.Password = PasswordUtil.SaltAndHashPassword(user.Password, data.PasswordSalt);

                //save changes 
                dbContext.Entry(data).State = EntityState.Added;
                dbContext.SaveChanges();

                //map output 
                output = UserDataToUser(data);
            }

            return output;
        }

        public static void DeleteUser(int userId)
        {
            using (var dbContext = new CalWatchEntities())
            {
                //TODO: should be done in transaction 

                var existingUser = dbContext.Users.FirstOrDefault(u => u.Id == userId);
                if (existingUser != null)
                {
                    //delete meals first 
                    while (existingUser.Meals.Count > 0)
                    {
                        dbContext.Entry(existingUser.Meals.First()).State = EntityState.Deleted;
                        dbContext.SaveChanges();
                    }

                    //then delete user 
                    if (existingUser != null)
                    {
                        dbContext.Entry(existingUser).State = EntityState.Deleted;
                        dbContext.SaveChanges();
                    }
                    else
                    {
                        throw new EntityNotFoundException(String.Format("User {0} not found.", userId));
                    }
                }
            }
        }


        public static List<Meal> GetMeals(int userId)
        {
            return GetMeals(userId, null);
        }

        public static List<Meal> GetMealsFiltered(
            int userId,
            FilterParams filterParams
          )
        {
            return GetMeals(userId, filterParams);
        }

        public static Meal GetMeal(int mealId)
        {
            Meal output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var meal = dbContext.Meals.FirstOrDefault(r => r.Id == mealId);
                if (meal != null)
                {
                    output = meal; // MealDataToMeal(meal);
                }
            }

            return output;
        }

        public static Meal UpdateMeal(Meal meal)
        {
            Meal output = null;

            using (var dbContext = new CalWatchEntities())
            {
                var existingMeal = dbContext.Meals.FirstOrDefault(r => r.Id == meal.Id);

                if (existingMeal != null)
                {
                    //map user object to existing user
                    existingMeal.DateTime = meal.DateTime;
                    existingMeal.Calories = meal.Calories;
                    existingMeal.Description = meal.Description;
                    existingMeal.DateModified = DateTime.Now;

                    dbContext.Entry(existingMeal).State = EntityState.Modified;
                    dbContext.SaveChanges();

                    output = existingMeal; // MealDataToMeal(existingMeal);
                }
                else
                    throw new EntityNotFoundException(String.Format("Meal {0} not found.", meal.Id));
            }

            return output;
        }

        public static Meal CreateMeal(int userId, Meal meal)
        {
            Meal output = null;
            using (var dbContext = new CalWatchEntities())
            {
                var existingUser = dbContext.Users.FirstOrDefault(u => u.Id == userId);

                if (existingUser != null)
                {
                    meal.DateCreated = DateTime.Now;
                    meal.DateModified = meal.DateCreated;
                    meal.UserId = userId;

                    existingUser.Meals.Add(meal);

                    dbContext.Entry(existingUser).State = EntityState.Modified;
                    dbContext.SaveChanges();

                    output = meal;// MealDataToMeal(data);
                }
                else
                    throw new EntityNotFoundException(String.Format("User {0} not found.", userId));
            }

            return output;
        }

        public static void DeleteMeal(int mealId)
        {
            using (var dbContext = new CalWatchEntities())
            {
                var existingMeal = dbContext.Meals.FirstOrDefault(r => r.Id == mealId);

                if (existingMeal != null)
                {
                    dbContext.Entry(existingMeal).State = EntityState.Deleted;
                    dbContext.SaveChanges();
                }
                else
                    throw new EntityNotFoundException(String.Format("Meal {0} not found.", mealId));
            }
        }


        private static User UserDataToUser(UserData data)
        {
            User user = new User();

            user.DateCreated = data.DateCreated;
            user.DateModified = data.DateModified;
            user.Id = data.Id;
            user.Password = data.Password;
            user.PasswordSalt = data.PasswordSalt;
            user.PermissionLevel = (UserPermissionLevel)data.PermissionLevel;
            user.TargetCaloriesPerDay = data.TargetCaloriesPerDay;
            user.Username = data.Username;
            user.AuthToken = data.AuthToken;
            user.LastLogin = data.LastLogin;

            return user;
        }

        private static UserData UserToUserData(User user)
        {
            UserData data = new UserData();

            data.DateCreated = user.DateCreated;
            data.DateModified = user.DateModified;
            data.Id = user.Id;
            data.Password = user.Password;
            data.PasswordSalt = user.PasswordSalt;
            data.PermissionLevel = (byte)user.PermissionLevel;
            data.TargetCaloriesPerDay = user.TargetCaloriesPerDay;
            data.Username = user.Username;
            data.AuthToken = user.AuthToken;
            data.LastLogin = user.LastLogin;

            return data;
        }

        private static List<Meal> GetMeals(int userId, FilterParams filterParams)
        {
            List<Meal> output = new List<Meal>();
            using (var dbContext = new CalWatchEntities())
            {
                var meals = dbContext.Meals.Where(m => m.UserId == userId).OrderBy(m => m.DateTime).ToList();

                int caloriesTally = 0;
                int dayNumber = 0;
                if (meals.Count > 0)
                    dayNumber = meals[0].DateTime.DaysSince1970();

                //tally total calories per day 
                Dictionary<int, int> caloriesPerDay = new Dictionary<int, int>();
                for (int n = 0; n < meals.Count; n++)
                {
                    var meal = meals[n];
                    int day = meal.DateTime.DaysSince1970();
                    if (day > dayNumber)
                    {
                        caloriesPerDay.Add(dayNumber, caloriesTally);
                        caloriesTally = 0;
                        dayNumber = day;
                    }

                    caloriesTally += meal.Calories;

                    var mealObject = meal; // MealDataToMeal(meal);
                    output.Add(mealObject);
                }

                if (!caloriesPerDay.ContainsKey(dayNumber))
                    caloriesPerDay.Add(dayNumber, caloriesTally);

                //do filtering here if necessary 
                if (filterParams != null)
                {
                    output = output.Where(m =>
                        m.UserId == userId &&
                        m.DateTime >= filterParams.DateFrom &&
                        m.DateTime <= filterParams.DateTo &&
                        (m.DateTime.Hour * 100 + m.DateTime.Minute) >= (filterParams.TimeFrom.Hours * 100 + filterParams.TimeFrom.Minutes) &&
                        (m.DateTime.Hour * 100 + m.DateTime.Minute) <= (filterParams.TimeTo.Hours * 100 + filterParams.TimeTo.Minutes)
                    ).OrderBy(m => m.DateTime).ToList();
                }

                //reconcile calories per day 
                foreach (var meal in output)
                {
                    meal.TotalCaloriesForDay = caloriesPerDay[meal.DateTime.DaysSince1970()];
                }

                //finally, adjust filter params. If values are MinValue or MaxValue, 
                //adjust to REAL min & max values 
                if (output.Count > 0 && filterParams != null)
                {
                    if (filterParams.DateFrom == DateTime.MinValue)
                        filterParams.SetDateFrom(output.First().DateTime);
                    if (filterParams.DateTo == DateTime.MaxValue)
                        filterParams.SetDateTo(output.Last().DateTime.AddDays(1)); 
                }
            }

            return output;
        }
    }
}
