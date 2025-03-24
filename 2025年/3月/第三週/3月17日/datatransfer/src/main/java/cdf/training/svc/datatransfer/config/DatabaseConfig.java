package cdf.training.svc.datatransfer.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    // 目前無需額外配置，JdbcTemplate 會自動使用 application.yml 的 datasource
    // 可在此添加其他 JDBC 相關配置（如 DataSource 客製化），但這裡留空即可
}
// package cdf.training.svc.datatransfer.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @Configuration
// @EnableJpaRepositories(basePackages = "cdf.training.svc.datatransfer.repository")
// public class DatabaseConfig {
// }