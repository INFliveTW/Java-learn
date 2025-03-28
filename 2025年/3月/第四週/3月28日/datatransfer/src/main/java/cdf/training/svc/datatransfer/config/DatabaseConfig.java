
package cdf.training.svc.datatransfer.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "cdf.training.svc.datatransfer.repository")
public class DatabaseConfig {

}
// @MapperScan：告訴 MyBatis 掃描指定的 Mapper 接口路徑（這裡是 repository 包）。
// 移除 JPA 配置：不再需要 @EnableJpaRepositories。