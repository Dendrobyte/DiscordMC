package me.zaphoo.discordmc.util;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {

    void runCommand(MessageReceivedEvent e, List<String> args);
}
