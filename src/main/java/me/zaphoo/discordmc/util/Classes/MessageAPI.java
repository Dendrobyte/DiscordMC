package me.zaphoo.discordmc.util.Classes;

import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.listener.DiscordEventListener;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class MessageAPI {
    private static Main plugin;

    public MessageAPI(Main main) {
        MessageAPI.plugin = main;
    }

    /**
     * Send a message to a specific channel
     *
     * @param channel channel to receive message
     * @param message message to send
     */
    public static void sendToDiscord(long guild, long channel, String message) {
        try {
            RequestBuffer.request(() -> Main.getClient().getGuildByID(guild).getChannelByID(channel).sendMessage(message));
        } catch (DiscordException | MissingPermissionsException e) {
            DiscordEventListener.reportError(e);
        }
    }

    public static void sendToDiscord(long guild, long channel, IMessage message) {
        try {
            RequestBuffer.request(() -> Main.getClient().getGuildByID(guild).getChannelByID(channel).sendMessage(message.getContent()));
        } catch (DiscordException | MissingPermissionsException e) {
            DiscordEventListener.reportError(e);
        }
    }

    public static void sendToDiscord(long guild, long channel, EmbedObject embedObject) {
        try {
            RequestBuffer.request(() -> Main.getClient().getGuildByID(guild).getChannelByID(channel).sendMessage(embedObject));
        } catch (DiscordException | MissingPermissionsException e) {
            DiscordEventListener.reportError(e);
        }
    }
}
