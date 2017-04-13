using System;
using System.Collections.Generic;
using System.Linq;

using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.Exceptions;

namespace CalWatch.Api.Services
{
    /// <summary>
    /// Service layer will 
    /// - Check user permissions 
    /// - Perform any necessary validation 
    /// - Perform any business logic 
    /// </summary>
    public class UserService : ServiceBase
    {
        public static List<User> GetUsers(string authToken)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.ViewUsers))
                throw new UserNotAuthorizedException("Current user is not authorized to view a list of users."); 

            return AppDbContext.GetUsers().OrderBy(u => u.Username).ToList();
        }

        public static User GetUser(string authToken, int userId)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //protip: passing 0 for userId returns current user
            if (userId == 0)
                userId = currentUser.Id;

            //if current user is requested user, just return it 
            if (userId == currentUser.Id)
                return currentUser;

            //check security permissions
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.ViewUser, userId))
                throw new UserNotAuthorizedException("Current user is not authorized to view other users."); 

            return GetUser(userId); 
        }

        /// <summary>
        /// Authenticates the user with username/password. 
        /// </summary>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <returns>An authentication token, if successful.</returns>
        public static User Authenticate(string username, string password)
        {
            //get existing user
            User user = AppDbContext.GetUser(username); 

            //authenticate 
            if (user ==null)
                throw new AuthenticationException("Username incorrect."); 
            else 
            {
                if (!PasswordUtil.ComparePassword(password, user.Password, user.PasswordSalt))                    
                    throw new AuthenticationException("Password incorrect."); 
            }

            //update user with new authtoken
            user.AuthToken = AuthTokenUtil.GenerateNewAuthToken();
            user.LastLogin = DateTime.Now;
            AppDbContext.UpdateAuthData(user); 

            return user;
        }

        public static void Logout(string authToken)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //set auth token to ""
            currentUser.AuthToken = String.Empty;
            AppDbContext.UpdateAuthData(currentUser); 
        }

        public static User CreateUser(User user)
        {
            //check for uniqueness of username 
            var existingUser = GetUser(user.Username); 
            if (existingUser != null)
                throw new DuplicateEntityException(String.Format("The username {0} is already taken.", user.Username)); 

            //input validation 
            ValidationUtil.ValidateAndCleanCreateUser(user);

            //create user
            return AppDbContext.CreateUser(user);
        }

        public static User UpdateUser(string authToken, int userId, User user)
        {
            //get current user 
            var currentUser = AuthorizeUser(authToken);

            //check security permissions 
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.UpdateUser, userId))
                throw new UserNotAuthorizedException(String.Format("Current user not authorized to update user {0}.", userId)); 

            //input validation 
            ValidationUtil.ValidateAndCleanUpdateUser(currentUser, user);

            //update user 
            user.Id = userId;
            return AppDbContext.UpdateUser(user);
        }

        public static void DeleteUser(string authToken, int userId)
        {
            //get current user 
            var currentUser = AuthorizeUser(authToken);

            //check security permissions 
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.UpdateUser, userId))
                throw new UserNotAuthorizedException(String.Format("Current user not authorized to delete user {0}.", userId));

            //delete user 
            AppDbContext.DeleteUser(userId);
        }
    }
}
