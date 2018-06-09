package me.zaphoo.discordmc;

import me.zaphoo.discordmc.listener.DiscordEventListener;
import me.zaphoo.discordmc.util.Classes.DiscordUtil;
import me.zaphoo.discordmc.util.Classes.EmbedUtils;
import me.zaphoo.discordmc.util.Classes.MessageAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main instance;

    private static IDiscordClient client;
    private static File reminders;
    private static FileConfiguration remindersConfig;
    private static File mutes;
    private static FileConfiguration mutesConfig;

    private static boolean validToken;

    /**
     * Get instance of Main main class
     *
     * @return instance of main class
     */
    public static Main get() {
        return instance;
    }

    public static IDiscordClient getClient() {
        return client;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createRemindersFile();
        createMutesFile();

        // Set timer
        DiscordUtil.setUptime(LocalDateTime.now());

        // Disable all modules
        Configuration.LOAD_EXTERNAL_MODULES = false;
        Configuration.AUTOMATICALLY_ENABLE_MODULES = false;

        // Initialize messaging API
        new MessageAPI(this);

        String token = "settings.token",
                guild = "settings.guild",
                chan = "settings.log-channel",
                rules = "settings.rules-channel",
                announce = "settings.rules-channel",
                muteRank = "settings.mute-role",
                voiceMuteRank = "settings.voice-mute-role";


        // Token not entered, probably initial start
        if (Main.get().getConfig().getString(token).equalsIgnoreCase("TOKEN_HERE") || Main.get().getConfig().getString(token).equalsIgnoreCase("")) {
            getLogger().warning("You haven't entered a valid token for your bot. Please do this in the config");
            validToken = false;
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            validToken = true;
        }

        if (Main.get().getConfig().getString(guild).equalsIgnoreCase("") || Main.get().getConfig().getString(guild).equalsIgnoreCase("00")) {
            getLogger().warning("You haven't entered a valid guild ID for your bot. Please do this in the config");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        isValidSetting(chan);
        isValidSetting(rules);
        isValidSetting(announce);
        isValidSetting(muteRank);
        isValidSetting(voiceMuteRank);

        // Client builder and login
        try {
            client = new ClientBuilder().withToken(Main.get().getConfig().getString(token)).build();
        } catch (DiscordException e) {
            e.printStackTrace();
            getLogger().severe("Failed to build client");
        }

        // Disable audio
        Discord4J.disableAudio();

        // Register listeners
        client.getDispatcher().registerListener(new DiscordEventListener(this));
        client.getDispatcher().registerListener(this);

        try {
            client.login();
        } catch (DiscordException e) {
            e.printStackTrace();
            getLogger().severe("Failed to login");
        } catch (RateLimitException e) {
            e.printStackTrace();
            getLogger().severe("Ratelimited while logging in");
        }
        // Register command
        getCommand("discord").setExecutor(new InGameCommand());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "op " + client.getApplicationClientID());

    }

    @Override
    public void onDisable() {
        if (!validToken) {
            return;
        }
        client.logout();
        while (client.isLoggedIn()) { //Wait until client logs out
        }
    }

    @EventSubscriber
    public void onGuildCreate(GuildCreateEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        getLogger().info("Successfully logged in with '" + event.getClient().getOurUser().getName() + "'");
    }


    private void createRemindersFile() {
        reminders = new File(this.getDataFolder(), "reminders.yml");
        if (!reminders.exists()) {
            reminders.getParentFile().mkdirs();
            this.saveResource("reminders.yml", false);
        }
        remindersConfig = new YamlConfiguration();
        try {
            remindersConfig.load(reminders);
        } catch (IOException | InvalidConfigurationException var2) {
            var2.printStackTrace();
        }
    }

    public static void saveReminders() {
        if (reminders != null) {
            try {
                getRemindFile().save(reminders);
            } catch (IOException var2) {
                Main.get().getLogger().log(Level.SEVERE, "Could not save reminder to " + reminders, var2);
            }

        }
    }

    private void createMutesFile() {
        mutes = new File(this.getDataFolder(), "mutes.yml");
        if (!mutes.exists()) {
            mutes.getParentFile().mkdirs();
            this.saveResource("mutes.yml", false);
        }
        mutesConfig = new YamlConfiguration();
        try {
            mutesConfig.load(mutes);
        } catch (IOException | InvalidConfigurationException var2) {
            var2.printStackTrace();
        }
    }

    private static void saveMutes() {
        if (mutes != null) {
            try {
                getMutesFile().save(mutes);
            } catch (IOException var2) {
                Main.get().getLogger().log(Level.SEVERE, "Could not save reminder to " + mutes, var2);
            }

        }
    }

    public static FileConfiguration getMutesFile() {
        return mutesConfig;
    }

    public static FileConfiguration getRemindFile() {
        return remindersConfig;
    }

    private static boolean isValidSetting(String setting) {
        String set = Main.get().getConfig().getString(setting);
        if (set.equalsIgnoreCase("") || set.equalsIgnoreCase("00")) {
            Main.get().getLogger().warning("" + setting + " is either empty or invalid. Value was " + set);
            Main.get().getServer().getPluginManager().disablePlugin(Main.get());
            return false;
        } else {
            return true;
        }
    }

    public static void checkReminders() {
        try {
            Timer t = new Timer();
            if (getRemindFile().getConfigurationSection("reminders") == null) {
                getRemindFile().createSection("reminders");
            }
            if (getRemindFile().getConfigurationSection("count") == null) {
                getRemindFile().createSection("count");
            }
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (String s : getRemindFile().getConfigurationSection("reminders").getKeys(false)) {
                            long time = Long.parseUnsignedLong(s.replaceAll("'", ""));
                            if (time < System.currentTimeMillis()) {
                                long ID = Long.parseLong(getRemindFile().getConfigurationSection("reminders." + time).getKeys(true).iterator().next().replaceAll("'", ""));
                                String message = getRemindFile().getString("reminders." + time + "." + ID);
                                getClient().getUserByID(ID).getOrCreatePMChannel().sendMessage(EmbedUtils.scheduledRemind(message, time));
                                int count = getRemindFile().getInt("count." + ID);
                                getRemindFile().set("count." + ID, --count);
                                getRemindFile().set("reminders." + time, null);
                                saveReminders();

                            }

                        }
                    } catch (Exception e) {
                        DiscordEventListener.reportError(e);
                    }
                }
                // Check mutes here
            }, 0, 60000);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (String raw : getMutesFile().getConfigurationSection("mutes").getKeys(false)) {
                            long time = Long.parseUnsignedLong(raw.replaceAll("'", ""));
                            if (time < System.currentTimeMillis()) {

                                long ID = Long.parseUnsignedLong(getMutesFile().getConfigurationSection("mutes." + time).getKeys(true).iterator().next().replaceAll("'", ""));
                                if (getMutesFile().getLong("mutes." + time + "." + ID) != 8074) {
                                    IRole role = getClient().getRoleByID(getMutesFile().getLong("mutes." + time + "." + ID));
                                    getClient().getUserByID(ID).removeRole(role);
                                } else {
                                    IRole roleChat = getClient().getRoleByID(get().getConfig().getLong("settings.mute-role"));
                                    IRole roleVoice = getClient().getRoleByID(get().getConfig().getLong("settings.voice-mute-role"));
                                    getClient().getUserByID(ID).removeRole(roleChat);
                                    getClient().getUserByID(ID).removeRole(roleVoice);
                                }
                                getClient().getUserByID(ID).getOrCreatePMChannel().sendMessage("You have been unmuted! :white_check_mark:");
                                getMutesFile().set("mutes." + time, null);
                                saveMutes();
                            }
                        }
                    } catch (Exception e) {
                        DiscordEventListener.reportError(e);
                    }
                }
            }, 0, 60000);
        } catch (Exception e) {
            DiscordEventListener.reportError(e);
        }
    }
}


