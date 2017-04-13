package com.calwatch.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by John R. Kosinski on 29/1/2559.
 */
public class StringUtil
{
    public static boolean matchesPattern(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isNullOrEmpty(String s)
    {
        return (s == null || s.length() == 0);
    }

    public static String padLeft(String s, int totalLength, char paddingChar)
    {
        if (s == null)
            s = "";

        while(s.length() < totalLength)
        {
            s = String.valueOf(paddingChar) + s;
        }

        return s;
    }

    public static String truncateText(String s, int maxLength)
    {
        if (s != null && maxLength >= 3) {
            if (s.length() > (maxLength)) {
                s = s.substring(0, maxLength -3) + "...";
            }
        }

        return s;
    }
}
