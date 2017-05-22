package main.java.com.djrapitops.plan.command.commands;

import main.java.com.djrapitops.plan.Permissions;
import main.java.com.djrapitops.plan.Phrase;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.Settings;
import main.java.com.djrapitops.plan.command.CommandType;
import main.java.com.djrapitops.plan.command.SubCommand;
import main.java.com.djrapitops.plan.data.cache.AnalysisCacheHandler;
import main.java.com.djrapitops.plan.ui.TextUI;
import main.java.com.djrapitops.plan.utilities.HtmlUtils;
import main.java.com.djrapitops.plan.utilities.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * This subcommand is used to run the analysis and access the /server link.
 *
 * @author Rsl1122
 * @since 2.0.0
 */
public class AnalyzeCommand extends SubCommand {

    private Plan plugin;
    private AnalysisCacheHandler analysisCache;

    /**
     * Subcommand Constructor.
     *
     * @param plugin Current instance of Plan
     */
    public AnalyzeCommand(Plan plugin) {
        super("analyze, analyse, analysis", Permissions.ANALYZE, Phrase.CMD_USG_ANALYZE.parse(), CommandType.CONSOLE, "");
        this.plugin = plugin;
        analysisCache = plugin.getAnalysisCache();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!Settings.WEBSERVER_ENABLED.isTrue()) {
            if (!Settings.SHOW_ALTERNATIVE_IP.isTrue()) {
                if (!Settings.USE_ALTERNATIVE_UI.isTrue()) {
                    sender.sendMessage(Phrase.ERROR_WEBSERVER_OFF_ANALYSIS + "");
                    return true;
                }
            }
        }
        sender.sendMessage(Phrase.GRABBING_DATA_MESSAGE + "");
        if (!analysisCache.isCached()) {
            int bootAnID = plugin.getBootAnalysisTaskID();
            if (bootAnID != -1) {
                plugin.getServer().getScheduler().cancelTask(bootAnID);
            }
            analysisCache.updateCache();
        } else if (MiscUtils.getTime() - analysisCache.getData().getRefreshDate() > 60000) {
            analysisCache.updateCache();
        }

        BukkitTask analysisMessageSenderTask = new BukkitRunnable() {
            private int timesrun = 0;

            @Override
            public void run() {
                timesrun++;
                if (analysisCache.isCached()) {
                    sendAnalysisMessage(sender);
                    this.cancel();
                }
                if (timesrun > 10) {
                    sender.sendMessage(Phrase.COMMAND_TIMEOUT.parse("Analysis"));
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1 * 20, 5 * 20);
        return true;
    }

    /**
     * Used to send the message after /plan analysis.
     *
     * @param sender Command sender.
     */
    public void sendAnalysisMessage(CommandSender sender) {
        boolean textUI = Settings.USE_ALTERNATIVE_UI.isTrue();
        sender.sendMessage(Phrase.CMD_ANALYZE_HEADER + "");
        if (textUI) {
            sender.sendMessage(TextUI.getAnalysisMessages());
        } else {
            // Link
            String url = HtmlUtils.getServerAnalysisUrl();
            String message = Phrase.CMD_LINK + "";
            boolean console = !(sender instanceof Player);
            if (console) {
                sender.sendMessage(message + url);
            } else {
                sender.sendMessage(message);
                Player player = (Player) sender;
                Bukkit.getServer().dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "tellraw " + player.getName() + " [\"\",{\"text\":\"" + Phrase.CMD_CLICK_ME + "\",\"underlined\":true,"
                        + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + url + "\"}}]");
            }
        }
        sender.sendMessage(Phrase.CMD_FOOTER + "");
    }
}
