package me.zaphoo.discordmc.util.Interfaces;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.Permissions;

public interface IPermissionsUtil {

    Permissions VIEW_AUDIT = Permissions.VIEW_AUDIT_LOG;
    Permissions SEND_MESSAGE = Permissions.SEND_MESSAGES;
    Permissions MANAGE_MESSAGE = Permissions.MANAGE_MESSAGES;

    static boolean hasPermission(UserBanEvent event, Permissions perm) {
        return event.getClient().getOurUser().getPermissionsForGuild(event.getGuild()).contains(perm);
    }

    static boolean hasPermission(MessageReceivedEvent event, Permissions perm) {

        return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(perm);
    }
}
