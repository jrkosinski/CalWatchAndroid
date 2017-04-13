using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Used for cases when user attempts to do something that he is not permitted to do
    /// (e.g. non-admin user attempts to delete record of another user)
    /// </summary>
    [Serializable]
    public class UserNotAuthorizedException : Exception
    {
        public UserNotAuthorizedException() : base() { }
        public UserNotAuthorizedException(string message) : base(message) { }
    }
}
