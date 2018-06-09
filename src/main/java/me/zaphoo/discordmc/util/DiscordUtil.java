package me.zaphoo.discordmc.util;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for utility methods
 */
public class DiscordUtil {

    private static LocalDateTime uptime;
    public static List<String> commandResponses = new ArrayList<>();
    /**
     * Logout the client
     *
     * @param client client to disconnect
     * @return True when disconnect was successful, False if otherwise
     */
    public static boolean logout(IDiscordClient client) {
        try {
            client.logout();
            return true;
        } catch (DiscordException ignored) {
            return false;
        }
    }

    /**
     * Login the client
     *
     * @param client client to connect
     * @return True when connect was successful, False if otherwise
     */
    public static boolean login(IDiscordClient client) {
        try {
            client.login();
            return true;
        } catch (DiscordException | RateLimitException ignored) {
            return false;
        }
    }

    public static void setUptime(LocalDateTime uptime) {
        DiscordUtil.uptime = uptime;
    }

    private static LocalDateTime getTimeNow() {
        return LocalDateTime.now();
    }

    public static String getTotalUptime() {
        String uptime = "";
        long day, hour, minute;
        day = ChronoUnit.DAYS.between(DiscordUtil.uptime, getTimeNow());
        hour = ChronoUnit.HOURS.between(DiscordUtil.uptime, getTimeNow());
        minute = ChronoUnit.MINUTES.between(DiscordUtil.uptime, getTimeNow());
        if (day > 0) {
            if (day == 1) {
                uptime += day + " day, ";
            } else {
                uptime += day + " days, ";
            }
        }
        if ((hour % 24) > 0) {
            if (hour == 1) {
                uptime += (hour % 24) + " hour, and ";
            } else {
                uptime += (hour % 24) + " hours, and ";
            }
        }
        if ((minute % 60) == 1) {
            uptime += (minute % 60) + " minute.";
        } else if ((minute % 60) != 1) {
            uptime += (minute % 60) + " minutes.";
        }
        return uptime;
    }
}
