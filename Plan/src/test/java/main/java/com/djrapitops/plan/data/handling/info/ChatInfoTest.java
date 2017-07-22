/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.main.java.com.djrapitops.plan.data.handling.info;

import main.java.com.djrapitops.plan.data.UserData;
import main.java.com.djrapitops.plan.data.handling.info.ChatInfo;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import test.java.utils.MockUtils;

/**
 *
 * @author Rsl1122
 */
public class ChatInfoTest {

    /**
     *
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     *
     */
    @Test
    public void testProcessNick() {
        UserData data = MockUtils.mockUser();
        String expected = "TestNicknameChatInfo";
        ChatInfo i = new ChatInfo(data.getUuid(), expected, "im 18 male");
        assertTrue("Didn't succeed", i.process(data));
        assertTrue("Didn't add nickname", data.getNicknames().contains(expected));
    }

    /**
     *
     */
    @Test
    public void testProcessWrongUUID() {
        UserData data = MockUtils.mockUser();
        String expected = "TestNicknameChatInfo";
        ChatInfo i = new ChatInfo(null, expected, "im 18 male");
        assertTrue("Succeeded.", !i.process(data));
    }
}
