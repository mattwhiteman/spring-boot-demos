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
@EnableJpaRepositories(
        basePackages = "matt.sb.example.repositories.primary",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager")
public class PrimaryDataSourceConfig {
    @Bean
    @ConfigurationProperties("app.hibernateconfig.primary")
    public Properties primaryHibernateConfig() {
        return new Properties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.primary.configuration")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties")DataSourceProperties primaryDataSourceProperties) {
        return primaryDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            ConfigurableListableBeanFactory beanFactory,
            @Qualifier("primaryDataSource")DataSource primaryDataSource,
            @Qualifier("primaryHibernateConfig")Properties primaryHibernateConfig,
            @Value("${app.datasource.primary.scanpackages}")String scanPackages) {

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
    public PlatformTransactionManager primaryTransactionManager(
            final @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }
}
