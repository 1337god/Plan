package main.java.com.djrapitops.plan.ui.html;

import java.util.List;
import main.java.com.djrapitops.plan.utilities.HtmlUtils;

/**
 *
 * @author Rsl1122
 */
public class RecentPlayersButtonsCreator {

    /**
     * Creates recent players buttons inside a p-tag.
     *
     * @param names Playernames sorted by last playtime.
     * @param limit How many playes will be shown
     * @return html p-tag list of recent logins.
     */
    public static String createRecentLoginsButtons(List<String> names, int limit) {
        StringBuilder html = new StringBuilder();
        html.append("<p>");
        for (int i = 0; i < names.size(); i++) {
            if (i < limit) {
                String name = names.get(i);
                html.append(Html.BUTTON.parse(HtmlUtils.getInspectUrl(name), name));
                html.append(" ");
            }
        }
        html.append("</p>");
        return html.toString();
    }
}
