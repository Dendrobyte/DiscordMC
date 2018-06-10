package me.zaphoo.discordmc.TrelloEditor;

import me.zaphoo.discordmc.Main;
import me.zaphoo.discordmc.util.Classes.EmbedUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 6/10/2018.
 * Written for project DiscordMC
 * Please do not use or edit this code unless permissions has been given.
 * If you would like to use this code for modification and/or editing, do so with giving original credit.
 * Contact me on Twitter, @Mobkinz78
 * §
 */

/*
 * The class to pick up messages by a player.
 * The report will be located in config- settings.reports-channel
 * In the case of oinkcraft, this is [#]issue_reports
 * The report will be constructed based on the different elements of a message.
 * The message must contain "Title of Issue:", "MC or Discord Related: ", "MC Username:", "World/Channel:", and "Description:"
 */
public class ReportEvent {

    private IChannel channel;
    private IUser reporter;
    private IMessage report;
    private boolean isValidReport;

    public static final Long REPORTS_CHANNEL = Main.get().getConfig().getLong("settings.reports-channel");

    // Construct the report itself
    public ReportEvent(MessageReceivedEvent event) {
        this.channel = event.getChannel();
        this.reporter = event.getAuthor();
        this.report = event.getMessage();
        System.out.println(REPORTS_CHANNEL);
    }

    /*
     * Check to make sure the message meets all criteria
     * The message must contain "MC Username:", "World:", and "Description:"
     * The message must be sent in the reports-channel
     */
    public boolean checkValidity() {
        List<String> falseFlags = new ArrayList<>();
        isValidReport = true;
        // Not sent in the reports channel, thus can be ignored.
        if (!(channel.getLongID() == REPORTS_CHANNEL)) {
            isValidReport = false;
            return isValidReport;
        }

        /* Check if message contains "MC Username:", "World:", and "Description:"
         * Delete message and inform user if the message does not meet criteria.
         */
        String fullReport = report.getContent();
        if (!fullReport.contains("Title of Issue:")) {
            falseFlags.add("Title");
            isValidReport = false;
        }
        if (!fullReport.contains("MC or Discord Related:")) {
            falseFlags.add("Relation");
            isValidReport = false;
        }
        if (!fullReport.contains("MC Username:")) {
            falseFlags.add("MC Username");
            isValidReport = false;
        }
        if (!fullReport.contains("World/Channel:")) {
            falseFlags.add("World or channel");
            isValidReport = false;
        }
        if (!fullReport.contains("Description:")) {
            falseFlags.add("Description");
            isValidReport = false;
        }
        if (isValidReport){
            // Has passed all checks and is a valid message
            return isValidReport;
        } else {
            RequestBuffer.request(() -> {
                report.delete();
                reporter.getOrCreatePMChannel().sendMessage(EmbedUtils.incorrectReportEmbed(falseFlags));
            });
            return false;
        }
    }

    /*
     * Confirm the report and add a new card to the General Issues board.
     * If isValidReport is false, no card will be created as the message could not be verified.
     */
    public void officiallyFileReport() {
        System.out.println("CALLED FILE REPORT METHOD");

        if (!isValidReport) return;
//
        System.out.println("MOVING ON TO TRELLO METHOD");

        // Create the card fields
        String fullReport = report.getContent();
        int indexOfTitle = fullReport.indexOf("Title of Issue:"); // 15 chars
        int indexOfRelated = fullReport.indexOf("MC or Discord Related:"); // 22 chars
        int indexOfUsername = fullReport.indexOf("MC Username:"); // 12 chars
        int indexOfWorld = fullReport.indexOf("World/Channel:"); // 14 chars
        int indexOfDescription = fullReport.indexOf("Description:"); // 12 chars
        String cardName = fullReport.substring(indexOfTitle + 15, indexOfRelated);
        String cardDesc = fullReport.substring(indexOfRelated, indexOfUsername) + "\n"
                + fullReport.substring(indexOfUsername, indexOfWorld) + "\n"
                + fullReport.substring(indexOfWorld, indexOfDescription) + "\n"
                + fullReport.substring(indexOfDescription);

        // Add the card to general issues.
        EditBoard.createCardInReports(cardName, cardDesc);

        // Send them a good 'ol confirmation message
        RequestBuffer.request(() -> reporter.getOrCreatePMChannel().sendMessage(EmbedUtils.correctReportEmbed(reporter)));
    }



}
