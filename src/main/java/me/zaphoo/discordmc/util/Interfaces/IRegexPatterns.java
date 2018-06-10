package me.zaphoo.discordmc.util.Interfaces;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IRegexPatterns {

    Pattern TIME_MATCH = Pattern.compile("((\\d+[dhm][\\s]?){1,3})");
    Pattern USER_MATCH = Pattern.compile("(<@(\\d+)>)");
    Pattern DATE_MATCH = Pattern.compile("([0]?[1-9]|[1|2][0-9]|[3][0|1])[\\s./-]([0]?[1-9]|[1][0-2])[\\s./-]([0-9]{4}|[0-9]{2})(\\s([0-2][0-9])\\s?([0-5][0-9]))?");

    static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    static boolean leqOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        return m.find() && m.group(0).split(" ").length <= length;
    }

}
