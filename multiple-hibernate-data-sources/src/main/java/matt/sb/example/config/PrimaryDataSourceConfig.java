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
import org.springframework.context.annotation.Primary;
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
 * associated with the primary (MySQL) db instance. Point these JPA repositories
 * at the hibernate beans being created in this configuration. Some of these beans
 * must be annotated with Primary since Spring normally only expects one of them to
 * be present.
 */
@EnableJpaRepositories(
        basePackages = "matt.sb.example.repositories.primary",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager")
public class PrimaryDataSourceConfig {
    @Bean
    // Read the hibernate configuration variables associated with the primary (MySQL) db into a properties object
    @ConfigurationProperties("app.hibernateconfig.primary")
    public Properties primaryHibernateConfig() {
        return new Properties();
    }

    @Bean
    @Primary
    // Read the JPA configuration variables associated with the primary (MySQL) db into a properties object. This bean
    // is normally automatically created by spring, but must be manually setup in the case of multiple data sources.
    @ConfigurationProperties("app.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.primary.configuration")
    // Create a data source bean using the primary (MySQL) JPA config properties. This bean is normally
    // automatically created by spring, but must be manually setup in the case of multiple data sources.
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties")DataSourceProperties primaryDataSourceProperties) {
        return primaryDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    // Create an entity manager using the primary data source and hibernate configuration. This bean is normally
    // automatically created by spring, but must be manually setup in the case of multiple data sources.
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            ConfigurableListableBeanFactory beanFactory,
            @Qualifier("primaryDataSource")DataSource primaryDataSource,
            @Qualifier("primaryHibernateConfig")Properties primaryHibernateConfig,
            @Value("${app.datasource.primary.scanpackages}")String scanPackages) {

        // Convert the properties object into a string-string hashmap
        Map<String, String> propertiesAsMap = new HashMap<>();
        primaryHibernateConfig.stringPropertyNames().forEach(propertyName -> {
            propertiesAsMap.put(propertyName, primaryHibernateConfig.getProperty(propertyName));
        });

        LocalContainerEntityManagerFactoryBean emfb = builder
                .dataSource(primaryDataSource)
                .packages(scanPackages)
                .properties(propertiesAsMap)
                .build();

        // Connects AttributeConverters to spring bean container, allowing for autowiring of other beans in converters
        emfb.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));

        return emfb;
    }

    @Bean
    @Primary
    // Create a transaction manager for the JPA repositories associated with the primary (MySQL) db instance. This bean
    // is normally automatically created by spring, but must be manually setup in the case of multiple data sources.
    public PlatformTransactionManager primaryTransactionManager(
            final @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }
}
