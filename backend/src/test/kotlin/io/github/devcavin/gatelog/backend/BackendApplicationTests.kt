package io.github.devcavin.gatelog.backend

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Temporarily disabled until real tests are added")
class BackendApplicationTests {
    private val logger = LoggerFactory.getLogger(BackendApplicationTests::class.java)

    @Disabled
    @Test
    fun contextLoads() {
        logger.info("Application context loaded successfully")
    }

}
