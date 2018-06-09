package me.zaphoo.discordmc.Objects;

import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.listener.DiscordEventListener;
import me.zaphoo.discordmc.util.Classes.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.RequestBuffer;

import java.util.*;

public class ConsoleReader implements ConsoleCommandSender, Permissible {

    private MessageReceivedEvent e;

    public ConsoleReader(MessageReceivedEvent e, String command) {
        this.e = e;
        try {

            Main.get().getServer().dispatchCommand(this, command);

            getMessage();


        } catch (Exception e1) {
            RequestBuffer.request(() -> e.getChannel().sendMessage(":x: Chain broken!"));
            DiscordEventListener.reportError(e1);
        }


    }

    private void getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : DiscordUtil.commandResponses) {
            stringBuilder.append(s).append("\n");
            if (stringBuilder.toString().length() >= 1850) {
                StringBuilder finalStringBuilder = stringBuilder;
                RequestBuffer.request(() -> e.getChannel().sendMessage(":white_check_mark: Command executed with response:\n```\n" + finalStringBuilder.toString() + "```"));
                stringBuilder = new StringBuilder();
            }
        }
        DiscordUtil.commandResponses.clear();
        StringBuilder finalStringBuilder1 = stringBuilder;
        RequestBuffer.request(() -> e.getChannel().sendMessage(":white_check_mark: Command executed with response:\n```\n" + finalStringBuilder1.toString() + "```"));
    }

    @Override
    public void sendMessage(String message) {
        DiscordUtil.commandResponses.add(ChatColor.stripColor(message));
    }

    @Override
    public void sendMessage(String[] messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : messages) {
            while (stringBuilder.toString().length() <= 1850) {
                stringBuilder.append(ChatColor.stripColor(s)).append("\n");
            }
            RequestBuffer.request(() -> e.getChannel().sendMessage(":white_check_mark: Command executed with following response:\n"));
        }
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return e.getClient().getApplicationName();
    }

    @Override
    public Spigot spigot() {
        return new Spigot();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {

    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String s) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {

    }

    @Override
    public void sendRawMessage(String s) {
        RequestBuffer.request(() -> {
            e.getChannel().sendMessage(":white_check_mark: Command executed with following response:\n```\n" + s + "\n```");
        });
    }
}
