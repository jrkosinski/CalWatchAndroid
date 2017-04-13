using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Diagnostics;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using CalWatch.Api;
using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Data;

namespace CalWatch.UnitTests
{
    //TODO: make these methods create & delete their own test object(s) for each method
    [TestClass]
    public class DataAccessTest : TestBase
    {   
        [TestMethod]
        public void TestStart()
        {
            DeleteAllUsers();
            CreateUsers();
        }

        [TestMethod]
        public void GetAllUsers()
        {
            var users = AppDbContext.GetUsers();

            Assert.IsNotNull(users);
            Assert.AreEqual(_testUsers.Count, users.Count());

            _testUsers = users;
        }

        [TestMethod]
        public void GetOneUser()
        {
            var user = AppDbContext.GetUser(_testUsers[0].Id);

            Assert.IsNotNull(user);
            Assert.AreEqual(_testUsers[0].Username, user.Username);
            Assert.AreEqual(_testUsers[0].PermissionLevel, user.PermissionLevel);
            Assert.AreEqual(_testUsers[0].TargetCaloriesPerDay, user.TargetCaloriesPerDay);
            Assert.AreEqual(_testUsers[0].DateCreated, user.DateCreated);
            Assert.AreEqual(_testUsers[0].DateModified, user.DateModified);
            Assert.AreEqual(user.DateCreated, user.DateModified);
        }

        [TestMethod]
        public void GetUserByUsername()
        {
            var user = AppDbContext.GetUser(_testUsers[0].Username); 

            Assert.IsNotNull(user); 
        }

        [TestMethod]
        public void UpdateUser()
        {
            var user = AppDbContext.GetUser(_testUsers[0].Id);

            Assert.IsNotNull(user);
            Assert.AreEqual(_testUsers[0].Password, user.Password);
            Assert.AreEqual(_testUsers[0].PermissionLevel, user.PermissionLevel);
            Assert.AreEqual(_testUsers[0].TargetCaloriesPerDay, user.TargetCaloriesPerDay);
            Assert.AreEqual(user.DateCreated, user.DateModified);

            UserPermissionLevel newPermission = UserPermissionLevel.DataViewer;
            int newCalories = 1;

            user.PermissionLevel = newPermission;
            user.TargetCaloriesPerDay = newCalories;

            AppDbContext.UpdateUser(user);
            var updatedUser = AppDbContext.GetUser(_testUsers[0].Id);

            Assert.IsNotNull(updatedUser);
            Assert.AreEqual(updatedUser.PermissionLevel, newPermission);
            Assert.AreEqual(updatedUser.TargetCaloriesPerDay, newCalories);
            Assert.AreEqual(user.DateCreated, updatedUser.DateCreated);
            Assert.AreNotEqual(user.DateModified, updatedUser.DateModified); 
        }

        [TestMethod]
        public void CreateMeals()
        {
            var user = AppDbContext.GetUser(_testUsers[0].Id);

            Assert.IsNotNull(user);

            var meal = new Meal(){ 
                UserId=user.Id, 
                Calories = 499, 
                Description = "Beef Stroganoff with whole wheat bread" ,
                DateTime = DateTime.Now
            };

            AppDbContext.CreateMeal(user.Id, meal);

            var meal2 = new Meal()
            {
                UserId = user.Id,
                Calories = 600,
                Description = "Pan-fried salmon with Longbottom Leaf",
                DateTime = DateTime.Now
            };

            AppDbContext.CreateMeal(user.Id, meal);

            user = AppDbContext.GetUser(_testUsers[0].Id);
            Assert.IsNotNull(user);

            var meals = AppDbContext.GetMeals(user.Id);
            Assert.AreEqual(2, meals.Count);
            Assert.AreEqual(meal.Calories, meals[0].Calories);
            Assert.AreEqual(meal.Description, meals[0].Description);
            Assert.AreEqual(meals[0].DateModified, meals[0].DateCreated); 
        }

        [TestMethod]
        public void GetMeals()
        {
        }

        [TestMethod]
        public void GetMeal()
        {
        }

        [TestMethod]
        public void GetMealsFiltered()
        {
            var newUser = CreateTestUser("rabbit");

            Assert.IsNotNull(newUser);
            List<Meal> meals = new List<Meal>(); 

            meals.Add(CreateTestMeal(newUser, DateTime.Parse("1/1/2015 00:00:00"), "0"));
            meals.Add(CreateTestMeal(newUser, DateTime.Parse("1/2/2015 00:00:00"), "1"));
            meals.Add(CreateTestMeal(newUser, DateTime.Parse("1/3/2015 00:00:00"), "2"));
            meals.Add(CreateTestMeal(newUser, DateTime.Parse("1/4/2015 00:00:00"), "3"));
            meals.Add(CreateTestMeal(newUser, DateTime.Parse("1/5/2015 00:00:00"), "4"));


            var filtered = AppDbContext.GetMealsFiltered(newUser.Id, new FilterParams());
            Assert.AreEqual(meals.Count, filtered.Count);

            filtered = AppDbContext.GetMealsFiltered(newUser.Id, 
                new FilterParams(
                    dateFrom:meals[2].DateTime));
            Assert.AreEqual(meals.Count - 2, filtered.Count);

            filtered = AppDbContext.GetMealsFiltered(newUser.Id, 
                new FilterParams(
                    dateTo: meals[3].DateTime));
            Assert.AreEqual(4, filtered.Count);

            filtered = AppDbContext.GetMealsFiltered(newUser.Id, 
                new FilterParams(
                    dateFrom: meals[1].DateTime, 
                    dateTo: meals[3].DateTime)); 
            Assert.AreEqual(3, filtered.Count);

            filtered = AppDbContext.GetMealsFiltered(newUser.Id,
                new FilterParams(
                    dateFrom: meals[2].DateTime,
                    dateTo: meals[3].DateTime)); 
            Assert.AreEqual(2, filtered.Count);

            DeleteUser(newUser);
        }

        [TestMethod]
        public void UpdateMeal()
        {
            var user = AppDbContext.GetUser(_testUsers[0].Id);
            var meals = AppDbContext.GetMeals(user.Id);
            Assert.IsNotNull(user);
            Assert.AreNotEqual(0, meals.Count);

            var meal = meals[0];

            int newCalories = 11;
            string newDescription = "new description";

            meal.Description = newDescription;
            meal.Calories = newCalories;

            AppDbContext.UpdateMeal(meal);
            var updatedMeal = AppDbContext.GetMeal(meal.Id);

            Assert.IsNotNull(updatedMeal);
            Assert.AreEqual(updatedMeal.Calories, newCalories);
            Assert.AreEqual(updatedMeal.Description, newDescription);
            Assert.AreEqual(meal.DateCreated, updatedMeal.DateCreated);
            Assert.AreNotEqual(meal.DateModified, updatedMeal.DateModified); 
        }

        [TestMethod]
        public void DeleteMeal()
        {
            var user = AppDbContext.GetUser(_testUsers[0].Id);
            var meals = AppDbContext.GetMeals(user.Id);
            
            Assert.IsNotNull(user);
            int mealCount = meals.Count;

            var meal = meals[0];

            Assert.IsNotNull(meal);

            AppDbContext.DeleteMeal(meal.Id);

            meal = AppDbContext.GetMeal(meal.Id);
            user = AppDbContext.GetUser(_testUsers[0].Id);
            meals = AppDbContext.GetMeals(user.Id);

            Assert.IsNull(meal);
            Assert.AreEqual(mealCount - 1, meals.Count); 
        }

        [TestMethod]
        public void PasswordCompareTest()
        {
            string password = "pass111";
            string username = "user111";
            var user = new User() { Username = username, Password = password, PermissionLevel = UserPermissionLevel.User, TargetCaloriesPerDay = 1400, AuthToken = AuthTokenUtil.GenerateNewAuthToken() };

            AppDbContext.CreateUser(user);

            user = AppDbContext.GetUsers().Where(u => u.Username == username).FirstOrDefault();

            Assert.IsNotNull(user);
            Assert.IsTrue(PasswordUtil.ComparePassword(password, user.Password, user.PasswordSalt)); 
        }

        
        protected User CreateTestUser(string username)
        {
            User user = new User()
            {
                Password = "123458",
                PermissionLevel = UserPermissionLevel.Admin,
                Username = username, 
                TargetCaloriesPerDay = 0
            };

            var existingUser = AppDbContext.GetUser(user.Username);
            while (existingUser != null)
            {
                user.Username += "X";
                existingUser = AppDbContext.GetUser(user.Username);
            }

            AppDbContext.CreateUser(user);
            return AppDbContext.GetUser(user.Username);
        }

        protected Meal CreateTestMeal(User user, DateTime dateTime, string description = null, int calories = 1000)
        {
            if (description == null)
                description = "Royale with Cheese";

            Meal meal = new Meal()
            {
                Calories = calories, 
                Description = description, 
                DateTime = dateTime
            };

            return AppDbContext.CreateMeal(user.Id, meal);
        }

        protected void DeleteUser(User user)
        {
            AppDbContext.DeleteUser(user.Id);
        }
    }
}
