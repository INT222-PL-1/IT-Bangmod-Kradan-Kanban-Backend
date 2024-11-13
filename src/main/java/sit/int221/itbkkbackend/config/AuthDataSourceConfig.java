package sit.int221.itbkkbackend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
@EnableJpaRepositories(
        basePackages =  "sit.int221.itbkkbackend.auth"  ,
        entityManagerFactoryRef = "authEntityManagerFactoryBean",
        transactionManagerRef = "authTransactionManager"
)
public class AuthDataSourceConfig {
    @ConfigurationProperties("spring.datasource.authen")
    @Bean
    public DataSourceProperties authDataSourceProperties(){
        return new DataSourceProperties();
    }


    @Bean
    public DataSource authDataSource(){
        HikariDataSource dataSource = authDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setPoolName("AuthPool");
        return dataSource;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean authEntityManagerFactoryBean(EntityManagerFactoryBuilder entityManagerFactoryBuilder, @Qualifier("authDataSource") DataSource dataSource){
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("sit.int221.itbkkbackend.auth")
                .build();
    }
    @Bean
    PlatformTransactionManager authTransactionManager(@Qualifier("authEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean emfb){
        return new JpaTransactionManager(emfb.getObject());
    }
}
