package me.zaphoo.discordmc;

import me.zaphoo.discordmc.listener.DiscordEventListener;
import me.zaphoo.discordmc.util.Classes.DiscordUtil;
import me.zaphoo.discordmc.util.Classes.MessageAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sx.blah.discord.util.RequestBuffer;

public class InGameCommand implements CommandExecutor {
    private static Main plugin;
    private final String USAGE = "Usage: \n\t/discord <logout|login>";
    private final String LACKING_PERMISSION = "You are lacking the required permission to execute this command";


    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            cs.sendMessage(ChatColor.RED + USAGE);
            return true;
        }
        if (cs.hasPermission("discord.admin")) {
            switch (args[0]) {
                case "logout":
                    if (!Main.getClient().isLoggedIn()) {
                        Bukkit.getServer().getLogger().warning(cs.getName() + " tried to log the bot out. The bot was already logged out");
                        cs.sendMessage(ChatColor.RED + "Error. The bot is already logged out");
                    } else {
                        Bukkit.getServer().getLogger().info("Bot is logging out, per request of " + cs.getName());
                        RequestBuffer.request(() -> DiscordUtil.logout(Main.getClient()));
                    }
                    break;
                case "login":
                    if (Main.getClient().isLoggedIn()) {
                        Bukkit.getServer().getLogger().warning(cs.getName() + " tried to log the bot in. The bot was already logged in");
                        cs.sendMessage(ChatColor.RED + "Error. The bot is already logged in");
                    } else {
                        Bukkit.getServer().getLogger().info("Bot is logging in, per request of" + cs.getName());
                        RequestBuffer.request(() -> DiscordUtil.login(Main.getClient()));
                    }

                    break;
                case "testlog":
                    long channel = DiscordEventListener.getLogChannel();
                    long guild = DiscordEventListener.getGuild();
                    MessageAPI.sendToDiscord(guild, channel, "Test log");
                    break;
                case "info":
                    cs.sendMessage("Bans: " + plugin.getConfig().getLong("case-id") +
                            "Warnings: " + plugin.getConfig().getLong("ticket-id") +
                            "Uptime: " + DiscordUtil.getTotalUptime() +
                            "");


            }
        } else {
            cs.sendMessage(ChatColor.RED + LACKING_PERMISSION);
        }
        return true;
    }
}
