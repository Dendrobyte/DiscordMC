package me.zaphoo.discordmc.Objects;

import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.listener.DiscordEventListener;
import me.zaphoo.discordmc.util.Classes.EmbedUtils;
import me.zaphoo.discordmc.util.Interfaces.IRegexPatterns;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.regex.Matcher;

public class Mute {


    private static final long MINUTE_IN_MILLIS = 1000 * 60, HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60, DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    private long days = 0;
    private long hours = 0;
    private long minutes = 0;
    public long then = 0;
    public long now = System.currentTimeMillis();
    public String message, type;
    public IUser responsible;

    public Mute(MessageReceivedEvent e) {
        try {
            responsible = e.getAuthor();

            this.message = e.getMessage().getContent();
            String[] content = this.message.split(" ");
            if (content.length >= 3) {
                RequestBuffer.request(() -> e.getMessage().delete());
                if (IRegexPatterns.isMatch(IRegexPatterns.USER_MATCH, content[1])) {
                    IUser muted = e.getMessage().getMentions().get(0);
                    Matcher matcher = IRegexPatterns.TIME_MATCH.matcher(this.message);
                    type = content[2];
                    IRole mute = Main.getClient().getRoleByID(Main.get().getConfig().getLong("settings.mute-role"));
                    IRole voiceMute = Main.getClient().getRoleByID(Main.get().getConfig().getLong("settings.voice-mute-role"));
                    if (muted.getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_MESSAGES)) {

                        if (type.equalsIgnoreCase("chat")) {
                            if (!muted.hasRole(mute)) {
                                if (matcher.find()) {
                                    if (matcher.group(0).split(" ").length <= 4) {
                                        for (String s : matcher.group().split(" ")) {
                                            if (s.endsWith("d"))
                                                days = Integer.parseInt(s.substring(0, s.indexOf('d')));
                                            if (s.endsWith("h"))
                                                hours = Integer.parseInt(s.substring(0, s.indexOf('h')));
                                            if (s.endsWith("m"))
                                                minutes = Integer.parseInt(s.substring(0, s.indexOf('m')));
                                        }
                                        this.now = System.currentTimeMillis();

                                        this.days = DAY_IN_MILLIS * days;
                                        this.hours = HOUR_IN_MILLIS * hours;
                                        this.minutes = MINUTE_IN_MILLIS * minutes;

                                        then = now + this.days + this.hours + this.minutes;

                                        this.message = e.getMessage().getContent().substring(matcher.end());
                                        if (this.message.isEmpty()) {
                                            this.message = "No reason provided.";
                                        }
                                        RequestBuffer.request(() -> {
                                            muted.addRole(mute);
                                            Main.getMutesFile().set("mutes." + String.valueOf(then) + "." + muted.getLongID(), mute.getLongID());
                                            muted.getOrCreatePMChannel().sendMessage(EmbedUtils.muteEmbed(this));
                                            muted.getOrCreatePMChannel().sendMessage("You can not reply to this message.");
                                            e.getChannel().sendMessage(":white_check_mark: Mute issued to " + muted.getName() + ", of the type: CHAT.");
                                        });

                                    }
                                } else {
                                    RequestBuffer.request(() -> e.getChannel().sendMessage(":white_check_mark: No expiration time found. Muting forever."));
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 3; i < content.length; i++) {
                                        sb.append(content[i]).append(" ");
                                    }
                                    if (sb.toString().length() == 0) {
                                        this.message = "No reason provided.";
                                    } else this.message = sb.toString().trim();
                                    RequestBuffer.request(() -> {
                                        muted.addRole(mute);
                                        muted.getOrCreatePMChannel().sendMessage(EmbedUtils.muteForeverEmbed(this));
                                        muted.getOrCreatePMChannel().sendMessage("You can not reply to this message.");
                                    });
                                }

                            } else {
                                RequestBuffer.request(() -> e.getChannel().sendMessage(":x: User " + muted.getName() + " is already chat muted."));
                            }
                        } else if (type.equalsIgnoreCase("voice")) {
                            if (!muted.hasRole(voiceMute)) {

                                if (matcher.find()) {
                                    if (matcher.group(0).split(" ").length <= 4) {
                                        for (String s : matcher.group().split(" ")) {
                                            if (s.endsWith("d"))
                                                days = Integer.parseInt(s.substring(0, s.indexOf('d')));
                                            if (s.endsWith("h"))
                                                hours = Integer.parseInt(s.substring(0, s.indexOf('h')));
                                            if (s.endsWith("m"))
                                                minutes = Integer.parseInt(s.substring(0, s.indexOf('m')));
                                        }
                                        this.now = System.currentTimeMillis();

                                        this.days = DAY_IN_MILLIS * days;
                                        this.hours = HOUR_IN_MILLIS * hours;
                                        this.minutes = MINUTE_IN_MILLIS * minutes;

                                        then = now + this.days + this.hours + this.minutes;

                                        this.message = e.getMessage().getContent().substring(matcher.end());
                                        if (this.message.isEmpty()) {
                                            this.message = "No reason provided.";
                                        }
                                        Main.getMutesFile().set(String.valueOf(then), muted.getLongID());
                                        Main.getMutesFile().set("mutes." + String.valueOf(then) + "." + muted.getLongID(), mute.getLongID());
                                        RequestBuffer.request(() -> {
                                            muted.addRole(voiceMute);
                                            muted.getOrCreatePMChannel().sendMessage(EmbedUtils.muteEmbed(this));
                                            muted.getOrCreatePMChannel().sendMessage("You can not reply to this message.");
                                            e.getChannel().sendMessage(":white_check_mark: Mute issued to " + muted.getName() + ", of the type: VOICE.");
                                        });
                                    }
                                } else {
                                    e.getChannel().sendMessage(":white_check_mark: No expiration time found. Muting forever.");
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 3; i < content.length; i++) {
                                        sb.append(content[i]).append(" ");
                                    }
                                    if (sb.toString().length() == 0) {
                                        this.message = "No reason provided.";
                                    } else this.message = sb.toString().trim();
                                    RequestBuffer.request(() -> {
                                        muted.addRole(voiceMute);
                                        muted.getOrCreatePMChannel().sendMessage(EmbedUtils.muteForeverEmbed(this));
                                        muted.getOrCreatePMChannel().sendMessage("You can not reply to this message.");
                                    });
                                }
                            } else {
                                RequestBuffer.request(() -> e.getChannel().sendMessage(":x: User " + muted.getName() + " is already voice muted."));
                            }
                        } else if (type.equalsIgnoreCase("both")) {
                            if (!muted.hasRole(voiceMute) || !muted.hasRole(mute)) {
                                if (matcher.find()) {
                                    if (matcher.group(0).split(" ").length <= 4) {
                                        for (String s : matcher.group().split(" ")) {
                                            if (s.endsWith("d"))
                                                days = Integer.parseInt(s.substring(0, s.indexOf('d')));
                                            if (s.endsWith("h"))
                                                hours = Integer.parseInt(s.substring(0, s.indexOf('h')));
                                            if (s.endsWith("m"))
                                                minutes = Integer.parseInt(s.substring(0, s.indexOf('m')));
                                        }
                                        this.now = System.currentTimeMillis();

                                        this.days = DAY_IN_MILLIS * days;
                                        this.hours = HOUR_IN_MILLIS * hours;
                                        this.minutes = MINUTE_IN_MILLIS * minutes;

                                        then = now + this.days + this.hours + this.minutes;

                                        this.message = e.getMessage().getContent().substring(matcher.end());
                                        RequestBuffer.request(() -> {
                                            muted.addRole(voiceMute);
                                            muted.addRole(mute);
                                            muted.getOrCreatePMChannel().sendMessage(EmbedUtils.muteEmbed(this));
                                            muted.getOrCreatePMChannel().sendMessage("You can not reply to this message.");
                                            e.getChannel().sendMessage(":white_check_mark: Mute issued to " + muted.getName() + ", of the type: BOTH.");
                                        });
                                        Main.getMutesFile().set(String.valueOf(then), muted.getLongID());
                                        Main.getMutesFile().set("mutes." + String.valueOf(then) + "." + muted.getLongID(), 8074);
                                    }
                                } else {
                                    RequestBuffer.request(() -> e.getChannel().sendMessage(":white_check_mark: No expiration time found. Muting forever."));
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 3; i < content.length; i++) {
                                        sb.append(content[i]).append(" ");
                                    }
                                    if (sb.toString().length() == 0) {
                                        this.message = "No reason provided.";
                                    } else this.message = sb.toString().trim();
                                    RequestBuffer.request(() -> {
                                        muted.addRole(voiceMute);
                                        muted.addRole(mute);
                                        muted.getOrCreatePMChannel().sendMessage(EmbedUtils.muteForeverEmbed(this));
                                        muted.getOrCreatePMChannel().sendMessage("You can not reply to this message.");
                                    });
                                }
                            } else {
                                RequestBuffer.request(() -> e.getChannel().sendMessage(":x: User " + muted.getName() + " is already muted in both categories."));
                            }
                        } else {
                            RequestBuffer.request(() -> e.getChannel().sendMessage(":x: `" + content[2] + "` is not a valid mute type. Please choose one of the following (voice | chat | both)"));
                        }
                    } else {
                        RequestBuffer.request(() -> e.getChannel().sendMessage(":x: That user can not be muted."));
                    }

                } else {
                    RequestBuffer.request(() -> e.getChannel().sendMessage(":x: `" + content[1] + "` is not a valid user. Please mention the user, as the first argument."));
                }
            } else {
                RequestBuffer.request(() -> e.getChannel().sendMessage(":x: Incorrect use of the mute command. Correct usage: `ob!mute <mentioned user> <type> [time] <message>`"));
            }
        } catch (Exception e1) {
            DiscordEventListener.reportError(e1);
        }
    }
}