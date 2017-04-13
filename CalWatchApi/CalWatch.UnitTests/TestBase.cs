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

namespace CalWatch.UnitTests
{
    public abstract class TestBase
    {
        protected static List<User> _testUsers = new List<User>(new User[]{
            new User(){ Username="user1", Password="pass1", PermissionLevel=UserPermissionLevel.User, TargetCaloriesPerDay=1400, AuthToken=Guid.NewGuid().ToString()}, 
            new User(){ Username="user2", Password="pass2", PermissionLevel=UserPermissionLevel.DataViewer, TargetCaloriesPerDay=3200, AuthToken=Guid.NewGuid().ToString()}, 
            new User(){ Username="user3", Password="pass3", PermissionLevel=UserPermissionLevel.Admin, TargetCaloriesPerDay=2000, AuthToken=Guid.NewGuid().ToString()},
            new User(){ Username="user4", Password="pass4", PermissionLevel=UserPermissionLevel.User, TargetCaloriesPerDay=2100, AuthToken=Guid.NewGuid().ToString()},
            new User(){ Username="user5", Password="pass5", PermissionLevel=UserPermissionLevel.User, TargetCaloriesPerDay=2400, AuthToken=Guid.NewGuid().ToString()}
        });


        protected void DeleteAllUsers()
        {
            var users = AppDbContext.GetUsers();

            Assert.IsNotNull(users);
            foreach (var user in users)
            {
                AppDbContext.DeleteUser(user.Id);
            }

            users = AppDbContext.GetUsers();
            Assert.AreEqual(0, users.Count);
        }

        protected void CreateUsers()
        {
            foreach (var user in _testUsers)
            {
                AppDbContext.CreateUser(user);
            }

            var users = AppDbContext.GetUsers();
            Assert.AreEqual(_testUsers.Count, users.Count);
        }

        protected User GetAnyUser()
        {
            return AppDbContext.GetUsers().FirstOrDefault();
        }
    }
}
