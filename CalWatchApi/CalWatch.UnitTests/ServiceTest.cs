using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Diagnostics;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using CalWatch.Api;
using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.Services;
using CalWatch.Api.Exceptions;
using CalWatch.Api.ViewModels;
using CalWatch.Data;


namespace CalWatch.UnitTests
{
    [TestClass]
    public class ServiceTest : TestBase
    {
        [TestMethod]
        public void TestStart()
        {
            DeleteAllUsers();
            CreateUsers();
        }

        [TestMethod]
        public void SaltAndHashPassword()
        {
            string password = "MojoJojo24333";
            string wrongPassword = "2190585"; 
            string salt = PasswordUtil.GenerateNewSalt(); 
            string wrongSalt = PasswordUtil.GenerateNewSalt(); 

            string hash = PasswordUtil.SaltAndHashPassword(password, salt); 
            
            Assert.IsTrue(PasswordUtil.ComparePassword(password, hash, salt));
            Assert.IsFalse(PasswordUtil.ComparePassword(wrongPassword, hash, salt));
            Assert.IsFalse(PasswordUtil.ComparePassword(password, hash, wrongSalt));
            Assert.IsFalse(PasswordUtil.ComparePassword(wrongPassword, hash, wrongSalt));
        }


        [TestMethod]
        public void CreateUser()
        {
            User user = null;
            var newUser = CreateTestUser("phineas_finch", out user);

            Assert.IsNotNull(newUser);
            Assert.AreEqual(user.Username, newUser.Username);
            Assert.AreEqual(user.TargetCaloriesPerDay, newUser.TargetCaloriesPerDay); 
            Assert.AreNotEqual(user.DateCreated, newUser.DateCreated);
            Assert.AreNotEqual(user.DateModified, newUser.DateModified);
            Assert.AreEqual(UserPermissionLevel.User, newUser.PermissionLevel);
            Assert.IsTrue(PasswordUtil.ComparePassword(user.Password, newUser.Password, newUser.PasswordSalt));

            //make sure you get back an auth token
            Assert.IsTrue(!String.IsNullOrEmpty(newUser.AuthToken)); 

            AppDbContext.DeleteUser(newUser.Id); 
        }

        [TestMethod]
        public void CreateUserValidation()
        {
            UserViewModel viewModel = new UserViewModel()
            {
                Id = "-1",
                Password = "1234567",
                PermissionLevel = "Admin",
                TargetCaloriesPerDay = "1000",
                Username = "phineas_finch"
            };
            User user = MappingLayer.ViewModelToUser(viewModel);

            //short password 
            user.Password = "1";
            AssertExceptionType<ValidationFailedException>(() => 
                UserService.CreateUser(user)
            ); 
            
            //long password 
            user.Password = "s".Repeat(400);
            AssertExceptionType<ValidationFailedException>(() =>
                UserService.CreateUser(user)
            ); 
            
            //short username 
            user.Password = viewModel.Password;
            user.Username = "";
            AssertExceptionType<ValidationFailedException>(() =>
                UserService.CreateUser(user)
            ); 
            
            //long username 
            user.Username = "s".Repeat(400);
            AssertExceptionType<ValidationFailedException>(() =>
                UserService.CreateUser(user)
            ); 
            
            //invalid calories target
            user.Username = viewModel.Username;
            user.TargetCaloriesPerDay = -100;
            AssertExceptionType<ValidationFailedException>(() =>
                UserService.CreateUser(user)
            ); 
            
            //duplicate username 
            user.Username = GetAnyUser().Username;
            AssertExceptionType<DuplicateEntityException>(() => 
                UserService.CreateUser(user)
            ); 
        }


        [TestMethod]
        public void Authenticate()
        {
            User user = null;
            var newUser = CreateTestUser("phineas_finch", out user); 

            //correct login 
            newUser = UserService.Authenticate(user.Username, user.Password);
            Assert.IsNotNull(newUser);
            Assert.AreEqual(user.Username, newUser.Username);

            //make sure you get back an auth token
            Assert.IsTrue(!String.IsNullOrEmpty(newUser.AuthToken)); 

            //wrong password 
            AssertExceptionType<AuthenticationException>(() => 
                UserService.Authenticate(user.Username, user.Password + "X")
            );

            //wrong username 
            AssertExceptionType<AuthenticationException>(() =>
                UserService.Authenticate(user.Username, user.Password + "X")
            ); 

            AppDbContext.DeleteUser(newUser.Id); 
        }

        [TestMethod]
        public void TestAuthToken()
        {
            User user; 
            var newUser = CreateTestUser("hubbabubba", out user);

            Assert.IsNotNull(newUser);
            Assert.IsTrue(!String.IsNullOrEmpty(newUser.AuthToken)); 

            //try to use auth token 
            AssertExceptionIsNull(() => 
                UserService.GetUser(newUser.AuthToken, newUser.Id)
            ); 

            //logout 
            UserService.Logout(newUser.AuthToken); 

            //attempt to authenticate 
            newUser = UserService.Authenticate(user.Username, user.Password); 

            //try to use auth token 
            AssertExceptionIsNull(() =>
                UserService.GetUser(newUser.AuthToken, newUser.Id)
            );

            AppDbContext.DeleteUser(newUser.Id);
        }


        [TestMethod]
        public void UpdateUser()
        {
            User user = null;
            var newUser = CreateTestUser("phineas_finch", out user); 

            //update calories per diem
            user.TargetCaloriesPerDay = newUser.TargetCaloriesPerDay + 1;
            var updatedUser = UserService.UpdateUser(newUser.AuthToken, newUser.Id, user);

            Assert.IsNotNull(updatedUser);
            Assert.AreEqual(user.TargetCaloriesPerDay, updatedUser.TargetCaloriesPerDay);
            Assert.IsTrue(updatedUser.DateModified > newUser.DateModified);
            Assert.IsTrue(updatedUser.DateModified > updatedUser.DateCreated);

            AppDbContext.DeleteUser(updatedUser.Id); 
        }

        [TestMethod]
        public void UpdateUserSecurity()
        {
            var newUser1 = CreateTestUser("phineas_finch"); 
            var newUser2 = CreateTestUser("subUser");

            //verify that user cannot update another user 
            AssertExceptionType<UserNotAuthorizedException>(() => 
                UserService.UpdateUser(newUser1.AuthToken, newUser2.Id, newUser2)
            ); 

            //verify that user can update self 
            AssertExceptionIsNull(() =>
                UserService.UpdateUser(newUser2.AuthToken, newUser2.Id, newUser2)
            ); 

            //verify that Admin can update another user 
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1); 
            AssertExceptionIsNull(() =>
                UserService.UpdateUser(newUser1.AuthToken, newUser2.Id, newUser2)
            ); 

            AppDbContext.DeleteUser(newUser1.Id);
            AppDbContext.DeleteUser(newUser2.Id); 
        }

        [TestMethod]
        public void UpdateUserValidation()
        {
            var newUser = CreateTestUser("phineas_finch"); 

            var viewModel = new UserViewModel()
            {
                Id = (newUser.Id + 1).ToString(),
                Password = newUser.Password + "X",
                PermissionLevel = "User",
                TargetCaloriesPerDay = (newUser.TargetCaloriesPerDay + 1).ToString(),
                Username = newUser.Username + "X"
            };

            var userToUpdate =  MappingLayer.ViewModelToUser(viewModel);
            var updatedUser = UserService.UpdateUser(newUser.AuthToken, newUser.Id, userToUpdate);

            //ensure the right fields got updated 
            Assert.AreEqual(newUser.Id, updatedUser.Id);
            Assert.AreEqual(newUser.PermissionLevel, updatedUser.PermissionLevel);
            Assert.AreEqual(newUser.DateCreated, updatedUser.DateCreated);
            Assert.AreEqual(newUser.Password, updatedUser.Password);
            Assert.AreEqual(newUser.Username, updatedUser.Username);
            Assert.AreNotEqual(newUser.DateModified, updatedUser.DateModified);
            Assert.AreEqual(userToUpdate.TargetCaloriesPerDay, updatedUser.TargetCaloriesPerDay);

            //test validation 
            updatedUser.TargetCaloriesPerDay = -1;
            AssertExceptionType<ValidationFailedException>(() =>
                UserService.UpdateUser(newUser.AuthToken, newUser.Id, updatedUser)
            ); 

            AppDbContext.DeleteUser(updatedUser.Id); 
        }


        [TestMethod]
        public void GetUser()
        {
            var newUser = CreateTestUser("phineas_finch");
            Assert.IsNotNull(newUser);

            var user = UserService.GetUser(newUser.AuthToken, newUser.Id);
            Assert.IsNotNull(user);

            Assert.AreEqual(newUser.Id, user.Id);
            Assert.AreEqual(newUser.LastLogin, user.LastLogin);
            Assert.AreEqual(newUser.AuthToken, user.AuthToken);
            Assert.AreEqual(newUser.DateCreated, user.DateCreated);
            Assert.AreEqual(newUser.DateModified, user.DateModified);
            Assert.AreEqual(newUser.Password, user.Password);
            Assert.AreEqual(newUser.PasswordSalt, user.PasswordSalt);
            Assert.AreEqual(newUser.PermissionLevel, user.PermissionLevel);
            Assert.AreEqual(newUser.TargetCaloriesPerDay, user.TargetCaloriesPerDay);
            Assert.AreEqual(newUser.Username, user.Username);

            AppDbContext.DeleteUser(newUser.Id);
        }

        [TestMethod]
        public void GetUserSecurity()
        {
            var newUser1 = CreateTestUser("phineas_finch");
            var newUser2 = CreateTestUser("redmond");
            Assert.IsNotNull(newUser1);
            Assert.IsNotNull(newUser2);

            //user should be able to get himself 
            AssertExceptionIsNull(() =>
                UserService.GetUser(newUser1.AuthToken, newUser1.Id)
            );

            //user should not be able to get other user 
            AssertExceptionType<UserNotAuthorizedException>(() =>
                UserService.GetUser(newUser1.AuthToken, newUser2.Id)
            );

            //...unless he is DataViewer 
            newUser1.PermissionLevel = UserPermissionLevel.DataViewer;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                UserService.GetUser(newUser1.AuthToken, newUser2.Id)
            );

            //...or Admin
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                UserService.GetUser(newUser1.AuthToken, newUser2.Id)
            );

            //...and still should be able to get himself 
            AssertExceptionIsNull(() =>
                UserService.GetUser(newUser1.AuthToken, newUser1.Id)
            );

            AssertExceptionIsNull(() =>
                UserService.GetUser(newUser2.AuthToken, newUser2.Id)
            );

            AppDbContext.DeleteUser(newUser1.Id);
            AppDbContext.DeleteUser(newUser2.Id);
        }


        [TestMethod]
        public void GetUsers()
        {
            var newUser = CreateTestUser("phineas_finch");
            Assert.IsNotNull(newUser);

            newUser.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser);

            var users = UserService.GetUsers(newUser.AuthToken);
            int count = users.Count; 
            Assert.IsTrue(count > 0); 

            //create another user
            CreateTestUser("phineas_finch1");
            users = UserService.GetUsers(newUser.AuthToken);
            Assert.IsTrue(users.Count == (count+1)); 
        }

        [TestMethod]
        public void GetUsersSecurity()
        {
            var newUser = CreateTestUser("phileas_fogg");

            //verify that user cannot access users list 
            newUser.PermissionLevel = UserPermissionLevel.User;
            AppDbContext.UpdateUser(newUser); 
            AssertExceptionType<UserNotAuthorizedException>(() => 
                UserService.GetUsers(newUser.AuthToken)
            );

            //verify that dataviewer can access users list 
            newUser.PermissionLevel = UserPermissionLevel.DataViewer;
            AppDbContext.UpdateUser(newUser); 
            AssertExceptionIsNull(() =>
                UserService.GetUsers(newUser.AuthToken)
            );

            //verify that admin can access users list 
            newUser.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser); 
            AssertExceptionIsNull(() =>
                UserService.GetUsers(newUser.AuthToken)
            ); 
        }


        [TestMethod]
        public void GetMeals()
        {
            var newUser = CreateTestUser("trololo");
            var meal = CreateTestMeal(newUser);
            meal = CreateTestMeal(newUser); 

            var meals = MealService.GetMeals(newUser.AuthToken, newUser.Id);

            Assert.IsNotNull(meals);
            Assert.AreEqual(2, meals.Count); 

            AppDbContext.DeleteUser(newUser.Id);
        }

        [TestMethod]
        public void GetMealsSecurity()
        {
            var newUser1 = CreateTestUser("phineas_finch");
            var newUser2 = CreateTestUser("subUser");

            Assert.IsNotNull(newUser1);
            Assert.IsNotNull(newUser2);

            var newMeal1 = CreateTestMeal(newUser1);
            var newMeal2 = CreateTestMeal(newUser2);

            Assert.IsNotNull(newMeal1);
            Assert.IsNotNull(newMeal2);

            //verify that user cannot see another user's meal
            AssertExceptionType<UserNotAuthorizedException>(() =>
                MealService.GetMeals(newUser1.AuthToken, newUser2.Id)
            );

            //verify that user can see own meal 
            AssertExceptionIsNull(() =>
                MealService.GetMeals(newUser1.AuthToken, newUser1.Id)
            );

            //verify that DataViewer can see another user's meal 
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                MealService.GetMeals(newUser1.AuthToken, newUser2.Id)
            );

            //verify that Admin can see another user's meal 
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                MealService.GetMeals(newUser1.AuthToken, newUser2.Id)
            );

            AppDbContext.DeleteUser(newUser1.Id);
            AppDbContext.DeleteUser(newUser2.Id); 
        }


        [TestMethod]
        public void CreateMeal()
        {
            var newUser = CreateTestUser("trololo");
            Meal preCreatedMeal; 
            var newMeal = CreateTestMeal(newUser, out preCreatedMeal);

            Assert.IsNotNull(newMeal);
            Assert.AreEqual(preCreatedMeal.Calories, newMeal.Calories);
            Assert.AreEqual(preCreatedMeal.Description, newMeal.Description);
            Assert.AreEqual(preCreatedMeal.DateTime, newMeal.DateTime);

            AppDbContext.DeleteUser(newUser.Id); 
        }

        [TestMethod]
        public void CreateMealSecurity()
        {
            var newUser1 = CreateTestUser("phineas_finch");
            var newUser2 = CreateTestUser("subUser");

            Assert.IsNotNull(newUser1);
            Assert.IsNotNull(newUser2);

            var meal = new Meal()
            {
                Calories = 100,
                Description = "Hawaiian Punch",
                DateTime = DateTime.Now
            };

            //verify that user cannot create another user's meal
            AssertExceptionType<UserNotAuthorizedException>(() =>
                MealService.CreateMeal(newUser1.AuthToken, newUser2.Id, meal)
            );

            //verify that user can create own meal 
            AssertExceptionIsNull(() =>
                MealService.CreateMeal(newUser1.AuthToken, newUser1.Id, meal)
            );

            //verify that Admin can create another user's meal 
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                MealService.CreateMeal(newUser1.AuthToken, newUser2.Id, meal)
            );

            AppDbContext.DeleteUser(newUser1.Id);
            AppDbContext.DeleteUser(newUser2.Id); 
        }

        [TestMethod]
        public void CreateMealValidation()
        {
            var newUser = CreateTestUser("walken"); 

            MealViewModel viewModel = new MealViewModel()
            {
                Id = "-1",
                UserId = "-1",
                Description = "1234567",
                Calories = "120",
                DateTime = DateTimeUtil.DateTimeToString(DateTime.Now)
            };
            var  meal = MappingLayer.ViewModelToMeal(viewModel);

            //short description 
            meal.Description = ""; 
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.CreateMeal(newUser.AuthToken, newUser.Id, meal)
            );

            //long description 
            meal.Description = "s".Repeat(4000);
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.CreateMeal(newUser.AuthToken, newUser.Id, meal)
            );

            //short calories 
            meal.Description = viewModel.Description;
            meal.Calories = -1;
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.CreateMeal(newUser.AuthToken, newUser.Id, meal)
            );

            //invalid datetime
            meal.Calories = Int32.Parse(viewModel.Calories);
            meal.DateTime = DateTime.Now.AddYears(-300); 
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.CreateMeal(newUser.AuthToken, newUser.Id, meal)
            );

            AppDbContext.DeleteUser(newUser.Id); 
        }


        [TestMethod]
        public void UpdateMeal()
        {
            User user = null;
            var newUser = CreateTestUser("drygulchifer", out user);
            Meal meal;
            var newMeal = CreateTestMeal(newUser, out meal, "Lettuce Wedges", 10);

            //update calories per diem
            meal.Calories = newMeal.Calories + 1;
            meal.Description = newMeal.Description + "X";
            meal.DateTime = newMeal.DateTime.AddDays(1);

            var updatedMeal = MealService.UpdateMeal(newUser.AuthToken, newUser.Id, newMeal.Id, meal); 

            Assert.IsNotNull(newUser);
            Assert.AreEqual(meal.Calories, updatedMeal.Calories);
            Assert.AreEqual(meal.Description, updatedMeal.Description);
            Assert.AreEqual(meal.DateTime, updatedMeal.DateTime);
            Assert.IsTrue(updatedMeal.DateModified > newMeal.DateModified);
            Assert.IsTrue(updatedMeal.DateModified > updatedMeal.DateCreated);

            AppDbContext.DeleteUser(newUser.Id); 
        }

        [TestMethod]
        public void UpdateMealSecurity()
        {
            var newUser1 = CreateTestUser("phineas_finch");
            var newUser2 = CreateTestUser("subUser");

            Assert.IsNotNull(newUser1);
            Assert.IsNotNull(newUser2);

            var newMeal1 = CreateTestMeal(newUser1);
            var newMeal2 = CreateTestMeal(newUser2);

            Assert.IsNotNull(newMeal1);
            Assert.IsNotNull(newMeal2);

            //verify that user cannot update another user's meal
            AssertExceptionType<UserNotAuthorizedException>(() =>
                MealService.UpdateMeal(newUser1.AuthToken, newUser2.Id, newMeal2.Id, newMeal2)
            );

            //verify that user can update own meal 
            AssertExceptionIsNull(() =>
                MealService.UpdateMeal(newUser1.AuthToken, newUser1.Id, newMeal1.Id, newMeal1)
            );

            //verify that Admin can update another user's meal 
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                MealService.UpdateMeal(newUser1.AuthToken, newUser2.Id, newMeal2.Id, newMeal2)
            );

            AppDbContext.DeleteUser(newUser1.Id);
            AppDbContext.DeleteUser(newUser2.Id); 
        }

        [TestMethod]
        public void UpdateMealValidation()
        {
            var newUser = CreateTestUser("walken");

            MealViewModel viewModel = new MealViewModel()
            {
                Id = "-1",
                UserId = "-1",
                Description = "1234567",
                Calories = "120",
                DateTime = DateTimeUtil.DateTimeToString(DateTime.Now)
            };

            var meal = MealService.CreateMeal(newUser.AuthToken, newUser.Id, MappingLayer.ViewModelToMeal(viewModel)); 

            //short description 
            meal.Description = "";
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.UpdateMeal(newUser.AuthToken, newUser.Id, meal.Id, meal)
            );

            //long description 
            meal.Description = "s".Repeat(4000);
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.UpdateMeal(newUser.AuthToken, newUser.Id, meal.Id, meal)
            );

            //short calories 
            meal.Description = viewModel.Description;
            meal.Calories = -1;
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.UpdateMeal(newUser.AuthToken, newUser.Id, meal.Id, meal)
            );

            //invalid datetime
            meal.Calories = Int32.Parse(viewModel.Calories);
            meal.DateTime = DateTime.Now.AddYears(-300);
            AssertExceptionType<ValidationFailedException>(() =>
                MealService.UpdateMeal(newUser.AuthToken, newUser.Id, meal.Id, meal)
            );

            AppDbContext.DeleteUser(newUser.Id); 
        }


        [TestMethod]
        public void DeleteMeal()
        {
            var newUser = CreateTestUser("phineas_finch");
            var newMeal = CreateTestMeal(newUser);

            Assert.IsNotNull(newUser);
            Assert.IsNotNull(newMeal);

            newUser = UserService.GetUser(newUser.AuthToken, newUser.Id);
            Assert.AreEqual(1, AppDbContext.GetMeals(newUser.Id).Count);

            MealService.DeleteMeal(newUser.AuthToken, newUser.Id, newMeal.Id);

            newUser = UserService.GetUser(newUser.AuthToken, newUser.Id);
            var meals = AppDbContext.GetMeals(newUser.Id);
            Assert.AreEqual(0, meals.Count);
        }

        [TestMethod]
        public void DeleteMealSecurity()
        {
            var newUser1 = CreateTestUser("phineas_finch");
            var newUser2 = CreateTestUser("subUser");

            Assert.IsNotNull(newUser1);
            Assert.IsNotNull(newUser2); 

            var newMeal1 = CreateTestMeal(newUser1);
            var newMeal2 = CreateTestMeal(newUser2);

            Assert.IsNotNull(newMeal1);
            Assert.IsNotNull(newMeal2); 

            //verify that user cannot delete another user's meal
            AssertExceptionType<UserNotAuthorizedException>(() =>
                MealService.DeleteMeal(newUser1.AuthToken, newUser2.Id, newMeal2.Id)
            );

            //verify that user can delete own meal 
            AssertExceptionIsNull(() =>
                MealService.DeleteMeal(newUser1.AuthToken, newUser1.Id, newMeal1.Id)
            );

            //verify that Admin can delete another user's meal 
            newUser1.PermissionLevel = UserPermissionLevel.Admin;
            AppDbContext.UpdateUser(newUser1);
            AssertExceptionIsNull(() =>
                MealService.DeleteMeal(newUser1.AuthToken, newUser2.Id, newMeal2.Id)
            );

            AppDbContext.DeleteUser(newUser1.Id);
            AppDbContext.DeleteUser(newUser2.Id); 
        }

        [TestMethod]
        public void GetReport()
        {
            var newUser = CreateTestUser("drygulchifer");

            //50 calories
            var newMeal1 = CreateTestMeal(newUser, "breakfast 1", 10, DateTimeUtil.StringToDateTime("01/01/2015 09:00"));
            var newMeal2 = CreateTestMeal(newUser, "lunch 1", 20, DateTimeUtil.StringToDateTime("01/01/2015 13:00"));
            var newMeal3 = CreateTestMeal(newUser, "dinner 1", 20, DateTimeUtil.StringToDateTime("01/01/2015 19:00"));

            //80 calories
            var newMeal4 = CreateTestMeal(newUser, "breakfast 2", 30, DateTimeUtil.StringToDateTime("01/02/2015 09:00"));
            var newMeal5 = CreateTestMeal(newUser, "lunch 2", 20, DateTimeUtil.StringToDateTime("01/02/2015 13:00"));
            var newMeal6 = CreateTestMeal(newUser, "dinner 2", 30, DateTimeUtil.StringToDateTime("01/02/2015 19:00"));

            //130 calories
            var newMeal7 = CreateTestMeal(newUser, "breakfast 3", 50, DateTimeUtil.StringToDateTime("01/03/2015 09:00"));
            var newMeal8 = CreateTestMeal(newUser, "lunch 3", 70, DateTimeUtil.StringToDateTime("01/03/2015 13:00"));
            var newMeal9 = CreateTestMeal(newUser, "dinner 3", 10, DateTimeUtil.StringToDateTime("01/03/2015 19:00"));


            //1/1 to 1/3, 00:00-23.00  (first 2 days, all meals) 
            var filterParams = new FilterParams(
                DateTimeUtil.StringToDate("01/01/2015"),
                DateTimeUtil.StringToDate("01/03/2015"),
                DateTimeUtil.StringToTime("00:00"), 
                DateTimeUtil.StringToTime("23:00")
            );

            var report = ReportService.GetReport(newUser.AuthToken, newUser.Id, filterParams);
            Assert.AreEqual(130, report.TotalCalories);
            Assert.AreEqual((int)(130 / 2), (int)(report.TotalCalories/report.Days.Count));
            Assert.AreEqual(2, report.Days.Count);
            Assert.AreEqual(50, report.Days[0].TotalCaloriesForPeriod);
            Assert.AreEqual(80, report.Days[1].TotalCaloriesForPeriod);
            Assert.AreEqual(50, report.Days[0].TotalCaloriesForDay);
            Assert.AreEqual(80, report.Days[1].TotalCaloriesForDay);
            Assert.AreEqual(false, report.Days[0].OverDailyTarget);
            Assert.AreEqual(false, report.Days[1].OverDailyTarget); 
            Assert.AreEqual(130/2, (report.AverageCaloriesPerDay));
            Assert.AreEqual(0, report.NumberOfDaysOverTarget); 


            //1/1 to 1/4, 00:00-23.00  (all days, all meals) 
            filterParams = new FilterParams(
                DateTimeUtil.StringToDate("01/01/2015"),
                DateTimeUtil.StringToDate("01/04/2015"),
                DateTimeUtil.StringToTime("00:00"),
                DateTimeUtil.StringToTime("23:00")
            );

            report = ReportService.GetReport(newUser.AuthToken, newUser.Id, filterParams);
            Assert.AreEqual(260, report.TotalCalories);
            Assert.AreEqual((int)(260 / 3), (int)(report.TotalCalories / report.Days.Count));
            Assert.AreEqual(3, report.Days.Count);
            Assert.AreEqual(50, report.Days[0].TotalCaloriesForPeriod);
            Assert.AreEqual(80, report.Days[1].TotalCaloriesForPeriod);
            Assert.AreEqual(130, report.Days[2].TotalCaloriesForPeriod);
            Assert.AreEqual(50, report.Days[0].TotalCaloriesForDay);
            Assert.AreEqual(80, report.Days[1].TotalCaloriesForDay);
            Assert.AreEqual(130, report.Days[2].TotalCaloriesForDay);
            Assert.AreEqual(false, report.Days[0].OverDailyTarget);
            Assert.AreEqual(false, report.Days[1].OverDailyTarget);
            Assert.AreEqual(false, report.Days[2].OverDailyTarget);
            Assert.AreEqual(260 / 3, (report.AverageCaloriesPerDay));
            Assert.AreEqual(0, report.NumberOfDaysOverTarget);


            //1/1 to 1/4, 09:00-10.00  (all days, breakfast only) 
            filterParams = new FilterParams(
                DateTimeUtil.StringToDate("01/01/2015"),
                DateTimeUtil.StringToDate("01/04/2015"),
                DateTimeUtil.StringToTime("09:00"),
                DateTimeUtil.StringToTime("10:00")
            );

            report = ReportService.GetReport(newUser.AuthToken, newUser.Id, filterParams);
            Assert.AreEqual(90, report.TotalCalories);
            Assert.AreEqual((int)(90 / 3), (int)(report.TotalCalories / report.Days.Count));
            Assert.AreEqual(3, report.Days.Count);
            Assert.AreEqual(10, report.Days[0].TotalCaloriesForPeriod);
            Assert.AreEqual(30, report.Days[1].TotalCaloriesForPeriod);
            Assert.AreEqual(50, report.Days[2].TotalCaloriesForPeriod);
            Assert.AreEqual(50, report.Days[0].TotalCaloriesForDay);
            Assert.AreEqual(80, report.Days[1].TotalCaloriesForDay);
            Assert.AreEqual(130, report.Days[2].TotalCaloriesForDay);
            Assert.AreEqual(false, report.Days[0].OverDailyTarget);
            Assert.AreEqual(false, report.Days[1].OverDailyTarget);
            Assert.AreEqual(false, report.Days[2].OverDailyTarget);
            Assert.AreEqual((int)(90 / 3), (report.AverageCaloriesPerDay));
            Assert.AreEqual(0, report.NumberOfDaysOverTarget);


            //1/2 to 1/4, 09:00-10.00  (last two days, breakfast only) 
            filterParams = new FilterParams(
                DateTimeUtil.StringToDate("01/02/2015"),
                DateTimeUtil.StringToDate("01/04/2015"),
                DateTimeUtil.StringToTime("09:00"),
                DateTimeUtil.StringToTime("10:00")
            );

            report = ReportService.GetReport(newUser.AuthToken, newUser.Id, filterParams);
            Assert.AreEqual(80, report.TotalCalories);
            Assert.AreEqual((int)(80 / 2), (int)(report.TotalCalories / report.Days.Count));
            Assert.AreEqual(2, report.Days.Count);
            Assert.AreEqual(30, report.Days[0].TotalCaloriesForPeriod);
            Assert.AreEqual(50, report.Days[1].TotalCaloriesForPeriod);
            Assert.AreEqual(80, report.Days[0].TotalCaloriesForDay);
            Assert.AreEqual(130, report.Days[1].TotalCaloriesForDay);
            Assert.AreEqual(false, report.Days[0].OverDailyTarget);
            Assert.AreEqual(false, report.Days[1].OverDailyTarget);
            Assert.AreEqual((int)(80 / 2), (report.AverageCaloriesPerDay));
            Assert.AreEqual(0, report.NumberOfDaysOverTarget);


            //1/7 to 1/9, 00:00-00.00  (zero days, zero meals) 
            filterParams = new FilterParams(
                DateTimeUtil.StringToDate("01/07/2015"),
                DateTimeUtil.StringToDate("01/09/2015"),
                DateTimeUtil.StringToTime("00:00"),
                DateTimeUtil.StringToTime("00:00")
            );

            report = ReportService.GetReport(newUser.AuthToken, newUser.Id, filterParams);
            Assert.AreEqual(0, report.TotalCalories);
            Assert.AreEqual(0, report.Days.Count);
            Assert.AreEqual(0, (report.AverageCaloriesPerDay));
            Assert.AreEqual(0, report.NumberOfDaysOverTarget);


            //1/1 to 1/9, 06:00-07.00  (zero days, zero meals) 
            filterParams = new FilterParams(
                DateTimeUtil.StringToDate("01/01/2015"),
                DateTimeUtil.StringToDate("01/09/2015"),
                DateTimeUtil.StringToTime("06:00"),
                DateTimeUtil.StringToTime("07:00")
            );

            report = ReportService.GetReport(newUser.AuthToken, newUser.Id, filterParams);
            Assert.AreEqual(0, report.TotalCalories);
            Assert.AreEqual(0, report.Days.Count);
            Assert.AreEqual(0, (report.AverageCaloriesPerDay));
            Assert.AreEqual(0, report.NumberOfDaysOverTarget);

            AppDbContext.DeleteUser(newUser.Id); 
        }


        protected User CreateTestUser(string username)
        {
            User user;
            return CreateTestUser(username, out user);
        }

        protected User CreateTestUser(string username, out User preCreatedUser)
        {
            UserViewModel viewModel = new UserViewModel()
            {
                Id = "-1",
                Password = "123458",
                PermissionLevel = "Admin",
                TargetCaloriesPerDay = "1000",
                Username = username
            };

            preCreatedUser = MappingLayer.ViewModelToUser(viewModel);

            var existingUser = AppDbContext.GetUser(preCreatedUser.Username);
            while (existingUser != null)
            {
                preCreatedUser.Username += "X";
                existingUser = AppDbContext.GetUser(preCreatedUser.Username);
            }

            return UserService.CreateUser(preCreatedUser);
        }

        protected Meal CreateTestMeal(User user, string description = null, int calories = 1000, DateTime? dateTime = null)
        {
            Meal preCreated;
            return CreateTestMeal(user, out preCreated, description, calories, dateTime);
        }

        protected Meal CreateTestMeal(User user, out Meal preCreatedMeal, string description = null, int calories = 1000, DateTime? dateTime = null)
        {
            if (description == null)
                description = "Royale with Cheese";

            MealViewModel viewModel = new MealViewModel()
            {
                Id = "-1",
                Calories = calories.ToString(),
                UserId = "-1",
                DateTime = DateTimeUtil.DateTimeToString(dateTime == null ? DateTime.Now : dateTime.Value),
                Description = description
            };

            preCreatedMeal = MappingLayer.ViewModelToMeal(viewModel);
            return MealService.CreateMeal(user.AuthToken, user.Id, preCreatedMeal);
        }

        protected void AssertExceptionType<T>(Action action)
        {
            Exception exception = null;
            try
            {
                action();
            }
            catch (Exception e)
            {
                exception = e;
            }

            Assert.IsNotNull(exception);
            Assert.IsTrue(exception is T);
        }

        protected void AssertExceptionIsNull(Action action)
        {
            Exception exception = null;
            try
            {
                action();
            }
            catch (Exception e)
            {
                exception = e;
            }

            Assert.IsNull(exception);
        }
    }
}
