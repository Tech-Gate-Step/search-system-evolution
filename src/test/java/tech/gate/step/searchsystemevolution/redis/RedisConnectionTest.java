package tech.gate.step.searchsystemevolution.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedisConnectionTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisConnectionFactory cf;

    @AfterEach
    void setup() {
        // 매 테스트마다 캐시 초기화
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 레디스_연결_테스트() {
        //Ping
        String pong = cf.getConnection().ping();
        assertThat(pong).isEqualToIgnoringCase("PONG");

        // Set/Get
        stringRedisTemplate.opsForValue().set("health:ping", "ok");
        String value = stringRedisTemplate.opsForValue().get("health:ping");
        assertThat(value).isEqualTo("ok");
    }
    
}
