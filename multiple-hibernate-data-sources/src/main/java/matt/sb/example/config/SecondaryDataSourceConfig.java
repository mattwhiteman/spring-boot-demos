package matt.sb.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
/**
 * Scan for any JPA repository classes in the specified package so that they are
 * associated with the second (H2) db instance. Point these JPA repositories
 * at the hibernate beans being created in this configuration
 */
@EnableJpaRepositories(
        basePackages = "matt.sb.example.repositories.secondary",
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager")
public class SecondaryDataSourceConfig {
    @Bean
    // Read the hibernate configuration variables associated with the second (H2) db into a properties object
    @ConfigurationProperties("app.hibernateconfig.secondary")
    public Properties secondaryHibernateConfig() {
        return new Properties();
    }

    @Bean
    // Read the JPA configuration variables associated with the second (H2) db into a properties object. This bean
    // is normally automatically created by spring, but must be manually setup in the case of multiple data sources.
    @ConfigurationProperties("app.datasource.secondary")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    // Create a data source bean using the second (H2) JPA config properties. This bean is normally
    // automatically created by spring, but must be manually setup in the case of multiple data sources.
    @ConfigurationProperties("app.datasource.secondary.configuration")
    public DataSource secondaryDataSource(@Qualifier("secondaryDataSourceProperties")DataSourceProperties secondaryDataSourceProperties) {
        return secondaryDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    // Create an entity manager using the second data source and hibernate configuration. This bean is normally
    // automatically created by spring, but must be manually setup in the case of multiple data sources.
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            ConfigurableListableBeanFactory beanFactory,
            @Qualifier("secondaryDataSource")DataSource secondaryDataSource,
            @Qualifier("secondaryHibernateConfig")Properties secondaryHibernateConfig,
            @Value("${app.datasource.secondary.scanpackages}")String scanPackages) {

        Map<String, String> propertiesAsMap = new HashMap<>();
        secondaryHibernateConfig.stringPropertyNames().forEach(propertyName -> {
            propertiesAsMap.put(propertyName, secondaryHibernateConfig.getProperty(propertyName));
        });

        LocalContainerEntityManagerFactoryBean emfb = builder
                .dataSource(secondaryDataSource)
                .packages(scanPackages)
                .properties(propertiesAsMap)
                .build();

        // Connects AttributeConverters to spring bean container, allowing for autowiring of other beans in converters
        emfb.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));

        return emfb;
    }

    @Bean
    // Create a transaction manager for the JPA repositories associated with the second (H2) db instance. This bean
    // is normally automatically created by spring, but must be manually setup in the case of multiple data sources.
    public PlatformTransactionManager secondaryTransactionManager(
            final @Qualifier("secondaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
        return new JpaTransactionManager(secondaryEntityManagerFactory.getObject());
    }
}
