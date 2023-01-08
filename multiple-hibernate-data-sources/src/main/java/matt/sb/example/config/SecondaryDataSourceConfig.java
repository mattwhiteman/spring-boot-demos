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
@EnableJpaRepositories(
        basePackages = "matt.sb.example.repositories.secondary",
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager")
public class SecondaryDataSourceConfig {
    @Bean
    @ConfigurationProperties("app.hibernateconfig.secondary")
    public Properties secondaryHibernateConfig() {
        return new Properties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.secondary")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.secondary.configuration")
    public DataSource secondaryDataSource(@Qualifier("secondaryDataSourceProperties")DataSourceProperties secondaryDataSourceProperties) {
        return secondaryDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
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
    public PlatformTransactionManager secondaryTransactionManager(
            final @Qualifier("secondaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
        return new JpaTransactionManager(secondaryEntityManagerFactory.getObject());
    }
}
