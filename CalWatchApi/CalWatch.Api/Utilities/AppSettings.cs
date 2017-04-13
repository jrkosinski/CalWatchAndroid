using System;

namespace CalWatch.Api.Utilities
{
    public static class AppSettings
    {
        public static int SessionExpirationMinutes
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["SessionExpirationMinutes"]); }
        }

        public static int UsernameMinLength
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["UsernameMinLength"]); }
        }

        public static int UsernameMaxLength
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["UsernameMaxLength"]); }
        }

        public static int PasswordMinLength
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["PasswordMinLength"]); }
        }

        public static int PasswordMaxLength
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["PasswordMaxLength"]); }
        }

        public static int DescriptionMaxLength
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["DescriptionMaxLength"]); }
        }

        public static int DescriptionMinLength
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["DescriptionMinLength"]); }
        }

        public static bool EnableOutputCompression
        {
            get { return Boolean.Parse(System.Configuration.ConfigurationManager.AppSettings["EnableOutputCompression"]); }
        }

        public static int MinCaloriesValue
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["MinCaloriesValue"]); }
        }

        public static int MaxCaloriesValue
        {
            get { return Int32.Parse(System.Configuration.ConfigurationManager.AppSettings["MaxCaloriesValue"]); }
        }

        public static string UsernameFormat
        {
            get { return "^[a-zA-Z0-9_]*$"; }
        }

        public static string PasswordFormat
        {
            get { return "^[^\\ ]*$"; }
        }

        public static string DateFormat
        {
            get { return "MM/dd/yyyy"; }
        }

        public static string TimeFormat
        {
            get { return @"hh\:mm"; }
        }

        public static string DateTimeFormat
        {
            get { return DateFormat + " HH:mm"; }
        }

        public static DateTime MinDateValue
        {
            get { return DateTime.Now.AddYears(-10); }
        }

        public static DateTime MaxDateValue
        {
            get { return DateTime.Now.AddYears(10); }
        }
    }
}
