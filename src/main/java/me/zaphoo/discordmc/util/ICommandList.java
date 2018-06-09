package me.zaphoo.discordmc.util;

import me.zaphoo.discordmc.Objects.HelpList;

public interface ICommandList {

    static void add(String name, String description) {
        HelpList.HelpList.put(name,description);
    }
}
