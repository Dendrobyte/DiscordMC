package me.zaphoo.discordmc;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class MessageAPI {
    private static Main plugin;

    MessageAPI(Main main) {
        MessageAPI.plugin = main;
    }

    /**
     * Send a message to a specific channel
     *
     * @param channel channel to receive message
     * @param message message to send
     */
    public static void sendToDiscord(long guild, long channel, String message) {
        RequestBuffer.request(() -> {
            try {
                Main.getClient().getGuildByID(guild).getChannelByID(channel).sendMessage(message);

            } catch (DiscordException e) {
                plugin.getLogger().severe("Critical issue while sending message (string)... See stacktrace below");
                e.printStackTrace();
            } catch (MissingPermissionsException e) {
                plugin.getLogger().severe("Your Bot is missing required permissions to perform this action! "
                        + e.getErrorMessage());
            }
            return null;
        });
    }

    public static void sendToDiscord(long guild, long channel, IMessage message) {
        RequestBuffer.request(() -> {
            try {
                Main.getClient().getGuildByID(guild).getChannelByID(channel).sendMessage(message.getContent());

            } catch (DiscordException e) {
                plugin.getLogger().severe("Critical issue while sending message (IMessage)... See stacktrace below");
                e.printStackTrace();
            } catch (MissingPermissionsException e) {
                plugin.getLogger().severe("Your Bot is missing required permissions to perform this action! "
                        + e.getErrorMessage());
            }
            return null;
        });
    }

    public static void sendToDiscord(long guild, long channel, EmbedObject embedObject) {
        RequestBuffer.request(() -> {
            try {
                Main.getClient().getGuildByID(guild).getChannelByID(channel).sendMessage(embedObject);

            } catch (DiscordException e) {
                plugin.getLogger().severe("Critical issue while sending embed... See stacktrace below");
                e.printStackTrace();
            } catch (MissingPermissionsException e) {
                plugin.getLogger().severe("Your Bot is missing required permissions to perform this action! "
                        + e.getErrorMessage());
            }
            return null;
        });
    }

}
