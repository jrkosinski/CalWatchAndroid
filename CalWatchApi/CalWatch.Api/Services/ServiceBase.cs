using System;

using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.Exceptions;

namespace CalWatch.Api.Services
{
    /// <summary>
    /// Base class for all service classes. 
    /// </summary>
    public abstract class ServiceBase
    {
        protected static User AuthorizeUser(string authToken)
        {
            if (String.IsNullOrEmpty(authToken))
            {
                throw new UserNotAuthorizedException("Auth token not received.");
            }

            var user = AppDbContext.GetUserByAuthToken(authToken);
            if (user == null)
                throw new AuthTokenInvalidException("Auth token invalid.");

            if (DateTime.Now.Subtract(user.LastLogin).TotalMinutes > AppSettings.SessionExpirationMinutes)
                throw new AuthExpiredException("Auth token expired.");

            return user;
        }

        protected static User GetUser(string username)
        {
            return AppDbContext.GetUser(username);
        }

        protected static User GetUser(int userId)
        {
            return AppDbContext.GetUser(userId);
        }
    }
}
