package me.zaphoo.discordmc.util.Classes;

import me.zaphoo.discordmc.Objects.Mute;
import me.zaphoo.discordmc.Objects.Reminder;
import me.zaphoo.discordmc.listener.DiscordEventListener;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.Date;

public class EmbedUtils {

    public static EmbedObject warningToUser(IUser warned, IUser warnee, long ticketID, String reason, IGuild guild) {
        IChannel rulesChan = guild.getChannelByID(DiscordEventListener.getRulesChannel());
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Warning**__")
                .withDesc("You have been warned for: " + reason + ". " +
                        "\nYour ticket ID is: **" + Long.toString(ticketID) + "**. " +
                        "\nYou were warned by: **" + warnee.getName() + "#" + warnee.getDiscriminator() + "**. " +
                        "\nPlease make sure you have read the rules in " + rulesChan.mention() + "." +
                        "\nIf you think this is a mistake, please report it to the owner of the server, with a screenshot of this message.");
        eb.withColor(new Color(145, 145, 145))
                .withFooterText(warned.getName())
                .withFooterIcon(warned.getAvatarURL())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public static EmbedObject warningToChannel(IUser warned, IUser warnee, long ticketID, String reason, IGuild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Warning**__")
                .withDesc("**User warned:** " + warned.getName() + "#" + warned.getDiscriminator() +
                        "\n**Ticket ID:** " + Long.toString(ticketID) + ". " +
                        "\n**User ID:** " + warned.getStringID() +
                        "\n**Warned by:** " + warnee.getName() + "#" + warnee.getDiscriminator() + ". " +
                        "\n**Reason:** " + reason + ".")
                .withColor(new Color(145, 145, 145))
                .withFooterText(warned.getName())
                .withFooterIcon(warned.getAvatarURL())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public static EmbedObject banToChannel(IUser banned, String bannee, long caseID, String reason, IGuild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Ban**__")
                .withDesc("**User banned:** " + banned.mention() + "#" + banned.getDiscriminator() +
                        "\n**User ID:** " + banned.getStringID() +
                        "\n**Case-ID:** " + caseID +
                        "\n**Banned by:** " + bannee +
                        "\n**Reason:** " + reason)
                .withColor(new Color(145, 145, 145))
                .withFooterIcon(banned.getAvatarURL())
                .withFooterText(banned.getName())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public static EmbedObject banToUser(IUser banned, String bannee, long caseID, String reason, IGuild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Ban**__")
                .withDesc("**You have been banned for:** " + reason +
                        "\n**Your case-ID is:** " + Long.toString(caseID) +
                        "\n**You were banned by:** " + bannee +
                        "\nIf you think this is a mistake, please report it to the owner of the server, with a screenshot of this message.")
                .withColor(new Color(145, 145, 145))
                .withFooterIcon(banned.getAvatarURL())
                .withFooterText(banned.getName())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public static EmbedObject kickToChannel(IUser kicked, String kicker, String reason, IGuild guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.withTitle("__**Kick**__")
                .withDesc("**User kicked:** " + kicked.mention() + "#" + kicked.getDiscriminator() +
                        "\n**User ID:** " + kicked.getStringID() +
                        "\n**kicked by:** " + kicker +
                        "\n**Reason:** " + reason)
                .withColor(new Color(145, 145, 145))
                .withFooterIcon(kicked.getAvatarURL())
                .withFooterText(kicked.getName())
                .withTimestamp(Instant.now())
                .withAuthorName(guild.getName())
                .withAuthorIcon(guild.getIconURL());
        return eb.build();
    }

    public static EmbedObject scheduledReminder(Reminder reminder) {
        EmbedBuilder eb = new EmbedBuilder()
                .withTitle("**Reminder scheduled for " + new Date(reminder.then) + "**")
                .withTimestamp(reminder.now)
                .appendField("Message", reminder.message, false)
                .appendField("ID for deletion", String.valueOf(reminder.ID),false)
                .withThumbnail("https://cdn.pixabay.com/photo/2012/04/15/21/36/envelope-35393_960_720.png")
                .withColor(new Color(145, 145, 145));
        return eb.build();
    }

    public static EmbedObject scheduledRemind(String message, long millis) {
        return new EmbedBuilder()
                .withColor(new Color(145, 145, 145))
                .withTimestamp(millis)
                .appendField("You have a reminder", message, false)
                .withThumbnail("https://cdn.pixabay.com/photo/2012/04/15/21/36/envelope-35394_960_720.png")
                .withTitle("**Reminder**")
                .build();
    }

    public static EmbedObject muteEmbed(Mute mute) {
        return new EmbedBuilder().withColor(new Color(145, 145, 145))
                .withTimestamp(mute.now)
                .appendField("Your mute will expire:", new Date(mute.then).toString(), false)
                .appendField("You mute was issued by:", mute.responsible.getName(), false)
                .appendField("Your mute had the following reason:", mute.message, false)
                .appendField("You mute was of the type:", mute.type.toUpperCase(), false)
                .appendField("Was this a mistake?", "If so, please report it to the owner of the server, with a screenshot of this message.", false)
                .withThumbnail("http://0x0.st/s254.png")
                .withTitle("**You have been muted**")
                .build();
    }

    public static EmbedObject muteForeverEmbed(Mute mute) {
        return new EmbedBuilder().withColor(new Color(145, 145, 145))
                .withTimestamp(mute.now)
                .appendField("Your mute will expire:", "Never", false)
                .appendField("You mute was issued by:", mute.responsible.getName(), false)
                .appendField("Your mute had the following reason:", mute.message, false)
                .appendField("You mute was of the type:", mute.type.toUpperCase(), false)
                .appendField("Was this a mistake?", "If so, please report it to the owner of the server, with a screenshot of this message.", false)
                .withThumbnail("http://0x0.st/s254.png")
                .withTitle("**You have been muted**")
                .build();
    }
}
