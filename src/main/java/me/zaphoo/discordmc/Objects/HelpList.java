package me.zaphoo.discordmc.Objects;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class HelpList {

    public static SortedMap<String, String> HelpList = new TreeMap<>();

    public static EmbedObject getHelpEmbed() {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .withTitle("__**Help**__")
                .withDesc("**Prefix:** ob!")
                .withColor(new Color(145,145,145))
                .withTimestamp(Instant.now());

        for (Map.Entry entry : HelpList.entrySet()) {
            embedBuilder.appendField("**" + entry.getKey() + "**", entry.getValue().toString(), false);

        }
        return embedBuilder.build();
    }





}
