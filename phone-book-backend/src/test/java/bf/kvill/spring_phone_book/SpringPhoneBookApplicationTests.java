package bf.kvill.spring_phone_book;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires MySQL - run with Docker Compose")
class SpringPhoneBookApplicationTests {

	@Test
	void contextLoads() {
	}

}
