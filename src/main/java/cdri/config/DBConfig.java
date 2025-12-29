package cdri.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.cdri-books.hikari")
    public HikariConfig cdriBooksHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public DataSource cdriBooksDataSourceProperties() {
        return new HikariDataSource(cdriBooksHikariConfig());
    }
}
