using System;

namespace CalWatch.Api.Utilities
{
    public static class DateTimeExtensions
    {
        public static long ToUnixTimestamp(this DateTime target)
        {
            System.Threading.Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo("en-US"); 

            var date = new DateTime(1970, 1, 1, 0, 0, 0, target.Kind);
            var unixTimestamp = System.Convert.ToInt64((target - date).TotalSeconds);

            return unixTimestamp;
        }

        public static DateTime FromUnixTimestamp(this DateTime target, long timestamp)
        {
            var dateTime = new DateTime(1970, 1, 1, 0, 0, 0, target.Kind);

            return dateTime.AddSeconds(timestamp);
        }

        public static long ToUnixTimestampUTC(this DateTime target)
        {
            return target.ToUniversalTime().ToUnixTimestamp();
        }

        public static DateTime FromUnixTimestampUTC(this DateTime target, long timestamp)
        {
            return target.FromUnixTimestamp(timestamp).ToLocalTime();
        }

        public static bool IsSameDay(this DateTime d, DateTime value)
        {
            return (d.Year == value.Year &&
                d.Month == value.Month &&
                d.Day == value.Day); 
        }

        public static int DaysSince1970(this DateTime date)
        {
            return (int)Math.Floor(date.Subtract(new DateTime(1970, 1, 1)).TotalDays);
        }
    }
}
