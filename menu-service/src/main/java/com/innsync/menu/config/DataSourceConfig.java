package com.innsync.menu.config;

import com.innsync.menu.tenant.TenantRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    // --- Step 1: Provide DataSourceProperties bean for primary DB
    @Primary
    @Bean
    @ConfigurationProperties("app.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    // --- Step 2: Create primary DataSource from the properties
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    // --- Step 3: JdbcTemplate for registry DB
    @Bean
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

    // --- Step 4: Tenant Routing DataSource
    @Bean
    public DataSource tenantDataSource(JdbcTemplate primaryJdbcTemplate,
                                      TenantDataSourceProperties tenantProps) {

        List<String> tenantIds = primaryJdbcTemplate.queryForList(
                "SELECT db_identifier FROM businesses", String.class);

        Map<Object, Object> targetDataSources = new HashMap<>();

        for (String tenantId : tenantIds) {
            String url = tenantProps.getUrlTemplate().replace("{dbName}", tenantId);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(tenantProps.getUsername());
            config.setPassword(tenantProps.getPassword());
            config.setDriverClassName(tenantProps.getDriverClassName());

            targetDataSources.put(tenantId, new HikariDataSource(config));
        }

        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);

        if (!tenantIds.isEmpty()) {
            routingDataSource.setDefaultTargetDataSource(targetDataSources.get(tenantIds.get(0)));
        }

        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}
