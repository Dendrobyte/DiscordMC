package me.zaphoo.discordmc.util.Interfaces;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IRegexPatterns {

    Pattern TIME_MATCH = Pattern.compile("((\\d+[dhm][\\s]?){1,3})");
    Pattern USER_MATCH = Pattern.compile("(<@(\\d+)>)");
    DateFormat DATE_MATCHER = new SimpleDateFormat("MMMM d, yyyy");

    static boolean isMatch(Pattern patternToFind, String stringToMatch) {
        return patternToFind.matcher(stringToMatch).find();
    }

    static boolean leqOfLength(int length, Pattern patternToFind, String stringToMatch) {
        Matcher m = patternToFind.matcher(stringToMatch);
        return m.find() && m.group(0).split(" ").length <= length;
    }

}
