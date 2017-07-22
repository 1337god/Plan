package main.java.com.djrapitops.plan.command.commands.manage;

import com.djrapitops.plugin.command.CommandType;
import com.djrapitops.plugin.command.ISender;
import com.djrapitops.plugin.command.SubCommand;
import com.djrapitops.plugin.task.AbsRunnable;
import com.djrapitops.plugin.utilities.Verify;
import java.sql.SQLException;
import main.java.com.djrapitops.plan.Log;
import main.java.com.djrapitops.plan.Permissions;
import main.java.com.djrapitops.plan.Phrase;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.database.Database;
import main.java.com.djrapitops.plan.utilities.Check;
import main.java.com.djrapitops.plan.utilities.ManageUtils;

/**
 * This manage subcommand is used to clear a database of all data.
 *
 * @author Rsl1122
 * @since 2.3.0
 */
public class ManageClearCommand extends SubCommand {

    private final Plan plugin;

    /**
     * Class Constructor.
     *
     * @param plugin Current instance of Plan
     */
    public ManageClearCommand(Plan plugin) {
        super("clear", CommandType.CONSOLE_WITH_ARGUMENTS, Permissions.MANAGE.getPermission(), Phrase.CMD_USG_MANAGE_CLEAR + "", "<DB> [-a]");

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(ISender sender, String commandLabel, String[] args) {
        if (!Check.isTrue(args.length >= 1, Phrase.COMMAND_REQUIRES_ARGUMENTS_ONE + "", sender)) {
            return true;
        }

        String dbName = args[0].toLowerCase();
        boolean isCorrectDB = "sqlite".equals(dbName) || "mysql".equals(dbName);

        if (!Check.isTrue(isCorrectDB, Phrase.MANAGE_ERROR_INCORRECT_DB + dbName, sender)) {
            return true;
        }

        if (!Check.isTrue(Verify.contains("-a", args), Phrase.COMMAND_ADD_CONFIRMATION_ARGUMENT.parse(Phrase.WARN_REMOVE.parse(args[0])), sender)) {
            return true;
        }

        final Database database = ManageUtils.getDB(plugin, dbName);

        // If DB is null return
        if (!Check.isTrue(Verify.notNull(database), Phrase.MANAGE_DATABASE_FAILURE + "", sender)) {
            Log.error(dbName + " was null!");
            return true;
        }

        runClearTask(sender, database);
        return true;
    }

    private void runClearTask(ISender sender, final Database database) {
        plugin.getRunnableFactory().createNew(new AbsRunnable("DBClearTask") {
            @Override
            public void run() {
                try {
                    sender.sendMessage(Phrase.MANAGE_PROCESS_START.parse());

                    if (database.removeAllData()) {
                        sender.sendMessage(Phrase.MANAGE_CLEAR_SUCCESS + "");
                    } else {
                        sender.sendMessage(Phrase.MANAGE_PROCESS_FAIL + "");
                    }
                } catch (SQLException e) {
                    Log.toLog(this.getClass().getName(), e);
                    sender.sendMessage(Phrase.MANAGE_PROCESS_FAIL + "");
                } finally {
                    this.cancel();
                }
            }
        }).runTaskAsynchronously();
    }
}
