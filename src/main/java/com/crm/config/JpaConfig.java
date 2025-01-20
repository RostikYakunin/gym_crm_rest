package com.crm.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class JpaConfig {
    @Value("${datasource.driver}")
    private String driver;
    @Value("${datasource.driver.type}")
    private String driverType;
    @Value("${datasource.host}")
    private String host;
    @Value("${datasource.port}")
    private String port;
    @Value("${datasource.db}")
    private String db;
    @Value("${datasource.username}")
    private String userName;
    @Value("${datasource.password}")
    private String userPassword;
    @Value("${jpa.ddl.auto}")
    private String jpaDdl;
    @Value("${jpa.show-sql}")
    private boolean showSql;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl("jdbc:" + driverType + "://" + host + ":" + port + "/" + db);
        dataSource.setUsername(userName);
        dataSource.setPassword(userPassword);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        var factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("com.crm.repositories.entities");
        factory.setPersistenceUnitName("gym-crm");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        var jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", jpaDdl);
        jpaProperties.put("hibernate.show_sql", showSql);
        jpaProperties.put("hibernate.format_sql", "true");
        factory.setJpaProperties(jpaProperties);

        return factory;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}