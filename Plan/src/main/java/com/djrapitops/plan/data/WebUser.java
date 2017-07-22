package main.java.com.djrapitops.plan.data;

/**
 * Object containing webserver security user information.
 *
 * @author Rsl1122
 * @since 3.5.2
 */
public class WebUser {

    private final String user;
    private final String saltedPassHash;
    private final int permLevel;

    public WebUser(String user, String saltedPassHash, int permLevel) {
        this.user = user;
        this.saltedPassHash = saltedPassHash;
        this.permLevel = permLevel;
    }

    public String getName() {
        return user;
    }

    public String getSaltedPassHash() {
        return saltedPassHash;
    }

    public int getPermLevel() {
        return permLevel;
    }

}
