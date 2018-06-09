package me.zaphoo.discordmc.listener;

import com.google.common.primitives.UnsignedLong;
import com.vdurmont.emoji.EmojiParser;
import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.MessageAPI;
import me.zaphoo.discordmc.Objects.HelpList;
import me.zaphoo.discordmc.Objects.Mute;
import me.zaphoo.discordmc.Objects.Reminder;
import me.zaphoo.discordmc.util.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.entry.TargetedEntry;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageSendEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class DiscordEventListener {
    private final Main plugin;
    private final String commandPrefix;
    private static long channel;
    private static long announce;
    private static long guild;
    private static long rules;
    private static int reactCount = 0;

    public DiscordEventListener(Main plugin) {
        this.plugin = plugin;
        this.commandPrefix = plugin.getConfig().getString("settings.discord_commands.command_prefix", "ob!");
        channel = Long.parseUnsignedLong(plugin.getConfig().getString("settings.log-channel", "00"));
        guild = Long.parseUnsignedLong(plugin.getConfig().getString("settings.guild", "00"));
        rules = Long.parseUnsignedLong(plugin.getConfig().getString("settings.rules-channel", "00"));
        announce = Long.parseUnsignedLong(plugin.getConfig().getString("settings.announce-channel", "00"));
    }

    public static long getRules() {
        return rules;
    }

    private static HashMap<String, ICommand> commandMap = new HashMap<>();

    static {

        commandMap.put("setlogchannel", ((event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            if (IPermissionsUtil.hasPermission(event, Permissions.VIEW_AUDIT_LOG)) {
                long chan = event.getChannel().getLongID();
                long guild = event.getGuild().getLongID();
                Main.get().getConfig().set("settings.log-channel", chan);
                Main.get().getConfig().set("settings.guild", guild);
                Main.get().saveConfig();
                Main.get().reloadConfig();
                MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":white_check_mark: Log channel set. Deleting this message in 30 seconds.");
            } else {
                event.getMessage().reply(":x: You do not have permission to do that.");
            }
        }));

        commandMap.put("mute", (event, args) -> {
            if (IPermissionsUtil.hasPermission(event, Permissions.MANAGE_MESSAGES)) {
                new Mute(event);
            }
        });

        commandMap.put("setruleschannel", ((event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            if (IPermissionsUtil.hasPermission(event, Permissions.VIEW_AUDIT_LOG)) {
                long chan = event.getChannel().getLongID();
                long guild = event.getGuild().getLongID();
                Main.get().getConfig().set("settings.rules-channel", chan);
                Main.get().getConfig().set("settings.guild", guild);
                Main.get().saveConfig();
                Main.get().reloadConfig();
                MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":white_check_mark: Rules channel set. Deleting this message in 30 seconds.");
            } else {
                RequestBuffer.request(() -> event.getMessage().reply(":x: You do not have permission to do that."));
            }
        }));

        commandMap.put("setannouncechannel", ((event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            if (IPermissionsUtil.hasPermission(event, Permissions.VIEW_AUDIT_LOG)) {
                long chan = event.getChannel().getLongID();
                long guild = event.getGuild().getLongID();
                Main.get().getConfig().set("settings.announce-channel", chan);
                Main.get().getConfig().set("settings.guild", guild);
                Main.get().saveConfig();
                Main.get().reloadConfig();
                RequestBuffer.request(() -> MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":white_check_mark: Announce channel set. Deleting this message in 30 seconds."));

            } else {
                RequestBuffer.request(() -> event.getMessage().reply(":x: You do not have permission to do that."));
            }
        }));

        commandMap.put("warn", ((event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            if (IPermissionsUtil.hasPermission(event, Permissions.MANAGE_MESSAGES)) {
                if (event.getMessage().getMentions().size() > 0) {

                    IUser warned = event.getMessage().getMentions().get(0);
                    String reason;
                    long ticket = getAndUpdateTickets("warning");
                    if (event.getMessage().getContent().split(" ").length > 2) {
                        String[] sarray = event.getMessage().getContent().split(" ");
                        if (sarray.length > 2) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < sarray.length; i++) {
                                sb.append(sarray[i] + " ");
                            }
                            reason = sb.toString().trim();
                        } else {
                            reason = "*" + event.getAuthor().getName() + " did not provide any reason.*";
                        }
                    } else {
                        reason = "*" + event.getAuthor().getName() + " did not provide a reason for the warning.*";
                    }
                    RequestBuffer.request(() -> {
                        warned.getOrCreatePMChannel().sendMessage(EmbedUtils.warningToUser(warned, event.getAuthor(), ticket, reason, event.getGuild()));
                        warned.getOrCreatePMChannel().sendMessage("You can not reply to this message");
                        MessageAPI.sendToDiscord(getGuild(), getChannel(), EmbedUtils.warningToChannel(warned, event.getAuthor(), ticket, reason, event.getGuild()));
                    });

                } else {
                    RequestBuffer.request(() -> MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":x: Please provide or tag the user, you wish to warn"));
                }
            } else {
                RequestBuffer.request(() -> event.getMessage().reply(":x: You do not have permission to do that."));
            }
        }));

        commandMap.put("serverannounce", ((event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            if (IPermissionsUtil.hasPermission(event, Permissions.ADMINISTRATOR)) {
                String[] sarray = event.getMessage().getContent().split(" ");
                if (sarray.length > 1) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 1; i < sarray.length; i++) {
                        if (EmojiParser.extractEmojis(sarray[i]) != null)
                            sarray[i] = EmojiParser.parseToAliases(sarray[i]);
                        stringBuilder.append(sarray[i] + " ");
                    }
                    String message = stringBuilder.toString().trim();
                    String prefix = Main.get().getConfig().getString("settings.chat-prefix");


                    Bukkit.getServer().broadcastMessage(prefix + ChatColor.RED + event.getAuthor().getName() + "#" + event.getAuthor()
                            .getDiscriminator() + ChatColor.RESET + ChatColor.GRAY + " >> " + ChatColor.AQUA + message);
                    RequestBuffer.request(() -> MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":white_check_mark: Message : \"**" + message + "**\" has been sent to the server"));
                } else {
                    RequestBuffer.request(() -> MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":x: Please provide a message to send to the server."));
                }
            } else {
                RequestBuffer.request(() -> event.getMessage().reply(":x: You do not have permission to do that."));
            }
        }));

        commandMap.put("setticket", (event, args) -> {
            event.getMessage().delete();
            if (IPermissionsUtil.hasPermission(event, Permissions.ADMINISTRATOR)) {
                String[] arr = event.getMessage().getContent().split(" ");
                if (arr.length == 3) {
                    long ID;
                    try {
                        ID = Long.parseUnsignedLong(arr[2]);
                        getAndUpdateTickets(event, arr[1], ID);
                    } catch (NumberFormatException e) {
                        RequestBuffer.request(() -> event.getChannel().sendMessage(":x: `" + arr[2] + "` is not a number!"));
                    }
                } else {
                    RequestBuffer.request(() -> event.getChannel().sendMessage(":x: Message too long or too short. It must be `ob!setticket <type> <ID>`"));
                }
            } else {
                RequestBuffer.request(() -> event.getMessage().reply(":x: You do not have permission to do that."));
            }
        });

        commandMap.put("kill", (e, args) -> {
            if (IPermissionsUtil.hasPermission(e, Permissions.MANAGE_MESSAGES)) {
                RequestBuffer.killAllRequests();
                RequestBuffer.request(e.getMessage()::delete);
                RequestBuffer.request(() -> e.getChannel().sendMessage(":white_check_mark: All pending requests were killed"));
            } else {
                e.getChannel().sendMessage(":x: You do not have permission to do that.");
            }
        });

        commandMap.put("announce", (event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            if (IPermissionsUtil.hasPermission(event, Permissions.ADMINISTRATOR)) {
                String[] sarray = event.getMessage().getContent().split(" ");
                if (sarray.length > 1) {
                    RequestBuffer.request(() -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i < sarray.length; i++) {
                            stringBuilder.append(sarray[i] + " ");
                        }
                        String message = stringBuilder.toString().trim();
                        RequestBuffer.request(() -> {
                            MessageAPI.sendToDiscord(guild, getAnnounce(), "**Announcement from " + event.getAuthor().getName() + "**\n" + event.getGuild().getEveryoneRole().mention() + ", " + message);
                            MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":white_check_mark: Announcement made with the message \"" + message + "\"");
                        });
                    });
                } else {
                    RequestBuffer.request(() -> MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), ":x: Please provide a message to announce"));
                }
            } else {
                RequestBuffer.request(() -> event.getMessage().reply(":x: You do not have permission to do that."));
            }
        });

        commandMap.put("servercommand", (event, args) -> {
            if (IPermissionsUtil.hasPermission(event, Permissions.ADMINISTRATOR)) {
                RequestBuffer.request(() -> event.getMessage().delete());
                StringBuilder s = new StringBuilder();
                String[] command = event.getMessage().getContent().split(" ");


                List<String> unsafe = Arrays.asList("reload", "rl", "stop", "op", "restart");
                if (unsafe.stream().anyMatch(s1 -> s1.equalsIgnoreCase(command[1]))) {
                    RequestBuffer.request(() -> event.getChannel().sendMessage(":x: You are not permitted to use that command through Discord."));
                    return;
                }


                for (int i = 1; i < command.length; i++) {
                    s.append(command[i]).append(" ");
                }
                RequestBuffer.request(() -> new ConsoleReader(event, s.toString().trim()));


            }

        });

        commandMap.put("poll", (event, args) ->

        {
            event.getMessage().delete();
            if (IPermissionsUtil.hasPermission(event, Permissions.ADMINISTRATOR)) {
                String[] sarray = event.getMessage().getContent().split(" ");
                int count = IFunctions.count(event.getMessage().getContent(), ']');
                if (!(sarray.length > 1)) {
                    MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), "Please enter a question and options to create a poll.");
                    return;
                }
                if (count < 2) {
                    MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), "Too few options provided... There must be between 2 and 6 options.");
                    return;
                }
                if (count > 6) {
                    MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), "Too many options provided... There must be between 2 and 6 options.");
                    return;
                }
                RequestBuffer.request(() -> {
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i < sarray.length; i++) {
                            stringBuilder.append(sarray[i] + " ");
                        }
                        String message = stringBuilder.toString().trim();
                        String question = message.substring(0, message.indexOf('['));
                        if (question.length() == 0) {
                            MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), "Please provide a question for the poll.");
                            return;
                        }
                        List<String> options = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            try {
                                String optionsRaw = message.substring(message.indexOf('[') + 1, message.indexOf(']'));
                                if (optionsRaw.equalsIgnoreCase("") || optionsRaw == null) {
                                    MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), "One or more of the options did not contain an actual option. Please provide options for your options.");
                                    return;
                                }
                                message = message.replaceAll("\\[" + optionsRaw + "]", "");
                                options.add(optionsRaw);
                            } catch (StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                break;
                            }

                        }
                        String toReturn = event.getGuild().getEveryoneRole().mention() + ", **POLL** \n";
                        toReturn += question + "\n";
                        for (int i = 0; i < options.size(); i++) {
                            toReturn += "Vote " + IVoteUtil.EMOJIS[i] + " for: " + options.get(i) + "\n";
                        }
                        MessageAPI.sendToDiscord(guild, announce, toReturn);
                        setReactCount(options.size());
                        MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), "Poll created, with the question: \"" + question + "\". The question has " + options.size() + " options.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                event.getMessage().reply(":x: You do not have permission to do that.");
            }
        });
    }


    private static HashMap<String, ICommand> baseCommands = new HashMap<>();

    static {
        baseCommands.put("help", ((event, args) -> {
            RequestBuffer.request(() -> event.getMessage().delete());
            event.getAuthor().getOrCreatePMChannel().sendMessage(HelpList.getHelpEmbed());
            RequestBuffer.request(() -> event.getChannel().sendMessage(":white_check_mark: A message with the help list, has been sent to you in a DM."));
        }));

        baseCommands.put("uptime", ((event, args) -> RequestBuffer.request(() -> {
            event.getMessage().delete();
            event.getChannel().sendMessage("This bot, and the server it is running on, have been up for " + DiscordUtil.getTotalUptime());
        })));

        baseCommands.put("remind", (event, args) -> {
            if (Main.getRemindFile().getInt("count." + event.getAuthor().getStringID()) < 5) {
                Reminder r = new Reminder(event);
                event.getAuthor().getOrCreatePMChannel().sendMessage(EmbedUtils.scheduledReminder(r));
            }
        });

        baseCommands.put("deletereminder", (event, args) -> {
            event.getMessage().delete();
            if (event.getMessage().getContent().split(" ").length != 2) {
                event.getChannel().sendMessage(":x: Incorrect use of command. Refer to the help list, for correct use!");
                return;
            }

            try {
                Iterator<String> $i = Main.getRemindFile().getConfigurationSection("reminders").getKeys(false).iterator();
                while ($i.hasNext()) {
                    String stringRaw = $i.next().toString();
                    long time = Long.parseUnsignedLong(stringRaw.replaceAll("'", ""));
                    if (time > System.currentTimeMillis()) {
                        long ID = Long.parseLong(Main.getRemindFile().getConfigurationSection("reminders." + time).getKeys(false).iterator().next().replaceAll("'", ""));
                        long mID = Main.getRemindFile().getLong("reminders." + time + "." + ID + ".ID");
                        if (mID == Long.parseUnsignedLong(event.getMessage().getContent().split(" ")[1])) {
                            int count = Main.getRemindFile().getInt("count." + ID);
                            Main.getRemindFile().set("count." + ID, --count);
                            Main.getRemindFile().set("reminders." + time, null);
                            Main.saveReminders();
                            event.getChannel().sendMessage(":white_check_mark: Reminder deleted!");
                        }
                    }

                }
            } catch (Exception e) {
                DiscordEventListener.reportError(e);
            }


        });

        baseCommands.put("reminders", (event, args) -> {
            try {
                int count = (int) Main
                        .getRemindFile()
                        .getConfigurationSection("reminders")
                        .getKeys(false)
                        .stream()
                        .mapToLong(Long::parseUnsignedLong)
                        .filter(time -> time > System.currentTimeMillis())
                        .filter(time -> Long.parseLong(Main.getRemindFile()
                                .getConfigurationSection("reminders." + time)
                                .getKeys(false)
                                .iterator()
                                .next()
                                .replaceAll("'", "")) != 0)
                        .count();
                if (!event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(IPermissionsUtil.MANAGE_MESSAGE)) {
                    event.getChannel().sendMessage(":white_check_mark: You currently have `" + count + "` reminders. You have `" + (5 - count) + "` reminders left.");
                } else {
                    event.getChannel().sendMessage(":white_check_mark: You currently have `" + count + "` reminders.");
                }
            } catch (Exception e) {
                reportError(e);
            }
        });


    }

    public static long getAnnounce() {
        return announce;
    }

    public static int getReactCount() {
        return reactCount;
    }

    public static void setReactCount(int reactCount) {
        DiscordEventListener.reactCount = reactCount;
    }

    @EventSubscriber
    public void userChat(MessageReceivedEvent event) {
        try {
            if (event.getChannel().isPrivate() && event.getClient().getApplicationOwner() != event.getAuthor()) {
                event.getChannel().sendMessage(":x: This bot can not be used in private messages.");
                return;
            } else if (event.getAuthor() == event.getClient().getApplicationOwner() && event.getChannel().isPrivate()) {
                if (event.getMessage().getContent().equalsIgnoreCase("statistics")) {
                    IChannel channel = event.getChannel();
                    channel.sendMessage("This bot is connected to `" + Main.getClient().getGuilds().get(0).getUsers().size() + "` users.");
                    channel.sendMessage("It has been used `" + Main.get().getConfig().getInt("uses") + "` times.");
                } else if (event.getMessage().getContent().startsWith("set listening")) {
                    event.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, event.getMessage().getContent().substring(14));
                } else if (event.getMessage().getContent().startsWith("message")) {
                    String[] message = event.getMessage().getContent().split(" ");
                    try {
                        long l = Long.parseUnsignedLong(message[1]);
                        String s = "";
                        for (int i = 2; i < message.length; i++) {
                            s += message[i] + " ";
                        }
                        Main.getClient().getUserByID(l).getOrCreatePMChannel().sendMessage(s);
                    } catch (Exception e) {
                        reportError(e);
                    }
                } else if (event.getMessage().getContent().startsWith("restart")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                }
            }
            String[] message = event.getMessage().getContent().split(" ");
            if (!event.getMessage().getContent().toLowerCase().startsWith(commandPrefix)) {
                return;
            }
            int uses = Main.get().getConfig().getInt("uses");
            Main.get().getConfig().set("uses", ++uses);
            Main.get().saveConfig();
            if (message.length == 0) {
                return;
            }
            String command = message[0].substring(commandPrefix.length()).toLowerCase();
            List<String> argsList = new ArrayList<>(Arrays.asList(command));
            argsList.remove(0);
            if (event.getMessage().getContent().length() == 3)
                MessageAPI.sendToDiscord(guild, event.getChannel().getLongID(), HelpList.getHelpEmbed());
            if (commandMap.containsKey(command.toLowerCase())) {
                commandMap.get(command).runCommand(event, argsList);
            } else if (baseCommands.containsKey(command.toLowerCase())) {
                baseCommands.get(command).runCommand(event, argsList);
            }
        } catch (Exception e) {
            reportError(e);
        }

    }

    @EventSubscriber
    public void onReady(ReadyEvent event) { // When the bot is ready to execute actions
        try {
            if (event.getClient().getGuilds().size() == 0) { // the bot is not connected to any guilds, disable the plugin
                plugin.getLogger().warning("Your bot is not joined to any guild. Disabling plugin");
                Main.get().getServer().getPluginManager().disablePlugin(Main.get());
            }
            Main.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, Main.get().getConfig().getString("settings.discord_commands.command_prefix") + "!help"); // Change presence to online and display main help command

            /*
            Using ICommandList.add we add commands to the help list. Provide command in camelCase and add description.
            Separate the two strings with a comma. Make sure every description is escaped with \n at every punctuation
             */

            ICommandList.add("help", "Displays the help list." +
                    "\nUsage: ob!help");
            ICommandList.add("uptime", "Returns the uptime of both the server and the discord bot." +
                    "\nUsage: ob!uptime");
            ICommandList.add("serverAnnounce", "Broadcasts a message to the host server." +
                    "\nUsage: ob!serverAnnounce <message>");
            ICommandList.add("setLogChannel", "Set the channel where all warnings are logged." +
                    "\nUsage: ob!setLogChannel");
            ICommandList.add("SetRulesChannel", "Set the channel where all rules are listed." +
                    "\nThis channel will be embedded in an embed sent to a warned user." +
                    "\nUsage: ob!setRulesChannel");
            ICommandList.add("warn", "Warn a user. The user will get a direct message, and the warning will be logged." +
                    "\nUsage: ob!warn @<user> <reason>");
            ICommandList.add("setAnnounceChannel", "Set the channel where all announcements are made." +
                    "\nUsage: ob!setAnnounceChannel");
            ICommandList.add("announce", "Broadcast a message to the announcement channel." +
                    "\nUsage: ob!announce <message>");
            ICommandList.add("poll", "Create a poll in the announcement channel, for everyone to vote." +
                    "\nThe size of the poll varies based on the amount of options, the users are given." +
                    "\nOptions must be enclosed in [...] in order to work." +
                    "\nThere can not be more than 6 options in a poll." +
                    "\nUsage: ob!poll <question> [Option 1][Option 2]...");
            ICommandList.add("remind", "Creates a reminder for the user. The user will receive their reminder at the time specified." +
                    "\nTime format is in dhm format. Therefore `1d 2h 3m` is `1 day, 2 hours, and 3 minutes`" +
                    "\nA user can not have more than five reminders at a time." +
                    "\nUsage: ob!remind <time format> <message>");
            ICommandList.add("deleteReminder", "Removes the specified reminder, from your current queue of reminders. " +
                    "\nThe reminder ID can be found in your current reminders." +
                    "\nUsage: ob!deleteReminder <ID>");
            ICommandList.add("serverCommand", "Performs a command on the server as if you were the console. " +
                    "\nCommands can not be prefixed with `/`." +
                    "\nCan only be used by users with the Administrator permission!" +
                    "\nUsage: ob!serverCommand <command and arguments>");
            ICommandList.add("kill", "Kills any pending thread or requests." +
                    "\nUseful in cases of bot spam, with many requests sent at once." +
                    "\nUsage: ob!kill");


            Main.checkReminders(); // Check if any reminders have expired since last shutdown

            for (IChannel channel : event.getClient().getGuilds().get(0).getChannels()) { // Clean any messages from bot, in case of crash
                MessageHistory messages = channel.getFullMessageHistory(); // Get the message history
                Timer t = new Timer(); // Instantiate new timer
                t.schedule(new TimerTask() { // Schedule new TimerTask
                    @Override
                    public void run() {
                        messages.forEach(m -> { // For each message in the message history, check if it matches any of the following patters, with author being the bot
                            if (m.getAuthor() == event.getClient().getOurUser() && (m.getContent().startsWith("This bot, and the server it is running on, have been up for")
                                    || m.getContent().startsWith(":white_check_mark:")
                                    || m.getContent().endsWith("you have too many reminders. The maximum is 5.")
                                    || m.getContent().startsWith(":x:")
                                    || m.getContent().endsWith("You do not have permission to do that."))) {
                                RequestBuffer.request(m::delete);
                            }

                        });
                    }
                }, 6000L); // Delay of one second
            }
        } catch (Exception e) {
            reportError(e); // If any exception is thrown, send it to bot owner.
            /*
            When catching an exception do DiscordEventListener.reportError instead of printing stacktrace.
             */
        }

    }

    public static void reportError(Exception e) { // Method for reporting errors to bot owner instead of console

        IUser master = Main.getClient().getApplicationOwner(); // Get the bot owner
        IPrivateChannel channel = master.getOrCreatePMChannel(); // Get a private channel with owner
        RequestBuffer.request(() -> { // Prevent ratelimiting;
            channel.sendMessage(":warning: An error occurred! :warning:"); // Send warning to bot owner
            channel.sendMessage("```fix\n" + ExceptionUtils.getFullStackTrace(e) + "\n```"); // Send stacktrace to bot owner
        });
    }

    @EventSubscriber
    public void messageSent(MessageSendEvent e) { // Message sent from bot event
        try {
            List<IMessage> ID = new ArrayList<>(); // Messages logged in memory
            if (e.getMessage().getContent().startsWith("This bot, and the server it is running on, have been up for")
                    || e.getMessage().getContent().startsWith(":white_check_mark:")
                    || e.getMessage().getContent().endsWith("you have too many reminders. The maximum is 5.")
                    || e.getMessage().getContent().startsWith(":x:")
                    || e.getMessage().getContent().endsWith("You do not have permission to do that.")) { // Check if message matches format
                ID.add(e.getMessage()); // If true add the message to temp message log
                Timer t = new Timer(); // New timer
                t.schedule(new TimerTask() { // Schedule new timer task
                    @Override
                    public void run() {
                        RequestBuffer.request(() -> e.getMessage().delete()); // Prevent ratelimit when deleting message
                        ID.remove(e.getMessage()); // Remove message from memory
                    }
                }, 30000); // Set delay of 30 seconds
            }
            if (e.getMessage().getContent().startsWith("@everyone, **POLL**")) { // If message is poll

                for (int i = 0; i < getReactCount(); i++) { // For the amount of options, add an emote
                    final int j = i;
                    RequestBuffer.request(() -> e.getMessage().addReaction(IVoteUtil.EMOJIS[j]));
                }
            } else if (e.getMessage().getContent().split(" ").length == 9 && e.getMessage().getContent().endsWith(" channel set. Deleting this message in 30 seconds.")) { // If message is channel setter
                Thread thread = new Thread(e.getMessage().getStringID()) {  // Create new Thread
                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().sleep(30000); // Wait 30 seconds
                        } catch (InterruptedException e1) {
                            reportError(e1); // If interrupted, send stacktrace to owner
                        }
                        RequestBuffer.request(e.getMessage()::delete); // Prevent ratelimit when deleting message after 30 seconds
                    }
                };
                thread.start(); // Start thread
            }


        } catch (Exception e1) {
            reportError(e1);
        }
    }


    @EventSubscriber
    public void logBans(UserBanEvent e) {
        try {


            if (IPermissionsUtil.hasPermission(e, Permissions.VIEW_AUDIT_LOG)) {

                TargetedEntry entry = e.getGuild().getAuditLog(ActionType.MEMBER_BAN_ADD)
                        .getEntriesByTarget(e.getUser().getLongID())
                        .stream().sorted(Comparator.comparing(TargetedEntry::getTargetID).reversed())
                        .findFirst().get();
                long ticket = getAndUpdateTickets("ban");
                String bannedBy = entry.getResponsibleUser().getName();
                String reason = entry.getReason().orElse(bannedBy + " banned " + e.getUser().getName() + " without providing a reason.");
                MessageAPI.sendToDiscord(
                        guild,
                        channel,
                        EmbedUtils.banToChannel(
                                e.getUser(),
                                bannedBy,
                                ticket,
                                reason,
                                e.getGuild()
                        ));
                RequestBuffer.request(() -> {
                    e.getUser().getOrCreatePMChannel().sendMessage(EmbedUtils.banToUser(e.getUser(), bannedBy, ticket, reason, e.getGuild()));
                    e.getUser().getOrCreatePMChannel().sendMessage("You can not reply to this message");
                });
            } else {
                RequestBuffer.request(() -> {
                    MessageAPI.sendToDiscord(guild, channel, ":x: Please make sure that the bot has access the right permissions. " +
                            "Permissions missing: " + Permissions.VIEW_AUDIT_LOG);
                });
            }

        } catch (Exception e1) {
            reportError(e1);
        }
    }

    public static long getChannel() {
        return channel;
    }

    public static long getGuild() {
        return guild;
    }

    public static long getAndUpdateTickets(String caze) throws NullPointerException {
        try {
            if (caze.equalsIgnoreCase("warning")) {
                long ticket = Main.get().getConfig().getLong("ticket-id");
                Main.get().getConfig().set("ticket-id", ++ticket);
                Main.get().saveConfig();
                return ticket;
            } else if (caze.equalsIgnoreCase("ban")) {
                long ticket = Main.get().getConfig().getLong("case-id");
                Main.get().getConfig().set("case-id", ++ticket);
                Main.get().saveConfig();
                return ticket;
            } else {
                throw new NullPointerException("No case type " + caze + " is present. Contact the developer with this error message");
            }

        } catch (Exception e) {
            reportError(e);
        }
        return 0;
    }

    public static void getAndUpdateTickets(MessageReceivedEvent e, String caze, long ID) throws NullPointerException {
        try {
            if (caze.equalsIgnoreCase("warning")) {
                Main.get().getConfig().set("ticket-id", ID);
                Main.get().saveConfig();
                e.getChannel().sendMessage(":white_check_mark: The warning tickets was successfully set to " + ID);
            } else if (caze.equalsIgnoreCase("ban")) {
                Main.get().getConfig().set("case-id", ID);
                Main.get().saveConfig();
                e.getChannel().sendMessage(":white_check_mark: The ban tickets was successfully set to " + ID);
            } else {
                e.getChannel().sendMessage(":x: " + caze + " is not a valid category. Must be ban or warning.");
            }
        } catch (Exception e1) {
            reportError(e1);
        }
    }
}
