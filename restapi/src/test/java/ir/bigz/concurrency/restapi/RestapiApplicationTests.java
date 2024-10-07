package ir.bigz.concurrency.restapi;

import ir.bigz.concurrency.restapi.dto.Purchase;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestapiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void generatePurchase() {
		Purchase purchase = Instancio.create(Purchase.class);
		System.out.println(purchase);
	}

}
