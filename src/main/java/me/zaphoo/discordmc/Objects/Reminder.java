package me.zaphoo.discordmc.Objects;

import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.util.Interfaces.IPermissionsUtil;
import me.zaphoo.discordmc.util.Interfaces.IRegexPatterns;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Stream;

public class Reminder {

    //MATCHER -> ((\d+[dhms](\s)?){1,4}) matches date format 1d 1h 1m 1s

    public String message;
    private long days = 0, hours = 0, minutes = 0; /*seconds = 0,*/
    public long then = 0, now = 0;
    private int count;
    public long ID;

    public Reminder(MessageReceivedEvent e) {

        if (Main.getRemindFile().get("count." + e.getAuthor().getStringID()) != null) { // If count in reminder file is not null, counter is grabbed from reminder file
            count = Main.getRemindFile().getInt("count." + e.getAuthor().getStringID());
        } else { // Else your count is set to 0
            count = 0;
        }
        if ((count < 5) || IPermissionsUtil.hasPermission(e, Permissions.MANAGE_MESSAGES)) { // Check if count is less than 5 or if author is staff
            this.message = e.getMessage().getContent();
            Matcher timeParse = IRegexPatterns.TIME_MATCH.matcher(this.message); // Grab regex patterns from IRegexPatterns
            Matcher dateParse = IRegexPatterns.DATE_MATCH.matcher(this.message); // Grab regex patterns from IRegexPatterns
            if (this.message.split(" ")[1].equalsIgnoreCase("-d")) {
                if (dateParse.find()) {


                    String[] dateParts = dateParse.group(0).split("([\\s./-])");
                    if (dateParts.length == 3) {
                        String date = dateParse.group(0) + " 0"+ (6 + new Random().nextInt(3)) + " "+ new Random().nextInt(60);

                        dateParts = date.split("([\\s./-])");
                    }
                    if (dateParts[3].length() == 1) dateParts[3] = "0"+dateParts[3];
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : dateParts) {
                        stringBuilder.append(s).append(" ");
                    }
                    String date = stringBuilder.toString().trim();
                    Date remindDate = getMatchedDate(date);


                    if (remindDate.getTime() - System.currentTimeMillis() <= 0) {
                        e.getChannel().sendMessage(":x: The date specified cannot be before right now.");
                        return;
                    }
                    this.now = System.currentTimeMillis();
                    this.then = remindDate.getTime();
                    this.ID = ThreadLocalRandom.current().nextLong(1000L, 9999999999L);
                    this.message = e.getMessage().getContent().substring(dateParse.end(), e.getMessage().getContent().length());
                    Main.getRemindFile().set("count." + e.getAuthor().getStringID(), ++count);
                    Main.getRemindFile().set("reminders." + then + "." + e.getAuthor().getStringID() + ".message", message);
                    Main.getRemindFile().set("reminders." + then + "." + e.getAuthor().getStringID() + ".ID", ID);
                    Main.saveReminders();
                    e.getMessage().delete();
                    e.getChannel().sendMessage(":white_check_mark: Reminder set!");
                }
            } else if (timeParse.find()) {
                if (timeParse.group(0).split(" ").length <= 3) {


                    for (String match : timeParse.group().split(" ")) {
                        if (match.endsWith("d")) days = Integer.parseInt(match.substring(0, match.indexOf('d')));
                        if (match.endsWith("h")) hours = Integer.parseInt(match.substring(0, match.indexOf('h')));
                        if (match.endsWith("m")) minutes = Integer.parseInt(match.substring(0, match.indexOf('m')));
//                    if (s.endsWith("s")) seconds = Integer.parseInt(s.substring(0, s.indexOf('s'))); //FOR TESTING
                    }
                    this.now = System.currentTimeMillis();

                    this.days = days * 86400000;
                    this.hours = hours * 3600000;
                    this.minutes = minutes * 60000;
//                seconds = seconds * 1000; //FOR TESTING

                    then = now + this.days + this.hours + this.minutes;
                    ID = ThreadLocalRandom.current().nextLong(1000L, 9999999999L);
                    //ID = new Random().nextLong();
                    this.message = e.getMessage().getContent().substring(timeParse.end(), e.getMessage().getContent().length());
                    Main.getRemindFile().set("count." + e.getAuthor().getStringID(), ++count);
                    Main.getRemindFile().set("reminders." + then + "." + e.getAuthor().getStringID() + ".message", message);
                    Main.getRemindFile().set("reminders." + then + "." + e.getAuthor().getStringID() + ".ID", ID);
                    Main.saveReminders();
                    e.getMessage().delete();
                    e.getChannel().sendMessage(":white_check_mark: Reminder set!");

                }
            } else {
                e.getChannel().sendMessage(":x: You need to specify the time to remind you!");
            }
        } else {
            e.getMessage().reply(":x: You have too many reminders. The maximum is 5.");
        }
    }

    /**
     * Check if provided string matches a date
     * @param date String to mach
     * @return Matched date if any, else null
     */

    private static Date getMatchedDate(String date) {
        return getDateFormats().stream().map(simpleDateFormat -> {
            try {
                return simpleDateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }


    /**
     * Gets all valid date formats
     * @return A list of valid date formats
     */

    private static List<SimpleDateFormat> getDateFormats() {
        return Arrays.asList(
                new SimpleDateFormat("d MM yyyy HH mm"),
                new SimpleDateFormat("d/MM yyyy HH mm"),
                new SimpleDateFormat("d.MM yyyy HH mm"),
                new SimpleDateFormat("d MM yyyy HH:mm"),
                new SimpleDateFormat("d/MM yyyy HH:mm"),
                new SimpleDateFormat("d.MM yyyy HH:mm"),
                new SimpleDateFormat("d MM yyyy HH.mm"),
                new SimpleDateFormat("d/MM yyyy HH.mm"),
                new SimpleDateFormat("d.MM yyyy HH.mm"),
                new SimpleDateFormat("d MM-yyyy HH mm"),
                new SimpleDateFormat("d/MM-yyyy HH mm"),
                new SimpleDateFormat("d.MM-yyyy HH mm"),
                new SimpleDateFormat("d MM-yyyy HH:mm"),
                new SimpleDateFormat("d/MM-yyyy HH:mm"),
                new SimpleDateFormat("d.MM-yyyy HH:mm"),
                new SimpleDateFormat("d MM-yyyy HH.mm"),
                new SimpleDateFormat("d/MM-yyyy HH.mm"),
                new SimpleDateFormat("d.MM-yyyy HH.mm"));
    }
}
