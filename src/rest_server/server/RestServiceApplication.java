package rest_server.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestServiceApplication {
	RestServiceApplication() {
	}

	public static void main(String[] args) {
		ModelStorage.createSampleModel();

		SpringApplication.run(RestServiceApplication.class, args);
	}
}
