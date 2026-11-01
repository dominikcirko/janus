package janus;

import janus.common.config.ApiVersioningConfig;
import janus.common.config.JpaAuditingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApiVersioningConfig.class, JpaAuditingConfig.class})
public class JanusApplication {

	public static void main(String[] args) {
		SpringApplication.run(JanusApplication.class, args);
	}

}
