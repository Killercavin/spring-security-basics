package io.github.devcavin.gatelog

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Temporarily disabled until real tests are added")
class GatelogApplicationTests {
    private val logger = LoggerFactory.getLogger(GatelogApplicationTests::class.java)

    @Disabled
    @Test
    fun contextLoads() {
        logger.info("Application context loaded successfully")
    }

}
