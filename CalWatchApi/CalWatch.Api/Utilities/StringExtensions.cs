using System;
using System.Text;

namespace CalWatch.Api.Utilities
{
    public static class StringExtensions
    {
        public static bool EqualsIgnoreCase(this string s, string s2)
        {
            if (s == null || s2 == null)
                return (s == null && s2 == null);

            return (s.ToLower() == s2.ToLower()); 
        }

        public static string Repeat(this string s, int count)
        {
            StringBuilder sb = new StringBuilder();
            for (int n = 0; n < count; n++)
                sb.Append(s);

            return sb.ToString(); 
        }
    }
}
