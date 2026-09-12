package io.gh.helder.aisafe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AisafeApplicationTests {

	@Test
	void contextLoads() {
	}

}
