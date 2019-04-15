package ee.project.offline.quiz;

import ee.project.offline.quiz.service.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class OfflineQuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfflineQuizApplication.class, args);
	}

}
