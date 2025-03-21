package cdf.training.svc.datatransfer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "cdf.training.svc.datatransfer.repository")
public class DatabaseConfig {
}