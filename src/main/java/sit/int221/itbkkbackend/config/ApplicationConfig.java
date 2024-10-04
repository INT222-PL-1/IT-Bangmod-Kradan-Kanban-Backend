package sit.int221.itbkkbackend.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.utils.UriExtractor;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
    @Bean
    public ListMapper listMapper() {return new ListMapper(); }

    @Bean
    public UriExtractor uriExtractor() {return new UriExtractor(); }
}

