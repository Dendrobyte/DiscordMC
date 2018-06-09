package me.zaphoo.discordmc.Objects;

import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.util.IPermissionsUtil;
import me.zaphoo.discordmc.util.IRegexPatterns;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;

public class Reminder {

    //MATCHER -> ((\d+[dhms](\s)?){1,4}) matches date format 1d 1h 1m 1s

    public String message;
    public long days = 0, hours = 0, minutes = 0, /*seconds = 0,*/
            then = 0, now = 0;
    private int count;
    public long ID;

    public Reminder(MessageReceivedEvent e) {

        if (Main.getRemindFile().get("count." + e.getAuthor().getStringID()) != null) {
            count = Main.getRemindFile().getInt("count." + e.getAuthor().getStringID());
        } else {
            count = 0;
        }
        if (count < 5 /*|| e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(IPermissionsUtil.MANAGE_MESSAGE)*/) {
            this.message = e.getMessage().getContent();
            Matcher matcher = IRegexPatterns.TIME_MATCH.matcher(this.message);
            if (matcher.find()) {
                if (matcher.group(0).split(" ").length <= 3) {


                    for (String match : matcher.group().split(" ")) {
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
                    ID = ThreadLocalRandom.current().nextLong(1000L,9999999999L);
                    //ID = new Random().nextLong();
                    this.message = e.getMessage().getContent().substring(matcher.end(), e.getMessage().getContent().length());
                    Main.getRemindFile().set("count." + e.getAuthor().getStringID(), ++count);
                    Main.getRemindFile().set("reminders." + then + "." + e.getAuthor().getStringID() + ".message", message);
                    Main.getRemindFile().set("reminders." + then + "." + e.getAuthor().getStringID() + ".ID", ID);
                    Main.get().saveReminders();
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

    /*public static void main(String[] args) {
        while (true) {
            //Pattern p = Pattern.compile("(\\b(0?[1-9]|[12]\\d|30|31)[^\\w\\d\\r\\n:](0?[1-9]|1[0-2])[^\\w\\d\\r\\n:](\\d{4}|\\d{2})\\b)|(\\b(0?[1-9]|1[0-2])[^\\w\\d\\r\\n:](0?[1-9]|[12]\\d|30|31)[^\\w\\d\\r\\n:](\\d{4}|\\d{2})\\b)");
            Scanner scanner = new Scanner(System.in);
            String string = scanner.nextLine();

            Date date = null;
            for (DateFormat dateFormat : getDateFormats()) {
                try {
                    if (dateFormat.parse(string) != null) {
                        date = dateFormat.parse(string);
                    }
                } catch (ParseException e) {
                }
            }
            System.out.println(date);
            System.out.println(date.getTime());

        }
    }*/

    private static List<DateFormat> getDateFormats() {
        DateFormat[] arr = new DateFormat[]{
                new SimpleDateFormat("MMMM d yyyy"),
                new SimpleDateFormat("MM d yyyy"),
                new SimpleDateFormat("MMMM d yyyy HH mm"),
                new SimpleDateFormat("MM d yyyy HH mm"),
                new SimpleDateFormat("MMMM d yyyy HH mm"),
                new SimpleDateFormat("MM d yyyy HH mm"),
                new SimpleDateFormat("MMMM/d yyyy"),
                new SimpleDateFormat("MM/d yyyy"),
                new SimpleDateFormat("MMMM/d yyyy HH mm"),
                new SimpleDateFormat("MM/d yyyy HH mm"),
                new SimpleDateFormat("MMMM/d yyyy HH mm"),
                new SimpleDateFormat("MM/d yyyy HH mm"),
                new SimpleDateFormat("MMMM.d yyyy"),
                new SimpleDateFormat("MM.d yyyy"),
                new SimpleDateFormat("MMMM.d yyyy HH mm"),
                new SimpleDateFormat("MM.d yyyy HH mm"),
                new SimpleDateFormat("MMMM.d yyyy HH mm"),
                new SimpleDateFormat("MM.d yyyy HH mm"),
                new SimpleDateFormat("MMMM d yyyy HH:mm"),
                new SimpleDateFormat("MM d yyyy HH:mm"),
                new SimpleDateFormat("MMMM d yyyy HH:mm"),
                new SimpleDateFormat("MM d yyyy HH:mm"),
                new SimpleDateFormat("MMMM/d yyyy HH:mm"),
                new SimpleDateFormat("MM/d yyyy HH:mm"),
                new SimpleDateFormat("MMMM/d yyyy HH:mm"),
                new SimpleDateFormat("MM/d yyyy HH:mm"),
                new SimpleDateFormat("MMMM.d yyyy HH:mm"),
                new SimpleDateFormat("MM.d yyyy HH:mm"),
                new SimpleDateFormat("MMMM.d yyyy HH:mm"),
                new SimpleDateFormat("MM.d yyyy HH:mm"),
                new SimpleDateFormat("MMMM d yyyy HH.mm"),
                new SimpleDateFormat("MM d yyyy HH.mm"),
                new SimpleDateFormat("MMMM d yyyy HH.mm"),
                new SimpleDateFormat("MM d yyyy HH.mm"),
                new SimpleDateFormat("MMMM/d yyyy HH.mm"),
                new SimpleDateFormat("MM/d yyyy HH.mm"),
                new SimpleDateFormat("MMMM/d yyyy HH.mm"),
                new SimpleDateFormat("MM/d yyyy HH.mm"),
                new SimpleDateFormat("MMMM.d yyyy HH.mm"),
                new SimpleDateFormat("MM.d yyyy HH.mm"),
                new SimpleDateFormat("MMMM.d yyyy HH.mm"),
                new SimpleDateFormat("MM.d yyyy HH.mm")
        };
        return Arrays.asList(arr);
    }
}
