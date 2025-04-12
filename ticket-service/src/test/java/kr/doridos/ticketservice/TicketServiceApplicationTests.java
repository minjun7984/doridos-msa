package kr.doridos.ticketservice;

import kr.doridos.ticketservice.util.ElasticSearchTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@Import(ElasticSearchTestContainer.class)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
class TicketServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
