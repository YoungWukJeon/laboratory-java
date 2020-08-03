package chatting.model;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class UserTest {
    @Test
    public void 계정_생성() {
        User user1 = new User();
        User user2 = new User();
        assertNotEquals(user1, user2);
    }
}