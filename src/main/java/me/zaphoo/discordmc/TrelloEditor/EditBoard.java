package me.zaphoo.discordmc.TrelloEditor;

import me.zaphoo.discordmc.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.trello4j.Trello;
import org.trello4j.TrelloImpl;
import org.trello4j.model.Board;
import org.trello4j.model.Card;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 6/10/2018.
 * Written for project DiscordMC
 * Please do not use or edit this code unless permissions has been given.
 * If you would like to use this code for modification and/or editing, do so with giving original credit.
 * Contact me on Twitter, @Mobkinz78
 * §
 */
public class EditBoard {
    private static FileConfiguration config = Main.get().getConfig();
    private static final String API_KEY = config.getString("settings.trello.API-key");
    private static final String API_TOKEN = config.getString("settings.trello.API-token");

    private static Trello botTrello = new TrelloImpl(API_KEY, API_TOKEN);

    // Check if the keys are there, thus preventing errors spamming console and such.

    /* Create board
     * General Issues Board ID: 5b159d93431c9a85db11a4f1
     */
    private final String genIssuesBoardID = "5b159d93431c9a85db11a4f1";
    private Board genIssues = botTrello.getBoard(genIssuesBoardID);

    /*
     * Create card in "Player Reports" on the "General Issues" board [no label]
     * List ID: 5b15ac6db7100d5b46e29774
     */
    public static void createCardInReports(String cardName, String cardDesc) {
        System.out.println(true);
        String reportsID = "5b15ac6db7100d5b46e29774";
        String name = cardName;
        String desc = cardDesc;
        Map<String, String> descMap = new HashMap<String, String>();
        descMap.put("desc", desc);

        // Create card
        Card card = botTrello.createCard(reportsID, name, descMap);
    }
    /* TODO: Add label support
     * private void createCardInReports(String name, String desc, int labelChoice){}
     * Where labelChoice is in the discord message and we've pre-assigned them
     */

}
