/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plan.system.info;

import com.djrapitops.plan.api.exceptions.EnableException;
import com.djrapitops.plan.api.exceptions.connection.BadRequestException;
import com.djrapitops.plan.api.exceptions.connection.ConnectionFailException;
import com.djrapitops.plan.api.exceptions.connection.NoServersException;
import com.djrapitops.plan.api.exceptions.connection.WebException;
import com.djrapitops.plan.system.PlanSystem;
import com.djrapitops.plan.system.SubSystem;
import com.djrapitops.plan.system.info.connection.ConnectionSystem;
import com.djrapitops.plan.system.info.request.GenerateAnalysisPageRequest;
import com.djrapitops.plan.system.info.request.GenerateInspectPageRequest;
import com.djrapitops.plan.system.info.request.InfoRequest;
import com.djrapitops.plan.system.info.request.SendDBSettingsRequest;
import com.djrapitops.plan.system.info.server.Server;
import com.djrapitops.plan.system.info.server.ServerInfo;
import com.djrapitops.plan.system.webserver.WebServerSystem;
import com.djrapitops.plugin.api.Check;
import com.djrapitops.plugin.api.utility.log.Log;
import com.djrapitops.plugin.utilities.Verify;

import java.util.UUID;

/**
 * Information management system.
 * <p>
 * Subclasses should decide how InfoRequests are run locally if necessary.
 *
 * @author Rsl1122
 */
public abstract class InfoSystem implements SubSystem {

    protected final ConnectionSystem connectionSystem;

    protected InfoSystem(ConnectionSystem connectionSystem) {
        this.connectionSystem = connectionSystem;
    }

    public static InfoSystem getInstance() {
        InfoSystem infoSystem = PlanSystem.getInstance().getInfoSystem();
        Verify.nullCheck(infoSystem, () -> new IllegalStateException("Info System was not initialized."));
        return infoSystem;
    }

    public void generateAndCachePlayerPage(UUID player) throws WebException {
        GenerateInspectPageRequest infoRequest = new GenerateInspectPageRequest(player);
        try {
            sendRequest(infoRequest);
        } catch (ConnectionFailException e) {
            connectionSystem.sendWideInfoRequest(infoRequest);
        }
    }

    public void generateAnalysisPageOfThisServer() throws WebException {
        generateAnalysisPage(ServerInfo.getServerUUID());
    }

    public void generateAnalysisPage(UUID serverUUID) throws WebException {
        GenerateAnalysisPageRequest request = new GenerateAnalysisPageRequest(serverUUID);
        if (ServerInfo.getServerUUID().equals(serverUUID)) {
            runLocally(request);
        } else {
            sendRequest(request);
        }
    }

    public void sendRequest(InfoRequest infoRequest) throws WebException {
        try {
            if (!connectionSystem.isServerAvailable()) {
                runLocally(infoRequest);
                return;
            }
            connectionSystem.sendInfoRequest(infoRequest);
        } catch (WebException original) {
            try {
                // Attempt to run locally.
                runLocally(infoRequest);
            } catch (NoServersException e2) {
                throw original;
            }
        }
    }

    public abstract void runLocally(InfoRequest infoRequest) throws WebException;

    @Override
    public void enable() throws EnableException {
        connectionSystem.enable();
        try {
            updateNetworkPage();
        } catch (NoServersException e) {
            /* Ignored */
        } catch (WebException e) {
            // TODO Exception handling
            Log.toLog(this.getClass(), e);
        }
    }

    @Override
    public void disable() {
        connectionSystem.disable();
    }

    public ConnectionSystem getConnectionSystem() {
        return connectionSystem;
    }

    public abstract void updateNetworkPage() throws WebException;

    public void requestSetUp(String addressToRequestServer) throws WebException {
        if (Check.isBungeeAvailable()) {
            throw new BadRequestException("Method not available on Bungee.");
        }
        Server bungee = new Server(-1, null, "Bungee", addressToRequestServer, -1);
        String addressOfThisServer = WebServerSystem.getInstance().getWebServer().getAccessAddress();

        ConnectionSystem connectionSystem = ConnectionSystem.getInstance();
        connectionSystem.setSetupAllowed(true);
        connectionSystem.sendInfoRequest(new SendDBSettingsRequest(addressOfThisServer), bungee);
    }
}