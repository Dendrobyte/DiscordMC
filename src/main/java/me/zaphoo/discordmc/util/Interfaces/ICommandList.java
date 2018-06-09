package me.zaphoo.discordmc.util.Interfaces;

import me.zaphoo.discordmc.Objects.HelpList;

public interface ICommandList {

    static void add(String name, String description) {
        HelpList.HelpList.put(name,description);
    }
}
