/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plan.system.webserver.auth;

import com.djrapitops.plan.PlanPlugin;
import com.djrapitops.plan.api.exceptions.WebUserAuthException;
import com.djrapitops.plan.data.WebUser;
import com.djrapitops.plan.database.tables.SecurityTable;
import com.djrapitops.plan.utilities.PassEncryptUtil;

import java.util.Base64;

/**
 * //TODO Class Javadoc Comment
 *
 * @author Rsl1122
 */
public class BasicAuthentication implements Authentication {

    private String authenticationString;

    private WebUser user;

    public BasicAuthentication(String authenticationString) {
        this.authenticationString = authenticationString;
    }

    @Override
    public boolean isAuthorized(String permission) throws WebUserAuthException {
        if (user == null) {
            user = getUser();
            return user.hasPermission(permission);
        }
        return false;
    }

    public WebUser getUser() throws WebUserAuthException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decoded = decoder.decode(authenticationString);
        String[] userInfo = new String(decoded).split(":");
        if (userInfo.length != 2) {
            throw new WebUserAuthException(FailReason.USER_AND_PASS_NOT_SPECIFIED);
        }

        String user = userInfo[0];
        String passwordRaw = userInfo[1];

        try {
            SecurityTable securityTable = PlanPlugin.getInstance().getDB().getSecurityTable();
            if (!securityTable.userExists(user)) {
                throw new WebUserAuthException(FailReason.USER_DOES_NOT_EXIST);
            }

            WebUser webUser = securityTable.getWebUser(user);

            boolean correctPass = PassEncryptUtil.verifyPassword(passwordRaw, webUser.getSaltedPassHash());
            if (!correctPass) {
                throw new WebUserAuthException(FailReason.USER_PASS_MISMATCH);
            }
            return webUser;
        } catch (Exception e) {
            throw new WebUserAuthException(e);
        }
    }
}