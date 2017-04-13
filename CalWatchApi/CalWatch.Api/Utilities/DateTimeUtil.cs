using System;
using System.Globalization;

namespace CalWatch.Api.Utilities
{
    public static class DateTimeUtil
    {
        public static CultureInfo CultureInfo = new CultureInfo("en-US");

        public static string DateTimeToString(DateTime value)
        {
            return value.ToString(AppSettings.DateTimeFormat, CultureInfo);
        }

        public static DateTime? StringToDateTime(string value)
        {
            DateTime output;
            if (!DateTime.TryParseExact(value, AppSettings.DateTimeFormat, CultureInfo, DateTimeStyles.AssumeLocal, out output))
                return null;

            return output;
        }

        public static string DateToString(DateTime value)
        {
            return value.ToString(AppSettings.DateFormat, CultureInfo);
        }

        public static DateTime? StringToDate(string value)
        {
            DateTime output;
            if (!DateTime.TryParseExact(value, AppSettings.DateFormat, CultureInfo, DateTimeStyles.AssumeLocal, out output))
                return null;

            return output;
        }

        public static string TimeToString(TimeSpan value)
        {
            return value.ToString(AppSettings.TimeFormat, CultureInfo);
        }

        public static TimeSpan? StringToTime(string value)
        {
            TimeSpan output;
            if (!TimeSpan.TryParseExact(value, AppSettings.TimeFormat, CultureInfo, TimeSpanStyles.None, out output))
                return null;

            return output;
        }
    }
}
