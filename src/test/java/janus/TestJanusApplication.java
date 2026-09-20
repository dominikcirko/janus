package janus;

import org.springframework.boot.SpringApplication;

public class TestJanusApplication {

	public static void main(String[] args) {
		SpringApplication.from(JanusApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
