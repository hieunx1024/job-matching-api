package vn.hieu.jobhunter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class CheckDBTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testShowData() {
        System.out.println("====== START OUTPUT ======");
        List<Map<String, Object>> users = jdbcTemplate
                .queryForList("SELECT id, email FROM users WHERE email='hr@gmail.com'");
        if (users.isEmpty()) {
            System.out.println("No user found");
        } else {
            long userId = ((Number) users.get(0).get("id")).longValue();
            System.out.println("User ID: " + userId);

            List<Map<String, Object>> subs = jdbcTemplate
                    .queryForList("SELECT * FROM user_subscriptions WHERE user_id=" + userId);
            System.out.println("User Subscriptions: " + subs);

            List<Map<String, Object>> payments = jdbcTemplate
                    .queryForList("SELECT * FROM payment_histories WHERE user_id=" + userId);
            System.out.println("Payment History: " + payments);

            List<Map<String, Object>> packages = jdbcTemplate.queryForList("SELECT * FROM subscriptions");
            System.out.println("Packages: " + packages);
        }
        System.out.println("====== END OUTPUT ======");
    }
}
