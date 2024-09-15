package sit.int221.itbkkbackend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
@EnableJpaRepositories(
        basePackages = {"sit.int221.itbkkbackend.v3.repositories","sit.int221.itbkkbackend.v2.repositories" , "sit.int221.itbkkbackend.v1.repositories"}  ,
        entityManagerFactoryRef = "taskEntityManagerFactoryBean",
        transactionManagerRef = "taskTransactionManager"
)
public class TaskDataSourceConfig {
    @ConfigurationProperties("spring.datasource.data")
    @Bean
    public DataSourceProperties taskDataSourceProperties(){
        return new DataSourceProperties();
    };
    @Primary
    @Bean
    public DataSource taskDataSource(){
        HikariDataSource dataSource = taskDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setPoolName("TaskPool");
        return dataSource;
    }
    @Primary
    @Bean
    LocalContainerEntityManagerFactoryBean taskEntityManagerFactoryBean(EntityManagerFactoryBuilder entityManagerFactoryBuilder, @Qualifier("taskDataSource") DataSource dataSource){
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("sit.int221.itbkkbackend.v3.entities","sit.int221.itbkkbackend.v2.entities","sit.int221.itbkkbackend.v1.entities")
                .build();
    }
    @Primary
    @Bean
    PlatformTransactionManager taskTransactionManager(@Qualifier("taskEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean emfb){
        return new JpaTransactionManager(emfb.getObject());
    }
}
