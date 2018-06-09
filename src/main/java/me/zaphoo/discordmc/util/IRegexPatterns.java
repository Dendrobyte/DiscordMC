package me.zaphoo.discordmc.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRegexPatterns {

    public static final Pattern TIME_MATCH = Pattern.compile("((\\d+[dhm][\\s]?){1,3})");
    public static final Pattern USER_MATCH = Pattern.compile("(<@(\\d+)>)");
    public static final DateFormat DATE_MATCHER = new SimpleDateFormat("MMMM d, yyyy");

    public static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    public static boolean leqOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        if (m.find()) return m.group(0).split(" ").length <= length;
        return false;
    }

}
