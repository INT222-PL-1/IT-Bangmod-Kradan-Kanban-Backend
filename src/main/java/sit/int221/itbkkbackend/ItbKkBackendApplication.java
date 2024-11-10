package sit.int221.itbkkbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import sit.int221.itbkkbackend.v3.properties.StorageProperties;

@EnableConfigurationProperties({StorageProperties.class})
@SpringBootApplication
public class ItbKkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItbKkBackendApplication.class, args);
    }

}
