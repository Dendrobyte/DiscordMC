package me.zaphoo.discordmc.util.Interfaces;

import me.zaphoo.discordmc.Main;
import sx.blah.discord.handle.obj.IEmoji;

public interface IVoteUtil {

    IEmoji VOTE_GREEN = Main.getClient().getGuildByID(403264935745814560L).getEmojiByName("vote_green");
    IEmoji VOTE_RED = Main.getClient().getGuildByID(403264935745814560L).getEmojiByName("vote_red");
    IEmoji VOTE_YELLOW = Main.getClient().getGuildByID(403264935745814560L).getEmojiByName("vote_yellow");
    IEmoji VOTE_BLUE = Main.getClient().getGuildByID(403264935745814560L).getEmojiByName("vote_blue");
    IEmoji VOTE_GREY = Main.getClient().getGuildByID(403264935745814560L).getEmojiByName("vote_grey");
    IEmoji VOTE_AQUA = Main.getClient().getGuildByID(403264935745814560L).getEmojiByName("vote_aqua");
    IEmoji OKAY = Main.getClient().getGuilds().get(0).getEmojiByName("white_check_mark");

    IEmoji[] EMOJIS = {VOTE_GREEN, VOTE_RED, VOTE_YELLOW, VOTE_BLUE, VOTE_GREY, VOTE_AQUA};

}
