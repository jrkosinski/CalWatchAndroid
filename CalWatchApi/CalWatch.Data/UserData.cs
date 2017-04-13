//------------------------------------------------------------------------------
// <auto-generated>
//    This code was generated from a template.
//
//    Manual changes to this file may cause unexpected behavior in your application.
//    Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace CalWatch.Data
{
    using System;
    using System.Collections.Generic;
    
    public partial class UserData
    {
        public UserData()
        {
            this.Meals = new HashSet<Meal>();
        }
    
        public int Id { get; set; }
        public string Username { get; set; }
        public string Password { get; set; }
        public string PasswordSalt { get; set; }
        public string AuthToken { get; set; }
        public byte PermissionLevel { get; set; }
        public int TargetCaloriesPerDay { get; set; }
        public System.DateTime LastLogin { get; set; }
        public System.DateTime DateCreated { get; set; }
        public System.DateTime DateModified { get; set; }
    
        public virtual ICollection<Meal> Meals { get; set; }
    }
}