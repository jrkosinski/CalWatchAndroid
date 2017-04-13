using System;

namespace CalWatch.Api.Models
{
    public class User
    {
        /// <summary>
        /// Unique record id.
        /// </summary>
        public int Id { get; set; }
        /// <summary>
        /// Unique username.
        /// </summary>
        public string Username { get; set; }
        /// <summary>
        /// User password (if coming from database will be the hashed/salted version)
        /// </summary>
        public string Password { get; set; }
        /// <summary>
        /// Value used to salt password in DB.
        /// </summary>
        public string PasswordSalt { get; set; }
        /// <summary>
        /// Passed from client application, for authorization.
        /// </summary>
        public string AuthToken { get; set; }
        /// <summary>
        /// Indicates user's level.
        /// </summary>
        public UserPermissionLevel PermissionLevel { get; set; }
        /// <summary>
        /// User's target number of calories per day.
        /// </summary>
        public int TargetCaloriesPerDay { get; set; }
        /// <summary>
        /// Date of record creation. 
        /// </summary>
        public DateTime DateCreated { get; set; }
        /// <summary>
        /// Date of record last modification.
        /// </summary>
        public DateTime DateModified { get; set; }
        /// <summary>
        /// Date of last login.
        /// </summary>
        public DateTime LastLogin { get; set; }
    }
}
